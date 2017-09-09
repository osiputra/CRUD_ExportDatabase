package com.productlistdemo.Transactions;

/**
 * Created by Putra_Osi_PC on 6/21/2017.
 */

public class Transaction {

    private final static String TAG = "Transaction";

    private int id;
    private String name;
    private String qty;
    private String price;

    public Transaction(String name, String qty, String price, int id) {
        this.name = name;
        this.qty = qty;
        this.price = price;
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

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
