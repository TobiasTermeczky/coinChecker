package nl.yzaazy.coinchecker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class ListAdapter extends BaseAdapter {
    private List<CryptoCoin> mList;
    private LayoutInflater mInflater;

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
        viewHolder.moneyImage.setImageResource(R.drawable.ic_dollar);
        viewHolder.price.setText(String.valueOf(coin.getPrice_usd()));
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
                mList.remove(position);
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
