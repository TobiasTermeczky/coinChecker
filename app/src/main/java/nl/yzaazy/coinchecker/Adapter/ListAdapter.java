package nl.yzaazy.coinchecker.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import nl.yzaazy.coinchecker.Helpers.SettingsHelper;
import nl.yzaazy.coinchecker.Objects.Coin;
import nl.yzaazy.coinchecker.R;

public class ListAdapter extends BaseAdapter {
    private List<Coin> mList;
    private LayoutInflater mInflater;
    private SettingsHelper settingsHelper = new SettingsHelper();
    private Context context;

    public ListAdapter(Context context, List<Coin> stringList, LayoutInflater mInflater){
        this.mList = stringList;
        this.mInflater = mInflater;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.coin_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = convertView.findViewById(R.id.ivIcon);
            viewHolder.fullName = convertView.findViewById(R.id.txtFullName);
            viewHolder.price = convertView.findViewById(R.id.textPrice);
            viewHolder.percent = convertView.findViewById(R.id.textPercentChange24h);
            viewHolder.button = convertView.findViewById(R.id.btnRow);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Coin coin = mList.get(position);

        viewHolder.icon.setImageResource(R.drawable.ic_no_image);
        viewHolder.icon.setAlpha(0.3f);
        viewHolder.fullName.setText(coin.getNameSymbol());

        //Money per coin
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS

        if(Objects.equals(settingsHelper.getCurrency(), "euro")) {
            if(coin.getPriceEur() == null){
                viewHolder.price.setText(R.string.no_price);
            }else {
                viewHolder.price.setText(coin.getPriceEur());
            }
        }else {
            if(coin.getPriceUsd() == null){
                viewHolder.price.setText(R.string.no_price);
            }else {
                viewHolder.price.setText(coin.getPriceUsd());
            }
        }

        //percent
        if(Objects.equals(settingsHelper.getCurrency(), "euro")) {
            if(coin.getPercentChangeUsd24h() == null) {
                viewHolder.percent.setText(R.string.no_data);
            } else {
                viewHolder.percent.setText(coin.getPercentChangeEur24h() + "%");
                //setting the write color
                if (Double.parseDouble(coin.getPercentChangeEur24h()) > 0) {
                    viewHolder.percent.setTextColor(Color.parseColor("#006400"));
                } else if (Double.parseDouble(coin.getPercentChangeEur24h()) < 0) {
                    viewHolder.percent.setTextColor(Color.RED);
                }
            }
        }else {
            if(coin.getPercentChangeUsd24h() == null){
                viewHolder.percent.setText(R.string.no_data);
            }else {
                viewHolder.percent.setText(String.valueOf(coin.getPercentChangeUsd24h() + "%"));
                if (Double.parseDouble(coin.getPercentChangeUsd24h()) > 0) {
                    viewHolder.percent.setTextColor(Color.parseColor("#006400"));
                } else if (Double.parseDouble(coin.getPercentChangeUsd24h()) < 0) {
                    viewHolder.percent.setTextColor(Color.RED);
                }
            }
        }


        if(coin.getIconLocal(context) != null){
            viewHolder.icon.setImageBitmap(coin.getIconLocal(context));
            viewHolder.icon.setAlpha(1f);
        }

        if(coin.getLocked()){
            viewHolder.button.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_lock_closed_dark));
        }

        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Coin coin = mList.get(position);
                if(coin.getLocked()){
                    coin.setLocked(false);
                    viewHolder.button.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_lock_open_dark));
                    Snackbar.make(v, context.getString(R.string.unlocked, coin.getNameSymbol()), Snackbar.LENGTH_SHORT).show();
                }else {
                    coin.setLocked(true);
                    viewHolder.button.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_lock_closed_dark));
                    Snackbar.make(v, context.getString(R.string.locked, coin.getNameSymbol()), Snackbar.LENGTH_SHORT).show();
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView fullName;
        TextView price;
        TextView percent;
        ImageButton button;
    }
}
