package openbeautyfacts.github.scrachx.openfood.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import openbeautyfacts.github.scrachx.openfood.R;
import openbeautyfacts.github.scrachx.openfood.models.Allergen;

public class AllergensAdapter extends RecyclerView.Adapter<AllergensAdapter.ViewHolder> {

    private List<Allergen> mAllergens;

    public AllergensAdapter(List<Allergen> allergens) {
        mAllergens = allergens;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView;
        public Button messageButton;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.allergen_name);
            messageButton = (Button) itemView.findViewById(R.id.delete_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_allergens, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Allergen allergen = mAllergens.get(position);
        TextView textView = holder.nameTextView;
        textView.setText(allergen.getName());
        Button button = holder.messageButton;
        button.setText(R.string.delete_txt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAllergens.remove(position);
                allergen.setEnable("false");
                allergen.save();
                AllergensAdapter.this.notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAllergens.size();
    }
}