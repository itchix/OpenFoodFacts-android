package openfoodfacts.github.scrachx.openfood.features.search

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.analytics.AnalyticsEvent
import openfoodfacts.github.scrachx.openfood.analytics.MatomoAnalytics
import openfoodfacts.github.scrachx.openfood.customtabs.CustomTabActivityHelper
import openfoodfacts.github.scrachx.openfood.customtabs.WebViewFallback
import openfoodfacts.github.scrachx.openfood.databinding.ActivityProductBrowsingListBinding
import openfoodfacts.github.scrachx.openfood.features.adapters.ProductSearchAdapter
import openfoodfacts.github.scrachx.openfood.features.product.view.ProductViewActivityStarter
import openfoodfacts.github.scrachx.openfood.features.shared.BaseActivity
import openfoodfacts.github.scrachx.openfood.listeners.CommonBottomListenerInstaller.installBottomNavigation
import openfoodfacts.github.scrachx.openfood.listeners.CommonBottomListenerInstaller.selectNavigationItem
import openfoodfacts.github.scrachx.openfood.listeners.EndlessRecyclerViewScrollListener
import openfoodfacts.github.scrachx.openfood.listeners.RecyclerItemClickListener
import openfoodfacts.github.scrachx.openfood.models.Barcode
import openfoodfacts.github.scrachx.openfood.models.Search
import openfoodfacts.github.scrachx.openfood.models.SearchInfo
import openfoodfacts.github.scrachx.openfood.models.SearchProduct
import openfoodfacts.github.scrachx.openfood.repositories.ProductRepository
import openfoodfacts.github.scrachx.openfood.repositories.TaxonomiesRepository
import openfoodfacts.github.scrachx.openfood.utils.Intent
import openfoodfacts.github.scrachx.openfood.utils.LocaleManager
import openfoodfacts.github.scrachx.openfood.utils.SearchType
import openfoodfacts.github.scrachx.openfood.utils.SearchType.ADDITIVE
import openfoodfacts.github.scrachx.openfood.utils.SearchType.ALLERGEN
import openfoodfacts.github.scrachx.openfood.utils.SearchType.BRAND
import openfoodfacts.github.scrachx.openfood.utils.SearchType.CATEGORY
import openfoodfacts.github.scrachx.openfood.utils.SearchType.CONTRIBUTOR
import openfoodfacts.github.scrachx.openfood.utils.SearchType.COUNTRY
import openfoodfacts.github.scrachx.openfood.utils.SearchType.EMB
import openfoodfacts.github.scrachx.openfood.utils.SearchType.INCOMPLETE_PRODUCT
import openfoodfacts.github.scrachx.openfood.utils.SearchType.LABEL
import openfoodfacts.github.scrachx.openfood.utils.SearchType.MANUFACTURING_PLACE
import openfoodfacts.github.scrachx.openfood.utils.SearchType.ORIGIN
import openfoodfacts.github.scrachx.openfood.utils.SearchType.PACKAGING
import openfoodfacts.github.scrachx.openfood.utils.SearchType.SEARCH
import openfoodfacts.github.scrachx.openfood.utils.SearchType.STATE
import openfoodfacts.github.scrachx.openfood.utils.SearchType.STORE
import openfoodfacts.github.scrachx.openfood.utils.SearchType.TRACE
import openfoodfacts.github.scrachx.openfood.utils.isGranted
import openfoodfacts.github.scrachx.openfood.utils.isImageLoadingDisabled
import openfoodfacts.github.scrachx.openfood.utils.isPowerSaveMode
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProductSearchActivity : BaseActivity() {
    private var _binding: ActivityProductBrowsingListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var client: ProductRepository

    @Inject
    lateinit var productViewActivityStarter: ProductViewActivityStarter

    @Inject
    lateinit var analytics: MatomoAnalytics

    @Inject
    lateinit var taxonomiesRepository: TaxonomiesRepository

    @Inject
    lateinit var picasso: Picasso

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var localeManager: LocaleManager

    private lateinit var mSearchInfo: SearchInfo
    private lateinit var adapter: ProductSearchAdapter

    private var contributionType = 0
    private val lowBatteryMode by lazy { isImageLoadingDisabled() && isPowerSaveMode() }

    /**
     * boolean to determine if image should be loaded or not
     */
    private var setupDone = false
    private var mCountProducts = 0
    private var pageAddress = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductBrowsingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarInclude.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Track search without details
        analytics.trackEvent(AnalyticsEvent.ProductSearch)

        // OnClick
        binding.buttonTryAgain.setOnClickListener { reloadSearch() }
        binding.addProduct.setOnClickListener { addProduct() }

        binding.textCountProduct.visibility = View.INVISIBLE

        // Get the search information (query, title, type) that we will use in this activity
        val extras = intent.extras
        if (extras != null) {
            mSearchInfo = extras.getParcelable(SEARCH_INFO) ?: SearchInfo.emptySearchInfo()
        } else if (Intent.ACTION_VIEW == intent.action) {
            // the user has entered the activity via a url
            val data = intent.data
            if (data != null) {
                // TODO: If we open advanced search from app it redirects here
                val paths = data.toString().split("/")
                mSearchInfo = SearchInfo.emptySearchInfo()

                if (paths[3] == "cgi" && paths[4].contains("search.pl")) {
                    mSearchInfo.searchTitle = data.getQueryParameter("search_terms") ?: ""
                    mSearchInfo.searchQuery = data.getQueryParameter("search_terms") ?: ""
                    mSearchInfo.searchType = SEARCH
                } else {
                    mSearchInfo.searchTitle = paths[4]
                    mSearchInfo.searchQuery = paths[4]
                    mSearchInfo.searchType = SearchType.fromUrl(paths[3]) ?: SEARCH
                }

            } else {
                Log.i(LOG_TAG, "No data was passed in with URL. Exiting.")
                finish()
            }
        } else {
            Log.e(LOG_TAG, "No data passed to the activity. Exiting.")
            finish()
        }
        newSearch()

        binding.navigationBottom.bottomNavigation.selectNavigationItem(0)
        binding.navigationBottom.bottomNavigation.installBottomNavigation(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchMenuItem = menu.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mSearchInfo.searchQuery = query
                mSearchInfo.searchType = SEARCH
                newSearch()
                return true
            }

            override fun onQueryTextChange(newText: String) = true
        })
        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem) = true

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                supportActionBar?.title = ""
                finish()
                return true
            }
        })
        if (CONTRIBUTOR == mSearchInfo.searchType) {
            menu.findItem(R.id.action_set_type).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_set_type -> {
                val contributionTypes = arrayOf(
                    getString(R.string.products_added),
                    getString(R.string.products_incomplete),
                    getString(R.string.product_pictures_contributed),
                    getString(R.string.picture_contributed_incomplete),
                    getString(R.string.product_info_added),
                    getString(R.string.product_info_tocomplete)
                )
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.show_by)
                    .setItems(contributionTypes) { _, position ->
                        contributionType = when (position) {
                            in 1..5 -> position
                            else -> 0
                        }
                        newSearch()
                    }.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupHungerGames() {
        val actualCountryTag = sharedPreferences.getString(getString(R.string.pref_country_key), "")
        if (actualCountryTag.isNullOrBlank()) {
            lifecycleScope.launch {
                val url = taxonomiesRepository
                    .getCountry(localeManager.getLocale().country)
                    ?.tag ?: "en:world"
                withContext(Main) { setupUrlHungerGames(url) }
            }
        } else {
            setupUrlHungerGames(actualCountryTag)
        }
    }

    private fun setupUrlHungerGames(countryTag: String?) {
        val url = ("https://hunger.openfoodfacts.org/questions?" +
                "type=${mSearchInfo.searchType.url}" +
                "&value_tag=${mSearchInfo.searchQuery}" +
                "&country=$countryTag").toUri()
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        binding.btnHungerGames.visibility = View.VISIBLE
        binding.btnHungerGames.text = resources.getString(R.string.hunger_game_call_to_action, mSearchInfo.searchTitle)
        binding.btnHungerGames.setOnClickListener {
            CustomTabActivityHelper.openCustomTab(this, customTabsIntent, url, WebViewFallback())
        }
    }

    private fun newSearch() {
        val bar = supportActionBar ?: error("Support action bar not set.")

        bar.title = mSearchInfo.searchTitle
        when (mSearchInfo.searchType) {
            BRAND -> {
                bar.setSubtitle(R.string.brand_string)
                setupHungerGames()
            }
            LABEL -> {
                bar.subtitle = getString(R.string.label_string)
                setupHungerGames()
            }
            CATEGORY -> {
                bar.subtitle = getString(R.string.category_string)
                setupHungerGames()
            }
            COUNTRY -> bar.setSubtitle(R.string.country_string)
            ORIGIN -> bar.setSubtitle(R.string.origin_of_ingredients)
            MANUFACTURING_PLACE -> bar.setSubtitle(R.string.manufacturing_place)
            ADDITIVE -> bar.setSubtitle(R.string.additive_string)
            SEARCH -> bar.setSubtitle(R.string.search_string)
            STORE -> bar.setSubtitle(R.string.store_subtitle)
            PACKAGING -> bar.setSubtitle(R.string.packaging_subtitle)
            CONTRIBUTOR -> bar.subtitle = getString(R.string.contributor_string)
            ALLERGEN -> bar.subtitle = getString(R.string.allergen_string)
            INCOMPLETE_PRODUCT -> bar.title = getString(R.string.products_to_be_completed)
            STATE -> bar.setSubtitle(R.string.state_subtitle)
            TRACE -> bar.setSubtitle(R.string.traces)
            EMB -> bar.setSubtitle(R.string.emb_code)
        }

        binding.progressBar.visibility = View.VISIBLE
        reloadSearch()
    }

    private fun reloadSearch() {
        binding.offlineCloudLinearLayout.visibility = View.INVISIBLE
        binding.textCountProduct.visibility = View.INVISIBLE
        binding.noResultsLayout.visibility = View.INVISIBLE

        pageAddress = 1
        loadDataFromAPI()
    }

    /**
     * When no matching products are found in the database then noResultsLayout is displayed.
     * This method is called when the user clicks on the add photo button in the noResultsLayout.
     */
    private fun addProduct() {
        when {
            checkSelfPermission(this, Manifest.permission.CAMERA).isGranted() -> startScanActivity()
            shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.action_about)
                    .setMessage(R.string.permission_camera)
                    .setNeutralButton(android.R.string.ok) { _, _ ->
                        requestCameraThenOpenScan.launch(Manifest.permission.CAMERA)
                    }.show()
            }
            else -> requestCameraThenOpenScan.launch(Manifest.permission.CAMERA)
        }
    }


    fun loadDataFromAPI() {
        val searchQuery = mSearchInfo.searchQuery
        when (mSearchInfo.searchType) {
            BRAND -> {
                startSearch(R.string.txt_no_matching_brand_products, -1) {
                    client.getProductsByBrand(searchQuery, pageAddress)
                }
            }

            COUNTRY -> {
                startSearch(R.string.txt_no_matching_country_products, -1) {
                    client.getProductsByCountry(searchQuery, pageAddress)
                }
            }

            ORIGIN -> {
                startSearch(R.string.txt_no_matching_country_products, -1) {
                    client.getProductsByOrigin(searchQuery, pageAddress)
                }
            }

            MANUFACTURING_PLACE -> {
                startSearch(R.string.txt_no_matching_country_products, -1) {
                    client.getProductsByManufacturingPlace(searchQuery, pageAddress)
                }
            }

            ADDITIVE -> {
                startSearch(R.string.txt_no_matching_additive_products, -1) {
                    client.getProductsByAdditive(searchQuery,
                        pageAddress)
                }
            }

            STORE -> {
                startSearch(R.string.txt_no_matching_store_products, -1) {
                    client.getProductsByStore(searchQuery, pageAddress)
                }
            }

            PACKAGING -> {
                startSearch(R.string.txt_no_matching_packaging_products, -1) {
                    client.getProductsByPackaging(searchQuery, pageAddress)
                }
            }

            SEARCH -> {
                val barcode = Barcode(searchQuery)
                if (barcode.isValid()) {
                    productViewActivityStarter.openProduct(
                        barcode = barcode,
                        activity = this@ProductSearchActivity,
                        productOpenedListener = { finish() }
                    )
                } else {
                    startSearch(R.string.txt_no_matching_products, R.string.txt_broaden_search) {
                        client.searchProductsByName(searchQuery, pageAddress)
                    }
                }
            }
            LABEL -> {
                startSearch(R.string.txt_no_matching_label_products, -1) {
                    client.getProductsByLabel(searchQuery, pageAddress)
                }
            }

            CATEGORY -> {
                startSearch(R.string.txt_no_matching__category_products, -1) {
                    client.getProductsByCategory(searchQuery, pageAddress)
                }
            }

            ALLERGEN -> {
                startSearch(R.string.txt_no_matching_allergen_products, -1) {
                    client.getProductsByAllergen(searchQuery, pageAddress)
                }
            }

            CONTRIBUTOR -> loadDataForContributor(searchQuery)

            STATE -> {
                startSearch(R.string.txt_no_matching_allergen_products, -1) {
                    client.getProductsByStates(searchQuery, pageAddress)
                }
            }

            // Get Products to be completed data and input it to loadData function
            INCOMPLETE_PRODUCT -> {
                startSearch(R.string.txt_no_matching_incomplete_products, -1)
                { client.getIncompleteProducts(pageAddress) }
            }

            else -> Log.e("Products Browsing", "No match case found for ${mSearchInfo.searchType}")
        }
    }

    private fun startSearch(
        @StringRes noResultsText: Int, @StringRes broadenSearchText: Int,
        searchFun: suspend () -> Search,
    ) {
        lifecycleScope.launch(Main) {
            var throwable: Throwable? = null
            val search = try {
                withContext(IO) { searchFun() }
            } catch (err: Exception) {
                throwable = err
                null
            }

            // Ensure the Fragment is still visible = job not cancelled
            if (isActive) {
                displaySearch(throwable == null, search, noResultsText, broadenSearchText)
            }
        }
    }

    private fun loadDataForContributor(searchQuery: String) {
        when (contributionType) {
            1 -> {
                startSearch(R.string.txt_no_matching_contributor_products, -1)
                { client.getToBeCompletedProductsByContributor(searchQuery, pageAddress) }
            }

            2 -> {
                startSearch(R.string.txt_no_matching_contributor_products, -1) {
                    client.getPicturesContributedProducts(searchQuery,
                        pageAddress)
                }
            }

            3 -> {
                startSearch(R.string.txt_no_matching_contributor_products,
                    -1) { client.getPicturesContributedIncompleteProducts(searchQuery, pageAddress) }
            }

            4 -> {
                startSearch(R.string.txt_no_matching_contributor_products,
                    -1) { client.getInfoAddedProducts(searchQuery, pageAddress) }
            }

            5 -> {
                startSearch(R.string.txt_no_matching_contributor_products, -1) {
                    client.getInfoAddedIncompleteProductsSingle(searchQuery, pageAddress)
                }
            }

            else -> {
                startSearch(R.string.txt_no_matching_contributor_products, -1) {
                    client.getProductsByContributor(searchQuery, pageAddress)
                }
            }
        }
    }

    private fun showSuccessfulSearch(search: Search) {
        mCountProducts = search.count.toInt()
        if (pageAddress == 1) {
            val number = NumberFormat.getInstance(Locale.getDefault()).format(search.count.toLong())
            binding.textCountProduct.text = "${resources.getString(R.string.number_of_results)} $number"

            // Hacky thing to make sure the count is right
            val products: MutableList<SearchProduct?> = search.products.toMutableList()
            if (products.size < mCountProducts) {
                products += null
            }

            if (setupDone) {
                adapter = ProductSearchAdapter(products, lowBatteryMode, this, picasso, client, localeManager)
                binding.productsRecyclerView.adapter = adapter
            }

            setUpRecyclerView(products)

        } else if (adapter.products.size - 1 < mCountProducts + 1) {
            val posStart = adapter.itemCount

            adapter.products.removeAt(posStart - 1)
            adapter.products += search.products

            // Hacky thing to make sure the count is right
            if (adapter.products.size < mCountProducts) {
                adapter.products += null
            }

            adapter.notifyItemRangeChanged(posStart - 1, adapter.products.size - 1)
        }
    }

    private fun showOfflineCloud() {
        binding.swipeRefresh.isRefreshing = false
        binding.textCountProduct.visibility = View.GONE
        binding.productsRecyclerView.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        binding.offlineCloudLinearLayout.visibility = View.VISIBLE
    }

    /**
     * Shows UI indicating that no matching products were found.
     *
     * Called by [displaySearch].
     *
     * @param message message to display when there are no results for given search
     * @param extendedMessage additional message to display, -1 if no message is displayed
     */
    private fun showEmptyResponse(@StringRes message: Int, @StringRes extendedMessage: Int) {
        binding.swipeRefresh.isEnabled = false
        binding.swipeRefresh.isRefreshing = false

        binding.productsRecyclerView.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        binding.offlineCloudLinearLayout.visibility = View.INVISIBLE
        binding.textCountProduct.visibility = View.GONE

        binding.textNoResults.setText(message)
        if (extendedMessage != -1) binding.textExtendSearch.setText(extendedMessage)
        binding.noResultsLayout.bringToFront()
        binding.noResultsLayout.visibility = View.VISIBLE

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
    private fun displaySearch(
        isResponseSuccessful: Boolean,
        response: Search?,
        @StringRes emptyMessage: Int,
        @StringRes extendedMessage: Int = -1,
    ) {
        if (response == null || !isResponseSuccessful) {
            showOfflineCloud()
            return
        }

        val count = try {
            response.count.toInt()
        } catch (e: NumberFormatException) {
            throw NumberFormatException("Cannot parse ${response.count}.")
        }

        if (count == 0) {
            showEmptyResponse(emptyMessage, extendedMessage)
        } else {
            showSuccessfulSearch(response)
        }
    }

    private fun setUpRecyclerView(products: MutableList<SearchProduct?>) {
        // Hide refreshing
        binding.swipeRefresh.isRefreshing = false

        // Hide progress bar
        binding.progressBar.visibility = View.INVISIBLE
        binding.offlineCloudLinearLayout.visibility = View.INVISIBLE

        // Show the recycler view
        binding.textCountProduct.visibility = View.VISIBLE
        binding.productsRecyclerView.visibility = View.VISIBLE

        // If this is the first time, set up the recycler view
        if (!setupDone) {
            val layoutManager = LinearLayoutManager(
                this@ProductSearchActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = ProductSearchAdapter(
                products,
                lowBatteryMode,
                this,
                picasso,
                client,
                localeManager
            )
            val dividerItemDecoration =
                DividerItemDecoration(binding.productsRecyclerView.context, DividerItemDecoration.VERTICAL)

            binding.productsRecyclerView.run RecyclerView@{
                this@RecyclerView.setHasFixedSize(true)
                this@RecyclerView.layoutManager = layoutManager
                this@RecyclerView.adapter = this@ProductSearchActivity.adapter
                this@RecyclerView.addItemDecoration(dividerItemDecoration)

                // Retain an instance so that you can call `resetState()` for fresh searches
                // Adds the scroll listener to RecyclerView
                addOnScrollListener(object : EndlessRecyclerViewScrollListener(layoutManager) {
                    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                        if (products.size < mCountProducts) {
                            pageAddress = page
                            loadDataFromAPI()
                        }
                    }
                })

                addOnItemTouchListener(
                    RecyclerItemClickListener(this@ProductSearchActivity) { _, position ->
                        this@ProductSearchActivity.adapter.getProduct(position)?.let {
                            productViewActivityStarter.openProduct(it.barcode, this@ProductSearchActivity)
                        }
                    }
                )
            }
            binding.swipeRefresh.setOnRefreshListener {
                products.clear()
                adapter.notifyDataSetChanged()
                binding.textCountProduct.text = resources.getString(R.string.number_of_results)
                pageAddress = 1
                reloadSearch()
                if (binding.swipeRefresh.isRefreshing) {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }

        setupDone = true
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            pageAddress = 1
            reloadSearch()
        }
    }

    companion object {
        /**
         * Must be public to be visible by TakeScreenshotIncompleteProductsTest class.
         */
        const val SEARCH_INFO = "search_info"

        /**
         * Start a new [ProductSearchActivity] given a search information
         *
         * @param context the context to use to start this activity
         * @param searchQuery the search query
         * @param searchTitle the title used in the activity for this search query
         * @param type the type of search
         */
        fun start(context: Context, type: SearchType, searchQuery: String, searchTitle: String = searchQuery) =
            start(context, SearchInfo(type, searchQuery, searchTitle))

        /**
         * @see [start]
         */
        private fun start(context: Context, searchInfo: SearchInfo) {
            context.startActivity(Intent<ProductSearchActivity>(context).apply {
                putExtra(SEARCH_INFO, searchInfo)
            })
        }

        private val LOG_TAG: String = ProductSearchActivity::class.simpleName!!
    }
}
