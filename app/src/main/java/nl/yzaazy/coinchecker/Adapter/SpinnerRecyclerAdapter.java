package nl.yzaazy.coinchecker.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nl.yzaazy.coinchecker.Objects.Coin;
import nl.yzaazy.coinchecker.R;

public class SpinnerRecyclerAdapter extends RecyclerView.Adapter<SpinnerRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Coin> originalData;
    private List<Coin> filteredData;
    private SpinnerRecyclerAdapter.ItemFilter mFilter = new SpinnerRecyclerAdapter.ItemFilter();

    public SpinnerRecyclerAdapter(Context context, List<Coin> originalData) {
        this.context = context;
        this.originalData = originalData;
        this.filteredData = originalData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.icon.setVisibility(View.INVISIBLE);
        holder.progress.setVisibility(View.VISIBLE);
        Coin coin = filteredData.get(position);
        holder.name.setText(coin.getName());
        holder.symbol.setText(coin.getSymbol());
        //todo write get image for the view
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public Coin getItem(int position){
        return filteredData.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView symbol;
        ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.dialog_icon);
            name = itemView.findViewById(R.id.dialog_name);
            symbol = itemView.findViewById(R.id.dialog_symbol);
            progress = itemView.findViewById(R.id.dialog_progress_spinner);
        }
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<Coin> list = originalData;
            int count = list.size();
            final List<Coin> nList = new ArrayList<>(count);
            Coin filterableCoin;
            for (int i = 0; i < count; i++) {
                filterableCoin = list.get(i);
                if (filterableCoin.getNameSymbol().toLowerCase().contains(filterString)) {
                    nList.add(filterableCoin);
                }
            }
            results.values = nList;
            results.count = nList.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (List<Coin>) results.values;
            notifyDataSetChanged();
        }
    }
}
