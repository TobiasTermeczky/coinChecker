package nl.yzaazy.coinchecker.Task;

import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import nl.yzaazy.coinchecker.Helpers.SettingsHelper;
import nl.yzaazy.coinchecker.Interface.OnTaskCompleted;
import nl.yzaazy.coinchecker.Objects.Coin;


public class JSONCoinInfoParser extends AsyncTask<JSONObject, Integer, ArrayList<String>> {
    SettingsHelper settingsHelper = new SettingsHelper();
    Gson mGson = new Gson();
    ArrayList<String> mNameList = new ArrayList<>();
    private OnTaskCompleted mListener;


    public JSONCoinInfoParser(OnTaskCompleted mListener){
        this.mListener = mListener;
    }

    @Override
    protected ArrayList<String> doInBackground(JSONObject... jsonObjects) {
        JSONObject response = jsonObjects[0];
        try {
            JSONObject data = response.getJSONObject("Data");
            Iterator<String> temp = data.keys();
            Coin.deleteAll(Coin.class);
            while (temp.hasNext()) {
                String key = temp.next();
                String value = data.getJSONObject(key).toString();
                System.out.println(value);
                Coin coin = mGson.fromJson(value, Coin.class);
                coin.save();
                mNameList.add(coin.getCoinName());
            }
            settingsHelper.setJSONDate(new Date());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mNameList;
    }

    @Override
        protected void onPostExecute(ArrayList<String> mNameList) {
            mListener.coinInfoGetterCallback(mNameList);
    }
}
