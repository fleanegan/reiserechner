package com.example.fleanegan.reiserechner;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by fleanegan on 02.03.17.
 */

public class Serializer implements Serializable {

    private ArrayList<User> userArrayList = new ArrayList<>();
    private BigDecimal saveTheAmount;
    private int numberOfUsers;


    public void addUser(ArrayList<User> users, BigDecimal saveTheAmount) {
        this.userArrayList = users;
        this.saveTheAmount = saveTheAmount;
        this.numberOfUsers = User.numberOfUsers;
    }


    public ArrayList<User> getUserArrayList() {
        return this.userArrayList;
    }

    public BigDecimal getSaveTheAmount() {
        return this.saveTheAmount;
    }

    public int getNumberOfUsers() {
        return this.numberOfUsers;
    }
}
