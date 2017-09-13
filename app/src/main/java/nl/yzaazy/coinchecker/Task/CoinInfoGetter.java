package nl.yzaazy.coinchecker.Task;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import nl.yzaazy.coinchecker.Helpers.SettingsHelper;
import nl.yzaazy.coinchecker.Objects.Coin;

public class CoinInfoGetter extends AsyncTask<JSONObject, Integer, ArrayList<String>> {
    SettingsHelper settingsHelper = new SettingsHelper();
    String TAG = getClass().getName();
    Gson mGson = new Gson();
    Context mContext;
    ArrayList<String> mNameList;
    SpinnerDialog mSpinnerDialog;
    SweetAlertDialog mPDialog;

    public CoinInfoGetter(Context context, ArrayList<String> mNameList, SpinnerDialog spinnerDialog, SweetAlertDialog pDialog) {
        this.mContext = context;
        this.mNameList = mNameList;
        this.mSpinnerDialog = spinnerDialog;
        this.mPDialog = pDialog;
    }

    public void getAllCoins() {
        mNameList.clear();
        long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
        Date date1 = settingsHelper.getJSONDate();
        Log.i(TAG, date1.toString());
        Date date2 = new Date();
        boolean moreThanDay = Math.abs(date1.getTime() - date2.getTime()) > MILLIS_PER_DAY;
        Log.i(TAG, "" + moreThanDay);
        System.out.println(moreThanDay);
        if (moreThanDay) {
            Log.i(TAG, "Getting data from JSON");
            RequestQueue queue = Volley.newRequestQueue(mContext);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, "https://www.cryptocompare.com/api/data/coinlist/", null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            execute(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, error.toString());
                        }
                    });
            queue.add(jsonObjectRequest);
        } else {
            Log.i(TAG, "Getting data from database");
            List<Coin> coinList = Coin.listAll(Coin.class);
            for (Coin coin : coinList) {
                mNameList.add(coin.getCoinName());
            }
            updateUI();
        }

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
        updateUI();
    }

    private void updateUI() {
        Collections.sort(mNameList);
        mPDialog.cancel();
        mSpinnerDialog.showSpinerDialog();
    }
}
