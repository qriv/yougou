package com.gsh.app.ugou.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author Tan Chunmao
 *         套餐详情
 */
public class M43 implements Serializable {
    public long id;
    public String name;
    public double price;
    public String summary;//描述
    public int deliverCount;
    public String picturePath;
    public List<M51> baskets;
    public double star;//评分
    public int commentCount;//评价人数
    public int saleCount;
    public String saleTip;//购买须知
    public String description;//图文详情，html

    public int localPicture;
    public boolean big;
    public String mainPicturePath;

    public M43() {
    }


    public M43(long id, String summary, double price, String name, String picturePath, int saleCount, int commentCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.summary = summary;
        this.picturePath = picturePath;
        this.saleCount = saleCount;
        this.commentCount = commentCount;
    }
}
