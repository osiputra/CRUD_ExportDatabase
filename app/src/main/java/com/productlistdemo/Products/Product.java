package com.productlistdemo.Products;

/**
 * Created by Putra_Osi_PC on 6/14/2017.
 */

public class Product {

    private final static String TAG = "Product";

    private int id;
    private String name;
    private String unit;
    private String price;
    private byte[] image;

    public Product(String name, String unit, String price, byte[] image, int id) {
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.image = image;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
