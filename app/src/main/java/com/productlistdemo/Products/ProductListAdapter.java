package com.productlistdemo.Products;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.productlistdemo.R;

import java.util.ArrayList;

/**
 * Created by Putra_Osi_PC on 6/14/2017.
 */

public class ProductListAdapter extends BaseAdapter {

    private final static String TAG = "Product List Adapter";

    private Context context;
    private  int layout;
    private ArrayList<Product> productsList;

    public ProductListAdapter(Context context, int layout, ArrayList<Product> productsList) {
        this.context = context;
        this.layout = layout;
        this.productsList = productsList;
    }

    @Override
    public int getCount() {
        return productsList.size();
    }

    @Override
    public Object getItem(int position) {
        return productsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView nameTxt, unitTxt, txtPrice;
    }

    @Override
        public View getView(int position, View view, ViewGroup parent) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.nameTxt = (TextView) row.findViewById(R.id.nameTxt);
            holder.unitTxt = (TextView) row.findViewById(R.id.unitTxt);
            holder.txtPrice = (TextView) row.findViewById(R.id.priceTxt);
            holder.imageView = (ImageView) row.findViewById(R.id.productImv);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Product product = productsList.get(position);

        holder.nameTxt.setText(product.getName());
        holder.unitTxt.setText(product.getUnit());
        holder.txtPrice.setText(product.getPrice());

        byte[] productImage = product.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(productImage, 0, productImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }


}
