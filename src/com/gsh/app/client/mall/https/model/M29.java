package com.gsh.app.client.mall.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/3/2.
 */
public class M29 implements Serializable {
    public List<M30> barGoods;
    public String message;
    public String messageId;
    public long endOn;
    public M30 limitedGoods;
    public String advanceBookingBarPicturePath;
    public List<M30> advanceBookingGoods;
    public List<M30> hotGoods;
}
