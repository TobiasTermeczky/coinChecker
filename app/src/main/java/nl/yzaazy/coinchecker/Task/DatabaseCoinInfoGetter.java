package nl.yzaazy.coinchecker.Task;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import nl.yzaazy.coinchecker.Interface.OnTaskCompleted;
import nl.yzaazy.coinchecker.Objects.Coin;


public class DatabaseCoinInfoGetter extends AsyncTask<String, Integer, ArrayList<String>> {
    ArrayList<String> mNameList = new ArrayList<>();
    private OnTaskCompleted mListener;

    public DatabaseCoinInfoGetter(OnTaskCompleted mListener){
        this.mListener = mListener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        List<Coin> coinList = Coin.listAll(Coin.class);
        for (Coin coin : coinList) {
            mNameList.add(coin.getCoinName());
        }
        return mNameList;
    }

    @Override
    protected void onPostExecute(ArrayList<String> mNameList) {
        mListener.coinInfoGetterCallback(mNameList);
    }
}
