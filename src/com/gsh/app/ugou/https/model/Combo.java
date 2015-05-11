package com.gsh.app.ugou.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/4/27.
 */
public class Combo implements Serializable {
    public long id;
    public String name;
    public double price;
    public String summary;
    public String deliveryCount;
    public String mainPicturePath;
    public List<Basket> baskets;
    public double star;
    public int commentCount;
    public int saleCount;
    public String description;
    public List<RelatedCombo> relatedCombos;
    public long sellTime;

    public boolean isSellBegin() {
        return System.currentTimeMillis() > sellTime;
    }
}
