package nl.yzaazy.coinchecker.Helpers;


import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import nl.yzaazy.coinchecker.Interface.OnTaskCompleted;
import nl.yzaazy.coinchecker.R;
import nl.yzaazy.coinchecker.Task.DatabaseCoinInfoGetter;
import nl.yzaazy.coinchecker.Task.JSONCoinInfoParser;

public class CoinInfoGetter implements OnTaskCompleted {
    SettingsHelper settingsHelper = new SettingsHelper();
    String TAG = getClass().getName();
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

    @Override
    public void coinInfoGetterCallback(ArrayList<String> NameList) {
        updateUI(NameList);
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

        if (moreThanDay) {
            mPDialog.setTitleText(mContext.getString(R.string.coin_internet));
            mPDialog.show();
            RequestQueue queue = Volley.newRequestQueue(mContext);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, "https://www.cryptocompare.com/api/data/coinlist/", null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            new JSONCoinInfoParser(CoinInfoGetter.this).execute(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mPDialog.cancel();
                            mPDialog.setTitleText(mContext.getString(R.string.coin_database_no_internet));
                            mPDialog.show();
                            new DatabaseCoinInfoGetter(CoinInfoGetter.this).execute();
                        }
                    });
            jsonObjectRequest.setShouldCache(false);
            queue.add(jsonObjectRequest);
        } else {
            mPDialog.setTitleText(mContext.getString(R.string.coin_database));
            mPDialog.show();
            new DatabaseCoinInfoGetter(this).execute();
        }
    }

    private void updateUI(ArrayList<String> NameList) {
        mNameList.clear();
        mNameList.addAll(NameList);
        Collections.sort(mNameList);
        mPDialog.cancel();
        mSpinnerDialog.showSpinerDialog();
    }
}
