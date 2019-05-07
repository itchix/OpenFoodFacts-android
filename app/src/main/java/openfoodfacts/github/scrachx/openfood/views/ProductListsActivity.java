package openfoodfacts.github.scrachx.openfood.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.models.Product;
import openfoodfacts.github.scrachx.openfood.models.ProductLists;
import openfoodfacts.github.scrachx.openfood.models.ProductListsDao;
import openfoodfacts.github.scrachx.openfood.utils.SwipeController;
import openfoodfacts.github.scrachx.openfood.utils.SwipeControllerActions;
import openfoodfacts.github.scrachx.openfood.utils.Utils;
import openfoodfacts.github.scrachx.openfood.views.adapters.ProductListsAdapter;
import openfoodfacts.github.scrachx.openfood.views.listeners.RecyclerItemClickListener;
import org.apache.commons.collections.CollectionUtils;

import java.util.Iterator;
import java.util.List;

public class ProductListsActivity extends BaseActivity implements SwipeControllerActions {
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;
    @BindView(R.id.product_lists_recycler_view)
    RecyclerView recyclerView;
    ProductListsAdapter adapter;
    List<ProductLists> productLists;
    ProductListsDao productListsDao;

    public static Intent getIntent(Context context) {
        return new Intent(context, ProductListsActivity.class);
    }

    public static ProductListsDao getProducListsDaoWithDefaultList(Context context) {
        ProductListsDao productListsDao = Utils.getDaoSession(context).getProductListsDao();
        if (productListsDao.loadAll().size() == 0) {
            ProductLists eatenList = new ProductLists(context.getString(R.string.txt_eaten_products), 0);
            productListsDao.insert(eatenList);
            ProductLists toBuyList = new ProductLists(context.getString(R.string.txt_products_to_buy), 0);
            productListsDao.insert(toBuyList);
        }
        return productListsDao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_lists);
        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle(R.string.your_lists);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productListsDao = getProducListsDaoWithDefaultList(this);
        productLists = productListsDao.loadAll();

        adapter = new ProductListsAdapter(this, productLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Product p = (Product) bundle.get("product");

            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(R.string.txt_create_new_list)
                    .input(R.string.create_new_list_list_name,
                            R.string.empty, false, (d, input) -> {
                               // do nothing
                            }
                    )
                    .positiveText(R.string.txtSave)
                    .negativeText(R.string.txt_discard)
                    .show();
            // this enable to avoid dismissing dalog if list name already exist
            dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Positive", "Positive clicked");
                    String listName = dialog.getInputEditText().getText().toString();
                    boolean isAlreadyIn = checkListNameExist(listName);
                    if (!isAlreadyIn) {
                        ProductLists productList = new ProductLists(listName, 0);
                        productLists.add(productList);
                        productListsDao.insert(productList);
                        Long id = productList.getId();
                        Intent intent = new Intent(ProductListsActivity.this, YourListedProducts.class);
                        intent.putExtra("listId", id);
                        intent.putExtra("listName", listName);
                        intent.putExtra("product", p);
                        startActivityForResult(intent, 1);
                    } else {
                        Toast.makeText(ProductListsActivity.this, R.string.error_duplicate_listname, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        recyclerView.addOnItemTouchListener(
            new RecyclerItemClickListener(ProductListsActivity.this, ((view, position) -> {
                Long id = productLists.get(position).getId();
                String listName = productLists.get(position).getListName();
                Intent intent = new Intent(this, YourListedProducts.class);
                intent.putExtra("listId", id);
                intent.putExtra("listName", listName);
                startActivityForResult(intent, 1);
            }))
        );

        SwipeController swipeController = new SwipeController(this, ProductListsActivity.this);
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        fabAdd.setOnClickListener(view -> {
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(R.string.txt_create_new_list)
                    .input("List name", "", false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        }
                    })
                    .positiveText(R.string.dialog_create)
                    .negativeText(R.string.dialog_cancel)
                    .show();
            // this enable to avoid dismissing dalog if list name already exist
            dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Positive", "Positive clicked");
                    String listName = dialog.getInputEditText().getText().toString();
                    boolean isAlreadyIn = checkListNameExist(listName);
                    if (!isAlreadyIn) {
                        ProductLists productList = new ProductLists(listName, 0);
                        productLists.add(productList);
                        productListsDao.insert(productList);
                        dialog.dismiss();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ProductListsActivity.this, R.string.error_duplicate_listname, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }


    /**
     * Check if listname already in products lists.
     * @param listName
     * @return
     */
    private boolean checkListNameExist(String listName) {
        for (Iterator<ProductLists> i = productLists.iterator(); i.hasNext();) {
            if (i.next().getListName().equals(listName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data.getExtras().getBoolean("update")) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRightClicked(int position) {
        if (CollectionUtils.isNotEmpty(productLists)) {
            final ProductLists productToRemove = productLists.get(position);
            productListsDao.delete(productToRemove);
            adapter.remove(productToRemove);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, adapter.getItemCount());
        }
    }
}
