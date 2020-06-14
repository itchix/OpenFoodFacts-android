package openfoodfacts.github.scrachx.openfood.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import openfoodfacts.github.scrachx.openfood.BuildConfig;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.databinding.ActivityAddProductBinding;
import openfoodfacts.github.scrachx.openfood.fragments.AddProductIngredientsFragment;
import openfoodfacts.github.scrachx.openfood.fragments.AddProductNutritionFactsFragment;
import openfoodfacts.github.scrachx.openfood.fragments.AddProductOverviewFragment;
import openfoodfacts.github.scrachx.openfood.fragments.AddProductPhotosFragment;
import openfoodfacts.github.scrachx.openfood.images.ProductImage;
import openfoodfacts.github.scrachx.openfood.jobs.OfflineProductWorker;
import openfoodfacts.github.scrachx.openfood.models.OfflineSavedProduct;
import openfoodfacts.github.scrachx.openfood.models.OfflineSavedProductDao;
import openfoodfacts.github.scrachx.openfood.models.Product;
import openfoodfacts.github.scrachx.openfood.models.ProductImageField;
import openfoodfacts.github.scrachx.openfood.models.State;
import openfoodfacts.github.scrachx.openfood.models.ToUploadProduct;
import openfoodfacts.github.scrachx.openfood.models.ToUploadProductDao;
import openfoodfacts.github.scrachx.openfood.network.ApiFields;
import openfoodfacts.github.scrachx.openfood.network.OpenFoodAPIClient;
import openfoodfacts.github.scrachx.openfood.network.services.OpenFoodAPIService;
import openfoodfacts.github.scrachx.openfood.utils.OfflineProductService;
import openfoodfacts.github.scrachx.openfood.utils.Utils;
import openfoodfacts.github.scrachx.openfood.views.adapters.ProductFragmentPagerAdapter;

import static openfoodfacts.github.scrachx.openfood.utils.Utils.isExternalStorageWritable;

public class AddProductActivity extends AppCompatActivity {
    private static final String ADD_TAG = AddProductActivity.class.getSimpleName();
    public static final String MODIFY_NUTRITION_PROMPT = "modify_nutrition_prompt";
    public static final String MODIFY_CATEGORY_PROMPT = "modify_category_prompt";
    public static final String KEY_EDIT_PRODUCT = "edit_product";
    public static final String KEY_IS_EDITION = "is_edition";
    private AddProductIngredientsFragment addProductIngredientsFragment = new AddProductIngredientsFragment();
    private AddProductNutritionFactsFragment addProductNutritionFactsFragment = new AddProductNutritionFactsFragment();
    private AddProductOverviewFragment addProductOverviewFragment = new AddProductOverviewFragment();
    private AddProductPhotosFragment addProductPhotosFragment = new AddProductPhotosFragment();
    private ActivityAddProductBinding binding;
    @Inject
    OpenFoodAPIService client;
    private boolean editionMode;
    private boolean imageFrontUploaded;
    private boolean imageIngredientsUploaded;
    private boolean imageNutritionFactsUploaded;
    private String[] imagesFilePath = new String[3];
    private Map<String, String> initialValues;
    private OfflineSavedProductDao mOfflineSavedProductDao;
    private Product mProduct;
    private ToUploadProductDao mToUploadProductDao;
    private Bundle mainBundle = new Bundle();
    private Disposable mainDisposable;
    private OfflineSavedProduct offlineSavedProduct;
    private final Map<String, String> productDetails = new HashMap<>();

