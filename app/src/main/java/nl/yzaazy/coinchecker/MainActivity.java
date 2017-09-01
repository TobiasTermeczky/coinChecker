package nl.yzaazy.coinchecker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private List<CryptoCoin> mList = new ArrayList<>();
    private ListView mListView;
    private ListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mListView = (ListView) findViewById(R.id.listview);
        UpdateUI();

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialDialog dialog = new MaterialDialog.Builder(MainActivity.this)
                        .title("Contactpersoon Toevoegen")
                        .titleColor(getResources().getColor(R.color.colorPrimary))
                        .backgroundColor(getResources().getColor(android.R.color.white))
                        .customView(R.layout.dialog_add, true)
                        .positiveText("Toevoegen")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                EditText text = (EditText) dialog.findViewById(R.id.add);
                                Toast.makeText(getApplicationContext(), text.getText().toString(), Toast.LENGTH_SHORT).show();

                                mAdapter.notifyDataSetChanged();


                            }
                        })
                        .positiveColor(getResources().getColor(R.color.colorPrimary))
                        .negativeText("Annuleren")
                        .negativeColor(getResources().getColor(R.color.colorAccent))
                        .show();
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
            case R.id.action_settings:
                return true;
            case R.id.action_refresh:
                UpdateUI();
                Snackbar.make(this.mListView, "Meer geld voor KETA!", Snackbar.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void UpdateUI() {
        mList.clear();
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
            try {
                JSONArray coinArray = new JSONArray(result);
                for (int i = 0; i <= coinArray.length(); i++) {
                    JSONObject coin = coinArray.getJSONObject(i);
                    CryptoCoin cryptoCoin = new CryptoCoin();
                    cryptoCoin.setId(coin.getString("id"));
                    cryptoCoin.setName(coin.getString("name"));
                    cryptoCoin.setSymbol(coin.getString("symbol"));
                    try{
                        cryptoCoin.setPercent_change_1h(Double.parseDouble(coin.getString("percent_change_1h")));
                    }catch (NumberFormatException e){
                        cryptoCoin.setPercent_change_1h(0.0);
                    }
                    try{
                        cryptoCoin.setPrice_usd(Double.parseDouble(coin.getString("price_usd")));
                    }catch (NumberFormatException e){
                        cryptoCoin.setPrice_usd(0.0);
                    }
                    try{
                        cryptoCoin.setPrice_eur(Double.parseDouble(coin.getString("price_eur")));
                    }catch (NumberFormatException e){
                        cryptoCoin.setPrice_eur(0.0);
                    }
                    mList.add(cryptoCoin);
                    mAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
}
