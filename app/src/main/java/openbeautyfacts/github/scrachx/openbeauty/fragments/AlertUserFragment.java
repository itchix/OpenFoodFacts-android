package openbeautyfacts.github.scrachx.openfood.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;

import net.steamcrafted.loadtoast.LoadToast;

import org.apache.commons.collections.IteratorUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;
import openbeautyfacts.github.scrachx.openfood.R;
import openbeautyfacts.github.scrachx.openfood.models.Allergen;
import openbeautyfacts.github.scrachx.openfood.models.FoodAPIRestClientUsage;
import openbeautyfacts.github.scrachx.openfood.views.adapters.AllergensAdapter;

public class AlertUserFragment extends BaseFragment {

    private List<Allergen> mAllergens;
    private AllergensAdapter mAdapter;
    private RecyclerView mRvAllergens;
    private SharedPreferences mSettings;
    private View mView;
    @Bind(R.id.fab) FloatingActionButton mFab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createView(inflater, container, R.layout.fragment_alert_allergens);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;
        mSettings = getActivity().getSharedPreferences("prefs", 0);
        boolean firstRunAlert = mSettings.getBoolean("firstRunAlert", true);
        if (firstRunAlert) {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.alert_dialog_warning_title)
                    .content(R.string.warning_alert_data)
                    .positiveText(R.string.ok_button)
                    .show();
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean("firstRunAlert", false);
            editor.apply();
        }

        mRvAllergens = (RecyclerView) view.findViewById(R.id.alergens_recycle);
        mAllergens = Allergen.find(Allergen.class, "enable = ?", "true");
        mAdapter = new AllergensAdapter(mAllergens);
        mRvAllergens.setAdapter(mAdapter);
        mRvAllergens.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRvAllergens.setHasFixedSize(true);
    }

    @OnClick(R.id.fab)
    protected void onAddAllergens() {
        final List<Allergen> all = IteratorUtils.toList(Allergen.findAll(Allergen.class));
        List<String> allS = new ArrayList<String>();
        for (Allergen a : all) {
            if (Locale.getDefault().getLanguage().contains("fr")){
                if(a.getIdAllergen().contains("fr:")) allS.add(a.getName().substring(a.getName().indexOf(":")+1));
            } else if (Locale.getDefault().getLanguage().contains("en")) {
                if(a.getIdAllergen().contains("en:")) allS.add(a.getName().substring(a.getName().indexOf(":")+1));
            }
        }
        if(allS.size() > 0) {
            new MaterialDialog.Builder(mView.getContext())
                    .title(R.string.title_dialog_alert)
                    .items(allS)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            all.get(which).setEnable("true");
                            all.get(which).save();
                            boolean canAdd = true;
                            for(Allergen a : mAllergens) {
                                if(a.getName().equals(all.get(which).getName())) canAdd = false;
                            }
                            if(canAdd) {
                                mAllergens.add(all.get(which));
                                mAdapter.notifyItemInserted(mAllergens.size() - 1);
                                mRvAllergens.scrollToPosition(mAdapter.getItemCount() - 1);
                            }
                        }
                    })
                    .show();
        } else {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if(isConnected) {
                final LoadToast lt = new LoadToast(getContext());
                lt.setText(getContext().getString(R.string.toast_retrieving));
                lt.setBackgroundColor(getContext().getResources().getColor(R.color.indigo_600));
                lt.setTextColor(getContext().getResources().getColor(R.color.white));
                lt.show();
                new MaterialDialog.Builder(mView.getContext())
                        .title(R.string.title_dialog_alert)
                        .content(R.string.info_download_data)
                        .positiveText(R.string.txtYes)
                        .negativeText(R.string.txtNo)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                final SharedPreferences.Editor editor = mSettings.edit();
                                FoodAPIRestClientUsage api = new FoodAPIRestClientUsage();
                                api.getAllergens(new FoodAPIRestClientUsage.OnAllergensCallback() {
                                    @Override
                                    public void onAllergensResponse(boolean value) {
                                        if (!value) {
                                            editor.putBoolean("errorAllergens", true);
                                            editor.apply();
                                        } else {
                                            editor.putBoolean("errorAllergens", false);
                                            editor.apply();
                                        }
                                        lt.success();
                                        dialog.hide();
                                    }
                                });
                            }
                        })
                        .show();
            } else {
                new MaterialDialog.Builder(mView.getContext())
                        .title(R.string.title_dialog_alert)
                        .content(R.string.info_download_data_connection)
                        .neutralText(R.string.txtOk)
                        .show();
            }
        }

    }

}
