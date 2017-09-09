package com.productlistdemo.Transactions;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.productlistdemo.view.MainActivity;
import com.productlistdemo.R;

import java.util.ArrayList;

/**
 * Created by Putra_Osi_PC on 6/21/2017.
 */

public class TransactionListAdapter extends BaseAdapter {

    private final static String TAG = "Transaction List Adapter";

    private Context context;
    private  int layout;
    private ArrayList<Transaction> transactionList;

    public TransactionListAdapter(Context context, int layout, ArrayList<Transaction> transactionList) {
        this.context = context;
        this.layout = layout;
        this.transactionList = transactionList;
    }

    @Override
    public int getCount() {
        return transactionList.size();
    }

    @Override
    public Object getItem(int position) {
        return transactionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView nameTxt, txtPrice;
        EditText qtyEdt;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View row = view;
        TransactionListAdapter.ViewHolder holder = new TransactionListAdapter.ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.nameTxt = (TextView) row.findViewById(R.id.nameTxt);
            holder.qtyEdt = (EditText) row.findViewById(R.id.qtyEdt);
            holder.txtPrice = (TextView) row.findViewById(R.id.priceTxt);
            row.setTag(holder);
        }
        else {
            holder = (TransactionListAdapter.ViewHolder) row.getTag();
        }

        final Transaction transaction = transactionList.get(position);

        holder.nameTxt.setText(transaction.getName());
        holder.qtyEdt.setText(transaction.getQty());
        holder.txtPrice.setText(transaction.getPrice());

        holder.nameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.transactionActivity);
                builder.setTitle("Informasi");
                builder.setMessage("Untuk Mengahapus silahkan tekan yang lama pada nama produk");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        holder.nameTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final CharSequence[] dialogItem = {
                        "Yes",
                        "No"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.transactionActivity);
                builder.setTitle("Apakah anda yakin ingin menghapus?");
                builder.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                    switch(item){
                        case 0 :
                            Log.i(TAG, "onClick: YES "+transaction.getId());
                            MainActivity.sqLiteHelper.deleteDataTrans(String.valueOf(transaction.getId()));
                            TransactionActivity.transactionActivity.refreshListTrans();
                            break;
                        case 1 :
                            Log.i(TAG, "onClick: NO "+transaction.getId());
                            break;
                    }
                    }
                });
                builder.show();

                return true;
            }
        });

        holder.qtyEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count > 0){
                    String dataTemp = String.valueOf(s);
                    int data = Integer.parseInt(dataTemp) * Integer.parseInt(transaction.getPrice());

                    Log.i(TAG, "onTextChanged: data: "+data);

                    MainActivity.sqLiteHelper.updateDataTrans(
                            transaction.getName(),
                            dataTemp,
                            transaction.getPrice(),
                            String.valueOf(transaction.getId())
                    );

                    TransactionActivity.transactionActivity.refreshListTrans();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return row;
    }

}
