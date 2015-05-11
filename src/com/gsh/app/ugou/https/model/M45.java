package com.gsh.app.ugou.https.model;

import java.io.Serializable;

/**
 * Created by taosj on 15/4/15.
 */
public class M45 implements Serializable {
    public int type;
    public String content;

    public M45() {
    }

    public M45(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public boolean isText() {
        return type == 0;
    }

    public boolean isPicture() {
        return type == 1;
    }
}
