package com.gsh.app.client.mall.https.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by taosj on 15/2/27.
 */
//CartGoods 购物车商品
public class M10 implements Serializable {
    public long id;
    public String name;
    public String picturePath;
    public double price;
    public int stock;//库存数量
    public int number;//购买数量
    public long googdId;
    public boolean isUndercarriage;
    public double orginPrice;
    public String description;//单份商品包含重量，如水果4.5斤，油2升

    //add
    public boolean commented;//是否已经评论


    public boolean isAvailable() {
        return stock > 0 && !isUndercarriage;
    }

    public M10(long id, String name, String picturePath, double price, int number, String description) {
        this.id = id;
        this.name = name;
        this.picturePath = picturePath;
        this.price = price;
        this.number = number;
        this.description = description;
    }

    public M10() {
    }
}
