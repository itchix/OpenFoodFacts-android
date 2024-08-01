package openfoodfacts.github.scrachx.openfood.features.categories.fragment

import android.app.SearchManager
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.databinding.FragmentCategoryListBinding
import openfoodfacts.github.scrachx.openfood.features.categories.adapter.CategoryListRecyclerAdapter
import openfoodfacts.github.scrachx.openfood.features.shared.BaseFragment
import openfoodfacts.github.scrachx.openfood.utils.SearchSuggestionProvider
import java.util.*

@AndroidEntryPoint
class CategoryListFragment : BaseFragment() {

    val viewModel: CategoryFragmentViewModel by viewModels()

    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryAdapter: CategoryListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.setHasFixedSize(true)
        binding.recycler.layoutManager = LinearLayoutManager(context)
        binding.recycler.addItemDecoration(DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL))

        binding.fastScroller.setRecyclerView(binding.recycler)
        binding.recycler.viewTreeObserver.addOnGlobalLayoutListener {
            val shownCategories = viewModel.shownCategories.value ?: return@addOnGlobalLayoutListener

            if (shownCategories.isEmpty()) {
                binding.fastScroller.visibility = View.GONE
            } else {
                binding.fastScroller.visibility = View.VISIBLE
                // check for an empty item in the start of the list
                if (shownCategories.first().name.isNullOrEmpty()) {
                    viewModel.shownCategories.postValue(shownCategories.drop(1))
                    binding.recycler.adapter?.notifyItemRemoved(0)
                    binding.recycler.adapter?.notifyItemRangeChanged(0, binding.recycler.adapter?.itemCount ?: 0)
                }
            }
        }
        binding.buttonToRefresh.setOnClickListener { viewModel.refreshCategories() }
        viewModel.showOffline.observe(viewLifecycleOwner) {
            binding.offlineView.isVisible = it
        }
        viewModel.showProgress.observe(viewLifecycleOwner) {
            binding.progressView.isVisible = it
        }
        viewModel.shownCategories.observe(viewLifecycleOwner) {
            categoryAdapter = CategoryListRecyclerAdapter(it)
            binding.recycler.adapter = categoryAdapter
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        val searchManager = requireActivity().getSystemService<SearchManager>()!!
        val searchMenuItem = menu.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as SearchView

        searchView.queryHint = getString(R.string.hint_category_list)

        if (searchManager.getSearchableInfo(requireActivity().componentName) == null) return

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                SearchRecentSuggestions(
                    context,
                    SearchSuggestionProvider.AUTHORITY,
                    SearchSuggestionProvider.MODE
                ).saveRecentQuery(query, null)

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.searchCategories(newText.lowercase(Locale.getDefault()))
                return false
            }
        })
    }
}
