package com.example.fleanegan.reiserechner;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by fleanegan on 28.02.17.
 */

public class User implements Serializable {


    static BigDecimal totalAmount;
    static int numberOfUsers;
    static ArrayList<Item> itemList = new ArrayList<>();
    private String name;
    Integer id;
    private ArrayList<Item> boughtItems = new ArrayList<>();
    private BigDecimal totalDispense = new BigDecimal(0);


    public User(String name, Integer id) {
        if (totalAmount == null) User.totalAmount = new BigDecimal(0);
        this.name = name;
        User.numberOfUsers++;
        this.id = id;
    }

    public void addItem(Item item) {
        this.boughtItems.add(item);
        User.itemList.add(item);
        this.totalDispense = this.totalDispense.add(item.getPrice());
        User.totalAmount = User.totalAmount.add(item.getPrice());
    }

    public void removeItem(int id) {
        User.totalAmount = User.totalAmount.subtract(this.boughtItems.get(id).getPrice());
        this.totalDispense = this.totalDispense.subtract(this.boughtItems.get(id).getPrice());
        User.itemList.remove(this.boughtItems.get(id));
        this.boughtItems.remove(id);
    }

    public ArrayList<Item> getBoughtItems() {
        return this.boughtItems;
    }

    public BigDecimal getTotalDispense() {
        return this.totalDispense;
    }

    public String getName() {
        return this.name;
    }


}
