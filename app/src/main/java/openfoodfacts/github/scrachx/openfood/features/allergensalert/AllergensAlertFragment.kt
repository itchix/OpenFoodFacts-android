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
package openfoodfacts.github.scrachx.openfood.features.allergensalert

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.databinding.FragmentAlertAllergensBinding
import openfoodfacts.github.scrachx.openfood.features.allergensalert.AllergensAlertViewModel.SideEffect
import openfoodfacts.github.scrachx.openfood.features.shared.NavigationBaseFragment
import openfoodfacts.github.scrachx.openfood.models.entities.allergen.AllergenName
import openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener
import openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener.NavigationDrawerType

@AndroidEntryPoint
class AllergensAlertFragment : NavigationBaseFragment() {

    private var _binding: FragmentAlertAllergensBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllergensAlertViewModel by viewModels()

    private val adapter = AllergensAdapter {
        viewModel.removeAllergen(it)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        setHasOptionsMenu(true)
        _binding = FragmentAlertAllergensBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addAllergenButton.setOnClickListener {
            viewModel.addAllergenClicked()
        }

        binding.allergensRecycle.adapter = adapter
        binding.allergensRecycle.layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
        binding.allergensRecycle.setHasFixedSize(true)
        binding.allergensRecycle.addItemDecoration(DividerItemDecoration(requireContext(),
            DividerItemDecoration.VERTICAL))

        lifecycleScope.launch {
            viewModel.viewStateFlow
                .flowWithLifecycle(lifecycle)
                .collect { state ->
                    binding.allergensProgressbar.isVisible = state.loading
                    adapter.allergens = state.allergens
                    binding.allergensRecycle.isVisible = state.allergens.isNotEmpty()
                }
        }

        lifecycleScope.launch {
            viewModel.sideEffectFlow
                .flowWithLifecycle(lifecycle)
                .collect { sideEffect ->
                    when (sideEffect) {
                        is SideEffect.ShowNoDataDialog -> showNoDataDialog()
                        is SideEffect.ShowAddAllergenDialog -> showAddAllergenDialog(sideEffect.items)
                        is SideEffect.ShowNetworkErrorDialog -> showNetworkErrorDialog()
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            (requireActivity() as? AppCompatActivity)?.supportActionBar?.setTitle(R.string.alert_drawer)
        } catch (e: IllegalStateException) {
            Log.e(AllergensAlertFragment::class.simpleName, "onResume", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Set search as invisible
        menu.findItem(R.id.action_search).isVisible = false
    }

    @NavigationDrawerType
    override fun getNavigationDrawerType() = NavigationDrawerListener.ITEM_ALERT

    private fun showAddAllergenDialog(items: List<AllergenName>) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.title_dialog_alert)
            .setItems(items.map { it.name }.toTypedArray()) { _, position ->
                viewModel.addAllergen(items[position])
            }
            .show()
    }

    private fun showNoDataDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.title_dialog_alert)
            .setMessage(R.string.info_download_data_connection)
            .setNeutralButton(android.R.string.ok) { d, _ -> d.dismiss() }
            .show()
    }

    private fun showNetworkErrorDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.alert_dialog_warning_title)
            .setMessage(R.string.txtConnectionError)
            .setPositiveButton(R.string.ok_button) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    companion object {
        fun newInstance() = AllergensAlertFragment().apply { arguments = Bundle() }
    }
}
