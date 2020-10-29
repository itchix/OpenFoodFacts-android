package openfoodfacts.github.scrachx.openfood.features.adapters.autocomplete;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import openfoodfacts.github.scrachx.openfood.network.CommonApiManager;
import openfoodfacts.github.scrachx.openfood.network.services.ProductsAPI;

public class EmbCodeAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private final ProductsAPI client;
    private final List<String> codeList;

    public EmbCodeAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        client = CommonApiManager.getInstance().getProductsApi();
        codeList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return codeList.size();
    }

    @Override
    public String getItem(int position) {
        if (position < 0 || position >= codeList.size()) {
            return StringUtils.EMPTY;
        }
        return codeList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                filterResults.count = 0;

                // if no value typed, return
                if (constraint == null) {
                    return filterResults;
                }
                // Retrieve the autocomplete results from server.
                ArrayList<String> list = client.getEMBCodeSuggestions(constraint.toString()).blockingGet();

                // Assign the data to the FilterResults
                filterResults.values = list;
                filterResults.count = list.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    codeList.clear();
                    //noinspection unchecked
                    codeList.addAll((ArrayList<String>) results.values);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
