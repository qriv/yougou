package com.gsh.app.client.mall.https.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by taosj on 15/2/27.
 */
//M4 Product 商品
public class M4 implements Serializable {
    public long id;
    public String name;
    public List<String> picturePaths;
    public double price;
    public int stock;
    public List<M5> commentsSummary;
    public String picturePath;

    public double originPrice;//原定售价
    public int saleCount;//销量，用于排序
    public boolean isOnSale;//是否限时促销
    public long onSaleEndOn;
    public int peopleCount;//购买人数
    public boolean isQuickMode;//是否支持30分钟速达
    public boolean isSupportArrivalPay;//是否支持货到付款
    public int commentCount;//评价人数
    public double rate;//好评度，百分数
    public String shareUrl;

    public int orderCount;//购买数量
    public String unit;//add 规格

    public M4() {
    }

    public M4(long id, String name, String picturePath, String unit, double price, double originPrice) {
        this.id = id;
        this.name = name;
        this.picturePath = picturePath;
        this.unit = unit;
        this.price = price;
        this.originPrice = originPrice;
    }
}
