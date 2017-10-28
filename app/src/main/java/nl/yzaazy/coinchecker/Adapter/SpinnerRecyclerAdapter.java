package nl.yzaazy.coinchecker.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import java.util.Objects;

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Coin coin = filteredData.get(position);
        holder.icon.setAlpha(0f);
        holder.progress.setAlpha(1f);
        holder.name.setText(coin.getName());
        holder.symbol.setText(coin.getSymbol());

        //todo fix images not loading correctly
        if (coin.getSmallIconLocal(context) == null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    ImageListener imageListener = new ImageListener() {
                        @Override
                        public void newImage(Bitmap bitmap, Coin checkCoin) {
                                setBitmap(holder, bitmap);
                        }
                    };
                    coin.setSmallIconLocal(context, imageListener, coin);
                } else {
                    setBitmap(holder, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_no_image));
                }
            } else {
                setBitmap(holder, BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_no_image));
            }
        } else {
            new AsyncTask<SpinnerRecyclerAdapter.ViewHolder, Void, Bitmap>() {
                private SpinnerRecyclerAdapter.ViewHolder holder;

                @Override
                protected Bitmap doInBackground(SpinnerRecyclerAdapter.ViewHolder... params) {
                    holder = params[0];
                    return coin.getSmallIconLocal(context);
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    setBitmap(holder, result);
                }
            }.execute(holder);
        }


    }

    private void setBitmap(ViewHolder holder, Bitmap bitmap) {
        holder.icon.setImageBitmap(bitmap);
        holder.icon.setAlpha(1f);
        holder.progress.setAlpha(0f);
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public Coin getItem(int position) {
        return filteredData.get(position);
    }

    public Filter getFilter() {
        return mFilter;
    }

    public interface ImageListener {
        void newImage(Bitmap bitmap, Coin coin);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView symbol;
        ProgressBar progress;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.dialog_icon);
            name = itemView.findViewById(R.id.dialog_name);
            symbol = itemView.findViewById(R.id.dialog_symbol);
            progress = itemView.findViewById(R.id.dialog_progress_spinner);
        }
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
