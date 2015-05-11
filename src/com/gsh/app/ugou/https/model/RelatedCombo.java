package com.gsh.app.ugou.https.model;

import java.io.Serializable;

/**
 * Created by taosj on 15/4/27.
 */
public class RelatedCombo implements Serializable {

    public long comboId;
    public String mainPicturePath;
    public String name;
    public int saleCount;
    public double price;
    public int commentCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatedCombo)) return false;

        RelatedCombo that = (RelatedCombo) o;

        if (comboId != that.comboId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (comboId ^ (comboId >>> 32));
    }
}
