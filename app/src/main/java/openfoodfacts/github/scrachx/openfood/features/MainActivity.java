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

import android.Manifest;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.AbstractBadgeableDrawerItem;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import openfoodfacts.github.scrachx.openfood.AppFlavors;
import openfoodfacts.github.scrachx.openfood.BuildConfig;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.customtabs.CustomTabActivityHelper;
import openfoodfacts.github.scrachx.openfood.customtabs.CustomTabsHelper;
import openfoodfacts.github.scrachx.openfood.customtabs.WebViewFallback;
import openfoodfacts.github.scrachx.openfood.databinding.ActivityMainBinding;
import openfoodfacts.github.scrachx.openfood.features.adapters.PhotosAdapter;
import openfoodfacts.github.scrachx.openfood.features.additives.AdditiveListActivity;
import openfoodfacts.github.scrachx.openfood.features.allergensalert.AllergensAlertFragment;
import openfoodfacts.github.scrachx.openfood.features.categories.activity.CategoryActivity;
import openfoodfacts.github.scrachx.openfood.features.compare.ProductCompareActivity;
import openfoodfacts.github.scrachx.openfood.features.listeners.CommonBottomListenerInstaller;
import openfoodfacts.github.scrachx.openfood.features.product.edit.ProductEditActivity;
import openfoodfacts.github.scrachx.openfood.features.productlists.ProductListsActivity;
import openfoodfacts.github.scrachx.openfood.features.scan.ContinuousScanActivity;
import openfoodfacts.github.scrachx.openfood.features.scanhistory.ScanHistoryActivity;
import openfoodfacts.github.scrachx.openfood.features.search.ProductSearchActivity;
import openfoodfacts.github.scrachx.openfood.features.searchbycode.SearchByCodeFragment;
import openfoodfacts.github.scrachx.openfood.features.shared.BaseActivity;
import openfoodfacts.github.scrachx.openfood.images.ProductImage;
import openfoodfacts.github.scrachx.openfood.jobs.OfflineProductWorker;
import openfoodfacts.github.scrachx.openfood.models.Product;
import openfoodfacts.github.scrachx.openfood.models.ProductImageField;
import openfoodfacts.github.scrachx.openfood.models.ProductState;
import openfoodfacts.github.scrachx.openfood.network.OpenFoodAPIClient;
import openfoodfacts.github.scrachx.openfood.utils.LocaleHelper;
import openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener;
import openfoodfacts.github.scrachx.openfood.utils.PrefManager;
import openfoodfacts.github.scrachx.openfood.utils.RealPathUtil;
import openfoodfacts.github.scrachx.openfood.utils.SearchSuggestionProvider;
import openfoodfacts.github.scrachx.openfood.utils.SearchType;
import openfoodfacts.github.scrachx.openfood.utils.Utils;

import static openfoodfacts.github.scrachx.openfood.BuildConfig.APP_NAME;

