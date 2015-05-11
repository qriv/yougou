package com.gsh.app.client.mall.https.model;

import java.io.Serializable;

/**
 * Created by taosj on 15/2/27.
 */
//CashCouponDetail 代金券详情
public class M15 implements Serializable {
    public long id;
    public int number;
    public String constraint;//满多少元
    public long beginOn;
    public long endOn;
    public String status;

    //    local
    public boolean checked;

    public M15() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        M15 m15 = (M15) o;

        if (id != m15.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public M15(int id, int number, String constraint, long beginOn, long endOn) {
        this.id = id;
        this.number = number;
        this.constraint = constraint;
        this.beginOn = beginOn;
        this.endOn = endOn;
    }
}

