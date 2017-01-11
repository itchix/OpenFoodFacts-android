package openfoodfacts.github.scrachx.openfood.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.picasso.Picasso;

import net.steamcrafted.loadtoast.LoadToast;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.OkHttpClient;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.models.AllergenRestResponse;
import openfoodfacts.github.scrachx.openfood.models.HistoryProduct;
import openfoodfacts.github.scrachx.openfood.models.HistoryProductDao;
import openfoodfacts.github.scrachx.openfood.models.Product;
import openfoodfacts.github.scrachx.openfood.models.ProductImage;
import openfoodfacts.github.scrachx.openfood.models.Search;
import openfoodfacts.github.scrachx.openfood.models.SendProduct;
import openfoodfacts.github.scrachx.openfood.models.State;
import openfoodfacts.github.scrachx.openfood.utils.Utils;
import openfoodfacts.github.scrachx.openfood.views.FullScreenImage;
import openfoodfacts.github.scrachx.openfood.views.ProductActivity;
import openfoodfacts.github.scrachx.openfood.views.SaveProductOfflineActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static openfoodfacts.github.scrachx.openfood.models.ProductImageField.FRONT;
import static openfoodfacts.github.scrachx.openfood.models.ProductImageField.INGREDIENTS;
import static openfoodfacts.github.scrachx.openfood.models.ProductImageField.NUTRITION;
import static openfoodfacts.github.scrachx.openfood.network.OpenFoodAPIService.PRODUCT_API_COMMENT;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class OpenFoodAPIClient {

    private static final JacksonConverterFactory jacksonConverterFactory = JacksonConverterFactory.create();

    private final static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .build();

    private final OpenFoodAPIService apiService;

    public OpenFoodAPIClient(Context context) {
        this(context.getString(R.string.openfoodUrl));
    }

    private OpenFoodAPIClient(String apiUrl) {
        apiService = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .client(httpClient)
                .addConverterFactory(jacksonConverterFactory)
                .build()
                .create(OpenFoodAPIService.class);
    }

    /**
     * @return The API service to be able to use directly retrofit API mapping
     */
    public OpenFoodAPIService getAPIService() {
        return apiService;
    }

    /**
     * Open the product activity if the barcode exist.
     * Also add it in the history if the product exist.
     * @param barcode product barcode
     * @param activity
     */
    public void getProduct(final String barcode, final Activity activity) {
        final LoadToast lt = getLoadToast(activity);

        apiService.getProductByBarcode(barcode).enqueue(new Callback<State>() {
            @Override
            public void onResponse(Call<State> call, Response<State> response) {

                final State s = response.body();

                if (s.getStatus() == 0) {
                    lt.error();
                    new MaterialDialog.Builder(activity)
                            .title(R.string.txtDialogsTitle)
                            .content(R.string.txtDialogsContent)
                            .positiveText(R.string.txtYes)
                            .negativeText(R.string.txtNo)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent intent = new Intent(activity, SaveProductOfflineActivity.class);
                                    intent.putExtra("barcode", barcode);
                                    activity.startActivity(intent);
                                    activity.finish();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    return;
                                }
                            })
                            .show();
                } else {
                    lt.success();
                    new HistoryTask(activity).doInBackground(s.getProduct());
                    Intent intent = new Intent(activity, ProductActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("state", s);
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<State> call, Throwable t) {
                new MaterialDialog.Builder(activity)
                        .title(R.string.txtDialogsTitle)
                        .content(R.string.txtDialogsContent)
                        .positiveText(R.string.txtYes)
                        .negativeText(R.string.txtNo)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Intent intent = new Intent(activity, SaveProductOfflineActivity.class);
                                intent.putExtra("barcode",barcode);
                                activity.startActivity(intent);
                                activity.finish();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                return;
                            }
                        })
                        .show();
                Toast.makeText(activity, activity.getString(R.string.errorWeb), Toast.LENGTH_LONG).show();
                lt.error();
            }
        });
    }

    /**
     * Open the product activity if the barcode exist.
     * Also add it in the history if the product exist.
     * @param barcode product barcode
     * @param activity
     * @param camera needed when the function is called by the barcodefragment else null
     * @param resultHandler needed when the function is called by the barcodefragment else null
     */
    public void getProduct(final String barcode, final Activity activity, final ZXingScannerView camera, final ZXingScannerView.ResultHandler resultHandler) {
        final LoadToast lt = getLoadToast(activity);

        apiService.getProductByBarcode(barcode).enqueue(new Callback<State>() {
            @Override
            public void onResponse(Call<State> call, Response<State> response) {

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
                final State s = response.body();

                if (s.getStatus() == 0) {
                    lt.error();
                    new MaterialDialog.Builder(activity)
                            .title(R.string.txtDialogsTitle)
                            .content(R.string.txtDialogsContent)
                            .positiveText(R.string.txtYes)
                            .negativeText(R.string.txtNo)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Intent intent = new Intent(activity, SaveProductOfflineActivity.class);
                                    intent.putExtra("barcode", barcode);
                                    activity.startActivity(intent);
                                    activity.finish();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    return;
                                }
                            })
                            .show();
                } else {
                    lt.success();
                    final Product product = s.getProduct();
                    new HistoryTask(activity).doInBackground(s.getProduct());
                    if (settings.getBoolean("powerMode", false) && camera != null) {
                        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                                .title(product.getProductName())
                                .customView(R.layout.alert_powermode_image, true)
                                .neutralText(R.string.txtOk)
                                .positiveText(R.string.txtSeeMore)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent intent = new Intent(activity, ProductActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("state", s);
                                        intent.putExtras(bundle);
                                        activity.startActivity(intent);
                                    }
                                })
                                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        camera.resumeCameraPreview(resultHandler);
                                    }
                                })
                                .build();

                        ImageView imgPhoto = (ImageView) dialog.getCustomView().findViewById(R.id.imagePowerModeProduct);
                        ImageView imgNutriscore = (ImageView) dialog.getCustomView().findViewById(R.id.imageGrade);
                        TextView quantityProduct = (TextView) dialog.getCustomView().findViewById(R.id.textQuantityProduct);
                        TextView brandProduct = (TextView) dialog.getCustomView().findViewById(R.id.textBrandProduct);

                        if(product.getQuantity() != null && !product.getQuantity().trim().isEmpty()) {
                            quantityProduct.setText(Html.fromHtml("<b>" + activity.getResources().getString(R.string.txtQuantity) + "</b>" + ' ' + product.getQuantity()));
                        } else {
                            quantityProduct.setVisibility(View.GONE);
                        }
                        if(product.getBrands() != null && !product.getBrands().trim().isEmpty()) {
                            brandProduct.setText(Html.fromHtml("<b>" + activity.getResources().getString(R.string.txtBrands) + "</b>" + ' ' + product.getBrands()));
                        } else {
                            brandProduct.setVisibility(View.GONE);
                        }
                        if (isNotEmpty(s.getProduct().getImageUrl())) {
                            Picasso.with(activity)
                                    .load(Utils.getImageGrade(product.getNutritionGradeFr()))
                                    .into(imgNutriscore);
                        }
                        if (isNotEmpty(s.getProduct().getImageUrl())) {
                            Picasso.with(activity)
                                    .load(s.getProduct().getImageUrl())
                                    .into(imgPhoto);
                            imgPhoto.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(view.getContext(), FullScreenImage.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("imageurl", product.getImageUrl());
                                    intent.putExtras(bundle);
                                    activity.startActivity(intent);
                                }
                            });
                        }
                        dialog.show();
                    } else {
                        Intent intent = new Intent(activity, ProductActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("state", s);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<State> call, Throwable t) {
                new MaterialDialog.Builder(activity)
                        .title(R.string.txtDialogsTitle)
                        .content(R.string.txtDialogsContent)
                        .positiveText(R.string.txtYes)
                        .negativeText(R.string.txtNo)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Intent intent = new Intent(activity, SaveProductOfflineActivity.class);
                                intent.putExtra("barcode",barcode);
                                activity.startActivity(intent);
                                activity.finish();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                return;
                            }
                        })
                        .show();
                Toast.makeText(activity, activity.getString(R.string.errorWeb), Toast.LENGTH_LONG).show();
                lt.error();
            }
        });
    }

    public void searchProduct(final String name, final Activity activity, final OnProductsCallback productsCallback) {
        final LoadToast lt = getLoadToast(activity);

        apiService.searchProductByName(name).enqueue(new Callback<Search>() {
            @Override
            public void onResponse(Call<Search> call, Response<Search> response) {
                if (!response.isSuccess()) {
                    productsCallback.onProductsResponse(false, null);
                    return;
                }

                Search s = response.body();
                if(Integer.valueOf(s.getCount()) == 0){
                    Toast.makeText(activity, R.string.txt_product_not_found, Toast.LENGTH_LONG).show();
                    lt.error();
                    productsCallback.onProductsResponse(false, null);
                }else{
                    lt.success();
                    productsCallback.onProductsResponse(true, s.getProducts());
                }
            }

            @Override
            public void onFailure(Call<Search> call, Throwable t) {
                Toast.makeText(activity, activity.getString(R.string.errorWeb), Toast.LENGTH_LONG).show();
                lt.error();
                productsCallback.onProductsResponse(false, null);
            }
        });
    }

    public void getAllergens(final OnAllergensCallback onAllergensCallback, final Activity activity) {
        apiService.getAllergens().enqueue(new Callback<AllergenRestResponse>() {
            @Override
            public void onResponse(Call<AllergenRestResponse> call, Response<AllergenRestResponse> response) {
                if (!response.isSuccess()) {
                    onAllergensCallback.onAllergensResponse(false);
                    return;
                }

                Utils.getAppDaoSession(activity).getAllergenDao().insertInTx(response.body().getAllergens());

                onAllergensCallback.onAllergensResponse(true);
            }

            @Override
            public void onFailure(Call<AllergenRestResponse> call, Throwable t) {

            }
        });
    }

    public void post(final Activity activity, final SendProduct product, final OnProductSentCallback productSentCallback){
        final LoadToast lt = new LoadToast(activity);
        lt.setText(activity.getString(R.string.toastSending));
        lt.setBackgroundColor(activity.getResources().getColor(R.color.indigo_600));
        lt.setTextColor(activity.getResources().getColor(R.color.white));
        lt.show();

        apiService.saveProduct(product.getBarcode(), product.getName(), product.getBrands(), product.getQuantity(), product.getUserId(), product.getPassword(), PRODUCT_API_COMMENT).enqueue(new Callback<State>() {
            @Override
            public void onResponse(Call<State> call, Response<State> response) {
                if (!response.isSuccess() || response.body().getStatus() == 0) {
                    lt.error();
                    productSentCallback.onProductSentResponse(false);
                    return;
                }

                String imguploadFront = product.getImgupload_front();
                if (StringUtils.isNotEmpty(imguploadFront )) {
                    ProductImage image = new ProductImage(product.getBarcode(), FRONT, new File(imguploadFront));
                    postImg(activity, image);
                }

                String imguploadIngredients = product.getImgupload_ingredients();
                if (StringUtils.isNotEmpty(imguploadIngredients)) {
                    postImg(activity, new ProductImage(product.getBarcode(), INGREDIENTS, new File(imguploadIngredients)));
                }

                String imguploadNutrition = product.getImgupload_nutrition();
                if (StringUtils.isNotBlank(imguploadNutrition)) {
                    postImg(activity, new ProductImage(product.getBarcode(), NUTRITION, new File(imguploadNutrition)));
                }

                lt.success();
                productSentCallback.onProductSentResponse(true);
            }

            @Override
            public void onFailure(Call<State> call, Throwable t) {
                lt.error();
                productSentCallback.onProductSentResponse(false);
            }
        });
    }

    public void postImg(final Context context, final ProductImage image) {
        final LoadToast lt = new LoadToast(context);
        lt.setText(context.getString(R.string.toastSending));
        lt.setBackgroundColor(context.getResources().getColor(R.color.indigo_600));
        lt.setTextColor(context.getResources().getColor(R.color.white));
        lt.show();

        apiService.saveImage(image.getCode(), image.getField(), image.getImguploadFront(), image.getImguploadIngredients(), image.getImguploadNutrition())
                .enqueue(new Callback<JsonNode>() {
            @Override
            public void onResponse(Call<JsonNode> call, Response<JsonNode> response) {
                if(!response.isSuccess()) {
                    Toast.makeText(context, context.getString(R.string.errorWeb), Toast.LENGTH_LONG).show();
                    lt.error();
                }

                JsonNode body = response.body();
                if (body.get("status").asText().contains("status not ok")) {
                    Toast.makeText(context, body.get("error").asText(), Toast.LENGTH_LONG).show();
                    lt.error();
                } else {
                    lt.success();
                }
            }

            @Override
            public void onFailure(Call<JsonNode> call, Throwable t) {
                Toast.makeText(context, context.getString(R.string.errorWeb), Toast.LENGTH_LONG).show();
                lt.error();
            }
        });
    }

    @NonNull
    private LoadToast getLoadToast(Activity activity) {
        final LoadToast lt = new LoadToast(activity);
        lt.setText(activity.getString(R.string.toast_retrieving));
        lt.setBackgroundColor(activity.getResources().getColor(R.color.indigo_600));
        lt.setTextColor(activity.getResources().getColor(R.color.white));
        lt.show();
        return lt;
    }

    public interface OnProductsCallback {

        void onProductsResponse(boolean isOk, List<Product> products);
    }

    public interface OnAllergensCallback {

        void onAllergensResponse(boolean value);
    }

    public interface OnProductSentCallback {
        void onProductSentResponse(boolean value);
    }

    /**
     * Create an history product asynchronously
     */
    private class HistoryTask extends AsyncTask<Product, Void, Void> {

        private Activity mActivity;

        public HistoryTask(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected Void doInBackground(Product... products) {
            Product product = products[0];

            List<HistoryProduct> historyProducts = Utils.getAppDaoSession(mActivity).getHistoryProductDao().queryBuilder().where(HistoryProductDao.Properties.Barcode.eq(product.getCode())).list();
            HistoryProduct hp;
            if(historyProducts.size() == 1) {
                hp = historyProducts.get(0);
                hp.setLastSeen(new Date());
            } else {
                hp = new HistoryProduct(product.getProductName(), product.getBrands(), product.getImageFrontUrl(), product.getCode());
            }
            Utils.getAppDaoSession(mActivity).getHistoryProductDao().insertOrReplace(hp);

            return null;
        }
    }
}
