package openfoodfacts.github.scrachx.openfood.views.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static openfoodfacts.github.scrachx.openfood.utils.Utils.bold;
import static openfoodfacts.github.scrachx.openfood.utils.Utils.getRoundNumber;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.models.CategoryName;
import openfoodfacts.github.scrachx.openfood.models.NutrientLevelItem;
import openfoodfacts.github.scrachx.openfood.models.NutrientLevels;
import openfoodfacts.github.scrachx.openfood.models.NutrimentLevel;
import openfoodfacts.github.scrachx.openfood.models.Nutriments;
import openfoodfacts.github.scrachx.openfood.models.Product;
import openfoodfacts.github.scrachx.openfood.network.WikidataApiClient;
import openfoodfacts.github.scrachx.openfood.repositories.IProductRepository;
import openfoodfacts.github.scrachx.openfood.repositories.ProductRepository;
import openfoodfacts.github.scrachx.openfood.utils.ProductInfoState;
import openfoodfacts.github.scrachx.openfood.utils.SearchType;
import openfoodfacts.github.scrachx.openfood.utils.Utils;
import openfoodfacts.github.scrachx.openfood.views.ProductBrowsingListActivity;
import openfoodfacts.github.scrachx.openfood.views.ProductComparisonActivity;
import openfoodfacts.github.scrachx.openfood.views.customtabs.CustomTabActivityHelper;
import openfoodfacts.github.scrachx.openfood.views.customtabs.CustomTabsHelper;
import openfoodfacts.github.scrachx.openfood.views.customtabs.WebViewFallback;
import openfoodfacts.github.scrachx.openfood.views.product.ProductActivity;
import openfoodfacts.github.scrachx.openfood.views.product.summary.SummaryProductFragment;

public class ProductComparisonAdapter extends RecyclerView.Adapter<ProductComparisonAdapter.ProductComparisonViewHolder>{

    private ArrayList<Product> productsToCompare;
    private Context context;
    private boolean isLowBatteryMode = false;
    private IProductRepository repository = ProductRepository.getInstance();
    private CompositeDisposable disposable = new CompositeDisposable();
    private String languageCode = Locale.getDefault().getLanguage();

    public static class ProductComparisonViewHolder extends RecyclerView.ViewHolder {
        public NestedScrollView listItemLayout;
        public TextView productNameTextView;
        public TextView productQuantityTextView;
        public TextView productBrandTextView;
        public RecyclerView nutrientsRecyclerView;
        public CardView productComparisonNutrientCv;
        public ImageButton productComparisonImage;
        public TextView productComparisonLabel;
        public ImageView productComparisonImageGrade;
        public ImageView productComparisonNovaGroup;
        public CardView productComparisonAdditiveCv;
        public TextView productComparisonAdditiveText;

        public ProductComparisonViewHolder(View view) {
            super(view);
            listItemLayout = (NestedScrollView) view.findViewById(R.id.product_comparison_list_item_layout);
            productNameTextView = (TextView) view.findViewById(R.id.product_comparison_name);
            productQuantityTextView = (TextView) view.findViewById(R.id.product_comparison_quantity);
            productBrandTextView = (TextView) view.findViewById(R.id.product_comparison_brand);
            nutrientsRecyclerView = (RecyclerView) view.findViewById(R.id.product_comparison_listNutrientLevels);
            productComparisonNutrientCv = (CardView) view.findViewById(R.id.product_comparison_nutrient_cv);
            productComparisonImage = (ImageButton) view.findViewById(R.id.product_comparison_image);
            productComparisonLabel = (TextView) view.findViewById(R.id.product_comparison_label);
            productComparisonImageGrade = (ImageView) view.findViewById(R.id.product_comparison_imageGrade);
            productComparisonNovaGroup = (ImageView) view.findViewById(R.id.product_comparison_nova_group);
            productComparisonAdditiveCv = (CardView) view.findViewById(R.id.product_comparison_additive);
            productComparisonAdditiveText = (TextView) view.findViewById(R.id.product_comparison_additive_text);
        }
    }

