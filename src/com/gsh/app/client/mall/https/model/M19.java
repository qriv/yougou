package com.gsh.app.client.mall.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/2/27.
 */
//OrderDetail 订单详情
public class M19 implements Serializable {
    public long id;
    public String orderNo;//订单编号，用于界面显示
    public String payNo;//订单流水号，用于生成支付宝和微信加密支付码
    public String status;//订单状态
    public List<M10> goods;//商品
    public M18 address;
    public M39 deliverInfo;
    public M27 worker;
    public double total_price;//订单总金额
    public int score;//使用积分
    public int coupon;//使用代金券总价值
    public double pay_price;//实际支付金额

    //    local
    public boolean deletePermitted;//允许删除
    public boolean commentPermitted;//允许评论


    public M19() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        M19 m19 = (M19) o;

        if (id != m19.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public M19(long id, String orderNo, String status, List<M10> goods, M27 worker) {
        this.id = id;
        this.orderNo = orderNo;
        this.status = status;
        this.goods = goods;
        this.worker = worker;
    }
}
