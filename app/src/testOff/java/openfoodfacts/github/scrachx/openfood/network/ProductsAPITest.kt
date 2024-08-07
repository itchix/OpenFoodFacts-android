package openfoodfacts.github.scrachx.openfood.network

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import openfoodfacts.github.scrachx.openfood.BuildConfig
import openfoodfacts.github.scrachx.openfood.models.Search
import openfoodfacts.github.scrachx.openfood.models.entities.SendProduct
import openfoodfacts.github.scrachx.openfood.network.ProductsAPITest.SearchSubject.Companion.assertThat
import openfoodfacts.github.scrachx.openfood.network.services.ProductsAPI
import openfoodfacts.github.scrachx.openfood.utils.getUserAgent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.create
import java.time.Duration

class ProductsAPITest {
    @Test
    fun `products by language found`() = runBlocking {
        val search = prodClient.getProductsByLanguage("italian")
        assertThat(search).hasFoundProducts()
    }

    @Test
    fun `products by label found`() = runBlocking {
        val search = prodClient.getProductsByLabel("utz-certified")
        assertThat(search).hasFoundProducts()
    }

    @Test
    fun `products by category found`() = runBlocking {
        val search = prodClient.getProductsByCategory("baby-foods")
        assertThat(search).hasFoundProducts()
    }

    @Test
    fun `products by state found`() = runBlocking {
        val fieldsToFetchFacets = "brands,product_name,image_small_url,quantity,nutrition_grades_tags"
        val search = prodClient.getProductsByState("complete", fieldsToFetchFacets)
        assertThat(search).hasFoundProducts()
    }

    @Test
    fun `products by packaging found`() = runBlocking {
        val search = prodClient.getProductsByPackaging("cardboard")
        assertThat(search).hasFoundProducts()
    }

    @Test
    fun `products by brand found`() = runBlocking {
        val search = prodClient.getProductsByBrand("monoprix")
        assertThat(search).hasFoundProducts()
    }

    @Test
    fun `products by purchase place found`() = runBlocking {
        val search = prodClient.getProductsByPurchasePlace("marseille-5")
        assertThat(search).hasFoundProducts()
    }

    @Test
    fun `products by store found`() = runBlocking {
        val search = prodClient.getProductsByStore("super-u")
        assertThat(search).isNotNull()
        assertThat(search.products).isNotNull()
    }

    @Test
    fun `products by country found`() = runBlocking {
        val search = prodClient.byCountry("france")
        assertThat(search).hasFoundProducts()
    }

    @Test
    fun `products by ingredients found`() = runBlocking {
        val search = prodClient.getProductsByIngredient("sucre")
        assertThat(search).hasFoundProducts()
    }

    @Test
    fun `products by trace found`() = runBlocking {
        val search = prodClient.getProductsByTrace("eggs")
        assertThat(search).hasFoundProducts()
    }

    @Test
    fun `products by trace eggs found`() = runBlocking {
        val response = prodClient.getProductsByTrace("eggs")
        assertThat(response).hasFoundProducts()
    }

    @Test
    fun `product by packager code emb35069c found`() = runBlocking {
        val search = prodClient.getProductsByPackagerCode("emb-35069c")
        assertThat(search).hasFoundProducts()
    }

    @Test
    fun `product by nutriscore a found`() = runBlocking {
        val search = prodClient.getProductsByNutriScore("a")
        assertThat(search).hasFoundProducts()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `products by city paris not found`() = runBlocking {
        val exception = assertThrows<Exception> { prodClient.getProductsByCity("paris") }
        Assertions.assertEquals("HTTP 404", exception.message?.trim())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun productByAdditive_e301_productsFound() = runBlocking {
        val fieldsToFetchFacets = "brands,product_name,image_small_url,quantity,nutrition_grades_tags"
        val response = prodClient.getProductsByAdditive("e301-sodium-ascorbate", fieldsToFetchFacets)
        assertThat(response).hasFoundProducts()
    }

    @Test
    fun `product not found`() = runBlocking {
        val barcode = "457457457457"
        val state = prodClient.getProductByBarcode(
            barcode,
            "code",
            LC,
            getUserAgent(ApiFields.UserAgents.SEARCH)
        )
        assertThat(state.status).isEqualTo(0)
        assertThat(state.statusVerbose).isEqualTo("product not found")
        assertThat(state.code).isEqualTo(barcode)
    }

    @Test
    fun `post product`() = runBlocking {

        val product = SendProduct().apply {
            barcode = "1234567890"
            name = "test-product-name"
            brands = "test-product-brand"
            weight = "123"
            weightUnit = "g"
            lang = LC
        }

        val productDetails = mapOf<String?, String?>(
            "lang" to product.lang,
            "product_name" to product.name,
            "brands" to product.brands,
            "quantity" to product.quantity
        )


        val body = devClientWithAuth.saveProduct(product.barcode, productDetails, "Automated test")

        assertThat(body.status).isEqualTo(1)
        assertThat(body.statusVerbose).isEqualTo("fields saved")

        val fields = "product_name,brands,brands_tags,quantity"
        val response = devClientWithAuth.getProductByBarcode(
            product.barcode,
            fields,
            LC,
            getUserAgent(ApiFields.UserAgents.SEARCH)
        )

        assertThat(response.product).isNotNull()
        val savedProduct = response.product!!

        assertThat(savedProduct.productName).isEqualTo(product.name)
        assertThat(savedProduct.brands).isEqualTo(product.brands)
        assertThat(savedProduct.brandsTags).contains(product.brands)
        assertThat(savedProduct.quantity).isEqualTo("${product.weight} ${product.weightUnit}")
    }

    companion object {
        const val LC = "en"

        /**
         * We need to use auth because we use world.openfoodfacts.dev
         */
        private lateinit var devClientWithAuth: ProductsAPI
        private lateinit var prodClient: ProductsAPI

        @BeforeAll
        @JvmStatic
        fun setupClient() {
            val httpClientWithAuth = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(Duration.ZERO)
                .readTimeout(Duration.ZERO)
                .addInterceptor {
                    val origReq = it.request()
                    it.proceed(
                        origReq.newBuilder()
                            .header("Authorization", Credentials.basic("off", "off"))
                            .header("Accept", "application/json")
                            .method(origReq.method(), origReq.body())
                            .build()
                    )
                }
                .build()

            prodClient = defaultBuilder()
                .baseUrl(BuildConfig.HOST)
                .build()
                .create()

            devClientWithAuth = defaultBuilder()
                .baseUrl(BuildConfig.TESTING_HOST)
                .client(httpClientWithAuth)
                .build()
                .create()
        }

        private fun defaultBuilder() = Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))

    }

    class SearchSubject private constructor(
        failureMetadata: FailureMetadata,
        private val actual: Search,
    ) : Subject(failureMetadata, actual) {

        fun hasFoundProducts() {
            check("count").that(actual.count.toInt()).isGreaterThan(0)
            check("products").that(actual.products).isNotEmpty()
        }

        fun hasFoundNoProducts() {
            check("count").that(actual.count.toInt()).isEqualTo(0)
            check("products").that(actual.products).isEmpty()
        }

        companion object {
            private fun searches() = Factory(::SearchSubject)

            fun assertThat(actual: Search): SearchSubject {
                return assertAbout(searches()).that(actual)
            }
        }
    }
}
