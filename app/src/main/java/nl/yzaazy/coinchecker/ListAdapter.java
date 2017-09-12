package nl.yzaazy.coinchecker;

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

class ListAdapter extends BaseAdapter {
    private List<CryptoCoin> mList;
    private LayoutInflater mInflater;
    private OptionHelper optionHelper = new OptionHelper();

    ListAdapter(List<CryptoCoin> stringList, LayoutInflater mInflater){
        this.mList = stringList;
        this.mInflater = mInflater;
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.bitcoin_row, null);

            viewHolder = new ViewHolder();
            viewHolder.symbol = convertView.findViewById(R.id.textSymbol);
            viewHolder.name = convertView.findViewById(R.id.txtName);
            viewHolder.moneyImage = convertView.findViewById(R.id.imageMoney);
            viewHolder.price = convertView.findViewById(R.id.textPrice);
            viewHolder.percent = convertView.findViewById(R.id.textPercentChange1h);
            viewHolder.percentImage = convertView.findViewById(R.id.imagePercent);
            viewHolder.trending = convertView.findViewById(R.id.imageTrending);
            viewHolder.button = convertView.findViewById(R.id.btnRow);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CryptoCoin coin = mList.get(position);
        viewHolder.symbol.setText(coin.getSymbol());
        viewHolder.name.setText(coin.getName());

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
        if(Objects.equals(optionHelper.getCurrencyValue(), "euro")) {
            viewHolder.moneyImage.setImageResource(R.drawable.ic_euro);
            viewHolder.price.setText(df.format(coin.getPrice_eur()));
        }else {
            viewHolder.moneyImage.setImageResource(R.drawable.ic_dollar);
            viewHolder.price.setText(df.format(coin.getPrice_usd()));
        }

        viewHolder.percent.setText(String.valueOf(coin.getPercent_change_1h()));
        viewHolder.percentImage.setImageResource(R.drawable.ic_percent);
        if(coin.getPercent_change_1h() > 0){
            viewHolder.trending.setImageResource(R.drawable.ic_trending_up);
        }else if(coin.getPercent_change_1h() < 0){
            viewHolder.trending.setImageResource(R.drawable.ic_trending_down);
        }


        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CryptoCoin coin = mList.get(position);
                CoinToCheck coinToCheck = CoinToCheck.find(CoinToCheck.class, "name = ?", coin.getName()).get(0);
                coinToCheck.delete();
                mList.remove(position);
                Snackbar.make(v, R.string.action_remove, Snackbar.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView symbol;
        TextView price;
        TextView percent;
        ImageView trending;
        ImageButton button;
        ImageView moneyImage;
        ImageView percentImage;
    }
}
