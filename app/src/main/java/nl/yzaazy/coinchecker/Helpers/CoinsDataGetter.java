package nl.yzaazy.coinchecker.Helpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

import nl.yzaazy.coinchecker.Interface.RefreshInterface;
import nl.yzaazy.coinchecker.Objects.Coin;
import nl.yzaazy.coinchecker.Task.JSONCoinDataParser;

public class CoinsDataGetter{
    SettingsHelper settingsHelper = new SettingsHelper();
    String TAG = getClass().getName();
    Context mContext;
    List<Coin> mCoinList;

    RefreshInterface mListener;

    public CoinsDataGetter(Context mContext, List<Coin> mCoinList, RefreshInterface mListener) {
        this.mContext = mContext;
        this.mCoinList = mCoinList;
        this.mListener = mListener;
    }

    public void getData(){

        String url = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=";
        for(Coin coin : mCoinList){
            url = url + coin.getSymbol() + ",";
        }
        url = url + "&tsyms=USD,EUR,BTC";
        Log.i(TAG, url);

        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        new JSONCoinDataParser(mListener).execute(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        jsonObjectRequest.setShouldCache(false);
        queue.add(jsonObjectRequest);
    }
}
