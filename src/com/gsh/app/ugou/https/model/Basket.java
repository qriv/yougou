package com.gsh.app.ugou.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/4/27.
 */
public class Basket implements Serializable{
    public int axisSerial;
    public List<BasketGoodsGroup> basketGoodsGroups;
}
