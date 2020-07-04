package openfoodfacts.github.scrachx.openfood.views.product;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import openfoodfacts.github.scrachx.openfood.AppFlavors;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.databinding.ActivityProductBinding;
import openfoodfacts.github.scrachx.openfood.fragments.ContributorsFragment;
import openfoodfacts.github.scrachx.openfood.fragments.ProductPhotosFragment;
import openfoodfacts.github.scrachx.openfood.models.Nutriments;
import openfoodfacts.github.scrachx.openfood.models.State;
import openfoodfacts.github.scrachx.openfood.models.eventbus.ProductNeedsRefreshEvent;
import openfoodfacts.github.scrachx.openfood.network.OpenFoodAPIClient;
import openfoodfacts.github.scrachx.openfood.utils.ShakeDetector;
import openfoodfacts.github.scrachx.openfood.utils.Utils;
import openfoodfacts.github.scrachx.openfood.views.AddProductActivity;
import openfoodfacts.github.scrachx.openfood.views.BaseActivity;
import openfoodfacts.github.scrachx.openfood.views.MainActivity;
import openfoodfacts.github.scrachx.openfood.views.adapters.ProductFragmentPagerAdapter;
import openfoodfacts.github.scrachx.openfood.views.listeners.BottomNavigationListenerInstaller;
import openfoodfacts.github.scrachx.openfood.views.listeners.OnRefreshListener;
import openfoodfacts.github.scrachx.openfood.views.product.environment.EnvironmentProductFragment;
import openfoodfacts.github.scrachx.openfood.views.product.ingredients.IngredientsProductFragment;
import openfoodfacts.github.scrachx.openfood.views.product.ingredients_analysis.IngredientsAnalysisProductFragment;
import openfoodfacts.github.scrachx.openfood.views.product.nutrition.NutritionProductFragment;
import openfoodfacts.github.scrachx.openfood.views.product.summary.SummaryProductFragment;

public class ProductActivity extends BaseActivity implements OnRefreshListener {
    private static final int LOGIN_ACTIVITY_REQUEST_CODE = 1;
    public static final String STATE_KEY = "state";
    private ActivityProductBinding binding;
    private ProductFragmentPagerAdapter adapterResult;
    private OpenFoodAPIClient api;
    private CompositeDisposable disp = new CompositeDisposable();
    private State mState;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    // boolean to determine if scan on shake feature should be enabled
    private boolean scanOnShake;

    /**
     * CAREFUL ! YOU MUST INSTANTIATE YOUR OWN ADAPTERRESULT BEFORE CALLING THIS METHOD
     */
    @NonNull
    public static ProductFragmentPagerAdapter setupViewPager(@NonNull ViewPager2 viewPager,
                                                             @NonNull ProductFragmentPagerAdapter adapter,
                                                             @NonNull State state,
                                                             @NonNull Activity activity) {

        String[] menuTitles = activity.getResources().getStringArray(R.array.nav_drawer_items_product);
        String[] newMenuTitles = activity.getResources().getStringArray(R.array.nav_drawer_new_items_product);

        Bundle fBundle = new Bundle();
        fBundle.putSerializable(STATE_KEY, state);

        adapter.addFragment(applyBundle(new SummaryProductFragment(), fBundle), menuTitles[0]);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        // Add Ingredients fragment for off, obf and opff
        if (Utils.isFlavor(AppFlavors.OFF, AppFlavors.OBF, AppFlavors.OPFF)) {
            adapter.addFragment(applyBundle(new IngredientsProductFragment(), fBundle), menuTitles[1]);
        }

        if (Utils.isFlavor(AppFlavors.OFF)) {
            adapter.addFragment(applyBundle(new NutritionProductFragment(), fBundle), menuTitles[2]);
            if ((state.getProduct().getNutriments() != null &&
                state.getProduct().getNutriments().contains(Nutriments.CARBON_FOOTPRINT)) ||
                (state.getProduct().getEnvironmentInfocard() != null && !state.getProduct().getEnvironmentInfocard().isEmpty())) {
                adapter.addFragment(applyBundle(new EnvironmentProductFragment(), fBundle), "Environment");
            }
            if (isPhotoMode(activity)) {
                adapter.addFragment(applyBundle(new ProductPhotosFragment(), fBundle), newMenuTitles[0]);
            }
        } else if (Utils.isFlavor(AppFlavors.OPFF)) {
            adapter.addFragment(applyBundle(new NutritionProductFragment(), fBundle), menuTitles[2]);
            if (isPhotoMode(activity)) {
                adapter.addFragment(applyBundle(new ProductPhotosFragment(), fBundle), newMenuTitles[0]);
            }
        } else if (Utils.isFlavor(AppFlavors.OBF)) {
            if (isPhotoMode(activity)) {
                adapter.addFragment(applyBundle(new ProductPhotosFragment(), fBundle), newMenuTitles[0]);
            }
            adapter.addFragment(applyBundle(new IngredientsAnalysisProductFragment(), fBundle), newMenuTitles[1]);
        } else if (Utils.isFlavor(AppFlavors.OPF)) {
            adapter.addFragment(applyBundle(new ProductPhotosFragment(), fBundle), newMenuTitles[0]);
        }

        if (preferences.getBoolean("contributionTab", false)) {
            adapter.addFragment(applyBundle(new ContributorsFragment(), fBundle), activity.getString(R.string.contribution_tab));
        }

        viewPager.setAdapter(adapter);
        return adapter;
    }

