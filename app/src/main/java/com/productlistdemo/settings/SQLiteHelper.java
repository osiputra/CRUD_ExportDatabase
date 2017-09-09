package com.productlistdemo.settings;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.productlistdemo.view.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by Quoc Nguyen on 13-Dec-16.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private final static String TAG = "SQLite Helper";

    public static final String DATABASE_NAME = "ProductDB";
    private static final int DATABASE_VERSION = 1;

    public SQLiteDatabase mDataBase;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    // Start SQL Product
    public void insertDataProduct(String name, String unit, String price, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO PRODUCT VALUES (NULL, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString(2, unit);
        statement.bindString(3, price);
        statement.bindBlob(4, image);

        statement.executeInsert();
    }

    public void updateDataProduct(String name, String unit, String price, byte[] image, String id){

        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE PRODUCT SET " +
                "name = ?, " +
                "unit = ?, " +
                "price = ?, " +
                "image = ? " +
                "WHERE Id = ?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString(2, unit);
        statement.bindString(3, price);
        statement.bindBlob(4, image);
        statement.bindString(5, id);

        statement.executeUpdateDelete();
    }

    public void deleteDataProduct(String id){

        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM PRODUCT WHERE Id = ?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, id);

        statement.executeUpdateDelete();
    }
    //  End SQL Product

    // Start SQL Transaction
    public void insertDataTrans(String name, String qty, String price){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO PRODUCTTEMP VALUES (NULL, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString(2, qty);
        statement.bindString(3, price);

        statement.executeInsert();
    }

    public void updateDataTrans(String name, String qty, String price, String id){

        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE PRODUCTTEMP SET " +
                "name = ?, " +
                "qty = ?, " +
                "price = ? " +
                "WHERE Id = ?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString(2, qty);
        statement.bindString(3, price);
        statement.bindString(4, id);

        statement.executeUpdateDelete();
    }



    public void deleteDataTrans(String id){

        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM PRODUCTTEMP WHERE Id = ?";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, id);

        statement.executeUpdateDelete();
    }


    public void deleteAllRecordTrans(){

        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM PRODUCTTEMP";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.executeUpdateDelete();
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }
    //  End SQL Transaction

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                "(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR, " +
                "unit VARCHAR, " +
                "price VARCHAR, " +
                "image BLOB)";

        Log.i(TAG, "onCreate: "+sql);
        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS PRODUCTTEMP" +
                "(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR, " +
                "qty VARCHAR, " +
                "price VARCHAR)";

        Log.i(TAG, "onCreate: "+sql);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "com.productlistdemo" +"/databases/"+DATABASE_NAME;
        String backupDBPath = DATABASE_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(MainActivity.mainActivity, "DB Has Been Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            Log.e(TAG, "exportDB: ",e );
            e.printStackTrace();
        }
    }
}
