package com.gsh.app.client.mall.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/2/27.
 */
//Comment 评论
public class M5 implements Serializable {
    public long id;
    public String nickname;
    public long commentOn;
    public int score;
    public String comment;
    public List<String> picturePaths;
    public long bargainOn;
    public String avatarPath;
    public String level;
}
