package openfoodfacts.github.scrachx.openfood.features.search;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import openfoodfacts.github.scrachx.openfood.BuildConfig;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.customtabs.CustomTabActivityHelper;
import openfoodfacts.github.scrachx.openfood.databinding.ActivityProductBrowsingListBinding;
import openfoodfacts.github.scrachx.openfood.features.adapters.ProductsRecyclerViewAdapter;
import openfoodfacts.github.scrachx.openfood.features.listeners.CommonBottomListenerInstaller;
import openfoodfacts.github.scrachx.openfood.features.listeners.EndlessRecyclerViewScrollListener;
import openfoodfacts.github.scrachx.openfood.features.listeners.RecyclerItemClickListener;
import openfoodfacts.github.scrachx.openfood.features.scan.ContinuousScanActivity;
import openfoodfacts.github.scrachx.openfood.features.shared.BaseActivity;
import openfoodfacts.github.scrachx.openfood.models.Product;
import openfoodfacts.github.scrachx.openfood.models.Search;
import openfoodfacts.github.scrachx.openfood.network.OpenFoodAPIClient;
import openfoodfacts.github.scrachx.openfood.repositories.ProductRepository;
import openfoodfacts.github.scrachx.openfood.utils.LocaleHelper;
import openfoodfacts.github.scrachx.openfood.utils.ProductUtils;
import openfoodfacts.github.scrachx.openfood.utils.SearchInfo;
import openfoodfacts.github.scrachx.openfood.utils.SearchType;
import openfoodfacts.github.scrachx.openfood.utils.Utils;

import static openfoodfacts.github.scrachx.openfood.utils.SearchType.CONTRIBUTOR;
import static openfoodfacts.github.scrachx.openfood.utils.SearchType.SEARCH;
import static openfoodfacts.github.scrachx.openfood.utils.SearchType.fromUrl;

public class ProductSearchActivity extends BaseActivity {
    /**
     * Must be public to be visible by TakeScreenshotIncompleteProductsTest class.
     */
    public static final String SEARCH_INFO = "search_info";
    private OpenFoodAPIClient api;
    private OpenFoodAPIClient apiClient;
    private ActivityProductBrowsingListBinding binding;
    private int contributionType;
    private CompositeDisposable disp;
    private int mCountProducts = 0;
    private List<Product> mProducts;
    private SearchInfo mSearchInfo;
    /**
     * boolean to determine if image should be loaded or not
     */
    private boolean isLowBatteryMode = false;
    private int pageAddress = 1;
    private boolean setupDone = false;

    /**
     * Start a new {@link ProductSearchActivity} given a search information
     *
     * @param context the context to use to start this activity
     * @param searchQuery the search query
     * @param searchTitle the title used in the activity for this search query
     * @param type the type of search
     */
    public static void start(Context context, String searchQuery, String searchTitle, SearchType type) {
        start(context, new SearchInfo(searchQuery, searchTitle, type));
    }

    /**
     * @see #start(Context, String, String, SearchType) )
     */
    public static void start(Context context, String searchQuery, SearchType type) {
        start(context, searchQuery, searchQuery, type);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disp.dispose();
        binding = null;
    }

