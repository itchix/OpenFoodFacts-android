package openfoodfacts.github.scrachx.openfood.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import java.io.File;

import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.databinding.FragmentAddProductPhotosBinding;
import openfoodfacts.github.scrachx.openfood.images.PhotoReceiver;
import openfoodfacts.github.scrachx.openfood.images.ProductImage;
import openfoodfacts.github.scrachx.openfood.jobs.PhotoReceiverHandler;
import openfoodfacts.github.scrachx.openfood.models.OfflineSavedProduct;
import openfoodfacts.github.scrachx.openfood.models.Product;
import openfoodfacts.github.scrachx.openfood.views.AddProductActivity;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.Manifest.permission.CAMERA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static openfoodfacts.github.scrachx.openfood.models.ProductImageField.OTHER;
import static openfoodfacts.github.scrachx.openfood.utils.Utils.MY_PERMISSIONS_REQUEST_CAMERA;

/**
 * Fragment for adding photos of the product
 *
 * @see R.layout#fragment_add_product_photos
 */
public class AddProductPhotosFragment extends BaseFragment implements PhotoReceiver {
    private FragmentAddProductPhotosBinding binding;
    private PhotoReceiverHandler photoReceiverHandler;
    private String code;
    private Activity activity;
    private File photoFile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddProductPhotosBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnAddOtherImage.setOnClickListener(v -> addOtherImage());
        binding.btnAdd.setOnClickListener(v -> next());


        photoReceiverHandler = new PhotoReceiverHandler(this);
        Bundle b = getArguments();
        if (b != null) {
            Product product = (Product) b.getSerializable("product");
            OfflineSavedProduct offlineSavedProduct = (OfflineSavedProduct) b.getSerializable("edit_offline_product");
            boolean editionMode = b.getBoolean(AddProductActivity.KEY_IS_EDITION);
            if (product != null) {
                code = product.getCode();
            }
            if (editionMode && product != null) {
                binding.btnAdd.setText(R.string.save_edits);
            } else if (offlineSavedProduct != null) {
                code = offlineSavedProduct.getBarcode();
            }
        } else {
            Toast.makeText(activity, R.string.error_adding_product_photos, Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    private void addOtherImage() {
        if (ContextCompat.checkSelfPermission(activity, CAMERA) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            EasyImage.openCamera(this, 0);
        }
    }

    private void next() {
        Activity fragmentActivity = getActivity();
        if (fragmentActivity instanceof AddProductActivity) {
            ((AddProductActivity) fragmentActivity).proceed();
        }
    }

    @Override
    public void onPhotoReturned(File newPhotoFile) {
        photoFile = newPhotoFile;
        ProductImage image = new ProductImage(code, OTHER, photoFile);
        image.setFilePath(photoFile.toURI().getPath());
        if (activity instanceof AddProductActivity) {
            ((AddProductActivity) activity).addToPhotoMap(image, 4);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoReceiverHandler.onActivityResult(this, requestCode, resultCode, data);
    }

    public void showImageProgress() {
        binding.imageProgress.setVisibility(View.VISIBLE);
        binding.imageProgressText.setVisibility(View.VISIBLE);
        binding.imageProgressText.setText(R.string.toastSending);
        addImageRow();
    }

    public void hideImageProgress(boolean errorUploading, String message) {
        binding.imageProgress.setVisibility(View.GONE);
        binding.btnAddOtherImage.setVisibility(View.VISIBLE);
        if (errorUploading) {
            binding.imageProgressText.setVisibility(View.GONE);
        } else {
            binding.imageProgressText.setText(R.string.image_uploaded_successfully);
        }
    }

    /**
     * Load image into the image view and add it to tableLayout
     */
    private void addImageRow() {
        TableRow image = new TableRow(activity);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpsToPixels(100));
        lp.topMargin = dpsToPixels(10);
        ImageView imageView = new ImageView(activity);
        Picasso.get()
            .load(photoFile)
            .resize(dpsToPixels(100), dpsToPixels(100))
            .centerInside()
            .into(imageView);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(lp);
        image.addView(imageView);
        binding.tableLayout.addView(image);
    }
}
