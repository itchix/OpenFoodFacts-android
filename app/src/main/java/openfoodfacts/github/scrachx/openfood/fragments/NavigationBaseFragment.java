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

package openfoodfacts.github.scrachx.openfood.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import openfoodfacts.github.scrachx.openfood.utils.INavigationItem;
import openfoodfacts.github.scrachx.openfood.utils.NavigationDrawerListener;


/**
 * An abstract navigation base fragment which extends base fragment and
 * implements INavigationItem.
 * @author Lobster
 * @since 06.03.18
 */
abstract class NavigationBaseFragment extends BaseFragment implements INavigationItem {

    private NavigationDrawerListener navigationDrawerListener;

    /**
     * This method attaches the fragment to its content which will be called after onCreate() method
     * Since it is overridden method super class implementation must be called.
     *
     * @param context: Context to attach the fragment
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NavigationDrawerListener) {
            navigationDrawerListener = (NavigationDrawerListener) context;
        }
    }

    /**
     * This method gives subclasses a chance to initialize themselves once they know their view
     * hierarchy has been completely created.
     * This method is called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned.
     * @param view: The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState: If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (navigationDrawerListener != null) {
            navigationDrawerListener.setItemSelected(getNavigationDrawerType());
        }
    }

    /**
     * This method returns the navigation drawer listener created
     * @return: navigation drawer listener
     */
    @Override
    public NavigationDrawerListener getNavigationDrawerListener() {
        return navigationDrawerListener;
    }
}
