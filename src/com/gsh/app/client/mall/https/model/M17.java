package com.gsh.app.client.mall.https.model;

import java.io.Serializable;

/**
 * Created by taosj on 15/2/27.
 */
//BalanceDetail 陌邻余额详情
public class M17 implements Serializable {
    public long id;
    public String item;
    public String orderNo;
    public long createOn;
    public boolean isConsume;
    public double number;
}
