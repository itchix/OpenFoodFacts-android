package openfoodfacts.github.scrachx.openfood.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import butterknife.OnClick;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.models.DaoSession;
import openfoodfacts.github.scrachx.openfood.models.Diet;
import openfoodfacts.github.scrachx.openfood.models.DietDao;
import openfoodfacts.github.scrachx.openfood.views.OFFApplication;
import openfoodfacts.github.scrachx.openfood.views.adapters.DietsAdapter;

import static openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener.ITEM_DIET;

/**
 * @see R.layout#fragment_diets
 * Created by dobriseb on 2018.10.15.
 * Gestion des régimes dans la BD locale.
 */
public class DietsFragment extends NavigationBaseFragment {

    private RecyclerView mRvDiet;
    private SharedPreferences mSettings;

    @Override
    public int getNavigationDrawerType() {
        return ITEM_DIET;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return createView(inflater, container, R.layout.fragment_diets);
    }

    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Management of the warning message, the data remains on the device.
        mSettings = getActivity().getSharedPreferences("prefs", 0);
        boolean firstRunDiets = mSettings.getBoolean("firstRunDiets", true);
        if (firstRunDiets) {
            new MaterialDialog.Builder(view.getContext())
                    .title(R.string.diets_dialog_warning_title)
                    .content(R.string.warning_diets_data)
                    .positiveText(R.string.ok_button)
                    .show();
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean("firstRunDiets", false);
            editor.apply();
        }

        // Search for data to display in the Recycler
        mRvDiet = (RecyclerView) view.findViewById(R.id.diets_recycler);
        DaoSession daoSession = OFFApplication.getInstance().getDaoSession();
        DietDao dietDao = daoSession.getDietDao();
        List dietList = dietDao.loadAll();
        //Pour le test.
/*
        if (dietList.isEmpty()) {
            Diet diet1 = new Diet();
            diet1.setTag("en:Vegetarian");
            diet1.setEnabled(true);
            dietList.add(diet1);
            Diet diet2 = new Diet();
            diet2.setTag("en:Vegan");
            diet2.setEnabled(false);
            dietList.add(diet2);
        }
*/

        // Enabling the recycler with the proper adapter
        mRvDiet.setLayoutManager(new LinearLayoutManager(this.getContext()));
        //DietsAdapter adapter = new DietsAdapter(dietList);
        //mRvDiet.setAdapter(adapter);
        mRvDiet.setAdapter(new DietsAdapter(dietList, new ClickListener() {
            @Override
            public void onPositionClicked(int position, View v) {
                Log.i("INFO", "Click sur " + v.getId() + " de l'enregistrement n°" + position + " ayant pour Tag : " + v.getTag());
                Fragment fragment = new EditDietFragment();
                if (v.getTag() != null) {
                    Bundle args = new Bundle();
                    args.putString("dietName", v.getTag().toString());
                    fragment.setArguments(args);
                }
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment );
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
/*
            @Override
            public void onLongClicked(int position, View v) {
                //Log.i("INFO", "LongClick sur " + v.getId() + " de l'enregistrement n°" + position);
            }
*/
        }));
    }

    /**
     * Ajout d'un régime.
     * Add a diet.
     */
    @OnClick(R.id.fab)
    void openFragmentAddDiet () {
        Log.i("INFO", "Starting onClick on fab from FragmentDiets");
        Fragment fragment = new EditDietFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment );
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }

    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.your_diets));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public interface ClickListener {
        void onPositionClicked(int position, View v);
    }
}
