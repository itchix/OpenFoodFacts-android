/*
 * Copyright 2016-2020 Open Food Facts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package openfoodfacts.github.scrachx.openfood.features;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.afollestad.materialdialogs.MaterialDialog;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import openfoodfacts.github.scrachx.openfood.AppFlavors;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.app.OFFApplication;
import openfoodfacts.github.scrachx.openfood.customtabs.CustomTabActivityHelper;
import openfoodfacts.github.scrachx.openfood.customtabs.WebViewFallback;
import openfoodfacts.github.scrachx.openfood.jobs.LoadTaxonomiesWorker;
import openfoodfacts.github.scrachx.openfood.jobs.OfflineProductWorker;
import openfoodfacts.github.scrachx.openfood.models.DaoSession;
import openfoodfacts.github.scrachx.openfood.models.entities.analysistag.AnalysisTagName;
import openfoodfacts.github.scrachx.openfood.models.entities.analysistag.AnalysisTagNameDao;
import openfoodfacts.github.scrachx.openfood.models.entities.analysistagconfig.AnalysisTagConfig;
import openfoodfacts.github.scrachx.openfood.models.entities.analysistagconfig.AnalysisTagConfigDao;
import openfoodfacts.github.scrachx.openfood.models.entities.country.CountryName;
import openfoodfacts.github.scrachx.openfood.models.entities.country.CountryNameDao;
import openfoodfacts.github.scrachx.openfood.utils.INavigationItem;
import openfoodfacts.github.scrachx.openfood.utils.LocaleHelper;
import openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener;
import openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener.NavigationDrawerType;
import openfoodfacts.github.scrachx.openfood.utils.PreferencesUtils;
import openfoodfacts.github.scrachx.openfood.utils.SearchSuggestionProvider;

import static androidx.work.WorkInfo.State.RUNNING;
import static openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener.ITEM_PREFERENCES;

/**
 * A class for creating all the ListPreference
 */
public class PreferencesFragment extends PreferenceFragmentCompat implements INavigationItem, SharedPreferences.OnSharedPreferenceChangeListener {
    @NonNull
    public static final String LOGIN_PREF = "login";
    private final CompositeDisposable disp = new CompositeDisposable();
    private NavigationDrawerListener navigationDrawerListener;

