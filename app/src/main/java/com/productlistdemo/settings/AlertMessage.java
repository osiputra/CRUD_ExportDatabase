package com.productlistdemo.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Putra_Osi_PC on 6/16/2017.
 */

public class AlertMessage{

    private final static String TAG = "Alert Message";

    public AlertDialog.Builder alertNetwork(final Context context, final String selection) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Informasi");
        builder.setMessage("Anda yakin ingin mengubah/menghapus product");

        builder.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder;
    }
}
