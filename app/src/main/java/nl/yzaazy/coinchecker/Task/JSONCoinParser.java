package nl.yzaazy.coinchecker.Task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.yzaazy.coinchecker.Helpers.SettingsHelper;
import nl.yzaazy.coinchecker.Interface.CoinGetterInterface;
import nl.yzaazy.coinchecker.Objects.Coin;

public class JSONCoinParser extends AsyncTask<JSONObject, Integer, List<Coin>> {
    private String TAG = getClass().getName();
    private SettingsHelper settingsHelper = new SettingsHelper();
    private List<Coin> mSpinnerList = new ArrayList<>();
    private CoinGetterInterface mListener;
    private SweetAlertDialog pDialog;

    public JSONCoinParser(CoinGetterInterface mListener, SweetAlertDialog pDialog) {
        this.mListener = mListener;
        this.pDialog = pDialog;
    }

    @Override
    protected List<Coin> doInBackground(JSONObject... jsonObjects) {
        JSONObject response = jsonObjects[0];
        try {
            JSONObject data = response.getJSONObject("Data");
            int length = data.length();
            int i = 0;
            Iterator<String> temp = data.keys();
            while (temp.hasNext()) {
                try {
                    JSONObject coinData = data.getJSONObject(temp.next());
                    Coin coin = new Coin();
                    List<Coin> storedCoin = Coin.find(Coin.class, "symbol = ?", coinData.getString("Symbol"));
                    if (!storedCoin.isEmpty()) {
                        coin = storedCoin.get(0);
                    }
                    coin.setSymbol(coinData.getString("Symbol"));
                    coin.setName(coinData.getString("CoinName"));
                    coin.setNameSymbol(coinData.getString("FullName"));
                    coin.setSortOrder(coinData.getInt("SortOrder"));
                    try {
                        coin.setIconUrl(response.getString("BaseImageUrl") + coinData.getString("ImageUrl"));
                    } catch (JSONException e) {
                        Log.w(TAG, "Skipped image URL for coin: " + coin.getNameSymbol() + ", couldn't get!");
                        coin.setIconUrl(null);
                    }
                    coin.save();
                    mSpinnerList.add(coin);
                } catch (JSONException e) {
                    Log.w(TAG, "Skipped 1, couldn't save coin!");

                }
                i++;
                publishProgress(i, length);
            }
            settingsHelper.setJSONDate(new Date());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mSpinnerList;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        float percentage = (float) values[0]/values[1];
        Log.w(TAG, percentage + "%");
        pDialog.getProgressHelper().setInstantProgress(percentage);
    }

    @Override
    protected void onPostExecute(List<Coin> mSpinnerList) {
        mListener.coinGetterCallback(mSpinnerList);

    }
}
