package openfoodfacts.github.scrachx.openfood.views.product.environment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.fragments.BaseFragment;
import openfoodfacts.github.scrachx.openfood.models.Nutriments;
import openfoodfacts.github.scrachx.openfood.models.Product;
import openfoodfacts.github.scrachx.openfood.models.State;
import openfoodfacts.github.scrachx.openfood.views.product.ProductFragment;

import static openfoodfacts.github.scrachx.openfood.utils.Utils.bold;

public class EnvironmentProductFragment extends BaseFragment {

    @BindView(R.id.textCarbonFootprint)
    TextView carbonFootprint;
    @BindView(R.id.environment_info_text)
    TextView environmentInfoText;

    private State mState;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createView(inflater, container, R.layout.fragment_environment_product);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Intent intent = getActivity().getIntent();
        if(intent!=null && intent.getExtras()!=null && intent.getExtras().getSerializable("state")!=null)
            mState = (State) intent.getExtras().getSerializable("state");
        else
            mState = ProductFragment.mState;

        final Product product = mState.getProduct();
        Nutriments nutriments = product.getNutriments();

        if(nutriments != null && nutriments.contains(Nutriments.CARBON_FOOTPRINT)) {
            Nutriments.Nutriment carbonFootprintNutriment = nutriments.get(Nutriments.CARBON_FOOTPRINT);
            carbonFootprint.setText(bold(getString(R.string.textCarbonFootprint)));
            carbonFootprint.append(carbonFootprintNutriment.getFor100g());
            carbonFootprint.append(carbonFootprintNutriment.getUnit());
        }

        if (product.getEnvironmentInfocard() != null && !product.getEnvironmentInfocard().isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                environmentInfoText.append(Html.fromHtml(product.getEnvironmentInfocard(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                environmentInfoText.append(Html.fromHtml(product.getEnvironmentInfocard()));
            }
        }

        refreshView(mState);
    }

    @Override
    public void refreshView(State state) {
        super.refreshView(state);

    }

}
