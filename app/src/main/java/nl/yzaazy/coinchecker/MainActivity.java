package nl.yzaazy.coinchecker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import nl.yzaazy.coinchecker.Adapter.ListAdapter;
import nl.yzaazy.coinchecker.Helpers.CoinsDataGetter;
import nl.yzaazy.coinchecker.Helpers.CoinsGetter;
import nl.yzaazy.coinchecker.Helpers.SettingsHelper;
import nl.yzaazy.coinchecker.Interface.RefreshInterface;
import nl.yzaazy.coinchecker.Objects.Coin;

public class MainActivity extends AppCompatActivity implements RefreshInterface {

    private SettingsHelper mSettingsHelper = new SettingsHelper();
    private ArrayList<String> mSpinnerList = new ArrayList<>();
    private List<Coin> mCoinList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private ListAdapter mAdapter;
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingsHelper.checkSettings();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mListView = findViewById(R.id.lvCoins);


        mCoinList = Coin.find(Coin.class, "is_checked = ?", "1");
        mAdapter = new ListAdapter(getApplicationContext(), mCoinList, LayoutInflater.from(getApplicationContext()));
        mListView.setAdapter(mAdapter);


        updateUI();
        //Swipe Refresh Layout
        mSwipeRefreshLayout = findViewById(R.id.srlCoins);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateUI();
                mSwipeRefreshLayout.setRefreshing(true);
                Snackbar.make(mListView, R.string.refresh_notification, Snackbar.LENGTH_SHORT).show();
            }
        });
        //Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillFabButton();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
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
                mCoinList = Coin.find(Coin.class, "is_checked = ?", "1");
                for(Coin coin : mCoinList) {
                    coin.removeIsChecked(getApplicationContext());
                }
                refresh();
                Snackbar.make(this.mListView, R.string.delete_all_notification, Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.switch_currency:
                mSettingsHelper.switchCurrency();
                switch (mSettingsHelper.getCurrency()){
                    case "dollar":
                        item.setTitle(R.string.action_switch_currency_dollar);
                        item.setIcon(R.drawable.ic_dollar);
                        Snackbar.make(this.mListView, R.string.switch_currency_euro, Snackbar.LENGTH_SHORT).show();

                        break;
                    case "euro":
                        item.setTitle(R.string.action_switch_currency_euro);
                        item.setIcon(R.drawable.ic_euro);
                        Snackbar.make(this.mListView, R.string.switch_currency_dollar, Snackbar.LENGTH_SHORT).show();
                        break;
                }
                refresh();
                break;
            case R.id.force_database:
                mSettingsHelper.setJSONDate(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
                Snackbar.make(this.mListView, R.string.force_database, Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.order:
                mSettingsHelper.switchOrder();
                switch (mSettingsHelper.getSortOrder()){
                    case "index":
                        item.setTitle(R.string.action_order_descending);
                        item.setIcon(R.drawable.ic_index);
                        Snackbar.make(this.mListView, R.string.order_index, Snackbar.LENGTH_SHORT).show();
                        break;
                    case "descending":
                        item.setTitle(R.string.action_order_ascending);
                        item.setIcon(R.drawable.ic_descending);
                        Snackbar.make(this.mListView, R.string.order_descending, Snackbar.LENGTH_SHORT).show();
                        break;
                    case "ascending":
                        item.setTitle(R.string.action_order_index);
                        item.setIcon(R.drawable.ic_ascending);
                        Snackbar.make(this.mListView, R.string.order_ascending, Snackbar.LENGTH_SHORT).show();
                        break;
                }
                refresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillFabButton() {
        SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#3F51B5"));
        pDialog.setCancelable(false);
        SpinnerDialog mSpinnerDialog = new SpinnerDialog(MainActivity.this, mSpinnerList, MainActivity.this.getResources().getString(R.string.add_coin));
        CoinsGetter mCoinsGetter = new CoinsGetter(getApplicationContext(), mSpinnerList, mSpinnerDialog, pDialog);
        mCoinsGetter.getAllCoins();
        //todo: create own spinner with better search and other cool stuff like custom list view.
        mSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                Coin coin = Coin.find(Coin.class, "name = ?", item).get(0);
                if (coin.getIsChecked()) {
                    Snackbar.make(mListView, R.string.duplicate_coin_input, Snackbar.LENGTH_SHORT).show();
                } else {
                    coin.setIsChecked(getApplicationContext(), MainActivity.this);
                    mCoinList = Coin.find(Coin.class, "is_checked = ?", "1");
                    Snackbar.make(mListView, R.string.saved_coin_to_check, Snackbar.LENGTH_SHORT).show();
                    updateUI();
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateUI() {
        CoinsDataGetter coinsDataGetter = new CoinsDataGetter(getApplicationContext(), mCoinList, this);
        coinsDataGetter.getData();
    }

    @Override
    public void refresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        mCoinList = Coin.find(Coin.class, "is_checked = ?", "1");
        Collections.sort(mCoinList);
        mAdapter = new ListAdapter(getApplicationContext(), mCoinList, LayoutInflater.from(getApplicationContext()));
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
