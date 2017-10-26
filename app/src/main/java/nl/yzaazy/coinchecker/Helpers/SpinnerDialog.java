package nl.yzaazy.coinchecker.Helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import nl.yzaazy.coinchecker.Adapter.SpinnerAdapter;
import nl.yzaazy.coinchecker.Interface.OnSpinnerItemClick;
import nl.yzaazy.coinchecker.Objects.Coin;
import nl.yzaazy.coinchecker.R;

public class SpinnerDialog implements AdapterView.OnItemClickListener {
    private List<Coin> items;
    private Activity context;
    private OnSpinnerItemClick onSpinnerItemClick;
    private AlertDialog alertDialog;
    private SpinnerAdapter adapter;


    public SpinnerDialog(Activity activity, List<Coin> items) {
        this.items = items;
        this.context = activity;
    }

    public void bindOnSpinerListener(OnSpinnerItemClick onSpinnerItemClick1) {
        this.onSpinnerItemClick = onSpinnerItemClick1;
    }

    public void showSpinerDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
        View v = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView rippleViewClose = v.findViewById(R.id.close);
        final ListView listView = v.findViewById(R.id.list);
        final EditText searchBox = v.findViewById(R.id.searchBox);
        adapter = new SpinnerAdapter(items, context.getLayoutInflater(), context);
        listView.setAdapter(adapter);
        adb.setView(v);
        alertDialog = adb.create();
        alertDialog.setCancelable(true);
        listView.setOnItemClickListener(this);
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

        rippleViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        onSpinnerItemClick.onClick(adapter.getItem(i));
        alertDialog.cancel();
    }
}