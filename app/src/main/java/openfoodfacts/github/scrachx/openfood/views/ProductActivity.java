package openfoodfacts.github.scrachx.openfood.views;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.fragments.IngredientsProductFragment;
import openfoodfacts.github.scrachx.openfood.fragments.NutritionInfoProductFragment;
import openfoodfacts.github.scrachx.openfood.fragments.NutritionProductFragment;
import openfoodfacts.github.scrachx.openfood.fragments.SummaryProductFragment;
import openfoodfacts.github.scrachx.openfood.models.Allergen;
import openfoodfacts.github.scrachx.openfood.models.AllergenDao;
import openfoodfacts.github.scrachx.openfood.models.Product;
import openfoodfacts.github.scrachx.openfood.models.State;
import openfoodfacts.github.scrachx.openfood.utils.Utils;
import openfoodfacts.github.scrachx.openfood.views.adapters.ProductFragmentPagerAdapter;
import openfoodfacts.github.scrachx.openfood.views.customtabs.CustomTabActivityHelper;
import openfoodfacts.github.scrachx.openfood.views.customtabs.CustomTabsHelper;
import openfoodfacts.github.scrachx.openfood.views.customtabs.WebViewFallback;

public class ProductActivity extends BaseActivity {

    @BindView(R.id.pager) ViewPager viewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tabs) TabLayout tabLayout;
    private ShareActionProvider mShareActionProvider;
    private State mState;
    private AllergenDao mAllergenDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAllergenDao = Utils.getAppDaoSession(this).getAllergenDao();
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        mState = (State) intent.getExtras().getSerializable("state");

        Product product = mState.getProduct();

        List<String> allergens = product.getAllergensHierarchy();
        List<String> traces = product.getTracesTags();
        allergens.addAll(traces);

        List<String> matchAll = new ArrayList<>();
        List<Allergen> mAllergens = mAllergenDao.queryBuilder().where(AllergenDao.Properties.Enable.eq("true")).list();
        for (int a = 0; a < mAllergens.size(); a++) {
            for(int i = 0; i < allergens.size(); i++) {
                if (allergens.get(i).trim().equals(mAllergens.get(a).getIdAllergen().trim())) {
                    matchAll.add(mAllergens.get(a).getName());
                }
            }
        }

        if(matchAll.size() > 0) {
            new MaterialDialog.Builder(this)
                    .title(R.string.warning_allergens)
                    .items(matchAll)
                    .neutralText(R.string.txtOk)
                    .titleColorRes(R.color.red_500)
                    .dividerColorRes(R.color.indigo_900)
                    .icon(new IconicsDrawable(this)
                            .icon(GoogleMaterial.Icon.gmd_warning)
                            .color(Color.RED)
                            .sizeDp(24))
                    .show();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        String[] menuTitles = getResources().getStringArray(R.array.nav_drawer_items_product);

        ProductFragmentPagerAdapter adapterResult = new ProductFragmentPagerAdapter(getSupportFragmentManager());
        SummaryProductFragment summaryProductFragment = new SummaryProductFragment();

        adapterResult.addFragment(summaryProductFragment, menuTitles[0]);
        adapterResult.addFragment(new IngredientsProductFragment(), menuTitles[1]);
        adapterResult.addFragment(new NutritionProductFragment(), menuTitles[2]);
        adapterResult.addFragment(new NutritionInfoProductFragment(), menuTitles[3]);
        viewPager.setAdapter(adapterResult);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_edit_product:
                String url = getString(R.string.website) + "cgi/product.pl?type=edit&code=" + mState.getProduct().getCode();
                if (mState.getProduct().getUrl() != null) {
                    url = " " + mState.getProduct().getUrl();
                }

                CustomTabsIntent customTabsIntent = CustomTabsHelper.getCustomTabsIntent(getBaseContext(), null);

                CustomTabActivityHelper.openCustomTab(ProductActivity.this, customTabsIntent, Uri.parse(url), new WebViewFallback());
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String url = " " + getString(R.string.website_product) + mState.getProduct().getCode();
        if (mState.getProduct().getUrl() != null) {
            url = " " + mState.getProduct().getUrl();
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.msg_share) + url);
        shareIntent.setType("text/plain");
        setShareIntent(shareIntent);

        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
}