    public ProductComparisonAdapter(ArrayList<Product> productsToCompare, Context context) {
        this.productsToCompare = productsToCompare;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductComparisonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.product_comparison_list_item, parent, false);
        ProductComparisonViewHolder viewHolder = new ProductComparisonViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductComparisonViewHolder holder, int position) {
        if (!productsToCompare.isEmpty()) {
            Product product = productsToCompare.get(position);

            //set the visibility of UI components
            holder.productNameTextView.setVisibility(View.VISIBLE);
            holder.productQuantityTextView.setVisibility(View.VISIBLE);
            holder.productBrandTextView.setVisibility(View.VISIBLE);

            if (isNotBlank(product.getImageUrl())) {
                holder.productComparisonLabel.setVisibility(View.GONE);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                Utils.DISABLE_IMAGE_LOAD = preferences.getBoolean("disableImageLoad", false);
                if (Utils.DISABLE_IMAGE_LOAD && Utils.getBatteryLevel(context)) {
                    isLowBatteryMode = true;
                }
                // Load Image if isLowBatteryMode is false
                if (!isLowBatteryMode) {
                    Picasso.with(context)
                            .load(product.getImageUrl())
                            .into(holder.productComparisonImage);
                } else {
                    holder.productComparisonImage.setVisibility(View.GONE);

                }
            }

            if (isNotBlank(product.getProductName())) {
                holder.productNameTextView.setText(product.getProductName());
            } else {
                holder.productNameTextView.setVisibility(View.GONE);
            }

            if (isNotBlank(product.getQuantity())) {
                holder.productQuantityTextView.setText(bold("Quantity :"));
                holder.productQuantityTextView.append(' ' + product.getQuantity());
            } else {
                holder.productQuantityTextView.setVisibility(View.GONE);
            }

            if (isNotBlank(product.getBrands())) {
                holder.productBrandTextView.setText(bold("Brands :"));
                holder.productBrandTextView.append(" ");

                String[] brands = product.getBrands().split(",");
                for (int i = 0; i < brands.length - 1; i++) {
                    holder.productBrandTextView.append(brands[i].trim());
                    holder.productBrandTextView.append(", ");
                }
                holder.productBrandTextView.append(brands[brands.length - 1].trim());
            } else {
                holder.productBrandTextView.setVisibility(View.GONE);
            }

            Nutriments nutriments = product.getNutriments();

            NutrientLevels nutrientLevels = product.getNutrientLevels();
            NutrimentLevel fat = null;
            NutrimentLevel saturatedFat = null;
            NutrimentLevel sugars = null;
            NutrimentLevel salt = null;
            if (nutrientLevels != null) {
                fat = nutrientLevels.getFat();
                saturatedFat = nutrientLevels.getSaturatedFat();
                sugars = nutrientLevels.getSugars();
                salt = nutrientLevels.getSalt();
            }
            if (!(fat == null && salt == null && saturatedFat == null && sugars == null)) {

                holder.productComparisonImageGrade.setImageDrawable(ContextCompat.getDrawable(context, Utils.getImageGrade(product.getNutritionGradeFr())));

                if (nutriments!=null) {
                    holder.productComparisonNutrientCv.setVisibility(View.VISIBLE);
                    holder.nutrientsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    holder.nutrientsRecyclerView.setAdapter(new NutrientLevelListAdapter(context, loadLevelItems(product)));
                }
            }

            if (product.getNovaGroups() != null) {
                holder.productComparisonNovaGroup.setImageResource(Utils.getNovaGroupDrawable(product.getNovaGroups()));
            } else {
                holder.productComparisonNovaGroup.setImageResource(0);
            }
            if (product.getNovaGroups() == null && product.getNutritionGradeFr() == null) {
                holder.productComparisonImageGrade.setVisibility(View.GONE);
                holder.productComparisonNovaGroup.setVisibility(View.GONE);
            }

            String additiveText = loadAdditives(product);
            if (isNotBlank(additiveText)) {
                holder.productComparisonAdditiveCv.setVisibility(View.VISIBLE);
                holder.productComparisonAdditiveText.setVisibility(View.VISIBLE);
                holder.productComparisonAdditiveText.append(additiveText);
            } else {
                holder.productComparisonAdditiveCv.setVisibility(View.GONE);
            }

        } else {
            holder.listItemLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productsToCompare.size();
    }


    private List<NutrientLevelItem> loadLevelItems(Product product) {
        List<NutrientLevelItem> levelItem = new ArrayList<>();
        Nutriments nutriments = product.getNutriments();

        NutrientLevels nutrientLevels = product.getNutrientLevels();
        NutrimentLevel fat = null;
        NutrimentLevel saturatedFat = null;
        NutrimentLevel sugars = null;
        NutrimentLevel salt = null;
        if (nutrientLevels != null) {
            fat = nutrientLevels.getFat();
            saturatedFat = nutrientLevels.getSaturatedFat();
            sugars = nutrientLevels.getSugars();
            salt = nutrientLevels.getSalt();
        }

        if (!(fat == null && salt == null && saturatedFat == null && sugars == null)) {

            if(nutriments!=null)
            {

                Nutriments.Nutriment fatNutriment = nutriments.get(Nutriments.FAT);
                if (fat != null && fatNutriment != null) {
                    String fatNutrimentLevel = fat.getLocalize(context);
                    String modifier = nutriments.getModifier(Nutriments.FAT);
                    levelItem.add(new NutrientLevelItem("Fat",
                            (modifier == null ? "" : modifier)
                                    + getRoundNumber(fatNutriment.getFor100g())
                                    + " " + fatNutriment.getUnit(),
                            fatNutrimentLevel,
                            fat.getImageLevel()));
                }

                Nutriments.Nutriment saturatedFatNutriment = nutriments.get(Nutriments.SATURATED_FAT);
                if (saturatedFat != null && saturatedFatNutriment != null) {
                    String saturatedFatLocalize = saturatedFat.getLocalize(context);
                    String saturatedFatValue = getRoundNumber(saturatedFatNutriment.getFor100g()) + " " + saturatedFatNutriment.getUnit();
                    String modifier = nutriments.getModifier(Nutriments.SATURATED_FAT);
                    levelItem.add(new NutrientLevelItem("Saturated fat",
                            (modifier == null ? "" : modifier) + saturatedFatValue,
                            saturatedFatLocalize,
                            saturatedFat.getImageLevel()));
                }

                Nutriments.Nutriment sugarsNutriment = nutriments.get(Nutriments.SUGARS);
                if (sugars != null && sugarsNutriment  != null) {
                    String sugarsLocalize = sugars.getLocalize(context);
                    String sugarsValue = getRoundNumber(sugarsNutriment.getFor100g()) + " " + sugarsNutriment.getUnit();
                    String modifier = nutriments.getModifier(Nutriments.SUGARS);
                    levelItem.add(new NutrientLevelItem("Sugars",
                            (modifier == null ? "" : modifier) + sugarsValue,
                            sugarsLocalize,
                            sugars.getImageLevel()));
                }

                Nutriments.Nutriment saltNutriment = nutriments.get(Nutriments.SALT);
                if (salt != null && saltNutriment != null) {
                    String saltLocalize = salt.getLocalize(context);
                    String saltValue = getRoundNumber(saltNutriment.getFor100g()) + " " + saltNutriment.getUnit();
                    String modifier = nutriments.getModifier(Nutriments.SALT);
                    levelItem.add(new NutrientLevelItem("Salt",
                            (modifier == null ? "" : modifier) + saltValue,
                            saltLocalize,
                            salt.getImageLevel()));
                }
            }

        }
        return levelItem;
    }

    private String loadAdditives(Product product) {
        StringBuilder additivesBuilder = new StringBuilder();
        List<String> additivesTags = product.getAdditivesTags();
        if (additivesTags != null && !additivesTags.isEmpty()) {
            disposable.add(
                    Observable.fromArray(additivesTags.toArray(new String[additivesTags.size()]))
                            .flatMapSingle(tag -> repository.getAdditiveByTagAndLanguageCode(tag, languageCode)
                                    .flatMap(categoryName -> {
                                        if (categoryName.isNull()) {
                                            return repository.getAdditiveByTagAndDefaultLanguageCode(tag);
                                        } else {
                                            return Single.just(categoryName);
                                        }
                                    }))
                            .filter(additiveName -> additiveName.isNotNull())
                            .toList()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(d -> {})
                            .subscribe(additives -> {
                                if (!additives.isEmpty()) {
                                    additivesBuilder.append(bold("Additives :"));
                                    additivesBuilder.append(" ");
                                    additivesBuilder.append("\n");

                                    for (int i = 0; i < additives.size() - 1; i++) {
                                        additivesBuilder.append(additives.get(i).getName());
                                        additivesBuilder.append("\n");
                                    }

                                    additivesBuilder.append(additives.get(additives.size() - 1).getName());
                                }
                            }, e -> {
                                e.printStackTrace();
                            })
            );
        }
        return additivesBuilder.toString();
    }
}
