package com.gsh.app.client.mall.https.model;

import java.io.Serializable;

/**
 * Created by taosj on 15/2/27.
 */
//AdvanceBookingProduct 预售商品
public class M21 implements Serializable {
    public long id;
    public long goodsId;
    public String picturePath;
    public String name;
    public double price;
    public double orginPrice;
    public int peopleCount;//当前预订人数 add
    public int allCount;//预售总量 add
    public int remainCount;//当前剩余 add
}
