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

package openfoodfacts.github.scrachx.openfood.network.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import openfoodfacts.github.scrachx.openfood.BuildConfig;
import openfoodfacts.github.scrachx.openfood.models.Search;
import openfoodfacts.github.scrachx.openfood.models.State;
import openfoodfacts.github.scrachx.openfood.models.TaglineLanguageModel;
import openfoodfacts.github.scrachx.openfood.network.ApiFields;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Define our Open Food Facts API endpoints.
 * All REST methods such as GET, POST, PUT, UPDATE, DELETE can be stated in here.
 */
public interface ProductsAPI {
    @GET("api/v0/product/{barcode}.json")
    Call<State> getProductByBarcode(@Path("barcode") String barcode,
                                    @Query("fields") String fields,
                                    @Header("User-Agent") String header);

    @GET("api/v0/product/{barcode}.json")
    Single<State> getProductByBarcodeSingle(@Path("barcode") String barcode,
                                            @Query("fields") String fields,
                                            @Header("User-Agent") String header);

    @FormUrlEncoded
    @POST("cgi/product_jqm2.pl")
    Single<State> saveProductSingle(@Field(ApiFields.Keys.BARCODE) String code,
                                    @FieldMap Map<String, String> parameters,
                                    @Field(ApiFields.Keys.USER_COMMENT) String comment);

    @GET("api/v0/product/{barcode}.json?fields=image_small_url,product_name,brands,quantity,image_url,nutrition_grade_fr,code")
    Call<State> getShortProductByBarcode(@Path("barcode") String barcode,
                                         @Header("User-Agent") String header);

    @GET("cgi/search.pl?search_simple=1&json=1&action=process")
    Single<Search> searchProductByName(@Query("fields") String fields,
                                       @Query("search_terms") String name,
                                       @Query("page") int page);

    @FormUrlEncoded
    @POST("/cgi/session.pl")
    Single<Response<ResponseBody>> signIn(@Field(ApiFields.Keys.USER_ID) String login,
                                          @Field(ApiFields.Keys.USER_PASS) String password,
                                          @Field(".submit") String submit);

    @GET("api/v0/product/{barcode}.json?fields=ingredients")
    Single<JsonNode> getIngredientsByBarcode(@Path("barcode") String barcode);

    @Multipart
    @POST("/cgi/product_image_upload.pl")
    Call<JsonNode> saveImage(@PartMap Map<String, RequestBody> fields);

    @Multipart
    @POST("/cgi/product_image_upload.pl")
    Single<JsonNode> saveImageSingle(@PartMap Map<String, RequestBody> fields);

    @GET("/cgi/product_image_crop.pl")
    Single<JsonNode> editImageSingle(@Query(ApiFields.Keys.BARCODE) String code,
                                     @QueryMap Map<String, String> fields);

    @GET("/cgi/ingredients.pl?process_image=1&ocr_engine=google_cloud_vision")
    Single<JsonNode> getIngredients(@Query(ApiFields.Keys.BARCODE) String code,
                                    @Query("id") String id);

    @GET("cgi/suggest.pl?tagtype=emb_codes")
    Single<ArrayList<String>> getEMBCodeSuggestions(@Query("term") String term);

    @GET("/cgi/suggest.pl?tagtype=periods_after_opening")
    Single<ArrayList<String>> getPeriodAfterOpeningSuggestions(@Query("term") String term);

    @GET("brand/{brand}/{page}.json")
    Single<Search> getProductByBrands(@Path("brand") String brand,
                                      @Path("page") int page,
                                      @Query("fields") String fields);

    @GET("brand/{brand}/{page}.json")
    Single<Search> getProductByBrandsSingle(@Path("brand") String brand,
                                            @Path("page") int page,
                                            @Query("fields") String fields);

    @GET("additive/{additive}/{page}.json")
    Call<Search> getProductsByAdditive(@Path("additive") String additive,
                                       @Path("page") int page,
                                       @Query("fields") String fields);

    @GET("allergen/{allergen}/{page}.json")
    Call<Search> getProductsByAllergen(@Path("allergen") String allergen,
                                       @Path("page") int page,
                                       @Query("fields") String fields);

    @GET("country/{country}/{page}.json")
    Call<Search> getProductsByCountry(@Path("country") String country,
                                      @Path("page") int page,
                                      @Query("fields") String fields);

    @GET("origin/{origin}/{page}.json")
    Call<Search> getProductsByOrigin(@Path("origin") String origin,
                                     @Path("page") int page,
                                     @Query("fields") String fields);

    @GET("manufacturing-place/{manufacturing-place}/{page}.json")
    Call<Search> getProductsByManufacturingPlace(@Path("manufacturing-place") String manufacturingPlace,
                                                 @Path("page") int page,
                                                 @Query("fields") String fields);

    @GET("store/{store}/{page}.json")
    Call<Search> getProductByStores(@Path("store") String store,
                                    @Path("page") int page,
                                    @Query("fields") String fields);

    @GET("packaging/{packaging}/{page}.json")
    Call<Search> getProductByPackaging(@Path("packaging") String packaging,
                                       @Path("page") int page,
                                       @Query("fields") String fields);

    @GET("label/{label}/{page}.json")
    Call<Search> getProductByLabel(@Path("label") String label,
                                   @Path("page") int page,
                                   @Query("fields") String fields);

    @GET("category/{category}/{page}.json?fields=product_name,brands,quantity,image_small_url,nutrition_grade_fr,code")
    Call<Search> getProductByCategory(@Path("category") String category,
                                      @Path("page") int page);

    @GET("contributor/{Contributor}/{page}.json?nocache=1")
    Call<Search> searchProductsByContributor(@Path("Contributor") String contributor,
                                             @Path("page") int page);

