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
package openfoodfacts.github.scrachx.openfood.features.preferences

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import openfoodfacts.github.scrachx.openfood.AppFlavor
import openfoodfacts.github.scrachx.openfood.AppFlavor.Companion.isFlavors
import openfoodfacts.github.scrachx.openfood.BuildConfig
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.analytics.AnalyticsEvent
import openfoodfacts.github.scrachx.openfood.analytics.MatomoAnalytics
import openfoodfacts.github.scrachx.openfood.customtabs.CustomTabActivityHelper
import openfoodfacts.github.scrachx.openfood.customtabs.CustomTabsHelper
import openfoodfacts.github.scrachx.openfood.customtabs.WebViewFallback
import openfoodfacts.github.scrachx.openfood.jobs.LoadTaxonomiesWorker
import openfoodfacts.github.scrachx.openfood.models.DaoSession
import openfoodfacts.github.scrachx.openfood.models.entities.analysistag.AnalysisTagNameDao
import openfoodfacts.github.scrachx.openfood.models.entities.analysistagconfig.AnalysisTagConfig
import openfoodfacts.github.scrachx.openfood.models.entities.analysistagconfig.AnalysisTagConfigDao
import openfoodfacts.github.scrachx.openfood.models.entities.country.CountryNameDao
import openfoodfacts.github.scrachx.openfood.utils.INavigationItem
import openfoodfacts.github.scrachx.openfood.utils.LocaleManager
import openfoodfacts.github.scrachx.openfood.utils.LocaleUtils
import openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener
import openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener.NavigationDrawerType
import openfoodfacts.github.scrachx.openfood.utils.OFFDatabaseHelper
import openfoodfacts.github.scrachx.openfood.utils.OneTimeWorkRequest
import openfoodfacts.github.scrachx.openfood.utils.SearchSuggestionProvider
import openfoodfacts.github.scrachx.openfood.utils.SupportedLanguages
import openfoodfacts.github.scrachx.openfood.utils.getAppPreferences
import openfoodfacts.github.scrachx.openfood.utils.list
import openfoodfacts.github.scrachx.openfood.utils.requirePreference
import openfoodfacts.github.scrachx.openfood.utils.unique
import org.greenrobot.greendao.query.WhereCondition.StringCondition
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PreferencesFragment : PreferenceFragmentCompat(), INavigationItem {

    @Inject
    lateinit var daoSession: DaoSession

    @Inject
    lateinit var matomoAnalytics: MatomoAnalytics

    @Inject
    lateinit var localeManager: LocaleManager

    @Inject
    lateinit var preferencesListener: PreferencesListener

    @NavigationDrawerType
    override fun getNavigationDrawerType() = NavigationDrawerListener.ITEM_PREFERENCES

    override val navigationDrawerListener: NavigationDrawerListener? by lazy {
        if (activity is NavigationDrawerListener) activity as NavigationDrawerListener
        else null
    }


    override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setHasOptionsMenu(true)

        val settings = requireActivity().getAppPreferences()

        setupLanguagePref()

        requirePreference<ListPreference>(getString(R.string.pref_app_theme_key)) {
            setEntries(R.array.application_theme_entries)
            setEntryValues(R.array.application_theme_entries)
            setOnPreferenceChangeListener { _, value ->
                when (value) {
                    getString(R.string.day) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    getString(R.string.night) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    getString(R.string.follow_system) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                true
            }
        }

        requirePreference<Preference>(getString(R.string.pref_delete_history_key)) {
            setOnPreferenceChangeListener { _, _ ->
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.pref_delete_history_dialog_content)
                    .setPositiveButton(R.string.delete_txt) { _, _ ->

                        // Clear history
                        SearchRecentSuggestions(
                            requireContext(),
                            SearchSuggestionProvider.AUTHORITY,
                            SearchSuggestionProvider.MODE
                        ).clearHistory()

                        Toast.makeText(requireContext(), getString(R.string.pref_delete_history), Toast.LENGTH_SHORT)
                            .show()
                    }
                    .setNegativeButton(R.string.dialog_cancel) { d, _ -> d.dismiss() }
                    .show()

                true
            }
        }

        requirePreference<SwitchPreference>(getString(R.string.pref_scanner_mlkit_key)) {

            if (!BuildConfig.USE_MLKIT) {
                // We're on F-Droid
                isEnabled = false

                setSummary(R.string.pref_scanner_mlkit_fdroid)
                return@requirePreference
            }

            setOnPreferenceChangeListener { _, newValue ->
                if (newValue == true) {
                    MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.preference_choose_scanner_dialog_title)
                        .setMessage(R.string.preference_choose_scanner_dialog_body)
                        .setPositiveButton(R.string.proceed) { d, _ ->
                            d.dismiss()
                            isChecked = true
                            settings.edit { putBoolean(getString(R.string.pref_scanner_mlkit_key), true) }
                            Toast.makeText(requireActivity(), getString(R.string.changes_saved), Toast.LENGTH_SHORT)
                                .show()
                        }
                        .setNegativeButton(android.R.string.cancel) { d, _ ->
                            d.dismiss()
                            isChecked = false
                            settings.edit { putBoolean(getString(R.string.pref_scanner_mlkit_key), false) }
                        }
                        .show()

                } else {
                    isChecked = false
                    settings.edit { putBoolean(getString(R.string.pref_scanner_mlkit_key), false) }
                    Toast.makeText(requireActivity(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show()
                }
                true
            }

        }

        requirePreference<Preference>(getString(R.string.pref_export_db_key)) {

            setOnPreferenceClickListener {
                OFFDatabaseHelper.exportDB(requireContext())
                true
            }

        }

        requirePreference<ListPreference>(getString(R.string.pref_country_key)) {

            lifecycleScope.launch(IO) {

                val countryNames = daoSession.countryNameDao.list {
                    where(CountryNameDao.Properties.LanguageCode.eq(localeManager.getLanguage()))
                        .orderAsc(CountryNameDao.Properties.Name)
                }

                withContext(Main) {
                    entries = countryNames.map { it.name }.toTypedArray()
                    entryValues = countryNames.map { it.countyTag }.toTypedArray()
                }
            }

            setOnPreferenceChangeListener { preference, newValue ->
                val country = newValue as String?
                settings.edit { putString(preference.key, country) }
                Toast.makeText(context, getString(R.string.changes_saved), Toast.LENGTH_SHORT).show()
                true
            }
        }

        requirePreference<Preference>(getString(R.string.pref_contact_us_key)) {
            setOnPreferenceClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_SENDTO).apply {
                        data = getString(R.string.off_mail).toUri()
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(requireActivity(), R.string.email_not_found, Toast.LENGTH_SHORT).show()
                }
                true
            }
        }

        requirePreference<Preference>(getString(R.string.pref_rate_us_key)) {
            setOnPreferenceClickListener {
                try {
                    startActivity(
                        Intent(
                            ACTION_VIEW,
                            "market://details?id=${requireActivity().packageName}".toUri()
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            ACTION_VIEW,
                            "https://play.google.com/store/apps/details?id=${requireActivity().packageName}".toUri()
                        )
                    )
                }
                true
            }
        }

        requirePreference<Preference>(getString(R.string.pref_faq_key))
            .setOnPreferenceClickListener { openWebCustomTab(R.string.faq_url) }

        requirePreference<Preference>(getString(R.string.pref_terms_key))
            .setOnPreferenceClickListener { openWebCustomTab(R.string.terms_url) }

        requirePreference<Preference>(getString(R.string.pref_help_translate_key))
            .setOnPreferenceClickListener { openWebCustomTab(R.string.translate_url) }

        requirePreference<ListPreference>(getString(R.string.pref_energy_unit_key)) {
            setEntries(R.array.energy_units)
            setEntryValues(R.array.energy_units)

            setOnPreferenceChangeListener { _, newValue ->
                settings.edit { putString(getString(R.string.pref_energy_unit_key), newValue as String?) }
                Toast.makeText(requireActivity(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show()
                true
            }
        }

        requirePreference<ListPreference>(getString(R.string.pref_volume_unit_key)) {
            setEntries(R.array.volume_units)
            setEntryValues(R.array.volume_units)

            setOnPreferenceChangeListener { _, newValue ->
                settings.edit { putString(getString(R.string.pref_volume_unit_key), newValue as String?) }
                Toast.makeText(requireActivity(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show()
                true
            }
        }

        requirePreference<ListPreference>(getString(R.string.pref_resolution_key)) {
            setEntries(R.array.upload_image)
            setEntryValues(R.array.upload_image)

            setOnPreferenceChangeListener { _, newValue ->
                settings.edit { putString(getString(R.string.pref_resolution_key), newValue as String?) }
                Toast.makeText(requireActivity(), getString(R.string.changes_saved), Toast.LENGTH_SHORT).show()
                true
            }
        }

        // Disable photo mode for OpenProductFacts
        if (isFlavors(AppFlavor.OPF)) {
            requirePreference<Preference>(getString(R.string.pref_show_product_photos_key)).isVisible = false
        }

        // Preference to show version name
        requirePreference<Preference>(getString(R.string.pref_version_key)) {
            try {
                val pInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
                val version = pInfo.versionName
                val versionCode = PackageInfoCompat.getLongVersionCode(pInfo)

                summary = "${getString(R.string.version_string)} $version ($versionCode)"
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(PreferencesFragment::class.simpleName, "onCreatePreferences", e)
            }

        }


        if (isFlavors(AppFlavor.OFF, AppFlavor.OBF, AppFlavor.OPFF)) {
            setupAnalysisTagConfigs()
        } else {
            preferenceScreen.removePreference(preferenceScreen.requirePreference(getString(R.string.pref_key_display)))
        }

        requirePreference<SwitchPreference>(getString(R.string.pref_analytics_reporting_key)) {
            setOnPreferenceChangeListener { _, newValue ->
                matomoAnalytics.setEnabled(newValue == true)
                true
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.action_search).isVisible = false
    }

    override fun onResume() {
        super.onResume()
        try {
            (activity as AppCompatActivity).supportActionBar!!.title = getString(R.string.action_preferences)
        } catch (e: NullPointerException) {
            throw IllegalStateException("Preference fragment not attached to AppCompatActivity.")
        }
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(preferencesListener)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(preferencesListener)
        super.onPause()
    }


    private fun openWebCustomTab(@StringRes urlRes: Int): Boolean {
        val helper = CustomTabActivityHelper()
        val customTabsIntent = CustomTabsHelper.getCustomTabsIntent(requireContext(), helper.session)
        CustomTabActivityHelper.openCustomTab(
            requireActivity(),
            customTabsIntent,
            getString(urlRes).toUri(),
            WebViewFallback()
        )
        return true
    }

    private fun setupDisplayCategory(analysisTagConfigs: List<AnalysisTagConfig>) {
        if (!isAdded) return

        val displayCategory = requirePreference<PreferenceCategory>(getString(R.string.pref_key_display))
            .apply { removeAll() }

        preferenceScreen.addPreference(displayCategory)

        // If analysis tag is empty show "Load ingredient detection data" option in order to manually reload taxonomies
        if (analysisTagConfigs.isNotEmpty()) {
            analysisTagConfigs
                .map(::createAnalysisTagPreference)
                .forEach(displayCategory::addPreference)
        } else {
            Preference(preferenceScreen.context).apply {
                setTitle(R.string.load_ingredient_detection_data)
                setSummary(R.string.load_ingredient_detection_data_summary)

                setOnPreferenceClickListener { pref ->
                    pref.onPreferenceClickListener = null
                    val request = OneTimeWorkRequest<LoadTaxonomiesWorker>()

                    // The service will load server resources only if newer than already downloaded...
                    WorkManager.getInstance(requireContext()).let {
                        it.enqueue(request)
                        it.getWorkInfoByIdLiveData(request.id)
                            .observe(this@PreferencesFragment) { workInfo: WorkInfo? ->
                                when (workInfo?.state) {
                                    WorkInfo.State.RUNNING -> {
                                        pref.setTitle(R.string.please_wait)
                                        pref.setIcon(R.drawable.ic_cloud_download_black_24dp)
                                        pref.summary = null
                                        pref.widgetLayoutResource = R.layout.loading
                                    }

                                    WorkInfo.State.SUCCEEDED -> {
                                        setupAnalysisTagConfigs()
                                    }

                                    else -> Unit  // Nothing
                                }
                            }
                    }
                    true
                }
                displayCategory.addPreference(this)
            }
        }

        displayCategory.isVisible = true
    }

    private fun createAnalysisTagPreference(config: AnalysisTagConfig) = CheckBoxPreference(requireContext()).apply {
        setDefaultValue(true)

        key = config.type
        title = getString(R.string.display_analysis_tag_status, config.typeName.lowercase(Locale.getDefault()))
        val drawable = when (config.typeName.lowercase(Locale.getDefault())) {
            "palm oil" -> R.drawable.ic_preference_drop
            "vegan" -> R.drawable.ic_preference_vegan
            "vegetarian" -> R.drawable.ic_preference_vegetarian
            else -> null
        }
        drawable?.let {
            icon = ContextCompat.getDrawable(requireContext(), it)
        }
        summary = null
        summaryOn = null
        summaryOff = null

        setOnPreferenceChangeListener { _, newValue ->
            val event = if (newValue == true) {
                AnalyticsEvent.IngredientAnalysisEnabled(config.type)
            } else {
                AnalyticsEvent.IngredientAnalysisDisabled(config.type)
            }
            matomoAnalytics.trackEvent(event)
            true
        }
    }

    private fun setupAnalysisTagConfigs() {
        val language = localeManager.getLanguage()

        lifecycleScope.launch(IO) {
            val analysisTagConfigs = daoSession.analysisTagConfigDao.list {
                where(StringCondition("1 GROUP BY type"))
                orderAsc(AnalysisTagConfigDao.Properties.Type)
            }

            analysisTagConfigs.forEach { config ->
                val type = "en:${config.type}"

                val analysisTagTypeName = daoSession.analysisTagNameDao.unique {
                    where(AnalysisTagNameDao.Properties.AnalysisTag.eq(type))
                    where(AnalysisTagNameDao.Properties.LanguageCode.eq(language))
                } ?: daoSession.analysisTagNameDao.unique {
                    where(AnalysisTagNameDao.Properties.AnalysisTag.eq(type))
                    where(AnalysisTagNameDao.Properties.LanguageCode.eq("en"))
                }

                config.typeName = if (analysisTagTypeName != null) analysisTagTypeName.name else config.type
            }

            withContext(Main) { setupDisplayCategory(analysisTagConfigs) }
        }
    }

    private fun setupLanguagePref() {
        val localeCodes = SupportedLanguages.codes()
        val localeNames = localeCodes.map { lc ->
            val locale = LocaleUtils.parseLocale(lc)
            // Get the locale name in the locale language (eg. "English", "Italiano", "Français")
            locale.getDisplayName(locale)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        }

        requirePreference<ListPreference>(getString(R.string.pref_language_key)) {
            entries = localeNames.toTypedArray()
            entryValues = localeCodes.toTypedArray()

            setOnPreferenceChangeListener { _, lc ->
                // Change app language
                if (lc is String) {
                    localeManager.saveLanguageToPrefs(requireContext(), Locale(lc))
                    Toast.makeText(context, getString(R.string.changes_saved), Toast.LENGTH_SHORT).show()
                    requireActivity().recreate()
                }
                true
            }
        }
    }

    companion object {
        const val LOGIN_SHARED_PREF = "login"
        const val APP_SHARED_PREF = "prefs"

        fun newInstance() = PreferencesFragment().apply { arguments = Bundle() }
    }
}
