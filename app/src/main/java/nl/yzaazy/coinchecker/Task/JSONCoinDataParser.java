package nl.yzaazy.coinchecker.Task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import nl.yzaazy.coinchecker.Interface.RefreshInterface;
import nl.yzaazy.coinchecker.Objects.Coin;

public class JSONCoinDataParser extends AsyncTask<JSONObject, Integer, Boolean> {
    private String TAG = getClass().getName();
    private RefreshInterface mListener;

    public JSONCoinDataParser(RefreshInterface mListener) {
        this.mListener = mListener;
    }

    @Override
    protected Boolean doInBackground(JSONObject... jsonObjects) {
        JSONObject response = jsonObjects[0];
        try {
            JSONObject data = response.getJSONObject("DISPLAY");
            Iterator<String> temp = data.keys();
            while (temp.hasNext()) {
                String key = temp.next();
                Coin coin = Coin.find(Coin.class, "symbol = ?", key).get(0);
                JSONObject coinData = data.getJSONObject(key);

                try {
                    JSONObject coinDataUsd = coinData.getJSONObject("USD");
                    coin.setPriceUsd(coinDataUsd.getString("PRICE"));
                    coin.setPercentChangeUsd24h(coinDataUsd.getString("CHANGEPCT24HOUR"));
                } catch (JSONException e) {
                    Log.w(TAG, "Can't get price USD");
                }

                try {
                    JSONObject coinDataEur = coinData.getJSONObject("EUR");
                    coin.setPriceEur(coinDataEur.getString("PRICE"));
                    coin.setPercentChangeEur24h(coinDataEur.getString("CHANGEPCT24HOUR"));
                } catch (JSONException e) {
                    Log.w(TAG, "Can't get price EUR");
                }
                coin.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mListener.refresh();
    }
}
