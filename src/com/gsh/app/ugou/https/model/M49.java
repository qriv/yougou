package com.gsh.app.ugou.https.model;

import com.gsh.app.client.mall.https.model.M4;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/4/14.
 */
public class M49 implements Serializable {
    public long id;
    public long date;
    public int order;
    public int status;
    public boolean canRecovery;
    public boolean canDelay;
    public String orderNo;
    public List<M4> goods;
}
