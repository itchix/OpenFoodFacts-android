package openfoodfacts.github.scrachx.openfood.views.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import openfoodfacts.github.scrachx.openfood.fragments.BaseFragment;
import openfoodfacts.github.scrachx.openfood.models.State;

public class ProductFragmentPagerAdapter extends FragmentPagerAdapter {
    private final List<BaseFragment> fragments;
    private final List<String> navMenuTitles;

    public ProductFragmentPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = new ArrayList<>();
        this.navMenuTitles = new ArrayList<>();
    }

    public void addFragment(BaseFragment fragment, String title) {
        this.fragments.add(fragment);
        this.navMenuTitles.add(title);
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return navMenuTitles.get(position);
    }

    public void refresh(State state) {
        for (BaseFragment f : fragments) {
            if (f.isAdded()) {
                f.refreshView(state);
            }
        }
    }
}