public class MainActivity extends BaseActivity implements NavigationDrawerListener {
    private static final long USER_ID = 500;
    private static final String CONTRIBUTIONS_SHORTCUT = "CONTRIBUTIONS";
    private static final String SCAN_SHORTCUT = "SCAN";
    private static final String BARCODE_SHORTCUT = "BARCODE";
    private static final int WEEK_IN_MS = 60 * 60 * 24 * 7 * 1000;
    public static final String PRODUCT_SEARCH_KEY = "product_search";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    private AccountHeader headerResult = null;
    private Drawer drawerResult = null;
    private MenuItem searchMenuItem;
    private CustomTabActivityHelper customTabActivityHelper;
    /**
     * Used to re-create the fragment after activity recreation
     */
    private CustomTabsIntent customTabsIntent;
    private Uri userAccountUri;
    private Uri contributeUri;
    private Uri discoverUri;
    private Uri userContributeUri;
    private String barcode;
    private PrefManager prefManager;
    private CompositeDisposable disp;
    ActivityResultLauncher<Void> loginActivityResultLauncher = registerForActivityResult(
        new LoginActivity.LoginContract(),
        (ActivityResultCallback<Boolean>) result -> {
            if (result){
                updateConnectedState();
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disp = new CompositeDisposable();
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utils.hideKeyboard(this);

        final IProfile<ProfileDrawerItem> profile = getUserProfile();
        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this));

        setSupportActionBar(binding.toolbarInclude.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        swapToHomeFragment();

        // chrome custom tab init
        customTabActivityHelper = new CustomTabActivityHelper();
        customTabActivityHelper.setConnectionCallback(new CustomTabActivityHelper.ConnectionCallback() {
            @Override
            public void onCustomTabsConnected() {

            }

            @Override
            public void onCustomTabsDisconnected() {

            }
        });

        customTabsIntent = CustomTabsHelper.getCustomTabsIntent(this,
            customTabActivityHelper.getSession());

        // Create the AccountHeader
        AccountHeaderBuilder accountHeaderBuilder = new AccountHeaderBuilder()
            .withActivity(this)
            .withTranslucentStatusBar(true)
            .withTextColorRes(R.color.white)
            .addProfiles(profile)
            .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                @Override
                public boolean onProfileImageClick(@NonNull View view, @NonNull IProfile profile, boolean current) {
                    if (!isUserLoggedIn()) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                    return false;
                }

                @Override
                public boolean onProfileImageLongClick(@NonNull View view, @NonNull IProfile profile, boolean current) {
                    return false;
                }
            })
            .withOnAccountHeaderSelectionViewClickListener((view, profile12) -> {
                if (!isUserLoggedIn()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                return false;
            })
            .withSelectionListEnabledForSingleProfile(false)
            .withOnAccountHeaderListener((view, profile1, current) -> {
                if (profile1 instanceof IDrawerItem && profile1.getIdentifier() == ITEM_MANAGE_ACCOUNT) {
                    CustomTabActivityHelper.openCustomTab(MainActivity.this,
                        customTabsIntent,
                        userAccountUri,
                        new WebViewFallback());
                }

                //false if you have not consumed the event and it should close the drawer
                return false;
            })
            .withSavedInstance(savedInstanceState);

        try {
            accountHeaderBuilder = accountHeaderBuilder.withHeaderBackground(R.drawable.header);
        } catch (OutOfMemoryError e) {
            Log.w(LOG_TAG, "Device has too low memory, loading color drawer header...", e);
            accountHeaderBuilder = accountHeaderBuilder.withHeaderBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.primary_dark)));
        }
        headerResult = accountHeaderBuilder.build();

        // Add Manage Account profile if the user is connected
        SharedPreferences preferences = getSharedPreferences(PreferencesFragment.LOGIN_PREF, 0);

        String userSessionPrefs = preferences.getString("user_session", null);
        boolean isUserConnected = isUserLoggedIn() && userSessionPrefs != null;

        if (isUserConnected) {
            updateProfileForCurrentUser();
        }
        //Create the drawer
        drawerResult = new DrawerBuilder()
            .withActivity(this)
            .withToolbar(binding.toolbarInclude.toolbar)
            .withHasStableIds(true)
            .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
            .withOnDrawerListener(new Drawer.OnDrawerListener() {
                @Override
                public void onDrawerOpened(View drawerView) {
                    Utils.hideKeyboard(MainActivity.this);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                }

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    Utils.hideKeyboard(MainActivity.this);
                }
            })
            .addDrawerItems(
                new PrimaryDrawerItem().withName(R.string.home_drawer).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(ITEM_HOME),
                new SectionDrawerItem().withName(R.string.search_drawer),
                new PrimaryDrawerItem().withName(R.string.search_by_barcode_drawer).withIcon(GoogleMaterial.Icon.gmd_dialpad).withIdentifier(ITEM_SEARCH_BY_CODE),
                new PrimaryDrawerItem().withName(R.string.search_by_category).withIcon(GoogleMaterial.Icon.gmd_filter_list).withIdentifier(ITEM_CATEGORIES).withSelectable(false),
                new PrimaryDrawerItem().withName(R.string.additives).withIcon(R.drawable.ic_additives).withIdentifier(ITEM_ADDITIVES)
                    .withSelectable(false),
                new PrimaryDrawerItem().withName(R.string.scan_search).withIcon(R.drawable.barcode_grey_24dp).withIdentifier(ITEM_SCAN).withSelectable(false),
                new PrimaryDrawerItem().withName(R.string.compare_products).withIcon(GoogleMaterial.Icon.gmd_swap_horiz).withIdentifier(ITEM_COMPARE).withSelectable(false),
                new PrimaryDrawerItem().withName(R.string.advanced_search_title).withIcon(GoogleMaterial.Icon.gmd_insert_chart).withIdentifier(ITEM_ADVANCED_SEARCH)
                    .withSelectable(false),
                new PrimaryDrawerItem().withName(R.string.scan_history_drawer).withIcon(GoogleMaterial.Icon.gmd_history).withIdentifier(ITEM_HISTORY).withSelectable(false),
                new SectionDrawerItem().withName(R.string.user_drawer).withIdentifier(USER_ID),
                new PrimaryDrawerItem().withName(getString(R.string.action_contributes)).withIcon(GoogleMaterial.Icon.gmd_rate_review).withIdentifier(ITEM_MY_CONTRIBUTIONS)
                    .withSelectable(false),
                new PrimaryDrawerItem().withName(R.string.your_lists).withIcon(GoogleMaterial.Icon.gmd_list).withIdentifier(ITEM_YOUR_LISTS).withSelectable(false),
                new PrimaryDrawerItem().withName(R.string.products_to_be_completed).withIcon(GoogleMaterial.Icon.gmd_edit).withIdentifier(ITEM_INCOMPLETE_PRODUCTS)
                    .withSelectable(false),
                new PrimaryDrawerItem().withName(R.string.alert_drawer).withIcon(GoogleMaterial.Icon.gmd_warning).withIdentifier(ITEM_ALERT),
                new PrimaryDrawerItem().withName(R.string.action_preferences).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(ITEM_PREFERENCES),
                new DividerDrawerItem(),
                new PrimaryDrawerItem().withName(R.string.action_discover).withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(ITEM_ABOUT).withSelectable(false),
                new PrimaryDrawerItem().withName(R.string.contribute).withIcon(GoogleMaterial.Icon.gmd_group).withIdentifier(ITEM_CONTRIBUTE).withSelectable(false),
                new PrimaryDrawerItem().withName(R.string.open_other_flavor_drawer).withIcon(GoogleMaterial.Icon.gmd_shop).withIdentifier(ITEM_OBF).withSelectable(false)
            )
            .withOnDrawerItemClickListener((view, position, drawerItem) -> {

                Fragment newFragment = null;
                switch ((int) drawerItem.getIdentifier()) {

                    case ITEM_HOME:
                        newFragment = HomeFragment.newInstance();
                        break;

                    case ITEM_SEARCH_BY_CODE:
                        newFragment = new SearchByCodeFragment();
                        CommonBottomListenerInstaller.selectNavigationItem(binding.bottomNavigationInclude.bottomNavigation, 0);
                        break;

                    case ITEM_CATEGORIES:
                        CategoryActivity.start(this);
                        break;

                    case ITEM_ADDITIVES:
                        AdditiveListActivity.start(this);
                        break;

                    case ITEM_SCAN:
                        openScan();
                        break;

                    case ITEM_COMPARE:
                        ProductCompareActivity.start(this);
                        break;

                    case ITEM_HISTORY:
                        ScanHistoryActivity.start(this);
                        break;

                    case ITEM_LOGIN:
                        loginActivityResultLauncher.launch(null);
                        break;

                    case ITEM_ALERT:
                        newFragment = AllergensAlertFragment.newInstance();
                        break;

                    case ITEM_PREFERENCES:
                        newFragment = PreferencesFragment.newInstance();
                        break;

                    case ITEM_ABOUT:
                        CustomTabActivityHelper.openCustomTab(this, customTabsIntent, discoverUri, new WebViewFallback());
                        break;

                    case ITEM_CONTRIBUTE:
                        CustomTabActivityHelper.openCustomTab(this, customTabsIntent, contributeUri, new WebViewFallback());
                        break;

                    case ITEM_INCOMPLETE_PRODUCTS:
                        // Search and display the products to be completed by moving to ProductBrowsingListActivity
                        ProductSearchActivity.start(this, "", SearchType.INCOMPLETE_PRODUCT);
                        break;

                    case ITEM_OBF:
                        boolean otherOFAppInstalled = Utils.isApplicationInstalled
                            (MainActivity.this, BuildConfig.OFOTHERLINKAPP);
                        if (otherOFAppInstalled) {
                            Intent launchIntent = getPackageManager()
                                .getLaunchIntentForPackage(BuildConfig.OFOTHERLINKAPP);
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                            } else {
                                Toast.makeText(this, R.string.app_disabled_text, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", BuildConfig.OFOTHERLINKAPP, null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        } else {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                                    ("market://details?id=" + BuildConfig.OFOTHERLINKAPP)));
                            } catch (ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" +
                                    BuildConfig.OFOTHERLINKAPP)));
                            }
                        }
                        break;

                    case ITEM_ADVANCED_SEARCH:
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        CustomTabsIntent customTabsIntent = builder.build();
                        CustomTabActivityHelper.openCustomTab(this, customTabsIntent, Uri.parse(getString(R.string.advanced_search_url)), new
                            WebViewFallback());
                        break;

                    case ITEM_MY_CONTRIBUTIONS:
                        openMyContributions();
                        break;

                    case ITEM_YOUR_LISTS:
                        ProductListsActivity.start(this);
                        break;

                    case ITEM_LOGOUT:
                        new MaterialDialog.Builder(MainActivity.this)
                            .title(R.string.confirm_logout)
                            .content(R.string.logout_dialog_content)
                            .positiveText(R.string.txtOk)
                            .negativeText(R.string.dialog_cancel)
                            .onPositive((dialog, which) -> logout())
                            .onNegative((dialog, which) -> {
                                dialog.dismiss();
                                Snackbar.make(binding.getRoot(), "Cancelled", BaseTransientBottomBar.LENGTH_SHORT).show();
                            }).show();
                        break;
                    default:
                        // nothing to do
                        break;
                }

                if (newFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, newFragment)
                        .addToBackStack(null)
                        .commit();
                }

                return false;
            })
            .withSavedInstance(savedInstanceState)
            .withShowDrawerOnFirstLaunch(false)
            .build();

        drawerResult.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        // Add Drawer items for the connected user
        drawerResult.addItemsAtPosition(drawerResult.getPosition(ITEM_MY_CONTRIBUTIONS), isUserLoggedIn() ?
            getLogoutDrawerItem() : getLoginDrawerItem());

        if (AppFlavors.isFlavors(AppFlavors.OBF)) {
            drawerResult.removeItem(ITEM_ALERT);
            drawerResult.removeItem(ITEM_ADDITIVES);
            drawerResult.updateName(ITEM_OBF, new StringHolder(getString(R.string.open_other_flavor_drawer)));
        } else if (AppFlavors.isFlavors(AppFlavors.OPFF)) {
            drawerResult.removeItem(ITEM_ALERT);
        } else if (AppFlavors.isFlavors(AppFlavors.OPF)) {
            drawerResult.removeItem(ITEM_ALERT);
            drawerResult.removeItem(ITEM_ADDITIVES);
            drawerResult.removeItem(ITEM_ADVANCED_SEARCH);
        }

        if (!Utils.isApplicationInstalled(MainActivity.this, BuildConfig.OFOTHERLINKAPP)) {
            drawerResult.updateName(ITEM_OBF, new StringHolder(getString(R.string.install) + " " + getString(R.string.open_other_flavor_drawer)));
        } else {
            drawerResult.updateName(ITEM_OBF, new StringHolder(getString(R.string.open_other_flavor_drawer)));
        }

        // Remove scan item if the device does not have a camera, for example, Chromebooks or
        // Fire devices
        if (!Utils.isHardwareCameraInstalled(this)) {
            drawerResult.removeItem(ITEM_SCAN);
        }

