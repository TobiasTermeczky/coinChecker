package nl.yzaazy.coinchecker.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nl.yzaazy.coinchecker.Helpers.SettingsHelper;
import nl.yzaazy.coinchecker.Objects.Coin;
import nl.yzaazy.coinchecker.R;

public class SpinnerAdapter extends BaseAdapter {
    public int position;
    private List<Coin> originalData;
    private List<Coin> filteredData;
    private ItemFilter mFilter = new ItemFilter();
    private LayoutInflater mInflater;
    private SettingsHelper settingsHelper = new SettingsHelper();
    private Context context;

    public SpinnerAdapter(List<Coin> originalData, LayoutInflater mInflater, Context context) {
        this.originalData = originalData;
        this.filteredData = originalData;
        this.mInflater = mInflater;
        this.context = context;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Coin getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dialog_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = convertView.findViewById(R.id.dialog_icon);
            viewHolder.name = convertView.findViewById(R.id.dialog_name);
            viewHolder.symbol = convertView.findViewById(R.id.dialog_symbol);
            viewHolder.progress = convertView.findViewById(R.id.dialog_progress_spinner);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.icon.setVisibility(View.INVISIBLE);
        viewHolder.progress.setVisibility(View.VISIBLE);

        final Coin coin = filteredData.get(position);

        if (coin.getSmallIconLocal(context) == null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    coin.setSmallIconLocal(context, this);
                } else {
                    viewHolder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_no_image));
                }
            } else {
                viewHolder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_no_image));
            }
        } else {
            new AsyncTask<ViewHolder, Void, Bitmap>() {
                private ViewHolder v;

                @Override
                protected Bitmap doInBackground(ViewHolder... params) {
                    v = params[0];
                    return coin.getSmallIconLocal(context);
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    v.icon.setVisibility(View.VISIBLE);
                    v.icon.setImageBitmap(result);
                    v.icon.setAlpha(1f);
                    v.progress.setVisibility(View.GONE);
                }
            }.execute(viewHolder);
        }

        viewHolder.name.setText(coin.getName());
        viewHolder.symbol.setText(coin.getSymbol());
        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView symbol;
        ProgressBar progress;
        int position;
    }

    public Filter getFilter() {
        return mFilter;
    }

    class ItemFilter extends Filter {
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
