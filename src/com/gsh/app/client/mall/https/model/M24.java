package com.gsh.app.client.mall.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/2/27.
 */
//Comments 所有评论
public class M24 implements Serializable {
    public int count;
    public int countA;
    public int countB;
    public int countC;
    public int countHasPicture;
    public List<M5> comments;
}

