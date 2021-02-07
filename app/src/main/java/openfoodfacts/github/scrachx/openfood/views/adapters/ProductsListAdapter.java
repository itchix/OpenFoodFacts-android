package openfoodfacts.github.scrachx.openfood.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import openfoodfacts.github.scrachx.openfood.utils.LocaleHelper;
import org.apache.commons.lang.StringUtils;

import java.util.List;

import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.models.Product;

public class ProductsListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Product> products;

    public ProductsListAdapter(Context context, List<Product> items){
        this.context = context;
        this.products = items;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.products_list_item, null);
        }

        ImageView imgIcon = convertView.findViewById(R.id.imgProduct);
        TextView txtTitle = convertView.findViewById(R.id.nameProduct);

        Picasso.get()
                .load(products.get(position).getImageSmallUrl(LocaleHelper.getLanguage(context)))
                .placeholder(R.drawable.placeholder_thumb)
                .error(R.drawable.error_image)
                .fit()
                .centerCrop()
                .into(imgIcon);

        Product product = products.get(position);
        if (product == null) {
            txtTitle.setText(R.string.product_not_found);
        }
        else {
            StringBuilder stringBuilder = new StringBuilder(product.getProductName() + "\n");

            if (!product.getBrands().isEmpty()) {
                stringBuilder.append(StringUtils.capitalize(product.getBrands().split(",")[0].trim()));
            }

            if (!product.getQuantity().isEmpty()) {
                stringBuilder.append(" - ").append(product.getQuantity());
            }

            txtTitle.setText(stringBuilder.toString());
        }

        return convertView;
    }
}