    public static File getCameraPicLocation(Context context) {
        File cacheDir = context.getCacheDir();
        if (isExternalStorageWritable()) {
            cacheDir = context.getExternalCacheDir();
        }
        File dir = new File(cacheDir, "EasyImage");
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                Log.i(ADD_TAG, "Directory created");
            } else {
                Log.i(ADD_TAG, "Couldn't create directory");
            }
        }
        return dir;
    }

    public static void clearCachedCameraPic(Context context) {
        File[] files = getCameraPicLocation(context).listFiles();
        for (File file : files) {
            if (file.delete()) {
                Log.i(ADD_TAG, "Deleted cached photo");
            } else {
                Log.i(ADD_TAG, "Couldn't delete cached photo");
            }
        }
    }

    private static void updateTimeLine(int stage, View view) {
        switch (stage) {
            case 0:
                view.setBackgroundResource(R.drawable.stage_inactive);
                break;
            case 1:
                view.setBackgroundResource(R.drawable.stage_active);
                break;
            case 2:
                view.setBackgroundResource(R.drawable.stage_complete);
                break;
        }
    }

    public static Map<String, String> buildImageQueryMap(JsonNode jsonNode) {
        String imagefield = jsonNode.get("imagefield").asText();
        String imgid = jsonNode.get("image").get("imgid").asText();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("imgid", imgid);
        queryMap.put("id", imagefield);
        return queryMap;
    }

    private void selectPage(int position) {
        switch (position) {
            case 0:
            default:
                updateTimelineIndicator(1, 0, 0);
                break;
            case 1:
                updateTimelineIndicator(2, 1, 0);
                break;
            case 2:
                updateTimelineIndicator(2, 2, 1);
                break;
        }
    }

    /**
     * This method is used to update the timeline.
     * 0 means inactive stage, 1 means active stage and 2 means completed stage
     *
     * @param overviewStage change the state of overview indicator
     * @param ingredientsStage change the state of ingredients indicator
     * @param nutritionFactsStage change the state of nutrition facts indicator
     */
    private void updateTimelineIndicator(int overviewStage, int ingredientsStage, int nutritionFactsStage) {
        updateTimeLine(overviewStage, binding.overviewIndicator);
        updateTimeLine(ingredientsStage, binding.ingredientsIndicator);
        updateTimeLine(nutritionFactsStage, binding.nutritionFactsIndicator);
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
            .content(R.string.save_product)
            .positiveText(R.string.txtSave)
            .negativeText(R.string.txtPictureNeededDialogNo)
            .onPositive((dialog, which) -> checkFieldsThenSave())
            .onNegative((dialog, which) -> super.onBackPressed())
            .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            new MaterialDialog.Builder(this)
                .content(R.string.save_product)
                .positiveText(R.string.txtSave)
                .negativeText(R.string.txt_discard)
                .onPositive((dialog, which) -> checkFieldsThenSave())
                .onNegative((dialog, which) -> finish())
                .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OFFApplication.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);

        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup onclick listeners
        binding.overviewIndicator.setOnClickListener(v -> switchToOverviewPage());
        binding.ingredientsIndicator.setOnClickListener(v -> switchToIngredientsPage());
        binding.nutritionFactsIndicator.setOnClickListener(v -> switchToNutritionFactsPage());
        binding.viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                selectPage(position);
            }
        });

        setTitle(R.string.offline_product_addition_title);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToUploadProductDao = Utils.getDaoSession().getToUploadProductDao();
        mOfflineSavedProductDao = Utils.getDaoSession().getOfflineSavedProductDao();
        final State state = (State) getIntent().getSerializableExtra("state");
        offlineSavedProduct = (OfflineSavedProduct) getIntent().getSerializableExtra("edit_offline_product");
        Product mEditProduct = (Product) getIntent().getSerializableExtra(KEY_EDIT_PRODUCT);

        if (getIntent().getBooleanExtra("perform_ocr", false)) {
            mainBundle.putBoolean("perform_ocr", true);
        }

        if (getIntent().getBooleanExtra("send_updated", false)) {
            mainBundle.putBoolean("send_updated", true);
        }

        if (state != null) {
            mProduct = state.getProduct();
            // Search if the barcode already exists in the OfflineSavedProducts db
            offlineSavedProduct = OfflineProductService.getOfflineProductByBarcode(mProduct.getCode());
        }
        if (mEditProduct != null) {
            setTitle(R.string.edit_product_title);
            mProduct = mEditProduct;
            editionMode = true;
            mainBundle.putBoolean(KEY_IS_EDITION, true);
            initialValues = new HashMap<>();
        } else if (offlineSavedProduct != null) {
            mainBundle.putSerializable("edit_offline_product", offlineSavedProduct);
            // Save the already existing images in productDetails for UI
            imagesFilePath[0] = offlineSavedProduct.getImageFront();
            imagesFilePath[1] = offlineSavedProduct.getProductDetailsMap().get(ApiFields.Keys.IMAGE_INGREDIENTS);
            imagesFilePath[2] = offlineSavedProduct.getProductDetailsMap().get(ApiFields.Keys.IMAGE_NUTRITION);
            // get the status of images from productDetailsMap, whether uploaded or not
            imageFrontUploaded = "true".equals(offlineSavedProduct.getProductDetailsMap().get(ApiFields.Keys.IMAGE_FRONT_UPLOADED));
            imageIngredientsUploaded = "true".equals(offlineSavedProduct.getProductDetailsMap().get(ApiFields.Keys.IMAGE_INGREDIENTS_UPLOADED));
            imageNutritionFactsUploaded = "true".equals(offlineSavedProduct.getProductDetailsMap().get(ApiFields.Keys.IMAGE_NUTRITION_UPLOADED));
        }
        if (state == null && offlineSavedProduct == null && mEditProduct == null) {
            Toast.makeText(this, R.string.error_adding_product, Toast.LENGTH_SHORT).show();
            finish();
        }
        setupViewPager(binding.viewpager);
    }

    public Map<String, String> getInitialValues() {
        return initialValues;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mainDisposable != null && !mainDisposable.isDisposed()) {
            mainDisposable.dispose();
        }
        clearCachedCameraPic(this);
        binding = null;
    }

    private void setupViewPager(ViewPager viewPager) {
        ProductFragmentPagerAdapter adapterResult = new ProductFragmentPagerAdapter(getSupportFragmentManager());
        mainBundle.putSerializable("product", mProduct);
        addProductOverviewFragment.setArguments(mainBundle);
        addProductIngredientsFragment.setArguments(mainBundle);
        adapterResult.addFragment(addProductOverviewFragment, "Overview");
        adapterResult.addFragment(addProductIngredientsFragment, "Ingredients");
        if (isNutritionDataAvailable()) {
            addProductNutritionFactsFragment.setArguments(mainBundle);
            adapterResult.addFragment(addProductNutritionFactsFragment, "Nutrition Facts");
        } else if (BuildConfig.FLAVOR.equals("obf") || BuildConfig.FLAVOR.equals("opf")) {
            binding.textNutritionFactsIndicator.setText(R.string.photos);
            addProductPhotosFragment.setArguments(mainBundle);
            adapterResult.addFragment(addProductPhotosFragment, "Photos");
        }
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapterResult);
    }

    private void saveProduct() {
        addProductOverviewFragment.addUpdatedFieldsToMap(productDetails);
        addProductIngredientsFragment.addUpdatedFieldsTomap(productDetails);
        if (isNutritionDataAvailable()) {
            addProductNutritionFactsFragment.addUpdatedFieldsToMap(productDetails);
        }
        addLoginInfoInProductDetails(productDetails);
        saveProductOffline();
    }

    private RequestBody createTextPlain(String code) {
        return RequestBody.create(MediaType.parse(OpenFoodAPIClient.MIME_TEXT), code);
    }

    private void addLoginPasswordInfo(Map<String, RequestBody> imgMap) {
        final SharedPreferences settings = getSharedPreferences("login", 0);
        final String login = settings.getString("user", "");
        final String password = settings.getString("pass", "");
        if (!login.isEmpty() && !password.isEmpty()) {
            imgMap.put(ApiFields.Keys.USER_ID, createTextPlain(login));
            imgMap.put(ApiFields.Keys.USER_PWD, createTextPlain(password));
        }
        imgMap.put("comment", createTextPlain(OpenFoodAPIClient.getCommentToUpload(login)));
    }

    /**
     * Save the current product in the offline db
     */
    private void saveProductOffline() {
        // Add the images to the productDetails to display them in UI later.
        productDetails.put(ApiFields.Keys.IMAGE_FRONT, imagesFilePath[0]);
        productDetails.put(ApiFields.Keys.IMAGE_INGREDIENTS, imagesFilePath[1]);
        productDetails.put(ApiFields.Keys.IMAGE_NUTRITION, imagesFilePath[2]);
        // Add the status of images to the productDetails, whether uploaded or not
        if (imageFrontUploaded) {
            productDetails.put(ApiFields.Keys.IMAGE_FRONT_UPLOADED, "true");
        }
        if (imageIngredientsUploaded) {
            productDetails.put(ApiFields.Keys.IMAGE_INGREDIENTS_UPLOADED, "true");
        }
        if (imageNutritionFactsUploaded) {
            productDetails.put(ApiFields.Keys.IMAGE_NUTRITION_UPLOADED, "true");
        }
        OfflineSavedProduct offlineSavedProduct = new OfflineSavedProduct();
        offlineSavedProduct.setBarcode(productDetails.get("code"));
        offlineSavedProduct.setProductDetailsMap(productDetails);
        mOfflineSavedProductDao.insertOrReplace(offlineSavedProduct);

        OfflineProductWorker.scheduleSync();

        OpenFoodAPIClient.addToHistory(Utils.getDaoSession().getHistoryProductDao(), offlineSavedProduct);

        Toast.makeText(this, R.string.productSavedToast, Toast.LENGTH_SHORT)
            .show();

        Utils.hideKeyboard(this);

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        finish();
    }

    public void proceed() {
        switch (binding.viewpager.getCurrentItem()) {
            case 0:
                binding.viewpager.setCurrentItem(1, true);
                break;
            case 1:
                binding.viewpager.setCurrentItem(2, true);
                break;
            case 2:
                checkFieldsThenSave();
                break;
        }
    }

    private void checkFieldsThenSave() {
        if (!editionMode) {
            if (addProductOverviewFragment.areRequiredFieldsEmpty()) {
                binding.viewpager.setCurrentItem(0, true);
            } else if (isNutritionDataAvailable() && addProductNutritionFactsFragment.containsInvalidValue()) {
                binding.viewpager.setCurrentItem(2, true);
            } else {
                saveProduct();
            }
        } else {
            // edit mode, therefore do not check whether front image is empty or not however do check the nutrition facts values.
            if (isNutritionDataAvailable() && addProductNutritionFactsFragment.containsInvalidValue()) {
                // If there are any invalid field and there is nutrition data, scroll to the nutrition fragment
                binding.viewpager.setCurrentItem(2, true);
            } else {
                saveProduct();
            }
        }
    }

    private boolean isNutritionDataAvailable() {
        return BuildConfig.FLAVOR.equals("off") || BuildConfig.FLAVOR.equals("opff");
    }

    private void addLoginInfoInProductDetails(Map<String, String> targetMap) {
        final SharedPreferences settings = getSharedPreferences("login", 0);
        final String login = settings.getString("user", "");
        final String password = settings.getString("pass", "");
        if (!login.isEmpty() && !password.isEmpty()) {
            targetMap.put(ApiFields.Keys.USER_ID, login);
            targetMap.put(ApiFields.Keys.USER_PWD, password);
        }
    }

    void switchToOverviewPage() {
        binding.viewpager.setCurrentItem(0, true);
    }

    void switchToIngredientsPage() {
        binding.viewpager.setCurrentItem(1, true);
    }

    void switchToNutritionFactsPage() {
        binding.viewpager.setCurrentItem(2, true);
    }

    public void addToMap(String key, String value) {
        productDetails.put(key, value);
    }

    public void addToPhotoMap(ProductImage image, int position) {
        String lang = getProductLanguageForEdition();
        boolean ocr = false;
        Map<String, RequestBody> imgMap = new HashMap<>();
        imgMap.put("code", image.getCode());
        RequestBody imageField = createTextPlain(image.getImageField().toString() + '_' + lang);
        imgMap.put("imagefield", imageField);
        if (image.getImguploadFront() != null) {
            imagesFilePath[0] = image.getFilePath();
            imgMap.put("imgupload_front\"; filename=\"front_" + lang + ".png\"", image.getImguploadFront());
        }
        if (image.getImguploadIngredients() != null) {
            imgMap.put("imgupload_ingredients\"; filename=\"ingredients_" + lang + ".png\"", image.getImguploadIngredients());
            ocr = true;
            imagesFilePath[1] = image.getFilePath();
        }
        if (image.getImguploadNutrition() != null) {
            imgMap.put("imgupload_nutrition\"; filename=\"nutrition_" + lang + ".png\"", image.getImguploadNutrition());
            imagesFilePath[2] = image.getFilePath();
        }
        if (image.getImguploadOther() != null) {
            imgMap.put("imgupload_other\"; filename=\"other_" + lang + ".png\"", image.getImguploadOther());
        }
        // Attribute the upload to the connected user
        addLoginPasswordInfo(imgMap);
        savePhoto(imgMap, image, position, ocr);
    }

    private void savePhoto(Map<String, RequestBody> imgMap, ProductImage image, int position, boolean ocr) {
        client.saveImageSingle(imgMap)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(disposable -> showImageProgress(position))
            .subscribe(new SingleObserver<JsonNode>() {
                @Override
                public void onSubscribe(Disposable d) {
                    mainDisposable = d;
                }

                @Override
                public void onSuccess(JsonNode jsonNode) {
                    String status = jsonNode.get("status").asText();
                    if (status.equals("status not ok")) {
                        String error = jsonNode.get("error").asText();
                        final boolean alreadySent = error.equals("This picture has already been sent.");
                        if (alreadySent && ocr) {
                            hideImageProgress(position, false, getString(R.string.image_uploaded_successfully));
                            performOCR(image.getBarcode(), "ingredients_" + getProductLanguageForEdition());
                        } else {
                            hideImageProgress(position, true, error);
                        }
                    } else {
                        if (image.getImageField() == ProductImageField.FRONT) {
                            imageFrontUploaded = true;
                        } else if (image.getImageField() == ProductImageField.INGREDIENTS) {
                            imageIngredientsUploaded = true;
                        } else if (image.getImageField() == ProductImageField.NUTRITION) {
                            imageNutritionFactsUploaded = true;
                        }
                        hideImageProgress(position, false, getString(R.string.image_uploaded_successfully));
                        String imagefield = jsonNode.get("imagefield").asText();
                        String imgid = jsonNode.get("image").get("imgid").asText();
                        if (position != 3 && position != 4) {
                            // Not OTHER image
                            setPhoto(image, imagefield, imgid, ocr);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    // A network error happened
                    if (e instanceof IOException) {
                        hideImageProgress(position, false, getString(R.string.no_internet_connection));
                        Log.e(ADD_TAG, e.getMessage());
                        if (image.getImageField() == ProductImageField.OTHER) {
                            ToUploadProduct product = new ToUploadProduct(image.getBarcode(), image.getFilePath(), image.getImageField().toString());
                            mToUploadProductDao.insertOrReplace(product);
                        }
                    } else {
                        hideImageProgress(position, true, e.getMessage());
                        Log.i(this.getClass().getSimpleName(), e.getMessage());
                        Toast.makeText(OFFApplication.getInstance(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void setPhoto(ProductImage image, String imagefield, String imgid, boolean ocr) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("imgid", imgid);
        queryMap.put("id", imagefield);
        client.editImageSingle(image.getBarcode(), queryMap)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new SingleObserver<JsonNode>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(JsonNode jsonNode) {
                    String status = jsonNode.get("status").asText();
                    if (ocr && status.equals("status ok")) {
                        performOCR(image.getBarcode(), imagefield);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if (e instanceof IOException) {
                        if (ocr) {
                            View view = findViewById(R.id.coordinator_layout);
                            Snackbar.make(view, R.string.no_internet_unable_to_extract_ingredients, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.txt_try_again, v -> setPhoto(image, imagefield, imgid, true)).show();
                        }
                    } else {
                        Log.i(this.getClass().getSimpleName(), e.getMessage());
                        Toast.makeText(OFFApplication.getInstance(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    public void performOCR(String code, String imageField) {
        client.getIngredients(code, imageField)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(disposable -> addProductIngredientsFragment.showOCRProgress())
            .subscribe(new SingleObserver<JsonNode>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(JsonNode jsonNode) {
                    addProductIngredientsFragment.hideOCRProgress();
                    String status = jsonNode.get("status").toString();
                    if (status.equals("0")) {
                        String ocrResult = jsonNode.get("ingredients_text_from_image").asText();
                        addProductIngredientsFragment.setIngredients(status, ocrResult);
                    } else {
                        addProductIngredientsFragment.setIngredients(status, null);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    addProductIngredientsFragment.hideOCRProgress();
                    if (e instanceof IOException) {
                        View view = findViewById(R.id.coordinator_layout);
                        Snackbar.make(view, R.string.no_internet_unable_to_extract_ingredients, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.txt_try_again, v -> performOCR(code, imageField)).show();
                    } else {
                        Log.i(this.getClass().getSimpleName(), e.getMessage(), e);
                        Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void hideImageProgress(int position, boolean errorUploading, String message) {
        switch (position) {
            case 0:
                addProductOverviewFragment.hideImageProgress(errorUploading, message);
                break;
            case 1:
                addProductIngredientsFragment.hideImageProgress(errorUploading, message);
                break;
            case 2:
                addProductNutritionFactsFragment.hideImageProgress(errorUploading, message);
                break;
            case 3:
                addProductOverviewFragment.hideOtherImageProgress(errorUploading, message);
                break;
            case 4:
                addProductPhotosFragment.hideImageProgress(errorUploading, message);
        }
    }

    private void showImageProgress(int position) {
        switch (position) {
            case 0:
                addProductOverviewFragment.showImageProgress();
                break;
            case 1:
                addProductIngredientsFragment.showImageProgress();
                break;
            case 2:
                addProductNutritionFactsFragment.showImageProgress();
                break;
            case 3:
                addProductOverviewFragment.showOtherImageProgress();
                break;
            case 4:
                addProductPhotosFragment.showImageProgress();
        }
    }

    public String getProductLanguageForEdition() {
        return productDetails.get(ApiFields.Keys.LANG);
    }

    public void setProductLanguage(String languageCode) {
        addToMap(ApiFields.Keys.LANG, languageCode);
    }

    public void updateLanguage() {
        addProductIngredientsFragment.loadIngredientsImage();
        addProductNutritionFactsFragment.loadNutritionImage();
    }

    public void setIngredients(String status, String ingredients) {
        addProductIngredientsFragment.setIngredients(status, ingredients);
    }
}
