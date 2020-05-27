package openfoodfacts.github.scrachx.openfood.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.databinding.FragmentAlertAllergensBinding;
import openfoodfacts.github.scrachx.openfood.models.AllergenName;
import openfoodfacts.github.scrachx.openfood.repositories.IProductRepository;
import openfoodfacts.github.scrachx.openfood.repositories.ProductRepository;
import openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener.NavigationDrawerType;
import openfoodfacts.github.scrachx.openfood.views.adapters.AllergensAdapter;
import openfoodfacts.github.scrachx.openfood.views.listeners.BottomNavigationListenerInstaller;

import static openfoodfacts.github.scrachx.openfood.utils.LocaleHelper.getLanguage;
import static openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener.ITEM_ALERT;

/**
 * @see R.layout#fragment_alert_allergens
 */
public class AllergensAlertFragment extends NavigationBaseFragment {
    private FragmentAlertAllergensBinding binding;
    private List<AllergenName> mAllergensEnabled;
    private List<AllergenName> mAllergensFromDao;
    private AllergensAdapter mAdapter;
    private SharedPreferences mSettings;
    private IProductRepository productRepository;
    private View currentView;
    private DataObserver mDataObserver;

    public static Integer getKey(Map<Integer, String> map, String value) {
        Integer key = null;
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if ((value == null && entry.getValue() == null) || (value != null && value.equals(entry.getValue()))) {
                key = entry.getKey();
                break;
            }
        }
        return key;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        binding = FragmentAlertAllergensBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // OnClick
        binding.btnAdd.setOnClickListener(v -> onAddAllergens());

        productRepository = ProductRepository.getInstance();
        mDataObserver = new DataObserver();
        BottomNavigationListenerInstaller.selectNavigationItem(binding.navigationBottom.bottomNavigation, 0);
        productRepository.getAllergensByEnabledAndLanguageCode(true, Locale.getDefault().getLanguage());

        final String language = getLanguage(getContext());
        productRepository.getAllergensByEnabledAndLanguageCode(true, language)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                allergens -> {
                    mAllergensEnabled = allergens;
                    mAdapter = new AllergensAdapter(productRepository, mAllergensEnabled);
                    binding.allergensRecycle.setAdapter(mAdapter);
                    binding.allergensRecycle.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    binding.allergensRecycle.setHasFixedSize(true);
                    mAdapter.registerAdapterDataObserver(mDataObserver);
                    mDataObserver.onChanged();
                },
                e -> Log.e(AllergensAlertFragment.class.getSimpleName(), "getAllergensByEnabledAndLanguageCode", e)
            );

        productRepository.getAllergensByLanguageCode(language)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                allergens -> mAllergensFromDao = allergens,
                e -> Log.e(AllergensAlertFragment.class.getSimpleName(), "getAllergensByLanguageCode", e)
            );

        currentView = view;
        mSettings = getActivity().getSharedPreferences("prefs", 0);
    }

    /**
     * Add an allergen to be checked for when browsing products.
     */
    protected void onAddAllergens() {
        if (mAllergensEnabled != null && mAllergensFromDao != null && !mAllergensFromDao.isEmpty()) {
            productRepository.getAllergensByEnabledAndLanguageCode(false, getLanguage(getContext()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((List<AllergenName> allergens) -> {
                    Collections.sort(allergens, (a1, a2) -> a1.getName().compareToIgnoreCase(a2.getName()));
                    List<String> allergensNames = new ArrayList<>();
                    for (AllergenName allergenName : allergens) {
                        allergensNames.add(allergenName.getName());
                    }
                    new MaterialDialog.Builder(currentView.getContext())
                        .title(R.string.title_dialog_alert)
                        .items(allergensNames)
                        .itemsCallback((dialog, view, position, text) -> {
                            productRepository.setAllergenEnabled(allergens.get(position).getAllergenTag(), true);
                            mAllergensEnabled.add(allergens.get(position));
                            mAdapter.notifyItemInserted(mAllergensEnabled.size() - 1);
                            binding.allergensRecycle.scrollToPosition(mAdapter.getItemCount() - 1);
                        })
                        .show();
                }, Throwable::printStackTrace);
        } else {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (isConnected) {
                final LoadToast lt = new LoadToast(getContext());
                lt.setText(getContext().getString(R.string.toast_retrieving));
                lt.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
                lt.setTextColor(getContext().getResources().getColor(R.color.white));
                lt.show();
                final SharedPreferences.Editor editor = mSettings.edit();
                productRepository.getAllergens()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toObservable()
                    .subscribe(allergens -> {
                        editor.putBoolean("errorAllergens", false).apply();
                        mAdapter.setAllergens(mAllergensEnabled);
                        mAdapter.notifyDataSetChanged();
                        updateAllergenDao();
                        onAddAllergens();
                        lt.success();
                    }, e -> {
                        editor.putBoolean("errorAllergens", true).apply();
                        lt.error();
                    });
            } else {
                new MaterialDialog.Builder(currentView.getContext())
                    .title(R.string.title_dialog_alert)
                    .content(R.string.info_download_data_connection)
                    .neutralText(R.string.txtOk)
                    .show();
            }
        }
    }

    /**
     * Retrieve modified list of allergens from ProductRepository
     */
    private void updateAllergenDao() {
        final String language = getLanguage(getContext());
        productRepository.getAllergensByEnabledAndLanguageCode(true, language)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(allergens -> mAllergensEnabled = allergens, e -> Log.e(AllergensAlertFragment.class.getSimpleName(), "getAllergensByEnabledAndLanguageCode", e));

        productRepository.getAllergensByLanguageCode(language)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(allergens -> mAllergensFromDao = allergens, e -> Log.e(AllergensAlertFragment.class.getSimpleName(), "getAllergensByLanguageCode", e));
    }

    @Override
    @NavigationDrawerType
    public int getNavigationDrawerType() {
        return ITEM_ALERT;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.alert_drawer));
        } catch (NullPointerException e) {
            Log.e(AllergensAlertFragment.class.getSimpleName(), "onResume", e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (binding.allergensRecycle != null) {
            binding.allergensRecycle.getAdapter().unregisterAdapterDataObserver(mDataObserver);
        }
    }

    /**
     * Dat aobserver of the Recycler Views
     */
    class DataObserver extends RecyclerView.AdapterDataObserver {
        DataObserver() {
            super();
        }

        private void setAppropriateView() {
            if (binding.emptyAllergensView != null && mAdapter != null) {
                boolean isListEmpty = mAdapter.getItemCount() == 0;
                binding.emptyAllergensView.setVisibility(isListEmpty ? View.VISIBLE : View.GONE);
                binding.allergensRecycle.setVisibility(isListEmpty ? View.GONE : View.VISIBLE);
            }
        }

        @Override
        public void onChanged() {
            super.onChanged();
            setAppropriateView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            setAppropriateView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            setAppropriateView();
        }
    }
}
