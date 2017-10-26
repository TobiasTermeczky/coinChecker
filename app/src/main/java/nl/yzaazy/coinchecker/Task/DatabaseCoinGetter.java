package nl.yzaazy.coinchecker.Task;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import nl.yzaazy.coinchecker.Interface.CoinGetterInterface;
import nl.yzaazy.coinchecker.Objects.Coin;


public class DatabaseCoinGetter extends AsyncTask<String, Integer, List<Coin>> {
    List<Coin> mSpinnerList = new ArrayList<>();
    private CoinGetterInterface mListener;

    public DatabaseCoinGetter(CoinGetterInterface mListener) {
        this.mListener = mListener;
    }

    @Override
    protected List<Coin> doInBackground(String... strings) {
        List<Coin> coinList = Coin.listAll(Coin.class);
        for (Coin coin : coinList) {
            mSpinnerList.add(coin);
        }
        return mSpinnerList;
    }

    @Override
    protected void onPostExecute(List<Coin> mSpinnerList) {
        mListener.coinGetterCallback(mSpinnerList);
    }
}
