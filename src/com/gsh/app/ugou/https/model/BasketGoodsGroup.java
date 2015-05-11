package com.gsh.app.ugou.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/4/27.
 */
public class BasketGoodsGroup implements Serializable{

    public String categoryName;
    public List<BasketGoods> basketGoodsDTOs;
}
