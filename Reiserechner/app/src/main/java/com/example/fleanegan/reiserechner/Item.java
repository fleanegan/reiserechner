package com.example.fleanegan.reiserechner;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fleanegan on 28.02.17.
 */

public class Item implements Serializable {

    private String name;
    private BigDecimal price = new BigDecimal(0);
    private String description = "";
    private Date timeStamp;

    public Item(String name, String price, String description) {
        this.name = name;
        this.price = this.price.add(new BigDecimal(price));
        this.description = description;
        this.timeStamp = new Date();
    }

    public Item(String name, String price) {
        this.name = name;
        this.price = this.price.add(new BigDecimal(price));
        this.timeStamp = new Date();
    }


    public BigDecimal getPrice() {
        return this.price;
    }


    public String getName() {
        return this.name;
    }

    public Date getDate() {
        return this.timeStamp;
    }

    public String getTimeStamp() {
        return new SimpleDateFormat("dd.MM.yyyy, HH:mm").format(this.timeStamp);
    }

}
