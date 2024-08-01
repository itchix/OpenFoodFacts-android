package openfoodfacts.github.scrachx.openfood.features.compare

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import openfoodfacts.github.scrachx.openfood.analytics.AnalyticsEvent
import openfoodfacts.github.scrachx.openfood.analytics.MatomoAnalytics
import openfoodfacts.github.scrachx.openfood.features.compare.ProductCompareViewModel.CompareProduct
import openfoodfacts.github.scrachx.openfood.features.compare.ProductCompareViewModel.SideEffect
import openfoodfacts.github.scrachx.openfood.models.Barcode
import openfoodfacts.github.scrachx.openfood.models.Product
import openfoodfacts.github.scrachx.openfood.models.entities.additive.AdditiveName
import openfoodfacts.github.scrachx.openfood.repositories.ProductRepository
import openfoodfacts.github.scrachx.openfood.repositories.TaxonomiesRepository
import openfoodfacts.github.scrachx.openfood.utils.CoroutineDispatchersTest
import openfoodfacts.github.scrachx.openfood.utils.InstantTaskExecutorExtension
import openfoodfacts.github.scrachx.openfood.utils.LocaleManager
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(
    InstantTaskExecutorExtension::class,
    MockKExtension::class
)
@ExperimentalCoroutinesApi
class ProductCompareViewModelTest {

    @MockK
    lateinit var taxonomiesRepository: TaxonomiesRepository

    @MockK
    lateinit var localeManager: LocaleManager

    @MockK
    lateinit var matomoAnalytics: MatomoAnalytics

    @MockK
    lateinit var productRepository: ProductRepository

    private val coroutineDispatcher = CoroutineDispatchersTest()
    private lateinit var viewModel: ProductCompareViewModel

    @BeforeEach
    fun setup() {
        every { localeManager.getLanguage() } returns "en"
        viewModel = ProductCompareViewModel(
            taxonomiesRepository,
            localeManager,
            matomoAnalytics,
            coroutineDispatcher,
            productRepository
        )
    }

    @Test
    fun onInit_productsAreEmpty() {
        assertThat(viewModel.productsFlow.value).isEmpty()
    }

    @Test
    fun addProductToCompare_shouldEmitAlreadyExistEvent_whenTheSameProductAdded() = runBlockingTest {
        // GIVEN
        val product = mockk<Product> {
            every { code } returns "qwerty"
            every { barcode } returns Barcode(code)
            every { additivesTags } returns mutableListOf()
        }

        every { matomoAnalytics.trackEvent(ofType<AnalyticsEvent.AddProductToComparison>()) } returns Unit

        coEvery { taxonomiesRepository.getAdditive("qwerty", "en") }
            .returns(AdditiveName("test-name"))

        val flowItems = mutableListOf<SideEffect>()
        val job = launch {
            viewModel.sideEffectFlow.toList(flowItems)
        }

        // WHEN
        viewModel.addProductToCompare(product)
        viewModel.addProductToCompare(product)

        // THEN
        assertThat(flowItems.size).isEqualTo(1)
        assertThat(flowItems[0] is SideEffect.ProductAlreadyAdded).isTrue()
        verify(exactly = 1) { matomoAnalytics.trackEvent(ofType<AnalyticsEvent.AddProductToComparison>()) }
        job.cancel()
    }

    @Test
    fun addProductToCompare_shouldAddProducts_whenProductsAreDifferent() = runBlockingTest {
        // GIVEN
        val barcode1 = Barcode("qwerty1")
        val product1 = mockk<Product> {
            every { barcode } returns barcode1
            every { additivesTags } returns mutableListOf()
        }
        val barcode2 = Barcode("qwerty2")
        val product2 = mockk<Product> {
            every { barcode } returns barcode2
            every { additivesTags } returns mutableListOf()
        }
        val barcode3 = Barcode("qwerty3")
        val product3 = mockk<Product> {
            every { barcode } returns barcode3
            every { additivesTags } returns mutableListOf()
        }
        coEvery { taxonomiesRepository.getAdditive("qwerty1", "en") }
            .returns(AdditiveName("test-name1"))
        coEvery { taxonomiesRepository.getAdditive("qwerty2", "en") }
            .returns(AdditiveName("test-name2"))
        coEvery { taxonomiesRepository.getAdditive("qwerty3", "en") }
            .returns(AdditiveName("test-name3"))

        every { matomoAnalytics.trackEvent(ofType<AnalyticsEvent.AddProductToComparison>()) } returns Unit
        every { matomoAnalytics.trackEvent(ofType<AnalyticsEvent.CompareProducts>()) } returns Unit

        val flowItems = mutableListOf<List<CompareProduct>>()
        val job = launch {
            viewModel.productsFlow.toList(flowItems)
        }

        // WHEN
        viewModel.addProductToCompare(product1)
        viewModel.addProductToCompare(product2)
        viewModel.addProductToCompare(product3)

        // THEN
        assertThat(flowItems.last().size).isEqualTo(3)
        assertThat(flowItems.last()[0].product.barcode).isEqualTo(barcode1)
        assertThat(flowItems.last()[1].product.barcode).isEqualTo(barcode2)
        assertThat(flowItems.last()[2].product.barcode).isEqualTo(barcode3)
        job.cancel()
    }

    @Test
    fun addProductToCompare_shouldTrackRightAnalytics_whenProductsAddedToCompare() = runBlockingTest {
        // GIVEN
        val barcode1 = Barcode("qwerty1")
        val product1: Product = mockk {
            every { barcode } returns barcode1
            every { additivesTags } returns mutableListOf()
        }
        coEvery { taxonomiesRepository.getAdditive("qwerty1", "en") }
            .returns(AdditiveName("test-name1"))

        val barcode2 = Barcode("qwerty2")
        val product2: Product = mockk {
            every { barcode } returns barcode2
            every { additivesTags } returns mutableListOf()
        }
        coEvery { taxonomiesRepository.getAdditive("qwerty2", "en") }
            .returns(AdditiveName("test-name2"))

        val argumentCapture = mutableListOf<AnalyticsEvent>()
        every { matomoAnalytics.trackEvent(capture(argumentCapture)) } returns Unit

        // WHEN
        viewModel.addProductToCompare(product1)
        viewModel.addProductToCompare(product2)

        // THEN
        verify(exactly = 3) { matomoAnalytics.trackEvent(any()) }
        assertThat((argumentCapture[0] as AnalyticsEvent.AddProductToComparison).barcode).isEqualTo(barcode1)
        assertThat((argumentCapture[1] as AnalyticsEvent.AddProductToComparison).barcode).isEqualTo(barcode2)
        assertThat((argumentCapture[2] as AnalyticsEvent.CompareProducts).count).isEqualTo(2f)
    }
}
