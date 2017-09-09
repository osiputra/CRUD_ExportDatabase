package com.productlistdemo.Products;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.productlistdemo.settings.FormatNumber;
import com.productlistdemo.view.MainActivity;
import com.productlistdemo.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProductEdit extends AppCompatActivity {

    private final static String TAG = "Product Edit";

    private EditText nameEdt;
    private EditText unitEdt;
    private EditText priceEdt;

    private ImageView imageView;

    private Button chooseBtn;
    private Button editBtn;

    private String id;

    final int REQUEST_CODE_GALLERY = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        init();

        priceEdt.addTextChangedListener(new FormatNumber(priceEdt));

        SQLiteDatabase db = MainActivity.sqLiteHelper.getReadableDatabase();

        // get data from sqlite
        Cursor cursor = db.rawQuery("SELECT * FROM PRODUCT WHERE Id = '" + getIntent().getStringExtra("Id") + "'",null);
        cursor.moveToFirst();

        if (cursor.getCount()>0){
            id = cursor.getString(0).toString();

            nameEdt.setText(cursor.getString(1).toString());
            unitEdt.setText(cursor.getString(2).toString());
            priceEdt.setText(cursor.getString(3).toString());

            byte[] productImage = cursor.getBlob(4);
            Bitmap bitmap = BitmapFactory.decodeByteArray(productImage, 0, productImage.length);
            imageView.setImageBitmap(bitmap);
        }

        editBtn.setOnClickListener(mEdit);
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        ProductEdit.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });
    }

    private View.OnClickListener mEdit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String data = priceEdt.getText().toString();
            String[] arrData = data.split(",");
            data = "";
            for (int i = 0; i < arrData.length; i++){
                data = data + arrData[i];
            }

            try{
                MainActivity.sqLiteHelper.updateDataProduct(
                        nameEdt.getText().toString().trim(),
                        unitEdt.getText().toString().trim(),
                        data.toString().trim(),
                        imageViewToByte(imageView),
                        id.toString().trim()
                );
                Toast.makeText(getApplicationContext(), "Berhasil mengubah data!", Toast.LENGTH_SHORT).show();
                nameEdt.setText("");
                unitEdt.setText("");
                priceEdt.setText("");
                imageView.setImageResource(R.drawable.no_image);

                MainActivity.mainActivity.refreshList();
                finish();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private byte[] imageViewToByte(ImageView image) {


        Bitmap originalImage = ((BitmapDrawable)image.getDrawable()).getBitmap();

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        Matrix matrix = new Matrix();
        float scaleWidth = ((float) 200) / width;
        float scaleHeight = ((float) 200) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(0);

        Bitmap resizedBitmap = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        width = resizedBitmap.getWidth();
        height = resizedBitmap.getHeight();
        Log.i(TAG, "imageViewToByte: New Width: "+width);
        Log.i(TAG, "imageViewToByte: New Height: "+height);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

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

    private void init(){
        nameEdt = (EditText) findViewById(R.id.namaProdukEdt);
        unitEdt = (EditText) findViewById(R.id.unitEdt);
        priceEdt = (EditText) findViewById(R.id.priceEdt);

        imageView = (ImageView) findViewById(R.id.imageProductImv);

        chooseBtn = (Button) findViewById(R.id.choiceImageBtn);
        editBtn = (Button) findViewById(R.id.editBtn);
    }
}