    @NonNull
    public static PreferencesFragment newInstance() {

        Bundle args = new Bundle();

        PreferencesFragment fragment = new PreferencesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setHasOptionsMenu(true);
        final FragmentActivity activity = requireActivity();

        SharedPreferences settings = activity.getSharedPreferences("prefs", 0);

        String[] localeValues = activity.getResources().getStringArray(R.array.languages_array);
        String[] localeLabels = new String[localeValues.length];
        List<String> finalLocalValues = new ArrayList<>();
        List<String> finalLocalLabels = new ArrayList<>();

        for (int i = 0; i < localeValues.length; i++) {
            Locale current = LocaleHelper.getLocale(localeValues[i]);

            if (current != null) {
                localeLabels[i] = StringUtils.capitalize(current.getDisplayName(current));
                finalLocalLabels.add(localeLabels[i]);
                finalLocalValues.add(localeValues[i]);
            }
        }

        ListPreference languagePreference = requirePreference("Locale.Helper.Selected.Language");

        languagePreference.setEntries(finalLocalLabels.toArray(new String[0]));
        languagePreference.setEntryValues(finalLocalValues.toArray(new String[0]));

        languagePreference.setOnPreferenceChangeListener((preference, locale) -> {
            Configuration configuration = activity.getResources().getConfiguration();
            Toast.makeText(getContext(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(LocaleHelper.getLocale((String) locale));
                activity.recreate();
            }
            return true;
        });

        requirePreference("deleteSearchHistoryPreference").setOnPreferenceClickListener(preference -> {
            new MaterialDialog.Builder(activity)
                .content(R.string.search_history_pref_dialog_content)
                .positiveText(R.string.delete_txt)
                .onPositive((dialog, which) -> {
                    Toast.makeText(getContext(), getString(R.string.preference_delete_search_history), Toast.LENGTH_SHORT).show();
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getContext(), SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
                    suggestions.clearHistory();
                })
                .neutralText(R.string.dialog_cancel)
                .onNeutral((dialog, which) -> dialog.dismiss())
                .show();

            return true;
        });

        ListPreference countryPreference = requirePreference(LocaleHelper.USER_COUNTRY_PREFERENCE_KEY);
        List<String> countryLabels = new ArrayList<>();
        List<String> countryTags = new ArrayList<>();

        DaoSession daoSession = OFFApplication.getDaoSession();
        AsyncSession asyncSessionCountries = daoSession.startAsyncSession();
        CountryNameDao countryNameDao = daoSession.getCountryNameDao();

        asyncSessionCountries.setListenerMainThread(operation -> {
            @SuppressWarnings("unchecked")
            List<CountryName> countryNames = (List<CountryName>) operation.getResult();
            for (int i = 0; i < countryNames.size(); i++) {
                countryLabels.add(countryNames.get(i).getName());
                countryTags.add(countryNames.get(i).getCountyTag());
            }
            countryPreference.setEntries(countryLabels.toArray(new String[0]));
            countryPreference.setEntryValues(countryTags.toArray(new String[0]));
        });

        asyncSessionCountries.queryList(countryNameDao.queryBuilder()
            .where(CountryNameDao.Properties.LanguageCode.eq(LocaleHelper.getLanguage(getActivity())))
            .orderAsc(CountryNameDao.Properties.Name).build());

        countryPreference.setOnPreferenceChangeListener(((preference, newValue) -> {
            if (preference instanceof ListPreference &&
                preference.getKey().equals(LocaleHelper.USER_COUNTRY_PREFERENCE_KEY)) {
                String country = (String) newValue;
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(preference.getKey(), country);
                editor.apply();
                Toast.makeText(getContext(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();
            }
            return true;
        }));

        requirePreference("contact_team").setOnPreferenceClickListener(preference -> {

            Intent contactIntent = new Intent(Intent.ACTION_SENDTO);
            contactIntent.setData(Uri.parse(getString(R.string.off_mail)));
            contactIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(contactIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getActivity(), R.string.email_not_found, Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        Preference rateus = requirePreference("RateUs");
        rateus.setOnPreferenceClickListener(preference -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
            }
            return true;
        });

        requirePreference("FAQ").setOnPreferenceClickListener(preference -> openWebCustomTab(R.string.faq_url));
        requirePreference("Terms").setOnPreferenceClickListener(preference -> openWebCustomTab(R.string.terms_url));
        requirePreference("local_translate_help").setOnPreferenceClickListener(preference -> openWebCustomTab(R.string.translate_url));

        ListPreference energyUnitPreference = requirePreference("energyUnitPreference");
        String[] energyUnits = requireActivity().getResources().getStringArray(R.array.energy_units);
        energyUnitPreference.setEntries(energyUnits);
        energyUnitPreference.setEntryValues(energyUnits);
        energyUnitPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            settings.edit().putString("energyUnitPreference", (String) newValue).apply();
            Toast.makeText(getActivity(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();
            return true;
        });

        ListPreference volumeUnitPreference = requirePreference("volumeUnitPreference");
        String[] volumeUnits = requireActivity().getResources().getStringArray(R.array.volume_units);
        volumeUnitPreference.setEntries(volumeUnits);
        volumeUnitPreference.setEntryValues(volumeUnits);
        volumeUnitPreference.setOnPreferenceChangeListener(((preference, newValue) -> {
            settings.edit().putString("volumeUnitPreference", (String) newValue).apply();
            Toast.makeText(getActivity(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();
            return true;
        }));

        ListPreference imageUploadPref = requirePreference("ImageUpload");
        String[] values = requireActivity().getResources().getStringArray(R.array.upload_image);
        imageUploadPref.setEntries(values);
        imageUploadPref.setEntryValues(values);
        imageUploadPref.setOnPreferenceChangeListener((preference, newValue) -> {
            settings.edit().putString("imageUpload", (String) newValue).apply();
            Toast.makeText(getActivity(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show();
            return true;
        });

        if (AppFlavors.isFlavors(AppFlavors.OPF)) {
            requirePreference("photoMode").setVisible(false);
        }

        // Preference to show version name
        Preference versionPref = requirePreference("Version");
        versionPref.setEnabled(false);
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            String version = pInfo.versionName;
            versionPref.setSummary(getString(R.string.version_string) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(PreferencesFragment.class.getSimpleName(), "onCreatePreferences", e);
        }

        if (AppFlavors.isFlavors(AppFlavors.OFF, AppFlavors.OBF, AppFlavors.OPFF)) {
            getAnalysisTagConfigs(daoSession);
        } else {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            preferenceScreen.removePreference(PreferencesUtils.requirePreference(preferenceScreen, "display_category"));
        }
    }

    @NonNull
    private <T extends Preference> T requirePreference(@NonNull String key) {
        return PreferencesUtils.requirePreference(this, key);
    }

    private void buildDisplayCategory(List<AnalysisTagConfig> configs) {
        if (!isAdded()) {
            return;
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        PreferenceCategory displayCategory = preferenceScreen.findPreference("display_category");
        if (displayCategory == null) {
            throw new IllegalStateException("Display category preference does not exist.");
        }
        displayCategory.removeAll();
        preferenceScreen.addPreference(displayCategory);

        // If analysis tag is empty show "Load ingredient detection data" option in order to manually reload taxonomies
        if (configs == null || configs.isEmpty()) {
            Preference preference = new Preference(preferenceScreen.getContext());
            preference.setTitle(R.string.load_ingredient_detection_data);
            preference.setSummary(R.string.load_ingredient_detection_data_summary);

            preference.setOnPreferenceClickListener(pref -> {
                pref.setOnPreferenceClickListener(null);

                WorkManager manager = WorkManager.getInstance(PreferencesFragment.this.requireContext());
                OneTimeWorkRequest request = OneTimeWorkRequest.from(LoadTaxonomiesWorker.class);

                // The service will load server resources only if newer than already downloaded...
                manager.enqueue(request);
                manager.getWorkInfoByIdLiveData(request.getId()).observe(PreferencesFragment.this, workInfo -> {
                    if (workInfo != null) {
                        if (workInfo.getState() == RUNNING) {
                            preference.setTitle(R.string.please_wait);
                            preference.setIcon(R.drawable.ic_cloud_download_black_24dp);
                            preference.setSummary(null);
                            preference.setWidgetLayoutResource(R.layout.loading);
                        } else if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            getAnalysisTagConfigs(OFFApplication.getDaoSession());
                        }
                    }
                });
                return true;
            });
            displayCategory.addPreference(preference);
        } else {
            for (AnalysisTagConfig config : configs) {

                CheckBoxPreference preference = new CheckBoxPreference(preferenceScreen.getContext());
                preference.setKey(config.getType());
                preference.setDefaultValue(true);
                preference.setSummary(null);
                preference.setSummaryOn(null);
                preference.setSummaryOff(null);
                preference.setTitle(getString(R.string.display_analysis_tag_status, config.getTypeName().toLowerCase()));
                displayCategory.addPreference(preference);
            }
        }

        displayCategory.setVisible(true);
    }

    private boolean openWebCustomTab(int faqUrl) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        customTabsIntent.intent.putExtra("android.intent.extra.REFERRER", Uri.parse("android-app://" + requireContext().getPackageName()));
        CustomTabActivityHelper.openCustomTab(requireActivity(), customTabsIntent, Uri.parse(getString(faqUrl)), new WebViewFallback());
        return true;
    }

    @Override
    public NavigationDrawerListener getNavigationDrawerListener() {
        if (navigationDrawerListener == null && getActivity() instanceof NavigationDrawerListener) {
            navigationDrawerListener = (NavigationDrawerListener) getActivity();
        }

        return navigationDrawerListener;
    }

    @Override
    @NavigationDrawerType
    public int getNavigationDrawerType() {
        return ITEM_PREFERENCES;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            final AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null && activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle(getString(R.string.action_preferences));
            }
        } catch (NullPointerException e) {
            Log.e(getClass().getSimpleName(), "on resume error", e);
        }

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        disp.dispose();
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("enableMobileDataUpload".equals(key)) {
            OfflineProductWorker.scheduleSync();
        }
    }

    private void getAnalysisTagConfigs(DaoSession daoSession) {
        final String language = LocaleHelper.getLanguage(this.requireContext());

        disp.add(Single.fromCallable(() -> {
            AnalysisTagConfigDao analysisTagConfigDao = daoSession.getAnalysisTagConfigDao();
            List<AnalysisTagConfig> analysisTagConfigs = analysisTagConfigDao.queryBuilder()
                .where(new WhereCondition.StringCondition("1 GROUP BY type"))
                .orderAsc(AnalysisTagConfigDao.Properties.Type).build().list();

            AnalysisTagNameDao analysisTagNameDao = daoSession.getAnalysisTagNameDao();

            for (AnalysisTagConfig config : analysisTagConfigs) {
                String type = "en:" + config.getType();
                AnalysisTagName analysisTagTypeName = analysisTagNameDao.queryBuilder()
                    .where(AnalysisTagNameDao.Properties.AnalysisTag.eq(type),
                        AnalysisTagNameDao.Properties.LanguageCode.eq(language))
                    .unique();
                if (analysisTagTypeName == null) {
                    analysisTagTypeName = analysisTagNameDao.queryBuilder()
                        .where(AnalysisTagNameDao.Properties.AnalysisTag.eq(type),
                            AnalysisTagNameDao.Properties.LanguageCode.eq("en"))
                        .unique();
                }

                config.setTypeName(analysisTagTypeName != null ? analysisTagTypeName.getName() : config.getType());
            }
            return analysisTagConfigs;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::buildDisplayCategory));
    }
}
