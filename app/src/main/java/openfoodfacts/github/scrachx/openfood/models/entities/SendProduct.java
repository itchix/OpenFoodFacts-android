package openfoodfacts.github.scrachx.openfood.models.entities;

<<<<<<< HEAD
import androidx.annotation.NonNull;

=======
>>>>>>> b73375553e6727f1ebb0cf7dd743c16efdcd16ac
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

import openfoodfacts.github.scrachx.openfood.models.ProductImageField;
import openfoodfacts.github.scrachx.openfood.network.ApiFields;
import openfoodfacts.github.scrachx.openfood.utils.Utils;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
<<<<<<< HEAD
@Entity(indexes = {@Index(value = "barcode", unique = true)})
public class SendProduct implements Serializable {
=======
@Entity(indexes = {
    @Index(value = "barcode", unique = true)
})
public class
SendProduct implements Serializable {
>>>>>>> b73375553e6727f1ebb0cf7dd743c16efdcd16ac
    private static final long serialVersionUID = 2L;
    @Id
    private Long id;
    @JsonProperty(ApiFields.Keys.BARCODE)
    private String barcode;
    private String lang;
    @JsonProperty(ApiFields.Keys.PRODUCT_NAME)
    private String name;
    private String brands;
    @JsonIgnore
    private String weight;
    @JsonIgnore
    private String weight_unit = "g";
    @JsonIgnore
    private String imgupload_front;
    @JsonIgnore
    private String imgupload_ingredients;
    @JsonIgnore
    private String imgupload_nutrition;
<<<<<<< HEAD
    @JsonIgnore
    private String imgupload_packaging;
=======
>>>>>>> b73375553e6727f1ebb0cf7dd743c16efdcd16ac
    @JsonProperty(ApiFields.Keys.USER_ID)
    @Transient
    private String userId;
    @Transient
    private String password;

    public SendProduct() {
    }

<<<<<<< HEAD
    @Generated(hash = 88998839)
    public SendProduct(Long id, String barcode, String lang, String name, String brands, String weight, String weight_unit, String imgupload_front, String imgupload_ingredients,
                       String imgupload_nutrition, String imgupload_packaging) {
=======
    @Generated(hash = 994048396)
    public SendProduct(Long id, String barcode, String lang, String name, String brands, String weight, String weight_unit, String imgupload_front, String imgupload_ingredients, String imgupload_nutrition) {
>>>>>>> b73375553e6727f1ebb0cf7dd743c16efdcd16ac
        this.id = id;
        this.barcode = barcode;
        this.lang = lang;
        this.name = name;
        this.brands = brands;
        this.weight = weight;
        this.weight_unit = weight_unit;
        this.imgupload_front = imgupload_front;
        this.imgupload_ingredients = imgupload_ingredients;
        this.imgupload_nutrition = imgupload_nutrition;
<<<<<<< HEAD
        this.imgupload_packaging = imgupload_packaging;
=======
>>>>>>> b73375553e6727f1ebb0cf7dd743c16efdcd16ac
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWeight_unit() {
        return weight_unit;
    }

    public void setWeight_unit(String weight_unit) {
        this.weight_unit = weight_unit;
    }

    public String getQuantity() {
        if (weight == null || weight.length() == 0) {
            return null;
        }

        return this.weight + " " + this.weight_unit;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImgupload_front() {
        return imgupload_front;
    }

    public void setImgupload_front(String imgupload_front) {
        this.imgupload_front = imgupload_front;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getImgupload_ingredients() {
        return imgupload_ingredients;
    }

    public void setImgupload_ingredients(String imgupload_ingredients) {
        this.imgupload_ingredients = imgupload_ingredients;
    }

    public String getImgupload_nutrition() {
        return imgupload_nutrition;
    }

    public void setImgupload_nutrition(String imgupload_nutrition) {
        this.imgupload_nutrition = imgupload_nutrition;
    }

<<<<<<< HEAD
    public String getImgupload_packaging() {
        return imgupload_packaging;
    }

    public void setImgupload_packaging(String imgupload_packaging) {
        this.imgupload_packaging = imgupload_packaging;
    }

=======
>>>>>>> b73375553e6727f1ebb0cf7dd743c16efdcd16ac
    /**
     * Compress the image according to the {@link ProductImageField}.
     * Add a "_small" prefix in the image name after the compression
     *
<<<<<<< HEAD
     */
    public void compress(@NonNull ProductImageField field) {
=======
     * @param field
     */
    public void compress(ProductImageField field) {
>>>>>>> b73375553e6727f1ebb0cf7dd743c16efdcd16ac
        switch (field) {
            case NUTRITION:
                this.imgupload_nutrition = Utils.compressImage(this.imgupload_nutrition);
                break;
            case INGREDIENTS:
                this.imgupload_ingredients = Utils.compressImage(this.imgupload_ingredients);
                break;
<<<<<<< HEAD
            case PACKAGING:
                this.imgupload_packaging = Utils.compressImage(this.imgupload_packaging);
                break;
=======
>>>>>>> b73375553e6727f1ebb0cf7dd743c16efdcd16ac
            case FRONT:
                this.imgupload_front = Utils.compressImage(this.imgupload_front);
                break;
            default:
                //nothing to do
                break;
        }
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

<<<<<<< HEAD
    public void copy(@NonNull SendProduct sp) {
=======
    public void copy(SendProduct sp) {
>>>>>>> b73375553e6727f1ebb0cf7dd743c16efdcd16ac
        this.barcode = sp.getBarcode();
        this.name = sp.getName();
        this.brands = sp.getBrands();
        this.weight = sp.getWeight();
        this.weight_unit = sp.getWeight_unit();
        this.imgupload_front = sp.getImgupload_front();
        this.imgupload_ingredients = sp.getImgupload_ingredients();
        this.imgupload_nutrition = sp.getImgupload_nutrition();
<<<<<<< HEAD
        this.imgupload_packaging = sp.getImgupload_packaging();
        this.lang = sp.getLang();
    }

    public boolean isEqual(@NonNull SendProduct sp) {
        return (equalityOfString(this.barcode, sp.getBarcode()) && equalityOfString(this.name, sp.getName()) && equalityOfString(this.brands, sp
            .getBrands()) && equalityOfString(this.weight, sp.getWeight()) && equalityOfString(this.weight_unit, sp.getWeight_unit()) &&
            equalityOfString(this.imgupload_front, sp.getImgupload_front()) && equalityOfString(this.imgupload_nutrition, sp
            .getImgupload_nutrition()) && equalityOfString(this.imgupload_packaging, sp.getImgupload_packaging()) &&
            equalityOfString(this.imgupload_ingredients, sp.getImgupload_ingredients()));
=======
        this.lang = sp.getLang();
    }

    public boolean isEqual(SendProduct sp) {
        return (equalityOfString(this.barcode, sp.getBarcode()) && equalityOfString(this.name, sp.getName()) && equalityOfString(this.brands, sp
                .getBrands()) && equalityOfString(this.weight, sp.getWeight()) && equalityOfString(this.weight_unit, sp.getWeight_unit()) &&
                equalityOfString(this.imgupload_front, sp.getImgupload_front()) && equalityOfString(this.imgupload_nutrition, sp
                .getImgupload_nutrition
                ()) && equalityOfString(this.imgupload_ingredients, sp.getImgupload_ingredients()));
>>>>>>> b73375553e6727f1ebb0cf7dd743c16efdcd16ac
    }

    private boolean equalityOfString(String a, String b) {
        if (a != null) {
            return a.equals(b);
        } else {
            return b == null;
        }
    }
}