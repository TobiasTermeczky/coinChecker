package nl.yzaazy.coinchecker.Helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import nl.yzaazy.coinchecker.Adapter.SpinnerRecyclerAdapter;
import nl.yzaazy.coinchecker.Interface.RefreshInterface;
import nl.yzaazy.coinchecker.Listener.RecyclerTouchListener;
import nl.yzaazy.coinchecker.Objects.Coin;
import nl.yzaazy.coinchecker.R;

public class SpinnerDialog {
    private List<Coin> items;
    private Activity context;
    private AlertDialog alertDialog;
    private SpinnerRecyclerAdapter adapter;
    private RefreshInterface refreshInterface;


    public SpinnerDialog(Activity activity, List<Coin> items, RefreshInterface refreshInterface) {
        this.items = items;
        this.context = activity;
        this.refreshInterface = refreshInterface;
    }

    public void showSpinnerDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
        View v = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView dialogClose = v.findViewById(R.id.close);
        final EditText searchBox = v.findViewById(R.id.searchBox);

        RecyclerView recyclerView = v.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new SpinnerRecyclerAdapter(context, items);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(context,
                        new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                refreshInterface.setCoinChecked(adapter.getItem(position));
                                alertDialog.dismiss();
                            }
                        }));
        adb.setView(v);
        alertDialog = adb.create();
        alertDialog.setCancelable(true);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.getFilter().filter(searchBox.getText().toString());
            }
        });

        dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}