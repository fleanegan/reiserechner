package com.example.fleanegan.reiserechner;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by fleanegan on 28.02.17.
 */

public class Item implements Serializable {

    private String name;
    private BigDecimal price = new BigDecimal(0);
    private String description = "";
    private Date timeStamp;

    public Item(String name, String price) {
        this.name = name;
        this.price = this.price.add(new BigDecimal(price));
        this.timeStamp = new Date();
    }


    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(String newPrice) {
        this.price = new BigDecimal(newPrice);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return this.timeStamp;
    }

}
