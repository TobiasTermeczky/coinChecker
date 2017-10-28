package nl.yzaazy.coinchecker.Helpers;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.yzaazy.coinchecker.Interface.CoinGetterInterface;
import nl.yzaazy.coinchecker.Objects.Coin;
import nl.yzaazy.coinchecker.R;
import nl.yzaazy.coinchecker.Task.DatabaseCoinGetter;
import nl.yzaazy.coinchecker.Task.JSONCoinParser;

public class CoinsGetter implements CoinGetterInterface {
    private SettingsHelper settingsHelper = new SettingsHelper();
    private String TAG = getClass().getName();
    private Context context;
    private List<Coin> spinnerList;
    private SpinnerDialog spinnerDialog;
    private SweetAlertDialog pDialog;

    public CoinsGetter(Context context, List<Coin> spinnerList, SpinnerDialog spinnerDialog, SweetAlertDialog pDialog) {
        this.context = context;
        this.spinnerList = spinnerList;
        this.spinnerDialog = spinnerDialog;
        this.pDialog = pDialog;
    }

    @Override
    public void coinGetterCallback(List<Coin> SpinnerList) {
        updateUI(SpinnerList);
    }

    public void getAllCoins() {
        boolean moreThanDay = true;

        Date date1 = settingsHelper.getJSONDate();
        if (date1 != null) {
            Log.i(TAG, date1.toString());
            Date date2 = new Date();
            Log.i(TAG, date2.toString());
            long MILLIS_PER_DAY = 24 * 60 * 60 * 1000L;
            moreThanDay = Math.abs(date1.getTime() - date2.getTime()) > MILLIS_PER_DAY;
            Log.i(TAG, "" + moreThanDay);
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (moreThanDay & activeNetwork != null) {
            pDialog.setTitleText(context.getString(R.string.coin_internet));
            pDialog.show();
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://www.cryptocompare.com/api/data/coinlist/",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pDialog.setTitleText(context.getString(R.string.save_coin_internet));
                            new JSONCoinParser(CoinsGetter.this, pDialog).execute(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.cancel();
                            pDialog.setTitleText(context.getString(R.string.coin_database_no_internet));
                            pDialog.show();
                            new DatabaseCoinGetter(CoinsGetter.this).execute();
                        }
                    });
            jsonObjectRequest.setShouldCache(false);
            queue.add(jsonObjectRequest);
        } else {
            if(activeNetwork == null){
                pDialog.setTitleText(context.getString(R.string.coin_database_no_internet));
            }else {
                pDialog.setTitleText(context.getString(R.string.coin_database));
            }
            pDialog.show();
            new DatabaseCoinGetter(this).execute();
        }
    }

    private void updateUI(List<Coin> SpinnerList) {
        spinnerList.clear();
        spinnerList.addAll(SpinnerList);
        pDialog.dismissWithAnimation();
        spinnerDialog.showSpinnerDialog();
    }
}
