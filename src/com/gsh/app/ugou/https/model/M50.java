package com.gsh.app.ugou.https.model;

import com.gsh.app.client.mall.https.model.M18;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/16.
 * 创建套餐订单
 */
public class M50 implements Serializable {
    public long orderId;
    public M18 address;

    public M50() {
    }

    public M50(long orderId, M18 address) {
        this.orderId = orderId;
        this.address = address;
    }
}
