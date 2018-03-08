package openfoodfacts.github.scrachx.openfood.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lobster on 04.03.18.
 */

public class AdditivesWrapper {

    private List<AdditiveResponse> additives;

    public List<Additive> map() {
        List<Additive> entityLabels = new ArrayList<>();
        for (AdditiveResponse additive : additives) {
            entityLabels.add(additive.map());
        }

        return entityLabels;
    }

    public List<AdditiveResponse> getAdditives() {
        return additives;
    }

    public void setAdditives(List<AdditiveResponse> additives) {
        this.additives = additives;
    }
}
