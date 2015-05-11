package com.gsh.app.client.mall.https.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by taosj on 15/2/27.
 */
// 创建订单
public class M11 implements Serializable {
    public long id;
    public M18 address;
    public List<M10> goods;
    public List<M10> preGoods;
    public String deliverType;
    public String payType;//支付类型
    public double totalPrice;
    public int saleType; //0:presale 1:normal
    public String orderNo;

    public M33 m33;
}