    /**
     * @see #start(Context, String, String, SearchType)
     */
    private static void start(Context context, SearchInfo searchInfo) {
        Intent intent = new Intent(context, ProductSearchActivity.class);
        intent.putExtra(SEARCH_INFO, searchInfo);
        context.startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onCreate(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchInfo.setSearchQuery(query);
                mSearchInfo.setSearchType(SEARCH);
                newSearchQuery();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getSupportActionBar().setTitle(null);
                finish();
                return true;
            }
        });

        if (CONTRIBUTOR.equals(mSearchInfo.getSearchType())) {
            MenuItem contributionItem = menu.findItem(R.id.action_set_type);
            contributionItem.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        if (item.getItemId() == R.id.action_set_type) {
            String[] contributionTypes = new String[]{getString(R.string.products_added),
                getString(R.string.products_incomplete), getString(R.string.product_pictures_contributed),
                getString(R.string.picture_contributed_incomplete), getString(R.string.product_info_added),
                getString(R.string.product_info_tocomplete)};

            new MaterialDialog.Builder(this)
                .title(R.string.show_by)
                .items(contributionTypes)
                .itemsCallback((dialog, itemView, position, text) -> {

                    switch (position) {
                        case 1:
                            contributionType = 1;
                            newSearchQuery();
                            break;
                        case 2:
                            contributionType = 2;
                            newSearchQuery();
                            break;
                        case 3:
                            contributionType = 3;
                            newSearchQuery();
                            break;
                        case 4:
                            contributionType = 4;
                            newSearchQuery();
                            break;
                        case 5:
                            contributionType = 5;
                            newSearchQuery();
                            break;
                        case 0:
                        default:
                            contributionType = 0;
                            newSearchQuery();
                            break;
                    }
                }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disp = new CompositeDisposable();
        binding = ActivityProductBrowsingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarInclude.toolbar);

        // OnClick
        binding.buttonTryAgain.setOnClickListener(v -> setup());
        binding.addProduct.setOnClickListener(v -> addProduct());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.textCountProduct.setVisibility(View.INVISIBLE);

        // Get the search information (query, title, type) that we will use in this activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SearchInfo searchInfo = extras.getParcelable(SEARCH_INFO);
            mSearchInfo = searchInfo != null ? searchInfo : SearchInfo.emptySearchInfo();
        } else if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            // the user has entered the activity via a url
            Uri data = getIntent().getData();
            if (data != null) {
                String[] paths = data.toString().split("/");

                if (mSearchInfo == null) {
                    mSearchInfo = SearchInfo.emptySearchInfo();
                }

                mSearchInfo.setSearchTitle(paths[4]);
                mSearchInfo.setSearchQuery(paths[4]);
                mSearchInfo.setSearchType(Objects.requireNonNull(fromUrl(paths[3])));

                if (paths[3].equals("cgi") && paths[4] != null && paths[4].contains("search.pl")) {
                    mSearchInfo.setSearchTitle(data.getQueryParameter("search_terms"));
                    mSearchInfo.setSearchQuery(data.getQueryParameter("search_terms"));
                    mSearchInfo.setSearchType(SEARCH);
                }
            } else {
                Log.i(getClass().getSimpleName(), "No data was passed in with URL");
                finish();
            }
        }

        newSearchQuery();

        // If Battery Level is low and the user has checked the Disable Image in Preferences , then set isLowBatteryMode to true
        if (Utils.isDisableImageLoad(this) && Utils.isBatteryLevelLow(this)) {
            isLowBatteryMode = true;
        }

        CommonBottomListenerInstaller.selectNavigationItem(binding.navigationBottom.bottomNavigation, 0);
        CommonBottomListenerInstaller.install(this, binding.navigationBottom.bottomNavigation);
    }

    private void setupHungerGames() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        final String actualCountryTag = sharedPref.getString(LocaleHelper.USER_COUNTRY_PREFERENCE_KEY, "");
        if ("".equals(actualCountryTag)) {
            disp.add(ProductRepository.getInstance().getCountryByCC2OrWorld(LocaleHelper.getLocale().getCountry())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mayCountry ->
                    setupUrlHungerGames(mayCountry.isPresent() ? mayCountry.get().getTag() : "en:world")));
        } else {
            setupUrlHungerGames(actualCountryTag);
        }
    }

    private void setupUrlHungerGames(String countryTag) {
        final Uri url = Uri.parse(String.format("https://hunger.openfoodfacts.org/questions?type=%s&value_tag=%s&country=%s",
            mSearchInfo.getSearchType().getUrl(),
            mSearchInfo.getSearchQuery(),
            countryTag));

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();

        binding.btnHungerGames.setVisibility(View.VISIBLE);
        binding.btnHungerGames.setText(
            getResources().getString(R.string.hunger_game_call_to_action, mSearchInfo.getSearchTitle()));
        binding.btnHungerGames.setOnClickListener(view ->
            CustomTabActivityHelper.openCustomTab(this, customTabsIntent, url, null));
    }

    protected void newSearchQuery() {
        getSupportActionBar().setTitle(mSearchInfo.getSearchTitle());
        switch (mSearchInfo.getSearchType()) {
            case BRAND:
                getSupportActionBar().setSubtitle(R.string.brand_string);
                setupHungerGames();
                break;
            case LABEL:
                getSupportActionBar().setSubtitle(getString(R.string.label_string));
                setupHungerGames();
                break;
            case CATEGORY:
                getSupportActionBar().setSubtitle(getString(R.string.category_string));
                setupHungerGames();
                break;
            case COUNTRY:
                getSupportActionBar().setSubtitle(R.string.country_string);
                break;
            case ORIGIN:
                getSupportActionBar().setSubtitle(R.string.origin_of_ingredients);
                break;
            case MANUFACTURING_PLACE:
                getSupportActionBar().setSubtitle(R.string.manufacturing_place);
                break;
            case ADDITIVE:
                getSupportActionBar().setSubtitle(R.string.additive_string);
                break;
            case SEARCH:
                getSupportActionBar().setSubtitle(R.string.search_string);
                break;
            case STORE:
                getSupportActionBar().setSubtitle(R.string.store_subtitle);
                break;
            case PACKAGING:
                getSupportActionBar().setSubtitle(R.string.packaging_subtitle);
                break;
            case CONTRIBUTOR:
                getSupportActionBar().setSubtitle(getString(R.string.contributor_string));
                break;
            case ALLERGEN:
                getSupportActionBar().setSubtitle(getString(R.string.allergen_string));
                break;
            case INCOMPLETE_PRODUCT:
                getSupportActionBar().setTitle(getString(R.string.products_to_be_completed));
                break;
            case STATE:
                // TODO: 26/07/2020 use resources
                getSupportActionBar().setSubtitle("State");
                break;
            default:
                Log.e("Products Browsing", "No match case found for " + mSearchInfo.getSearchType());
        }

        apiClient = new OpenFoodAPIClient(ProductSearchActivity.this, BuildConfig.OFWEBSITE);
        api = new OpenFoodAPIClient(ProductSearchActivity.this);

        binding.progressBar.setVisibility(View.VISIBLE);

        setup();
    }

    public void setup() {
        binding.offlineCloudLinearLayout.setVisibility(View.INVISIBLE);
        binding.textCountProduct.setVisibility(View.INVISIBLE);
        pageAddress = 1;
        binding.noResultsLayout.setVisibility(View.INVISIBLE);
        getDataFromAPI();
    }

    /**
     * When no matching products are found in the database then noResultsLayout is displayed.
     * This method is called when the user clicks on the add photo button in the noResultsLayout.
     */
    public void addProduct() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new MaterialDialog.Builder(this)
                    .title(R.string.action_about)
                    .content(R.string.permission_camera)
                    .neutralText(R.string.txtOk)
                    .onNeutral((dialog, which) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .CAMERA}, Utils.MY_PERMISSIONS_REQUEST_CAMERA))
                    .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Utils.MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            Intent intent = new Intent(this, ContinuousScanActivity.class);
            startActivity(intent);
        }
    }

    public void getDataFromAPI() {
        // TODO: 31/08/2020 all api calls to rxjava single
        String searchQuery = mSearchInfo.getSearchQuery();
        switch (mSearchInfo.getSearchType()) {
            case BRAND:
                disp.add(apiClient.getProductsByBrand(searchQuery, pageAddress).observeOn(AndroidSchedulers.mainThread())
                    .subscribe((search, throwable) ->
                        displaySearch(throwable == null,
                            search,
                            R.string.txt_no_matching_brand_products)));
                break;
            case COUNTRY:
                apiClient.getProductsByCountry(searchQuery, pageAddress, (value, country) ->
                    displaySearch(value, country, R.string.txt_no_matching_country_products));
                break;
            case ORIGIN:
                apiClient.getProductsByOrigin(searchQuery, pageAddress, (value, origin) ->
                    displaySearch(value, origin, R.string.txt_no_matching_country_products));
                break;
            case MANUFACTURING_PLACE:
                apiClient.getProductsByManufacturingPlace(searchQuery, pageAddress, (value, manufacturingPlace) ->
                    displaySearch(value, manufacturingPlace, R.string.txt_no_matching_country_products));
                break;
            case ADDITIVE:
                disp.add(apiClient.getProductsByAdditive(searchQuery, pageAddress)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((search, throwable) ->
                        displaySearch(throwable == null,
                            search,
                            R.string.txt_no_matching_additive_products)));
                break;
            case STORE:
                apiClient.getProductsByStore(searchQuery, pageAddress);
                break;
            case PACKAGING:
                disp.add(apiClient.getProductsByPackaging(searchQuery, pageAddress)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((search, throwable) ->
                        displaySearch(throwable == null, search, R.string.txt_no_matching_packaging_products)));
                break;
            case SEARCH:
                if (ProductUtils.isBarcodeValid(searchQuery)) {
                    api.openProduct(searchQuery, this);
                } else {
                    disp.add(api.searchProductsByName(searchQuery, pageAddress)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((search, throwable) ->
                            displaySearch(throwable == null,
                                search,
                                R.string.txt_no_matching_products,
                                R.string.txt_broaden_search))
                    );
                }
                break;
            case LABEL:
                disp.add(api.getProductsByLabel(searchQuery, pageAddress)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((search, throwable) ->
                        displaySearch(throwable == null, search, R.string.txt_no_matching_label_products))
                );
                break;
            case CATEGORY:
                api.getProductsByCategory(searchQuery, pageAddress, (value, category) ->
                    displaySearch(value, category, R.string.txt_no_matching__category_products));
                break;
            case ALLERGEN:
                api.getProductsByAllergen(searchQuery, pageAddress, (value, allergen) ->
                    displaySearch(value, allergen, R.string.txt_no_matching_allergen_products));
                break;
            case CONTRIBUTOR:
                loadDataForContributor(searchQuery);
                break;
            case STATE:
                disp.add(api.getProductsByStates(searchQuery, pageAddress).observeOn(AndroidSchedulers.mainThread())
                    .subscribe((search, throwable) ->
                        displaySearch(throwable == null, search, R.string.txt_no_matching_allergen_products)));
                break;
            case INCOMPLETE_PRODUCT:
                // Get Products to be completed data and input it to loadData function
                disp.add(api.getIncompleteProducts(pageAddress).observeOn(AndroidSchedulers.mainThread())
                    .subscribe((search, throwable) ->
                        displaySearch(throwable == null, search, R.string.txt_no_matching_incomplete_products)));
                break;
            default:
                Log.e("Products Browsing", "No match case found for " + mSearchInfo.getSearchType());
        }
    }

    private void loadDataForContributor(String searchQuery) {
        switch (contributionType) {

            case 1:
                api.getToBeCompletedProductsByContributor(searchQuery, pageAddress, (value, category) ->
                    displaySearch(value, category, R.string.txt_no_matching_contributor_products));
                break;

            case 2:
                api.getPicturesContributedProducts(searchQuery, pageAddress, (value, category) ->
                    displaySearch(value, category, R.string.txt_no_matching_contributor_products));
                break;

            case 3:
                api.getPicturesContributedIncompleteProducts(searchQuery, pageAddress, (value, category) ->
                    displaySearch(value, category, R.string.txt_no_matching_contributor_products));
                break;

            case 4:
                api.getInfoAddedProducts(searchQuery, pageAddress, (value, category) ->
                    displaySearch(value, category, R.string.txt_no_matching_contributor_products));
                break;

            case 5:
                disp.add(api.getInfoAddedIncompleteProductsSingle(searchQuery, pageAddress).observeOn(AndroidSchedulers.mainThread()).subscribe((search, throwable) ->
                    displaySearch(throwable == null, search, R.string.txt_no_matching_contributor_products)));
                break;

            case 0:
            default:
                disp.add(api.getProductsByContributor(searchQuery, pageAddress)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((search, throwable) ->
                        displaySearch(throwable == null, search, R.string.txt_no_matching_contributor_products)));
                break;
        }
    }

    private void loadData(boolean isResponseOk, @Nullable Search response) {
        if (isResponseOk && response != null) {
            mCountProducts = Integer.parseInt(response.getCount());
            if (pageAddress == 1) {
                binding.textCountProduct.setText(
                    getResources().getString(R.string.number_of_results)
                        + NumberFormat.getInstance(getResources().getConfiguration().locale)
                        .format(Long.parseLong(response.getCount())));
                mProducts = new ArrayList<>();
                mProducts.addAll(response.getProducts());
                if (mProducts.size() < mCountProducts) {
                    mProducts.add(null);
                }
                if (setupDone) {
                    binding.productsRecyclerView.setAdapter(new ProductsRecyclerViewAdapter(mProducts, isLowBatteryMode));
                }
                setUpRecyclerView();
            } else {
                if (mProducts.size() - 1 < mCountProducts + 1) {
                    final int posStart = mProducts.size();
                    mProducts.remove(mProducts.size() - 1);
                    mProducts.addAll(response.getProducts());
                    if (mProducts.size() < mCountProducts) {
                        mProducts.add(null);
                    }
                    binding.productsRecyclerView.getAdapter().notifyItemRangeChanged(posStart - 1, mProducts.size() - 1);
                }
            }
        } else {
            binding.swipeRefresh.setRefreshing(false);
            binding.productsRecyclerView.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.offlineCloudLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Shows UI indicating that no matching products were found. Called by
     * {@link #displaySearch(boolean, Search, int)} and {@link #displaySearch(boolean, Search, int, int)}
     *
     * @param message message to display when there are no results for given search
     * @param extendedMessage additional message to display, -1 if no message is displayed
     */
    private void showEmptySearch(@StringRes int message, @StringRes int extendedMessage) {
        binding.textNoResults.setText(message);
        if (extendedMessage != -1) {
            binding.textExtendSearch.setText(extendedMessage);
        }
        binding.noResultsLayout.setVisibility(View.VISIBLE);
        binding.noResultsLayout.bringToFront();
        binding.productsRecyclerView.setVisibility(View.INVISIBLE);
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.offlineCloudLinearLayout.setVisibility(View.INVISIBLE);
        binding.textCountProduct.setVisibility(View.GONE);
        binding.swipeRefresh.setRefreshing(false);
    }

    /**
     * Loads the search results into the UI, otherwise shows UI indicating that no matching
     * products were found.
     *
     * @param isResponseSuccessful true if the search response was successful
     * @param response the search results
     * @param emptyMessage message to display if there are no results
     * @param extendedMessage extended message to display if there are no results
     */
    private void displaySearch(boolean isResponseSuccessful, Search response,
                               @StringRes int emptyMessage, @StringRes int extendedMessage) {
        if (response == null) {
            loadData(isResponseSuccessful, null);
        } else {
            final int count;
            try {
                count = Integer.parseInt(response.getCount());
            } catch (NumberFormatException e) {
                throw new NumberFormatException(String.format("Cannot parse %s.", response.getCount()));
            }
            if (isResponseSuccessful && count == 0) {
                showEmptySearch(emptyMessage, extendedMessage);
            } else {
                loadData(isResponseSuccessful, response);
            }
        }
    }

    /**
     * @see #displaySearch(boolean, Search, int, int)
     */
    private void displaySearch(boolean isResponseSuccessful, Search response, @StringRes int emptyMessage) {
        displaySearch(isResponseSuccessful, response, emptyMessage, -1);
    }

    private void setUpRecyclerView() {
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.swipeRefresh.setRefreshing(false);
        binding.textCountProduct.setVisibility(View.VISIBLE);
        binding.offlineCloudLinearLayout.setVisibility(View.INVISIBLE);
        binding.productsRecyclerView.setVisibility(View.VISIBLE);

        if (!setupDone) {
            binding.productsRecyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(ProductSearchActivity.this, LinearLayoutManager.VERTICAL, false);
            binding.productsRecyclerView.setLayoutManager(mLayoutManager);

            ProductsRecyclerViewAdapter adapter = new ProductsRecyclerViewAdapter(mProducts, isLowBatteryMode);
            binding.productsRecyclerView.setAdapter(adapter);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.productsRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
            binding.productsRecyclerView.addItemDecoration(dividerItemDecoration);

            // Retain an instance so that you can call `resetState()` for fresh searches
            // Adds the scroll listener to RecyclerView
            binding.productsRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view1) {
                    if (mProducts.size() < mCountProducts) {
                        pageAddress = page;
                        getDataFromAPI();
                    }
                }
            });

            binding.productsRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(ProductSearchActivity.this, (view, position) -> {
                    Product product = ((ProductsRecyclerViewAdapter) binding.productsRecyclerView.getAdapter()).getProduct(position);
                    if (product != null) {
                        String barcode = product.getCode();
                        if (Utils.isNetworkConnected(ProductSearchActivity.this)) {
                            api.openProduct(barcode, ProductSearchActivity.this);
                            try {
                                View viewWithFocus = ProductSearchActivity.this.getCurrentFocus();
                                if (viewWithFocus != null) {
                                    InputMethodManager imm = (InputMethodManager) ProductSearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    if (imm != null) {
                                        imm.hideSoftInputFromWindow(viewWithFocus.getWindowToken(), 0);
                                    }
                                }
                            } catch (NullPointerException e) {
                                Log.e(ProductSearchActivity.class.getSimpleName(), "addOnItemTouchListener", e);
                            }
                        } else {
                            new MaterialDialog.Builder(ProductSearchActivity.this)
                                .title(R.string.device_offline_dialog_title)
                                .content(R.string.connectivity_check)
                                .positiveText(R.string.txt_try_again)
                                .onPositive((dialog, which) -> {
                                    if (Utils.isNetworkConnected(ProductSearchActivity.this)) {
                                        api.openProduct(barcode, ProductSearchActivity.this);
                                    } else {
                                        Toast.makeText(ProductSearchActivity.this, R.string.device_offline_dialog_title, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .negativeText(R.string.dismiss)
                                .onNegative((dialog, which) -> dialog.dismiss())
                                .show();
                        }
                    }
                })
            );

            binding.swipeRefresh.setOnRefreshListener(() -> {

                mProducts.clear();
                adapter.notifyDataSetChanged();
                binding.textCountProduct.setText(getResources().getString(R.string.number_of_results));
                pageAddress = 1;
                setup();
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            });
        }

        setupDone = true;
        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(true);
            pageAddress = 1;
            setup();
        });
    }
}
