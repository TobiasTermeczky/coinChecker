package nl.yzaazy.coinchecker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mNameList = new ArrayList<>();
    private List<CryptoCoin> mList = new ArrayList<>();
    private ListView mListView;
    private ListAdapter mAdapter;
    private SpinnerDialog spinnerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mListView = findViewById(R.id.listview);
        UpdateUI();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(mNameList);
                System.out.println("Name list: " + mNameList.toString());
                spinnerDialog = new SpinnerDialog(MainActivity.this,mNameList,MainActivity.this.getResources().getString(R.string.addCoin));
                spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                    @Override
                    public void onClick(String item, int position) {
                        CoinToCheck coinToCheck = new CoinToCheck(item);
                        List<String> coinToChecksNameList = getCoinsToCheck();
                        Log.i("DIT IS DE UITKOMST: ", "" + coinToChecksNameList.contains(coinToCheck.name));
                        if (!coinToChecksNameList.contains(coinToCheck.name)) {
                            coinToCheck.save();
                            Snackbar.make(mListView, R.string.savedCoinToCheck, Snackbar.LENGTH_SHORT).show();
                            UpdateUI();
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Snackbar.make(mListView, R.string.duplicateCoinInput, Snackbar.LENGTH_SHORT).show();
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
                spinnerDialog.showSpinerDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_remove_all:
                CoinToCheck.deleteAll(CoinToCheck.class);
                UpdateUI();
                Snackbar.make(this.mListView, R.string.deleteAllNotification, Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.action_refresh:
                UpdateUI();
                Snackbar.make(this.mListView, R.string.refreshNotification, Snackbar.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void UpdateUI() {
        mList.clear();
        mNameList.clear();
        new GetCoinsJSON().execute("https://api.coinmarketcap.com/v1/ticker/?convert=EUR");
        mAdapter = new ListAdapter(mList, LayoutInflater.from(getApplicationContext()));
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private class GetCoinsJSON extends AsyncTask<String, String, String> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            List<String> coinToChecksNameList = getCoinsToCheck();

            try {
                JSONArray coinArray = new JSONArray(result);
                for (int i = 0; i <= coinArray.length(); i++) {
                    JSONObject coin = coinArray.getJSONObject(i);
                    mNameList.add(coin.getString("name"));
                    if (coinToChecksNameList.contains(coin.getString("name"))) {
                        CryptoCoin cryptoCoin = new CryptoCoin();
                        cryptoCoin.setId(coin.getString("id"));
                        cryptoCoin.setName(coin.getString("name"));
                        cryptoCoin.setSymbol(coin.getString("symbol"));
                        try {
                            cryptoCoin.setPercent_change_1h(Double.parseDouble(coin.getString("percent_change_24h")));
                        } catch (NumberFormatException e) {
                            cryptoCoin.setPercent_change_1h(0.0);
                        }
                        try {
                            cryptoCoin.setPrice_usd(Double.parseDouble(coin.getString("price_usd")));
                        } catch (NumberFormatException e) {
                            cryptoCoin.setPrice_usd(0.0);
                        }
                        try {
                            cryptoCoin.setPrice_eur(Double.parseDouble(coin.getString("price_eur")));
                        } catch (NumberFormatException e) {
                            cryptoCoin.setPrice_eur(0.0);
                        }
                        mList.add(cryptoCoin);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getCoinsToCheck() {
        List<CoinToCheck> coinToChecksList = CoinToCheck.listAll(CoinToCheck.class);
        List<String> coinToChecksNameList = new ArrayList<>(coinToChecksList.size());
        for (CoinToCheck coinsToCheck : coinToChecksList) {
            coinToChecksNameList.add(coinsToCheck != null ? coinsToCheck.getName() : null);
        }
        System.out.println("this: " + coinToChecksNameList.toString());
        return coinToChecksNameList;
    }
}
