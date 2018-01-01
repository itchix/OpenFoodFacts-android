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

import org.apache.commons.lang3.StringUtils;

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

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imgProduct);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.nameProduct);

        Picasso.with(context)
                .load(products.get(position).getImageSmallUrl())
                .placeholder(R.drawable.placeholder_thumb)
                .error(R.drawable.ic_error_outline_black_24dp)
                .fit()
                .centerCrop()
                .into(imgIcon);

        Product product = products.get(position);
        StringBuilder stringBuilder = new StringBuilder(product.getProductName() + "\n");

        if (!product.getBrands().isEmpty()) {
            stringBuilder.append(StringUtils.capitalize(product.getBrands().split(",")[0].trim()));
        }

        if (!product.getQuantity().isEmpty()) {
            stringBuilder.append(" - ").append(product.getQuantity());
        }

        txtTitle.setText(stringBuilder.toString());

        return convertView;
    }
}
