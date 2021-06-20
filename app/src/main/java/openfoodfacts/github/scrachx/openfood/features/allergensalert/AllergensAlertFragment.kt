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

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.rx2.rxSingle
import net.steamcrafted.loadtoast.LoadToast
import openfoodfacts.github.scrachx.openfood.R
import openfoodfacts.github.scrachx.openfood.analytics.AnalyticsEvent
import openfoodfacts.github.scrachx.openfood.analytics.MatomoAnalytics
import openfoodfacts.github.scrachx.openfood.databinding.FragmentAlertAllergensBinding
import openfoodfacts.github.scrachx.openfood.features.shared.NavigationBaseFragment
import openfoodfacts.github.scrachx.openfood.models.entities.allergen.AllergenName
import openfoodfacts.github.scrachx.openfood.repositories.ProductRepository
import openfoodfacts.github.scrachx.openfood.utils.LocaleManager
import openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener
import openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener.NavigationDrawerType
import openfoodfacts.github.scrachx.openfood.utils.Utils
import javax.inject.Inject

/**
 * @see R.layout.fragment_alert_allergens
 */
@AndroidEntryPoint
class AllergensAlertFragment : NavigationBaseFragment() {

    private var _binding: FragmentAlertAllergensBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var productRepository: ProductRepository

    @Inject
    lateinit var matomoAnalytics: MatomoAnalytics

    @Inject
    lateinit var localeManager: LocaleManager

    private var mAllergensEnabled: MutableList<AllergenName>? = null
    private var allergensFromDao: List<AllergenName>? = null

    private lateinit var adapter: AllergensAdapter
    private lateinit var mSettings: SharedPreferences
    private val dataObserver by lazy { AllergensObserver() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        _binding = FragmentAlertAllergensBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // OnClick
        binding.btnAdd.setOnClickListener { addAllergen() }

        val language = localeManager.getLanguage()

        productRepository.getAllergensByEnabledAndLanguageCode(true, language)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { Log.e(LOG_TAG, "getAllergensByEnabledAndLanguageCode", it) }
            .subscribe { allergens ->
                mAllergensEnabled = allergens.toMutableList()
                adapter = AllergensAdapter(productRepository, mAllergensEnabled!!)

                binding.allergensRecycle.adapter = adapter
                binding.allergensRecycle.layoutManager = LinearLayoutManager(view.context)
                binding.allergensRecycle.setHasFixedSize(true)
                adapter.registerAdapterDataObserver(dataObserver)
                dataObserver.onChanged()
            }
            .addTo(disp)

        productRepository.getAllergensByLanguageCode(language)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { Log.e(LOG_TAG, "getAllergensByLanguageCode", it) }
            .subscribe { allergens -> allergensFromDao = allergens }
            .addTo(disp)

        mSettings = requireActivity().getSharedPreferences("prefs", 0)
    }

    override fun onResume() {
        super.onResume()
        try {
            (requireActivity() as AppCompatActivity).supportActionBar!!.setTitle(getString(R.string.alert_drawer))
        } catch (e: IllegalStateException) {
            Log.e(AllergensAlertFragment::class.simpleName, "onResume", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.unregisterAdapterDataObserver(dataObserver)
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Set search as invisible
        menu.findItem(R.id.action_search).isVisible = false
    }

    /**
     * Add an allergen to be checked for when browsing products.
     */
    private fun addAllergen() {
        if (mAllergensEnabled != null && !allergensFromDao.isNullOrEmpty()) {
            productRepository.getAllergensByEnabledAndLanguageCode(false, localeManager.getLanguage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { it.printStackTrace() }
                .map { allergens -> allergens.sortedBy { it.name } }
                .subscribe { allergens ->
                    MaterialDialog.Builder(requireContext())
                        .title(R.string.title_dialog_alert)
                        .items(allergens.map { it.name })
                        .itemsCallback { _, _, position, _ ->
                            viewLifecycleOwner.lifecycleScope.launch {
                                productRepository.setAllergenEnabled(allergens[position].allergenTag, true).await()
                            }
                            mAllergensEnabled!!.add(allergens[position])
                            adapter.notifyItemInserted(mAllergensEnabled!!.size - 1)
                            binding.allergensRecycle.scrollToPosition(adapter.itemCount - 1)
                            matomoAnalytics.trackEvent(AnalyticsEvent.AllergenAlertCreated(allergens[position].allergenTag))
                        }
                        .show()
                }
                .addTo(disp)
        } else {
            if (Utils.isNetworkConnected(requireContext())) {
                val lt = LoadToast(context)
                    .setText(requireActivity().getString(R.string.toast_retrieving))
                    .setBackgroundColor(ResourcesCompat.getColor(requireContext().resources, R.color.blue, requireContext().theme))
                    .setTextColor(ResourcesCompat.getColor(requireActivity().resources, R.color.white, requireContext().theme))
                    .show()

                rxSingle { productRepository.getAllergens() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError {
                        mSettings.edit { putBoolean("errorAllergens", true) }
                        lt.error()
                    }
                    .subscribe { _ ->
                        mSettings.edit { putBoolean("errorAllergens", false) }
                        adapter.allergens = mAllergensEnabled!!
                        adapter.notifyDataSetChanged()
                        updateAllergenDao()
                        addAllergen()
                        lt.success()
                    }
                    .addTo(disp)
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.title_dialog_alert)
                    .setMessage(R.string.info_download_data_connection)
                    .setNeutralButton(android.R.string.ok) { d, _ -> d.dismiss() }
                    .show()
            }
        }
    }

    /**
     * Retrieve modified list of allergens from ProductRepository
     */
    private fun updateAllergenDao() {
        val language = localeManager.getLanguage()
        productRepository.getAllergensByEnabledAndLanguageCode(true, language)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { Log.e(AllergensAlertFragment::class.simpleName, "getAllergensByEnabledAndLanguageCode", it) }
            .subscribe { allergens -> mAllergensEnabled = allergens.toMutableList() }
            .addTo(disp)
        productRepository.getAllergensByLanguageCode(language)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { Log.e(AllergensAlertFragment::class.simpleName, "getAllergensByLanguageCode", it) }
            .subscribe { allergens -> allergensFromDao = allergens.toMutableList() }
            .addTo(disp)
    }

    @NavigationDrawerType
    override fun getNavigationDrawerType() = NavigationDrawerListener.ITEM_ALERT


    /**
     * Data observer of the Recycler Views
     */
    internal inner class AllergensObserver : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            setAppropriateView()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            setAppropriateView()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            setAppropriateView()
        }

        private fun setAppropriateView() {
            val isListEmpty = adapter.itemCount == 0
            binding.emptyAllergensView.visibility = if (isListEmpty) View.VISIBLE else View.GONE
            binding.allergensRecycle.visibility = if (isListEmpty) View.GONE else View.VISIBLE
        }
    }

    companion object {
        private val LOG_TAG = this::class.simpleName

        @JvmStatic
        fun newInstance() = AllergensAlertFragment().apply { arguments = Bundle() }
    }
}
