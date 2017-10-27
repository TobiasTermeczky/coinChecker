package nl.yzaazy.coinchecker;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import nl.yzaazy.coinchecker.Adapter.ListAdapter;
import nl.yzaazy.coinchecker.Helpers.CoinsDataGetter;
import nl.yzaazy.coinchecker.Helpers.CoinsGetter;
import nl.yzaazy.coinchecker.Helpers.SettingsHelper;
import nl.yzaazy.coinchecker.Helpers.SpinnerDialog;
import nl.yzaazy.coinchecker.Helpers.SwipeDismissListViewtouchListener;
import nl.yzaazy.coinchecker.Interface.OnSpinnerItemClick;
import nl.yzaazy.coinchecker.Interface.RefreshInterface;
import nl.yzaazy.coinchecker.Objects.Coin;

public class MainActivity extends AppCompatActivity implements RefreshInterface {

    private SettingsHelper mSettingsHelper = new SettingsHelper();
    private List<Coin> mSpinnerList = new ArrayList<>();
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
        mListView.setOnTouchListener(new SwipeDismissListViewtouchListener(mListView,
                new SwipeDismissListViewtouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return !mCoinList.get(position).getLocked();
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            mCoinList.get(position).removeIsChecked(getApplicationContext());
                            mCoinList.remove(position);
                            mAdapter.notifyDataSetChanged();
                            Snackbar.make(mListView, R.string.action_remove, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }));
        mCoinList = Coin.find(Coin.class, "is_checked = ?", "1");
        mAdapter = new ListAdapter(getApplicationContext(), mCoinList, LayoutInflater.from(getApplicationContext()));
        mListView.setAdapter(mAdapter);



        //Swipe Refresh Layout
        mSwipeRefreshLayout = findViewById(R.id.srlCoins);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                updateUI();
            }
        });
        updateUI();
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
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.getItem(0);
        switch (mSettingsHelper.getCurrency()) {
            case "dollar":
                item.setTitle(R.string.action_switch_currency_euro);
                item.setIcon(R.drawable.ic_dollar);
                break;
            case "euro":
                item.setTitle(R.string.action_switch_currency_bitcoins);
                item.setIcon(R.drawable.ic_euro);
                break;
            case "btc":
                item.setTitle(R.string.action_switch_currency_dollar);
                item.setIcon(R.drawable.ic_bitcoin);
        }
        item = menu.getItem(1);
        switch (mSettingsHelper.getSortOrder()) {
            case "index":
                item.setTitle(R.string.action_order_descending);
                item.setIcon(R.drawable.ic_index);
                break;
            case "descending":
                item.setTitle(R.string.action_order_ascending);
                item.setIcon(R.drawable.ic_descending);
                break;
            case "ascending":
                item.setTitle(R.string.action_order_index);
                item.setIcon(R.drawable.ic_ascending);
                break;
        }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle(R.string.remove_all_question);
                builder.setMessage(R.string.remove_all_info);
                builder.setNegativeButton(R.string.cancel_option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCoinList = Coin.find(Coin.class, "is_checked = ?", "1");
                        for (Coin coin : mCoinList) {
                            if (!coin.getLocked()) {
                                coin.removeIsChecked(getApplicationContext());
                            }
                        }
                        refresh();
                        Snackbar.make(mListView, R.string.delete_all_notification, Snackbar.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                break;
            case R.id.switch_currency:
                mSettingsHelper.switchCurrency();
                switch (mSettingsHelper.getCurrency()) {
                    case "dollar":
                        item.setTitle(R.string.action_switch_currency_euro);
                        item.setIcon(R.drawable.ic_dollar);
                        Snackbar.make(this.mListView, R.string.switch_currency_dollar, Snackbar.LENGTH_SHORT).show();
                        break;
                    case "euro":
                        item.setTitle(R.string.action_switch_currency_bitcoins);
                        item.setIcon(R.drawable.ic_euro);
                        Snackbar.make(this.mListView, R.string.switch_currency_euro, Snackbar.LENGTH_SHORT).show();
                        break;
                    case "btc":
                        item.setTitle(R.string.action_switch_currency_dollar);
                        item.setIcon(R.drawable.ic_bitcoin);
                        Snackbar.make(this.mListView, R.string.switch_currency_bitcoins, Snackbar.LENGTH_SHORT).show();
                }
                refresh();
                break;
            case R.id.force_database:
                mSettingsHelper.setJSONDate(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
                Snackbar.make(this.mListView, R.string.force_database, Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.order:
                mSettingsHelper.switchOrder();
                switch (mSettingsHelper.getSortOrder()) {
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
            case R.id.action_lock_all:
                for (Coin coin : mCoinList) {
                    coin.setLocked(true);
                }
                Snackbar.make(this.mListView, R.string.all_coins_locked, Snackbar.LENGTH_SHORT).show();
                refresh();
                break;
            case R.id.action_unlock_all:
                for (Coin coin : mCoinList) {
                    coin.setLocked(false);
                }
                Snackbar.make(this.mListView, R.string.all_coins_unlocked, Snackbar.LENGTH_SHORT).show();
                refresh();
                break;
            case R.id.delete_icon_small:
                AlertDialog.Builder builderIcon = new AlertDialog.Builder(MainActivity.this);
                builderIcon.setCancelable(true);
                builderIcon.setTitle(R.string.remove_all_question);
                builderIcon.setMessage(R.string.remove_all_icons_info);
                builderIcon.setNegativeButton(R.string.cancel_option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builderIcon.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Iterator<Coin> coins = Coin.findAll(Coin.class);
                        while (coins.hasNext()) {
                            coins.next().deleteSmallIconLocal(getApplicationContext());
                        }
                        Snackbar.make(mListView, R.string.icon_remove_all, Snackbar.LENGTH_SHORT).show();
                    }
                });
                builderIcon.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillFabButton() {
        SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#3F51B5"));
        pDialog.setCancelable(false);
        SpinnerDialog mSpinnerDialog = new SpinnerDialog(MainActivity.this, mSpinnerList);
        CoinsGetter mCoinsGetter = new CoinsGetter(getApplicationContext(), mSpinnerList, mSpinnerDialog, pDialog);
        mCoinsGetter.getAllCoins();
        //todo: create own spinner with better search and other cool stuff like custom list view.
        mSpinnerDialog.bindOnSpinnerListener(new OnSpinnerItemClick() {
            @Override
            public void onClick(Coin coin) {
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
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            Snackbar.make(mListView, R.string.refresh_notification, Snackbar.LENGTH_SHORT).show();
            CoinsDataGetter coinsDataGetter = new CoinsDataGetter(getApplicationContext(), mCoinList, this);
            coinsDataGetter.getData();
        } else {
            Snackbar.make(mListView, R.string.no_internet, Snackbar.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void refresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        mCoinList = Coin.find(Coin.class, "is_checked = ?", "1");
        Collections.sort(mCoinList);
        mAdapter = new ListAdapter(getApplicationContext(), mCoinList, getLayoutInflater());
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
