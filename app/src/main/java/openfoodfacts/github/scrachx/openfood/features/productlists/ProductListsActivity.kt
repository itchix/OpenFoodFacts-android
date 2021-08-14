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
package openfoodfacts.github.scrachx.openfood.features.productlists

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.analytics.AnalyticsEvent
import openfoodfacts.github.scrachx.openfood.analytics.MatomoAnalytics
import openfoodfacts.github.scrachx.openfood.databinding.ActivityProductListsBinding
import openfoodfacts.github.scrachx.openfood.features.productlist.ProductListActivity
import openfoodfacts.github.scrachx.openfood.features.productlist.ProductListActivity.Companion.KEY_LIST_ID
import openfoodfacts.github.scrachx.openfood.features.productlist.ProductListActivity.Companion.KEY_LIST_NAME
import openfoodfacts.github.scrachx.openfood.features.productlist.ProductListActivity.Companion.KEY_PRODUCT_TO_ADD
import openfoodfacts.github.scrachx.openfood.features.shared.BaseActivity
import openfoodfacts.github.scrachx.openfood.listeners.CommonBottomListenerInstaller.installBottomNavigation
import openfoodfacts.github.scrachx.openfood.listeners.CommonBottomListenerInstaller.selectNavigationItem
import openfoodfacts.github.scrachx.openfood.listeners.RecyclerItemClickListener
import openfoodfacts.github.scrachx.openfood.models.DaoSession
import openfoodfacts.github.scrachx.openfood.models.Product
import openfoodfacts.github.scrachx.openfood.models.entities.*
import openfoodfacts.github.scrachx.openfood.utils.SwipeController
import openfoodfacts.github.scrachx.openfood.utils.isEmpty
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProductListsActivity : BaseActivity(), SwipeController.Actions {
    private var _binding: ActivityProductListsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var daoSession: DaoSession

    @Inject
    lateinit var matomoAnalytics: MatomoAnalytics

    private lateinit var adapter: ProductListsAdapter
    private lateinit var productListsDao: ProductListsDao

    private val disp = CompositeDisposable()

    override fun onDestroy() {
        disp.dispose()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProductListsBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setTitle(R.string.your_lists)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.bottomNavigation.bottomNavigation.installBottomNavigation(this)

        binding.fabAdd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plus_blue_24, 0, 0, 0)

        // FIXME: remove runBlocking
        productListsDao = runBlocking { daoSession.getProductListsDaoWithDefaultList(this@ProductListsActivity) }
        val productLists = productListsDao.loadAll().toMutableList()

        adapter = ProductListsAdapter(this@ProductListsActivity, productLists)


        binding.productListsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productListsRecyclerView.adapter = adapter

        binding.tipBox.loadToolTip()

        val bundle = intent.extras
        if (bundle != null) {
            val productToAdd = bundle[KEY_PRODUCT] as Product?
            showCreateListDialog(productToAdd)
        }

        binding.productListsRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(this) { _, position ->
                val id = productLists[position].id
                val listName = productLists[position].listName
                Intent(this, ProductListActivity::class.java).apply {
                    putExtra(KEY_LIST_ID, id)
                    putExtra(KEY_LIST_NAME, listName)
                    startActivityForResult(this, 1)
                }
            }
        )
        val swipeController = SwipeController(this, this)
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(binding.productListsRecyclerView)

        binding.fabAdd.setOnClickListener { showCreateListDialog() }
    }

    private fun showCreateListDialog(productToAdd: Product? = null) {
        MaterialDialog.Builder(this)
            .title(R.string.txt_create_new_list)
            .alwaysCallInputCallback()
            .input(R.string.create_new_list_list_name, R.string.empty, false) { dialog, listName ->
                // validate if there is another list with the same name
                val cannotAdd = checkListNameExist(listName.toString())

                dialog.inputEditText?.error = if (cannotAdd) resources.getString(R.string.error_duplicate_listname) else null
                dialog.getActionButton(DialogAction.POSITIVE).isEnabled = !cannotAdd
            }
            .positiveText(R.string.dialog_create)
            .negativeText(R.string.dialog_cancel)
            .onPositive { dialog, _ ->  // this enable to avoid dismissing dialog if list name already exist
                matomoAnalytics.trackEvent(AnalyticsEvent.ShoppingListCreated)
                val inputEditText = dialog.inputEditText!!
                val listName = inputEditText.text.toString()
                val productList = ProductLists(listName, if (productToAdd != null) 1 else 0)

                adapter.lists.add(productList)
                productListsDao.insert(productList)

                adapter.notifyDataSetChanged()

                if (productToAdd != null) {
                    val id = productList.id
                    Intent(this@ProductListsActivity, ProductListActivity::class.java).apply {
                        putExtra(KEY_LIST_ID, id)
                        putExtra(KEY_LIST_NAME, listName)
                        putExtra(KEY_PRODUCT_TO_ADD, productToAdd)
                        startActivityForResult(this, 1)
                    }
                } else {
                    dialog.dismiss()
                }
            }.show()
    }

    /**
     * Check if listname already in products lists.
     */
    private fun checkListNameExist(listName: String) =
        adapter.lists.firstOrNull { it.listName == listName } != null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data!!.extras!!.getBoolean("update")) {
            adapter.notifyDataSetChanged()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private val chooseFileContract = registerForActivityResult(ActivityResultContracts.OpenDocument())
    { uri ->
        if (uri != null) {
            try {
                parseCSV(contentResolver.openInputStream(uri))
            } catch (e: Exception) {
                Log.e(ProductListsActivity::class.simpleName, "Error importing CSV.", e)
            }
        }
    }

    override fun onRightClicked(position: Int) {
        if (adapter.lists.isNullOrEmpty()) return
        val list = adapter.lists[position]

        // delete the product from YOUR_LISTED_PRODUCT_TABLE
        daoSession.listedProductDao.queryBuilder()
            .where(ListedProductDao.Properties.ListId.eq(list.id))
            .buildDelete()
            .executeDeleteWithoutDetachingEntities()
        daoSession.clear()

        productListsDao.delete(list)
        adapter.remove(list)
        adapter.notifyItemRangeChanged(position, adapter.itemCount)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_product_lists, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_import_csv -> {
            openCSVToImport()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun openCSVToImport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            chooseFileContract.launch(arrayOf("text/csv"))
        } else {
            Toast.makeText(this, "Feature disabled for your android version.", Toast.LENGTH_LONG).show()
        }
    }

    public override fun onResume() {
        super.onResume()
        binding.bottomNavigation.bottomNavigation.selectNavigationItem(R.id.my_lists)
    }

    private fun parseCSV(inputStream: InputStream?) {
        val progressDialog = ProgressDialog(this@ProductListsActivity)
        progressDialog.show()

        Observable.create { emitter: ObservableEmitter<Int> ->
            Single.fromCallable {
                val listProductDao = daoSession.listedProductDao
                val list = mutableListOf<ListedProduct>()
                try {
                    CSVParser(
                        InputStreamReader(inputStream),
                        CSVFormat.DEFAULT.withFirstRecordAsHeader()
                    ).use { parser ->
                        val size = parser.records.size
                        parser.records.withIndex().forEach { (index, record) ->
                            val listName = record[2]
                            var daoList = productListsDao.queryBuilder()
                                .where(ProductListsDao.Properties.ListName.eq(listName)).unique()
                            if (daoList == null) {
                                //create new list
                                val productList = ProductLists(listName, 0)
                                adapter.lists.add(productList)
                                productListsDao.insert(productList)
                                daoList = productListsDao.queryBuilder()
                                    .where(ProductListsDao.Properties.ListName.eq(listName)).unique()
                            }
                            val yourListedProduct = ListedProduct().apply {
                                this.barcode = record[0]
                                this.productName = record[1]
                                this.listName = listName
                                this.productDetails = record[3]
                                this.listId = daoList.id
                            }
                            list += yourListedProduct
                            emitter.onNext(index * 100 / size)
                        }
                        listProductDao.insertOrReplaceInTx(list)
                        return@fromCallable true
                    }
                } catch (e: Exception) {
                    Log.e("ParseCSV", e.message, e)
                    return@fromCallable false
                }
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe { success ->
                progressDialog.dismiss()
                if (success) {
                    Toast.makeText(this@ProductListsActivity, getString(R.string.toast_import_csv), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ProductListsActivity, getString(R.string.toast_import_csv_error), Toast.LENGTH_SHORT).show()
                }
            }.addTo(disp)
        }.subscribe { value -> progressDialog.progress = value!! }.addTo(disp)
    }

    companion object {
        private const val KEY_PRODUCT = "product"

        @JvmStatic
        fun start(context: Context, productToAdd: Product) = context.startActivity(
            Intent(context, ProductListsActivity::class.java).apply {
                putExtra(KEY_PRODUCT, productToAdd)
            })

        @JvmStatic
        fun start(context: Context) = context.startActivity(Intent(context, ProductListsActivity::class.java))

        suspend fun DaoSession.getProductListsDaoWithDefaultList(context: Context): ProductListsDao = withContext(Dispatchers.IO) {
            if (productListsDao.isEmpty()) {
                productListsDao.insertInTx(
                    ProductLists(context.getString(R.string.txt_eaten_products), 0),
                    ProductLists(context.getString(R.string.txt_products_to_buy), 0)
                )
            }
            return@withContext productListsDao
        }
    }
}
