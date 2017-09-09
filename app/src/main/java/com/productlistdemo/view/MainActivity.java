package com.productlistdemo.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.productlistdemo.Products.Product;
import com.productlistdemo.Products.ProductAdd;
import com.productlistdemo.Products.ProductEdit;
import com.productlistdemo.Products.ProductListAdapter;
import com.productlistdemo.R;
import com.productlistdemo.settings.SQLiteHelper;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "Main Activity";

    private FloatingActionButton fab;
    private ViewStub gridStub;
    private ViewStub listStub;
    private ListView listView;
    private GridView gridView;
    private ImageView imageView;
    private ProductListAdapter productListAdapter;
    private ArrayList<Product> productList;
    private int currentViewMode = 0;

    private static final int VIEW_MODE_LISTVIEW = 0;
    private static final int VIEW_MODE_GRIDVIEW = 1;

    private String[] daftar;

    final int REQUEST_CODE_GALLERY = 999;

    public static SQLiteHelper sqLiteHelper;
    public static MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        gridStub = (ViewStub) findViewById(R.id.stub_grid);
        listStub = (ViewStub) findViewById(R.id.stub_list);

        //  inflate ViewStub before get view
        gridStub.inflate();
        listStub.inflate();

        listView = (ListView) findViewById(R.id.myListView);
        gridView = (GridView) findViewById(R.id.gridView);

        sqLiteHelper = new SQLiteHelper(this);
        mainActivity  = this;

        refreshList();
    }

    private void switchView(){
        if (VIEW_MODE_LISTVIEW == currentViewMode){
            //  Display listview
            listStub.setVisibility(View.VISIBLE);
            //  Hide gridview
            gridView.setVisibility(View.INVISIBLE);
        }else{
            //  Hide listview
            listStub.setVisibility(View.GONE);
            //  Display gridview
            gridView.setVisibility(View.VISIBLE);
        }

        setAddapters();
    }

    private void setAddapters(){
        if (VIEW_MODE_LISTVIEW == currentViewMode){
            productListAdapter = new ProductListAdapter(this, R.layout.product_list_item, productList);
            listView.setAdapter(productListAdapter);
        }else{
            productListAdapter = new ProductListAdapter(this, R.layout.product_grid_item, productList);
            gridView.setAdapter(productListAdapter);
        }
    }

    //  Start Build Listener
    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final String selection = daftar[position];
            final CharSequence[] dialogItem = {
                    "Ubah Data",
                    "Hapus Data"
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Informasi Pilihan");
            builder.setItems(dialogItem, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch(item){
                        case 0 :
                            Intent intent = new Intent(MainActivity.this, ProductEdit.class);
                            intent.putExtra("Id", selection);
                            startActivity(intent);
                            break;
                        case 1 :
                            sqLiteHelper.deleteDataProduct(
                                    selection.toString().trim()
                            );
                            Toast.makeText(getApplicationContext(), "Berhasil menghapus data!", Toast.LENGTH_SHORT).show();
                            refreshList();
                            break;
                    }
                }
            });
            builder.show();
        }
    };


    private OnClickListener mFab = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ProductAdd.class);
            startActivity(intent);
        }
    };
    //  End Build Listener

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.switch_item:
                if (VIEW_MODE_LISTVIEW == currentViewMode){
                    currentViewMode = VIEW_MODE_GRIDVIEW;
                }else {
                    currentViewMode = VIEW_MODE_LISTVIEW;
                }

                //  switch view
                switchView();

                //  sace view mode in share references
                SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("currentViewMode", currentViewMode);
                editor.commit();
                break;
            case R.id.export_item:
                Log.i(TAG, "onOptionsItemSelected: export item");
                sqLiteHelper.exportDB();
                break;
        }

        return true;
    }

    public void refreshList(){

        productList = new ArrayList<>();
        Cursor cursor = sqLiteHelper.getData("SELECT * FROM PRODUCT ORDER BY name");
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

        listView = (ListView) findViewById(R.id.myListView);
        productListAdapter = new ProductListAdapter(this, R.layout.product_list_item, productList);
        listView.setAdapter(productListAdapter);

        productListAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences = getSharedPreferences("ViewMode", MODE_PRIVATE);
        currentViewMode = sharedPreferences.getInt("currentViewMode", VIEW_MODE_LISTVIEW); //   Default is view listview

        //  Register item click
        listView.setOnItemClickListener(onItemClick);
        gridView.setOnItemClickListener(onItemClick);
        fab.setOnClickListener(mFab);

        switchView();
    }
}
