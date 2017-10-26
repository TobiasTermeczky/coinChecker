package nl.yzaazy.coinchecker.Helpers;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.yzaazy.coinchecker.Interface.CoinGetterInterface;
import nl.yzaazy.coinchecker.Objects.Coin;
import nl.yzaazy.coinchecker.R;
import nl.yzaazy.coinchecker.Task.DatabaseCoinGetter;
import nl.yzaazy.coinchecker.Task.JSONCoinParser;

public class CoinsGetter implements CoinGetterInterface {
    SettingsHelper settingsHelper = new SettingsHelper();
    String TAG = getClass().getName();
    Context mContext;
    List<Coin> mSpinnerList;
    SpinnerDialog mSpinnerDialog;
    SweetAlertDialog mPDialog;

    public CoinsGetter(Context context, List<Coin> mSpinnerList, SpinnerDialog spinnerDialog, SweetAlertDialog pDialog) {
        this.mContext = context;
        this.mSpinnerList = mSpinnerList;
        this.mSpinnerDialog = spinnerDialog;
        this.mPDialog = pDialog;
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

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (moreThanDay & activeNetwork != null) {
            mPDialog.setTitleText(mContext.getString(R.string.coin_internet));
            mPDialog.show();
            RequestQueue queue = Volley.newRequestQueue(mContext);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://www.cryptocompare.com/api/data/coinlist/",
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            new JSONCoinParser(CoinsGetter.this, mContext).execute(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mPDialog.cancel();
                            mPDialog.setTitleText(mContext.getString(R.string.coin_database_no_internet));
                            mPDialog.show();
                            new DatabaseCoinGetter(CoinsGetter.this).execute();
                        }
                    });
            jsonObjectRequest.setShouldCache(false);
            queue.add(jsonObjectRequest);
        } else {
            if(activeNetwork == null){
                mPDialog.setTitleText(mContext.getString(R.string.coin_database_no_internet));
            }else {
                mPDialog.setTitleText(mContext.getString(R.string.coin_database));
            }
            mPDialog.show();
            new DatabaseCoinGetter(this).execute();
        }
    }

    private void updateUI(List<Coin> SpinnerList) {
        mSpinnerList.clear();
        mSpinnerList.addAll(SpinnerList);
        Collections.sort(mSpinnerList);
        mPDialog.cancel();
        mSpinnerDialog.showSpinerDialog();
    }
}
