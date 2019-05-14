package openfoodfacts.github.scrachx.openfood.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.LinearLayout;


import com.afollestad.materialdialogs.MaterialDialog;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import butterknife.OnClick;
import com.afollestad.materialdialogs.MaterialDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import net.steamcrafted.loadtoast.LoadToast;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.models.AllergenName;
import openfoodfacts.github.scrachx.openfood.repositories.IProductRepository;
import openfoodfacts.github.scrachx.openfood.repositories.ProductRepository;
import openfoodfacts.github.scrachx.openfood.utils.LocaleHelper;
import openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener.NavigationDrawerType;
import openfoodfacts.github.scrachx.openfood.utils.Utils;
import openfoodfacts.github.scrachx.openfood.views.ContinuousScanActivity;
import openfoodfacts.github.scrachx.openfood.views.HistoryScanActivity;
import openfoodfacts.github.scrachx.openfood.views.ProductComparisonActivity;
import openfoodfacts.github.scrachx.openfood.views.ProductListsActivity;
import openfoodfacts.github.scrachx.openfood.views.WelcomeActivity;
import openfoodfacts.github.scrachx.openfood.views.adapters.AllergensAdapter;


import java.util.*;

import android.annotation.SuppressLint;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;

import android.util.Log;
import java.lang.reflect.Field;

import static openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener.ITEM_ALERT;

/**
 * @see R.layout#fragment_alert_allergens
 */
public class AllergensAlertFragment extends NavigationBaseFragment {

