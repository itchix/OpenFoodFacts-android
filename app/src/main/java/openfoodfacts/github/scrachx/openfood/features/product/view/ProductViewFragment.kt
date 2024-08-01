package openfoodfacts.github.scrachx.openfood.features.product.view

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.databinding.ActivityProductBinding
import openfoodfacts.github.scrachx.openfood.features.product.ProductFragmentPagerAdapter
import openfoodfacts.github.scrachx.openfood.features.product.edit.ProductEditActivity
import openfoodfacts.github.scrachx.openfood.features.product.edit.ProductEditActivity.Companion.KEY_STATE
import openfoodfacts.github.scrachx.openfood.features.product.view.ProductViewActivity.Companion.onOptionsItemSelected
import openfoodfacts.github.scrachx.openfood.features.product.view.ProductViewActivity.Companion.setupViewPager
import openfoodfacts.github.scrachx.openfood.features.product.view.ProductViewActivity.ShowIngredientsAction
import openfoodfacts.github.scrachx.openfood.features.product.view.ProductViewActivity.ShowIngredientsAction.PERFORM_OCR
import openfoodfacts.github.scrachx.openfood.features.product.view.ProductViewActivity.ShowIngredientsAction.SEND_UPDATED
import openfoodfacts.github.scrachx.openfood.features.product.view.ingredients.IngredientsProductFragment
import openfoodfacts.github.scrachx.openfood.features.product.view.summary.SummaryProductFragment
import openfoodfacts.github.scrachx.openfood.features.shared.BaseActivity
import openfoodfacts.github.scrachx.openfood.listeners.CommonBottomListenerInstaller.installBottomNavigation
import openfoodfacts.github.scrachx.openfood.listeners.CommonBottomListenerInstaller.selectNavigationItem
import openfoodfacts.github.scrachx.openfood.listeners.OnRefreshListener
import openfoodfacts.github.scrachx.openfood.models.ProductState
import openfoodfacts.github.scrachx.openfood.repositories.ProductRepository
import openfoodfacts.github.scrachx.openfood.utils.Intent
import openfoodfacts.github.scrachx.openfood.utils.requireProductState
import javax.inject.Inject

@AndroidEntryPoint
class ProductViewFragment : Fragment(), IProductView, OnRefreshListener {
    private var _binding: ActivityProductBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var client: ProductRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var adapterResult: ProductFragmentPagerAdapter
    private lateinit var productState: ProductState

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (resources.getBoolean(R.bool.portrait_only)) {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        _binding = ActivityProductBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()
        check(activity is BaseActivity) { "Cannot initialize from an activity that does not inherit BaseActivity" }

        productState = requireProductState()

        binding.toolbar.visibility = View.GONE

        adapterResult = setupViewPager(binding.pager)
        ViewCompat.setNestedScrollingEnabled(binding.pager, true)

        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            tab.text = adapterResult.getPageTitle(position)
        }.attach()

        binding.navigationBottomInclude.bottomNavigation.selectNavigationItem(0)
        binding.navigationBottomInclude.bottomNavigation.installBottomNavigation(activity)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val intent = Intent<ProductEditActivity>(requireContext()) {
                putExtra(ProductEditActivity.KEY_EDIT_PRODUCT, productState.product)
            }
            startActivity(intent)
        }
    }

    private fun setupViewPager(viewPager: ViewPager2) = setupViewPager(
        viewPager,
        productState,
        requireActivity()
    )

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem) = onOptionsItemSelected(requireActivity(), item)

    override fun onRefresh() {
        lifecycleScope.launch {
            val newState = try {
                client.getProductStateFull(productState.product!!.code)
            } catch (err: Exception) {
                adapterResult.refresh(productState)
                return@launch
            }
            productState = newState
            adapterResult.refresh(newState)
        }
    }

    fun bottomSheetWillGrow() {
        if (adapterResult.itemCount == 0) return

        // without this, the view can be centered vertically on initial show. we force the scroll to top !
        (adapterResult.createFragment(0) as? SummaryProductFragment)?.resetScroll()
    }

    override fun showIngredientsTab(action: ShowIngredientsAction) {
        if (adapterResult.itemCount == 0) return

        for (i in 0 until adapterResult.itemCount) {
            val fragment = adapterResult.createFragment(i)

            if (fragment is IngredientsProductFragment) {
                binding.pager.currentItem = i
                when (action) {
                    PERFORM_OCR -> fragment.extractIngredients()
                    SEND_UPDATED -> fragment.changeIngImage()
                }
                return
            }
        }
    }

    companion object {
        private const val LOGIN_ACTIVITY_REQUEST_CODE = 1

        fun newInstance(productState: ProductState) = ProductViewFragment().apply {
            arguments = Bundle().apply {
                putSerializable(KEY_STATE, productState)
            }
        }
    }
}
