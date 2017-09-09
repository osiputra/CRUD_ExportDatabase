package com.productlistdemo.Transactions;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.productlistdemo.view.MainActivity;
import com.productlistdemo.Products.Product;
import com.productlistdemo.Products.ProductListAdapter;
import com.productlistdemo.R;

import java.util.ArrayList;

import static com.productlistdemo.view.MainActivity.sqLiteHelper;

public class TransactionActivity extends AppCompatActivity {

    private final static String TAG = "Transaction Activity";

    private GridView gridView;
    private ListView listView;
    private EditText searchEdt;
    private TextView nameTxt;
    private TextView totalTxt;

    private ProductListAdapter productListAdapter;
    private TransactionListAdapter testListAdapter;
    public static TransactionActivity transactionActivity;

    private ArrayList<Product> productList;
    private ArrayList<Transaction> transList = new ArrayList<>();
    private ArrayList<String> testList = new ArrayList<>();;

    private String[] daftar;
    private int index = 0;
    private int total = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        init();
        totalTxt.setText("Rp. "+total);

        MainActivity.sqLiteHelper.deleteAllRecordTrans();

        transactionActivity = this;

        refreshListSearch();
    }


    private void init() {

        totalTxt = (TextView) findViewById(R.id.totalTxt);
         nameTxt = (TextView) findViewById(R.id.nameTxt);

        searchEdt = (EditText) findViewById(R.id.searchEdt);

        gridView = (GridView) findViewById(R.id.searchLis);
        listView = (ListView) findViewById(R.id.item_Lis);
    }

    // Start Build Listenner
    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long idi) {

            Log.i(TAG, "onItemClick: "+position);

            final String selection = daftar[position];

            Cursor cursor = sqLiteHelper.getData("SELECT * FROM PRODUCT WHERE Id = '" + selection + "'");

            cursor.moveToFirst();
            if (cursor.getCount()>0){

                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String unit = cursor.getString(2);
                String price = cursor.getString(3);
                byte[] image = cursor.getBlob(4);

                MainActivity.sqLiteHelper.insertDataTrans(
                        name.toString().trim(),
                        String.valueOf(1),
                        price.toString().trim()
                );
            }

            refreshListTrans();
        }
    };
    //  End Build Listenner

    public void refreshListSearch(){

        productList = new ArrayList<>();
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM PRODUCT ORDER BY name");
        daftar = new String[cursor.getCount()];

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);
            daftar[i] = cursor.getString(0).toString();

            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String unit = cursor.getString(2);
            String price = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            char data[] = price.toCharArray();
            price = "";
            int pos = 0;
            for (int j = data.length - 1; j >= 0; j--){
                if (pos == 3){
                    price = price + ",";
                    price = price + String.valueOf(data[j]);
                    pos = 0;
                }else{
                    price = price + String.valueOf(data[j]);
                    pos++;
                }
            }
            data = price.toCharArray();
            price = "";
            for (int j = data.length - 1; j >= 0; j--){
                price = price + data[j];
            }
            price = "Rp. " + price;

            productList.add(new Product(name, unit, price, image, id));
        }

        gridView = (GridView) findViewById(R.id.searchLis);
        productListAdapter = new ProductListAdapter(this, R.layout.search_grid_item, productList);
        gridView.setAdapter(productListAdapter);

        productListAdapter.notifyDataSetChanged();

        //  Register item click
        gridView.setOnItemClickListener(onItemClick);

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

//                Log.i(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "onTextChanged: ");
                searchList(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

//                Log.i(TAG, "afterTextChanged: ");
            }
        });
    }

    public void refreshListTrans(){

        Cursor cursor = sqLiteHelper.getData("SELECT a.id, a.name, a.qty, b.price FROM PRODUCTTEMP a, PRODUCT b WHERE a.name = b.name");
        cursor.moveToFirst();
        transList.clear();
        total = 0;
        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);

            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String qty = cursor.getString(2);
            String price = cursor.getString(3);
            int a = Integer.parseInt(qty) * Integer.parseInt(price);
            total = total + a;

            transList.add(new Transaction(name, qty, String.valueOf(a), id));

        }

        testListAdapter = new TransactionListAdapter(TransactionActivity.this, R.layout.trans_list_item, transList);
        listView.setAdapter(testListAdapter);

        testListAdapter.notifyDataSetChanged();

        totalTxt.setText("Rp. "+total);
    }

    void searchList(String result){
        productList = new ArrayList<>();
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM PRODUCT WHERE name LIKE '%" + result + "%' ORDER BY name");
        daftar = new String[cursor.getCount()];

        cursor.moveToFirst();
        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);
            daftar[cc] = cursor.getString(0).toString();

            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String unit = cursor.getString(2);
            String price = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            char data[] = price.toCharArray();
            price = "";
            int pos = 0;
            for (int i = data.length - 1; i >= 0; i--){
                if (pos == 3){
                    price = price + ",";
                    price = price + String.valueOf(data[i]);
                    pos = 0;
                }else{
                    price = price + String.valueOf(data[i]);
                    pos++;
                }
            }
            data = price.toCharArray();
            price = "";
            for (int i = data.length - 1; i >= 0; i--){
                price = price + data[i];
            }
            price = "Rp. " + price;

            productList.add(new Product(name, unit, price, image, id));
        }

        productListAdapter = new ProductListAdapter(this, R.layout.search_grid_item, productList);
        gridView.setAdapter(productListAdapter);

        productListAdapter.notifyDataSetChanged();

        //  Register item click
        gridView.setOnItemClickListener(onItemClick);
        
    }
}
