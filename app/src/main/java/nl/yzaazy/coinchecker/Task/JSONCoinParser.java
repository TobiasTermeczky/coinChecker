package nl.yzaazy.coinchecker.Task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import nl.yzaazy.coinchecker.Helpers.SettingsHelper;
import nl.yzaazy.coinchecker.Interface.CoinGetterInterface;
import nl.yzaazy.coinchecker.Objects.Coin;

public class JSONCoinParser extends AsyncTask<JSONObject, Integer, ArrayList<String>> {
    private String TAG = getClass().getName();
    private SettingsHelper settingsHelper = new SettingsHelper();
    private ArrayList<String> mNameList = new ArrayList<>();
    private CoinGetterInterface mListener;

    public JSONCoinParser(CoinGetterInterface mListener) {
        this.mListener = mListener;
    }

    @Override
    protected ArrayList<String> doInBackground(JSONObject... jsonObjects) {
        JSONObject response = jsonObjects[0];
        try {
            JSONObject data = response.getJSONObject("Data");
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
                    mNameList.add(coin.getName());
                } catch (JSONException e) {
                    Log.w(TAG, "Skipped 1, couldn't save coin!");

                }
            }
            settingsHelper.setJSONDate(new Date());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mNameList;
    }

    @Override
    protected void onPostExecute(ArrayList<String> mNameList) {
        mListener.coinGetterCallback(mNameList);

    }
}