    @NonNull
    public static <T extends Fragment> T applyBundle(@NonNull T fragment, @NonNull Bundle bundle) {
        fragment.setArguments(bundle);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(getString(R.string.app_name_long));

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        api = new OpenFoodAPIClient(this);

        mState = (State) getIntent().getSerializableExtra(STATE_KEY);

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            // handle opening the app via product page url
            Uri data = getIntent().getData();
            String[] paths = data.toString().split("/"); // paths[4]
            mState = new State();
            loadProductDataFromUrl(paths[4]);
        } else if (mState == null) {
            //no state-> we can't display anything. we go back to home.
            final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            initViews();
        }
    }

    /**
     * Get the product data from the barcode. This takes the barcode and retrieves the information.
     *
     * @param barcode from the URL.
     */
    private void loadProductDataFromUrl(String barcode) {

        disp.add(api.getProductStateFull(barcode, Utils.HEADER_USER_AGENT_SCAN)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(state -> {
                mState = state;
                getIntent().putExtra(STATE_KEY, state);
                if (mState != null) {
                    initViews();
                } else {
                    finish();
                }
            }, e -> {
                Log.i(getClass().getSimpleName(), "Failed to load product data", e);
                finish();
            }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Open product editing after successful login
            Intent intent = new Intent(ProductActivity.this, AddProductActivity.class);
            intent.putExtra(AddProductActivity.KEY_EDIT_PRODUCT, mState.getProduct());
            startActivity(intent);
        }
    }

    /**
     * Initialise the content that shows the content on the device.
     */
    private void initViews() {

        setupViewPager(binding.pager);

        new TabLayoutMediator(binding.tabs, binding.pager, (tab, position) -> {
            tab.setText(adapterResult.getPageTitle(position));
        }).attach();

        // Get the user preference for scan on shake feature and open ContinuousScanActivity if the user has enabled the feature
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager != null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        mShakeDetector = new ShakeDetector();

        SharedPreferences shakePreference = PreferenceManager.getDefaultSharedPreferences(this);
        scanOnShake = shakePreference.getBoolean("shakeScanMode", false);

        mShakeDetector.setOnShakeListener(count -> {
            if (scanOnShake) {
                Utils.scan(ProductActivity.this);
            }
        });

        BottomNavigationListenerInstaller.selectNavigationItem(binding.navigationBottomInclude.bottomNavigation, 0);
        BottomNavigationListenerInstaller.install(binding.navigationBottomInclude.bottomNavigation, this);
    }

    private void setupViewPager(ViewPager2 viewPager) {
        adapterResult = setupViewPager(viewPager, new ProductFragmentPagerAdapter(this), mState, this);
    }

    private static boolean isPhotoMode(Activity activity) {
        return PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("photoMode", false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return onOptionsItemSelected(item, this);
    }

    public static boolean onOptionsItemSelected(MenuItem item, Activity activity) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            activity.finish();
        }
        return true;
    }

    @Subscribe
    public void onEventBusProductNeedsRefreshEvent(ProductNeedsRefreshEvent event) {
        if (event.getBarcode().equals(mState.getProduct().getCode())) {
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        api.openProduct(mState.getProduct().getCode(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mState = (State) intent.getSerializableExtra(STATE_KEY);
        adapterResult.refresh(mState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scanOnShake) {
            //unregister the listener
            mSensorManager.unregisterListener(mShakeDetector, mAccelerometer);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (scanOnShake) {
            //register the listener
            mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        disp.dispose();
        super.onDestroy();
    }

    public void showIngredientsTab(ShowIngredientsAction action) {
        if (adapterResult == null || adapterResult.getItemCount() == 0) {
            return;
        }
        for (int i = 0; i < adapterResult.getItemCount(); ++i) {
            Fragment fragment = adapterResult.createFragment(i);
            if (fragment instanceof IngredientsProductFragment) {
                binding.pager.setCurrentItem(i);

                if (action == ShowIngredientsAction.PERFORM_OCR) {
                    ((IngredientsProductFragment) fragment).extractIngredients();
                } else if (action == ShowIngredientsAction.SEND_UPDATED) {
                    ((IngredientsProductFragment) fragment).changeIngImage();
                }
                return;
            }
        }
    }

    public enum ShowIngredientsAction {
        PERFORM_OCR, SEND_UPDATED,
    }
}