    @GET("language/{language}.json")
    Call<Search> byLanguage(@Path("language") String language);

    @GET("label/{label}.json")
    Call<Search> byLabel(@Path("label") String label);

    @GET("category/{category}.json")
    Call<Search> byCategory(@Path("category") String category);

    @GET("state/{state}.json")
    Call<Search> byState(@Path("state") String state, @Query("fields") String fields);

    @GET("packaging/{packaging}.json")
    Call<Search> byPackaging(@Path("packaging") String packaging);

    @GET("brand/{brand}.json")
    Call<Search> byBrand(@Path("brand") String brand);

    @GET("purchase-place/{purchasePlace}.json")
    Call<Search> byPurchasePlace(@Path("purchasePlace") String purchasePlace);

    @GET("store/{store}.json")
    Call<Search> byStore(@Path("store") String store);

    @GET("country/{country}.json")
    Call<Search> byCountry(@Path("country") String country);

    @GET("trace/{trace}.json")
    Call<Search> byTrace(@Path("trace") String trace);

    @GET("packager-code/{PackagerCode}.json")
    Call<Search> byPackagerCode(@Path("PackagerCode") String packagerCode);

    @GET("city/{City}.json")
    Call<Search> byCity(@Path("City") String city);

    @GET("nutrition-grade/{NutritionGrade}.json")
    Call<Search> byNutritionGrade(@Path("NutritionGrade") String nutritionGrade);

    @GET("nutrient-level/{NutrientLevel}.json")
    Single<Search> byNutrientLevel(@Path("NutrientLevel") String nutrientLevel);

    @GET("contributor/{Contributor}.json?nocache=1")
    Single<Search> byContributor(@Path("Contributor") String contributor);

    @GET("contributor/{Contributor}/state/to-be-completed/{page}.json?nocache=1")
    Call<Search> getToBeCompletedProductsByContributor(@Path("Contributor") String contributor, @Path("page") int page);

    @GET("/photographer/{Contributor}/{page}.json?nocache=1")
    Call<Search> getPicturesContributedProducts(@Path("Contributor") String contributor, @Path("page") int page);

    @GET("photographer/{Photographer}.json?nocache=1")
    Single<Search> byPhotographer(@Path("Photographer") String photographer);

    @GET("photographer/{Contributor}/state/to-be-completed/{page}.json?nocache=1")
    Call<Search> getPicturesContributedIncompleteProducts(@Path("Contributor") String contributor,
                                                          @Path("page") int page);

    @GET("informer/{Informer}.json?nocache=1")
    Single<Search> byInformer(@Path("Informer") String informer);

    @GET("informer/{Contributor}/{page}.json?nocache=1")
    Call<Search> getInfoAddedProducts(@Path("Contributor") String contributor, @Path("page") int page);

    @GET("informer/{Contributor}/state/to-be-completed/{page}.json?nocache=1")
    Single<Search> getInfoAddedIncompleteProductsSingle(@Path("Contributor") String contributor, @Path("page") int page);

    @GET("last-edit-date/{LastEditDate}.json")
    Single<Search> byLastEditDate(@Path("LastEditDate") String lastEditDate);

    @GET("entry-dates/{EntryDates}.json")
    Single<Search> byEntryDates(@Path("EntryDates") String entryDates);

    @GET("unknown-nutrient/{UnknownNutrient}.json")
    Single<Search> byUnknownNutrient(@Path("UnknownNutrient") String unknownNutrient);

    @GET("additive/{Additive}.json")
    Call<Search> byAdditive(@Path("Additive") String additive, @Query("fields") String fields);

    @GET("code/{Code}.json")
    Single<Search> byCode(@Path("Code") String code);

    @GET("state/{State}/{page}.json")
    Single<Search> getProductsByState(@Path("State") String state,
                                      @Path("page") int page,
                                      @Query("fields") String fields);

    /**
     * Open Beauty Facts experimental and specific APIs
     */
    @GET("period-after-opening/{PeriodAfterOpening}.json")
    Call<Search> byPeriodAfterOpening(@Path("PeriodAfterOpening") String periodAfterOpening);

    @GET("ingredient/{ingredient}.json")
    Call<Search> byIngredient(@Path("ingredient") String ingredient);

    /**
     * This method gives a list of incomplete products
     */
    @GET("state/to-be-completed/{page}.json?nocache=1")
    Single<Search> getIncompleteProducts(@Path("page") int page, @Query("fields") String fields);

    /**
     * This method gives the # of products on Open Food Facts
     */
    @GET("/1.json?fields=null")
    Single<Search> getTotalProductCount(@Header("User-Agent") String header);

    /**
     * This method gives the news in all languages
     */
    @GET("/files/tagline/tagline-" + BuildConfig.FLAVOR + ".json")
    Single<ArrayList<TaglineLanguageModel>> getTagline(@Header("User-Agent") String header);

    /**
     * Returns images for the current product
     *
     * @param barcode barcode for the current product
     */
    @GET("api/v0/product/{barcode}.json?fields=images")
    Single<ObjectNode> getProductImages(@Path("barcode") String barcode);

    /**
     * This method is to crop images server side
     */
    @GET("/cgi/product_image_crop.pl")
    Call<String> editImages(@Query(ApiFields.Keys.BARCODE) String code,
                            @QueryMap Map<String, String> fields);

    @GET("/cgi/product_image_unselect.pl")
    Call<String> unSelectImage(@Query(ApiFields.Keys.BARCODE) String code,
                               @QueryMap Map<String, String> fields);

    @GET
    Single<ResponseBody> downloadFile(@Url String fileUrl);
}
