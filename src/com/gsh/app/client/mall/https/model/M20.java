package com.gsh.app.client.mall.https.model;

import java.io.Serializable;

/**
 * Created by taosj on 15/2/27.
 */
//PreferentialProduct 限时特惠商品
public class M20 implements Serializable {
    public long id;
    public String name;
    public double price;
    public double orginPrice;
    public long endOn;
    public String picturePath;
    public long goodsId;
    public int saleCount;//商品数量
    public int peopleCount;//当前抢购人数 add
    public long time;//抢光所用时间 add

    //    local
    public long remain;//剩余时间
}
