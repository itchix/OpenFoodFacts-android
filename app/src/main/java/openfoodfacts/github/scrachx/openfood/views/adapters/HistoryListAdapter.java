package openfoodfacts.github.scrachx.openfood.views.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import openfoodfacts.github.scrachx.openfood.R;
import openfoodfacts.github.scrachx.openfood.models.HistoryItem;
import openfoodfacts.github.scrachx.openfood.utils.Utils;
import openfoodfacts.github.scrachx.openfood.views.holders.HistoryScanHolder;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryScanHolder> {

    private final List<HistoryItem> list;
    private final String productUrl;
    private Activity mActivity;

    public HistoryListAdapter(List<HistoryItem> list, String productUrl, Activity activity) {
        this.list = list == null ? Collections.<HistoryItem>emptyList() : list;
        this.productUrl = productUrl;
        this.mActivity = activity;
    }

    @Override
    public HistoryScanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_item, parent, false);
        return new HistoryScanHolder(v, productUrl, mActivity);
    }

    @Override
    public void onBindViewHolder(HistoryScanHolder holder, int position) {
        holder.historyImageProgressbar.setVisibility(View.VISIBLE);
        HistoryItem item = list.get(position);

        StringBuilder stringBuilder = new StringBuilder();
        if (isNotEmpty(item.getBrands())) {
            stringBuilder.append(capitalize(item.getBrands().split(",")[0].trim()));
        }

        if (isNotEmpty(item.getQuantity())) {
            stringBuilder.append(" - ").append(item.getQuantity());
        }

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.txtTitle.setText(item.getTitle());
        holder.txtBarcode.setText(item.getBarcode());
        holder.txtProductDetails.setText(stringBuilder.toString());
        holder.imgNutritionGrade.setImageDrawable(ContextCompat.getDrawable(mActivity, Utils.getSmallImageGrade(item.getNutritionGrade())));
        if (item.getUrl() == null) {
            holder.historyImageProgressbar.setVisibility(View.GONE);
        }
        Picasso.with(mActivity)
                .load(item.getUrl())
                .placeholder(R.drawable.placeholder_thumb)
                .error(R.drawable.ic_no_red_24dp)
                .fit()
                .centerCrop()
                .into(holder.imgProduct, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.historyImageProgressbar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.historyImageProgressbar.setVisibility(View.GONE);
                    }
                });

        Date date = list.get(position).getTime();
        calcTime(date, holder);

        //animate(holder);
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, HistoryItem data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(HistoryItem data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }


    public void calcTime(Date date, HistoryScanHolder holder) {


        Date now = new Date();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - date.getTime());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - date.getTime());
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - date.getTime());
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - date.getTime());


        String secText = String.valueOf(seconds) + mActivity.getString(R.string.seconds_ago);
        String minText = String.valueOf(minutes) + mActivity.getString(R.string.minutes_ago);
        String hourText = String.valueOf(hours) + mActivity.getString(R.string.hours_ago);
        String dayText = String.valueOf(days) + mActivity.getString(R.string.days_ago);

        if (seconds < 60) {
            holder.txtDate.setText(secText);
        } else if (minutes < 60) {
            holder.txtDate.setText(minText);
        } else if (hours < 24) {
            holder.txtDate.setText(hourText);
        } else {
            holder.txtDate.setText(dayText);
        }
    }


}