    private List<AllergenName> mAllergensEnabled;
    private List<AllergenName> mAllergensFromDao;
    private AllergensAdapter mAdapter;
    private RecyclerView mRvAllergens;
    private SharedPreferences mSettings;
    private IProductRepository productRepository;
    private View mView;
    private LinearLayout mEmptyMessageView;                                         // Empty View containing the message that will be shown if the list is empty
    private DataObserver mDataObserver;
    private BottomNavigationView bottomNavigationView;
    public static Integer getKey(HashMap<Integer, String> map, String value) {
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
        return createView(inflater, container, R.layout.fragment_alert_allergens);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }
    /*
    public method in order to disable shift mode in the bottom navigation bar
    Can also be resolved by using : app:labelVisibilityMode="labeled" on xml fragment
    if using the library com.android.support:design.28.0.0-alpha1
     */
    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);

                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {

        } catch (IllegalAccessException e) {

        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvAllergens = view.findViewById(R.id.allergens_recycle);
        mEmptyMessageView = view.findViewById(R.id.emptyAllergensView);
        productRepository = ProductRepository.getInstance();
        mDataObserver = new DataObserver();
        bottomNavigationView  = view.findViewById((R.id.bottom_navigation));
        try{disableShiftMode(bottomNavigationView);}catch(Exception ew){}
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.scan_bottom_nav:
                   if (Utils.isHardwareCameraInstalled(getContext())) {
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                                new MaterialDialog.Builder(getContext())
                                        .title(R.string.action_about)
                                        .content(R.string.permission_camera)
                                        .neutralText(R.string.txtOk)
                                        .onNeutral((dialog, which) -> ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Utils.MY_PERMISSIONS_REQUEST_CAMERA))
                                        .show();
                            } else {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Utils.MY_PERMISSIONS_REQUEST_CAMERA);
                            }
                        } else {
                            Intent intent_test = new Intent(getActivity(), ContinuousScanActivity.class);
                            intent_test.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent_test);
                        }
                    }
                    break;
                case R.id.compare_products:
                    startActivity((new Intent(getActivity(), ProductComparisonActivity.class)));
                    break;
                case R.id.home_page:
                    startActivity((new Intent(getActivity(),WelcomeActivity.class)));
                    break;
                case R.id.history_bottom_nav:
                    Intent intent  = new Intent(getActivity(), HistoryScanActivity.class);
                    startActivity(intent);

                    break;
                case R.id.my_lists:
                    startActivity(new Intent(getActivity(), ProductListsActivity.class));
                    break;

                default:
                    return true;
            }



        return true;
        });

        productRepository.getAllergensByEnabledAndLanguageCode(true, Locale.getDefault().getLanguage());

        final String language = LocaleHelper.getLanguage(getContext());
        productRepository.getAllergensByEnabledAndLanguageCode(true, language)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(allergens -> {
                    mAllergensEnabled = allergens;
                    mAdapter = new AllergensAdapter(productRepository, mAllergensEnabled, getActivity());
                    mRvAllergens.setAdapter(mAdapter);
                    mRvAllergens.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    mRvAllergens.setHasFixedSize(true);
                    mAdapter.registerAdapterDataObserver(mDataObserver);
                    mDataObserver.onChanged();
                }, Throwable::printStackTrace);

        productRepository.getAllergensByLanguageCode(language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(allergens -> {
                    mAllergensFromDao = allergens;
                }, Throwable::printStackTrace);


        mView = view;
        mSettings = getActivity().getSharedPreferences("prefs", 0);
    }

    /**
     * Add an allergen to be checked for when browsing products.
     */
    @OnClick(R.id.btn_add)
    protected void onAddAllergens() {
        if (mAllergensEnabled != null && mAllergensFromDao != null && mAllergensFromDao.size() > 0) {
            productRepository.getAllergensByEnabledAndLanguageCode(false, LocaleHelper.getLanguage(getContext()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((List<AllergenName> allergens) -> {
                        Collections.sort(allergens, new Comparator<AllergenName>() {
                            @Override
                            public int compare(AllergenName a1, AllergenName a2) {
                                return a1.getName().compareToIgnoreCase(a2.getName());
                            }
                        });
                        List<String> allergensNames = new ArrayList<String>();
                        for (AllergenName allergenName : allergens) {
                            allergensNames.add(allergenName.getName());
                        }
                        new MaterialDialog.Builder(mView.getContext())
                                .title(R.string.title_dialog_alert)
                                .items(allergensNames)
                                .itemsCallback((dialog, view, position, text) -> {
                                    productRepository.setAllergenEnabled(allergens.get(position).getAllergenTag(), true);
                                    mAllergensEnabled.add(allergens.get(position));
                                    mAdapter.notifyItemInserted(mAllergensEnabled.size() - 1);
                                    mRvAllergens.scrollToPosition(mAdapter.getItemCount() - 1);
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
                productRepository.getAllergens(true)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toObservable()
                        .subscribe(allergens -> {
                            editor.putBoolean("errorAllergens", false).apply();
                            productRepository.saveAllergens(allergens);
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
                new MaterialDialog.Builder(mView.getContext())
                        .title(R.string.title_dialog_alert)
                        .content(R.string.info_download_data_connection)
                        .neutralText(R.string.txtOk)
                        .show();
            }
        }
    }

    private void updateAllergenDao() {
        final String language = LocaleHelper.getLanguage(getContext());
        productRepository.getAllergensByEnabledAndLanguageCode(true, language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(allergens -> {
                    mAllergensEnabled = allergens;
                }, Throwable::printStackTrace);

        productRepository.getAllergensByLanguageCode(language)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(allergens -> {
                    mAllergensFromDao = allergens;
                }, Throwable::printStackTrace);
    }

    @Override
    @NavigationDrawerType
    public int getNavigationDrawerType() {
        return ITEM_ALERT;
    }

    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.alert_drawer));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mRvAllergens != null){
            mRvAllergens.getAdapter().unregisterAdapterDataObserver(mDataObserver);
        }
    }

    class DataObserver extends RecyclerView.AdapterDataObserver{
        DataObserver() {
            super();
        }

        private void setAppropriateView() {
            if (mEmptyMessageView != null && mAdapter != null) {
                boolean isListEmpty = mAdapter.getItemCount() == 0;
                mEmptyMessageView.setVisibility(isListEmpty ? View.VISIBLE : View.GONE);
                mRvAllergens.setVisibility(isListEmpty ? View.GONE : View.VISIBLE);
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