//        //if you have many different types of DrawerItems you can magically pre-cache those items
//        // to get a better scroll performance
//        //make sure to init the cache after the DrawerBuilder was created as this will first
//        // clear the cache to make sure no old elements are in
//        new RecyclerViewCacheUtil<IDrawerItem>().withCacheSize(2).apply(result.getRecyclerView(),
//            result.getDrawerItems());

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 1
            drawerResult.setSelection(ITEM_HOME, false);

            //set the active profile
            headerResult.setActiveProfile(profile);
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("startScan", false)) {
            Intent cameraIntent = new Intent(MainActivity.this, ContinuousScanActivity.class);
            cameraIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(cameraIntent);
        }

        // prefetch uris
        contributeUri = Uri.parse(getString(R.string.website_contribute));
        discoverUri = Uri.parse(getString(R.string.website_discover));
        userContributeUri = Uri.parse(getString(R.string.website_contributor) + getUserLogin());

        customTabActivityHelper.mayLaunchUrl(contributeUri, null, null);
        customTabActivityHelper.mayLaunchUrl(discoverUri, null, null);
        customTabActivityHelper.mayLaunchUrl(userContributeUri, null, null);

        if (CONTRIBUTIONS_SHORTCUT.equals(getIntent().getAction())) {
            openMyContributions();
        }

        if (SCAN_SHORTCUT.equals(getIntent().getAction())) {
            openScan();
        }

        if (BARCODE_SHORTCUT.equals(getIntent().getAction())) {
            swapToSearchByCode();
        }

        //Scheduling background image upload job
        Utils.scheduleProductUploadJob(this);

        OfflineProductWorker.scheduleSync();

        //Adds nutriscore and quantity values in old history for schema 5 update
        SharedPreferences mSharedPref = getApplicationContext().getSharedPreferences("prefs", 0);
        boolean isOldHistoryDataSynced = mSharedPref.getBoolean("is_old_history_data_synced", false);
        if (!isOldHistoryDataSynced && Utils.isNetworkConnected(this)) {
            OpenFoodAPIClient apiClient = new OpenFoodAPIClient(this);
            apiClient.syncOldHistory();
        }

        CommonBottomListenerInstaller.selectNavigationItem(binding.bottomNavigationInclude.bottomNavigation, 0);
        CommonBottomListenerInstaller.install(this, binding.bottomNavigationInclude.bottomNavigation);

        handleIntent(getIntent());
    }

    private void swapToHomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.addOnBackStackChangedListener(() -> {
        });

        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, new HomeFragment())
            .commit();
        binding.toolbarInclude.toolbar.setTitle(APP_NAME);
    }

    private void openScan() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest
                .permission.CAMERA)) {
                new MaterialDialog.Builder(MainActivity.this)
                    .title(R.string.action_about)
                    .content(R.string.permission_camera)
                    .neutralText(R.string.txtOk)
                    .show().setOnDismissListener(dialogInterface -> ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    Utils.MY_PERMISSIONS_REQUEST_CAMERA));
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest
                    .permission.CAMERA}, Utils.MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            Intent intent = new Intent(MainActivity.this, ContinuousScanActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void updateProfileForCurrentUser() {
        headerResult.updateProfile(getUserProfile());
        if (isUserLoggedIn()) {
            if (headerResult.getProfiles() != null && headerResult.getProfiles().size() < 2) {
                headerResult.addProfiles(getProfileSettingDrawerItem());
            }
        } else {
            headerResult.removeProfileByIdentifier(ITEM_MANAGE_ACCOUNT);
        }
    }

    private void openMyContributions() {
        if (isUserLoggedIn()) {
            openMyContributionsInBrowser();
        } else {
            new MaterialDialog.Builder(MainActivity.this)
                .title(R.string.contribute)
                .content(R.string.contribution_without_account)
                .positiveText(R.string.create_account_button)
                .neutralText(R.string.login_button)
                .onPositive((dialog, which) -> CustomTabActivityHelper.openCustomTab(MainActivity.this, customTabsIntent, Uri.parse(getString(R
                    .string.website) + "cgi/user.pl"), new WebViewFallback()))
                .onNeutral((dialog, which) ->
                    registerForActivityResult(new LoginActivity.LoginContract(), isLoggedIn -> {
                        if (isLoggedIn) {
                            openMyContributionsInBrowser();
                        }
                    }).launch(null))
                .show();
        }
    }

    private void openMyContributionsInBrowser() {
        String userLogin = getUserLogin();
        userContributeUri = Uri.parse(getString(R.string.website_contributor) + userLogin);
        ProductSearchActivity.start(this, userLogin, SearchType.CONTRIBUTOR);
    }

    private IProfile<ProfileSettingDrawerItem> getProfileSettingDrawerItem() {

        String userLogin = getUserLogin();
        String userSession = getUserSession();
        userAccountUri = Uri.parse(getString(R.string.website) + "cgi/user.pl?type=edit&userid=" + userLogin + "&user_id=" + userLogin +
            "&user_session=" + userSession);
        customTabActivityHelper.mayLaunchUrl(userAccountUri, null, null);
        return new ProfileSettingDrawerItem()
            .withName(getString(R.string.action_manage_account))
            .withIcon(GoogleMaterial.Icon.gmd_settings)
            .withIdentifier(ITEM_MANAGE_ACCOUNT)
            .withSelectable(false);
    }

    /**
     * Replace logout menu item by the login menu item
     * Change current user profile (Anonymous)
     * Remove all Account Header items
     * Remove user login info
     */
    private void logout() {
        getSharedPreferences(PreferencesFragment.LOGIN_PREF, MODE_PRIVATE).edit().clear().apply();
        updateConnectedState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = drawerResult.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the
        // activity
        if (drawerResult != null && drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                //recreate the activity onBackPressed
                recreate();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onCreate(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.bottomNavigationInclude.bottomNavigation.setVisibility(View.GONE);
            } else {
                binding.bottomNavigationInclude.bottomNavigation.setVisibility(View.VISIBLE);
            }
        });
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                binding.bottomNavigationInclude.bottomNavigation.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {

                return true;
            }
        });

        if (getIntent().getBooleanExtra(PRODUCT_SEARCH_KEY, false)) {
            searchMenuItem.expandActionView();
        }

        return true;
    }

    private IDrawerItem<AbstractBadgeableDrawerItem.ViewHolder> getLogoutDrawerItem() {
        return new PrimaryDrawerItem()
            .withName(getString(R.string.logout_drawer))
            .withIcon(GoogleMaterial.Icon.gmd_settings_power)
            .withIdentifier(ITEM_LOGOUT)
            .withSelectable(false);
    }

    private IDrawerItem<AbstractBadgeableDrawerItem.ViewHolder> getLoginDrawerItem() {
        return new PrimaryDrawerItem()
            .withName(R.string.sign_in_drawer)
            .withIcon(GoogleMaterial.Icon.gmd_account_circle)
            .withIdentifier(ITEM_LOGIN)
            .withSelectable(false);
    }

    private IProfile<ProfileDrawerItem> getUserProfile() {
        String userLogin = getSharedPreferences(PreferencesFragment.LOGIN_PREF, 0)
            .getString("user", getResources().getString(R.string.txt_anonymous));

        return new ProfileDrawerItem()
            .withName(userLogin)
            .withIcon(R.drawable.img_home)
            .withIdentifier(ITEM_USER);
    }

    @Override
    protected void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(this);

        prefManager = new PrefManager(this);
        if (AppFlavors.isFlavors(AppFlavors.OFF)
            && isUserLoggedIn()
            && !prefManager.isFirstTimeLaunch()
            && !prefManager.getUserAskedToRate()) {

            long firstTimeLaunchTime = prefManager.getFirstTimeLaunchTime();
            // Check if it has been a week since first launch
            if ((Calendar.getInstance().getTimeInMillis() - firstTimeLaunchTime) >= WEEK_IN_MS) {
                showFeedbackDialog();
            }
        }
    }

    /**
     * show dialog to ask the user to rate the app/give feedback
     */
    private void showFeedbackDialog() {
        //dialog for rating the app on play store
        MaterialDialog.Builder rateDialog = new MaterialDialog.Builder(this)
            .title(R.string.app_name)
            .content(R.string.user_ask_rate_app)
            .positiveText(R.string.rate_app)
            .negativeText(R.string.no_thx)
            .onPositive((dialog, which) -> {
                //open app page in play store
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                dialog.dismiss();
            })
            .onNegative((dialog, which) -> dialog.dismiss());

        //dialog for giving feedback
        MaterialDialog.Builder feedbackDialog = new MaterialDialog.Builder(this)
            .title(R.string.app_name)
            .content(R.string.user_ask_show_feedback_form)
            .positiveText(R.string.txtOk)
            .negativeText(R.string.txtNo)
            .onPositive((dialog, which) -> {
                //show feedback form
                CustomTabActivityHelper.openCustomTab(MainActivity.this,
                    customTabsIntent, Uri.parse(getString(R.string.feedback_form_url)), new WebViewFallback());
                dialog.dismiss();
            })
            .onNegative((dialog, which) -> dialog.dismiss());

        new MaterialDialog.Builder(this)
            .title(R.string.app_name)
            .content(R.string.user_enjoying_app)
            .positiveText(R.string.txtYes)
            .onPositive((dialog, which) -> {
                prefManager.setUserAskedToRate(true);
                rateDialog.show();
                dialog.dismiss();
            })
            .negativeText(R.string.txtNo)
            .onNegative((dialog, which) -> {
                prefManager.setUserAskedToRate(true);
                feedbackDialog.show();
                dialog.dismiss();
            })
            .show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabsService(this);
    }

    @Override
    protected void onDestroy() {
        customTabActivityHelper.setConnectionCallback(null);
        disp.dispose();
        binding = null;
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String type = intent.getType();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.e("INTENT", "start activity");
            String query = intent.getStringExtra(SearchManager.QUERY);
            //Saves the most recent queries and adds it to the list of suggestions
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            ProductSearchActivity.start(this, query, SearchType.SEARCH);
            if (searchMenuItem != null) {
                searchMenuItem.collapseActionView();
            }
        } else if (type != null && type.startsWith("image/")) {
            if (Intent.ACTION_SEND.equals(intent.getAction())) {
                handleSendImage(intent); // Handle single image being sent
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(intent.getAction())) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        }
    }

    /**
     * This moves the main activity to the barcode entry fragment.
     */
    public void swapToSearchByCode() {
        changeFragment(new SearchByCodeFragment(), getResources().getString(R.string.search_by_barcode_drawer), ITEM_SEARCH_BY_CODE);
    }

    @Override
    public void setItemSelected(@NavigationDrawerType Integer type) {
        drawerResult.setSelection(type, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        CommonBottomListenerInstaller.selectNavigationItem(binding.bottomNavigationInclude.bottomNavigation, R.id.home_page);

        // change drawer menu item from "install" to "open" when navigating back from play store.
        if (Utils.isApplicationInstalled(MainActivity.this, BuildConfig.OFOTHERLINKAPP)) {
            drawerResult.updateName(ITEM_OBF, new StringHolder(getString(R.string.open_other_flavor_drawer)));

            drawerResult.getAdapter().notifyDataSetChanged();
        }

        updateConnectedState();
    }

    private void updateConnectedState() {
        updateProfileForCurrentUser();
        drawerResult.removeItem(ITEM_LOGIN);
        drawerResult.removeItem(ITEM_LOGOUT);
        drawerResult.addItemAtPosition(super.isUserLoggedIn() ? getLogoutDrawerItem() : getLoginDrawerItem(), drawerResult.getPosition(ITEM_MY_CONTRIBUTIONS));
    }

    private void handleSendImage(@NonNull Intent intent) {
        ArrayList<Uri> selectedImagesArray = new ArrayList<>();
        Uri selectedImage = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (selectedImage != null) {
            selectedImagesArray.add(selectedImage);
            chooseDialog(selectedImagesArray);
        }
    }

    private void handleSendMultipleImages(@NonNull Intent intent) {
        ArrayList<Uri> selectedImagesArray = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (selectedImagesArray != null) {
            selectedImagesArray.removeAll(Collections.singleton(null));
            chooseDialog(selectedImagesArray);
        }
    }

    private void chooseDialog(ArrayList<Uri> selectedImagesArray) {
        disp.add(detectBarcodeInImages(selectedImagesArray).observeOn(AndroidSchedulers.mainThread())
            .subscribe(isBarCodePresent -> {
                if (isBarCodePresent) {
                    createAlertDialog(false, barcode, selectedImagesArray);
                } else {
                    createAlertDialog(true, "", selectedImagesArray);
                }
            }));
    }

    /**
     * IO / Computing intensive operation
     *
     * @param selectedImages
     */
    private Single<Boolean> detectBarcodeInImages(List<Uri> selectedImages) {
        return Observable.fromIterable(selectedImages)
            .map(uri -> {
                Bitmap bMap = null;
                try (InputStream imageStream = getContentResolver().openInputStream(uri)) {
                    bMap = BitmapFactory.decodeStream(imageStream);
                } catch (FileNotFoundException e) {
                    Log.e(MainActivity.class.getSimpleName(), "Could not resolve file from Uri " + uri.toString(), e);
                } catch (IOException e) {
                    Log.e(MainActivity.class.getSimpleName(), "IO error during bitmap stream decoding: " + e.getMessage(), e);
                }
                //decoding bitmap
                if (bMap != null) {
                    int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Reader reader = new MultiFormatReader();
                    try {
                        EnumMap<DecodeHintType, Object> decodeHints = new EnumMap<>(DecodeHintType.class);
                        decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                        decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

                        Result decodedResult = reader.decode(bitmap, decodeHints);
                        if (decodedResult != null) {
                            barcode = decodedResult.getText();
                        }
                        if (barcode != null) {
                            return true;
                        }
                    } catch (FormatException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.format_error), Toast.LENGTH_SHORT).show();
                        Log.e(MainActivity.class.getSimpleName(), "Error decoding bitmap into barcode: " + e.getMessage());
                    } catch (Exception e) {
                        Log.e(MainActivity.class.getSimpleName(), "Error decoding bitmap into barcode: " + e.getMessage());
                    }
                }
                return false;
            })
            .filter(Boolean::booleanValue)
            .first(false)
            .subscribeOn(Schedulers.computation());
    }

    private void createAlertDialog(boolean hasEditText, @NonNull String barcode, @NonNull ArrayList<Uri> uri) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_barcode, null);
        alertDialogBuilder.setView(dialogView);

        final EditText barcodeEditText = dialogView.findViewById(R.id.barcode);
        final RecyclerView productImages = dialogView.findViewById(R.id.product_image);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,
            false);
        productImages.setLayoutManager(layoutManager);
        productImages.setAdapter(new PhotosAdapter(uri));

        if (hasEditText) {
            barcodeEditText.setVisibility(View.VISIBLE);
            alertDialogBuilder.setTitle(getString(R.string.no_barcode));
            alertDialogBuilder.setMessage(getString(R.string.enter_barcode));
        } else {
            alertDialogBuilder.setTitle(getString(R.string.code_detected));
            alertDialogBuilder.setMessage(barcode + "\n" + getString(R.string.do_you_want_to));
        }

        // set dialog message
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(R.string.txtYes, (dialog, id) -> {
                for (Uri selected : uri) {
                    OpenFoodAPIClient api = new OpenFoodAPIClient(MainActivity.this);
                    ProductImage image;
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    String tempBarcode;
                    if (hasEditText) {

                        tempBarcode = barcodeEditText.getText().toString();
                    } else {
                        tempBarcode = barcode;
                    }

                    if (tempBarcode.length() > 0) {
                        dialog.cancel();
                        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                            File imageFile = new File(RealPathUtil.getRealPath(MainActivity.this, selected));
                            image = new ProductImage(tempBarcode, ProductImageField.OTHER, imageFile);
                            disp.add(api.postImg(image).subscribe());
                        } else {
                            Product pd = new Product();
                            pd.setCode(tempBarcode);
                            ProductState st = new ProductState();
                            st.setProduct(pd);
                            ProductEditActivity.start(this, st);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.sorry_msg), Toast.LENGTH_LONG).show();
                    }
                }
            })
            .setNegativeButton(R.string.txtNo,
                (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    /**
     * Used to navigate Fragments which are children of <code>MainActivity</code>.
     * Use this method when the <code>Fragment</code> APPEARS in the <code>Drawer</code>.
     *
     * @param fragment The fragment class to display.
     * @param title The title that should be displayed on the top toolbar.
     * @param drawerName The fragment as it appears in the drawer. See {@link NavigationDrawerListener} for the value.
     * @author ross-holloway94
     * @see <a href="https://stackoverflow.com/questions/45138446/calling-fragment-from-recyclerview-adapter">Related Stack Overflow article</a>
     * @since 06/16/18
     */
    public void changeFragment(@NonNull Fragment fragment, @Nullable String title, long drawerName) {
        changeFragment(fragment, title);
        drawerResult.setSelection(drawerName);
    }

    /**
     * Used to navigate Fragments which are children of <code>MainActivity</code>.
     * Use this method when the <code>Fragment</code> DOES NOT APPEAR in the <code>Drawer</code>.
     *
     * @param fragment The fragment class to display.
     * @param title The title that should be displayed on the top toolbar.
     * @author ross-holloway94
     * @see <a href="https://stackoverflow.com/questions/45138446/calling-fragment-from-recyclerview-adapter">Related Stack Overflow article</a>
     * @since 06/16/18
     */
    public void changeFragment(@NonNull Fragment fragment, @Nullable String title) {

        String backStateName = fragment.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, backStateName);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
        if (title != null) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        }
    }

    public void changeFragment(@NonNull Fragment fragment) {
        changeFragment(fragment, null);
    }
}

