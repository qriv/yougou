package com.gsh.app.ugou.https.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by taosj on 15/4/28.
 */
public class Discovery implements Serializable {

    public long id;
    public String title;
    public boolean hot;
    public String fee;
    public String summary;
    public String organizer;

    //统计信息
    public int likeCount;
    public int reviewCount;
    public int shareCount;

    public String description;

    //主图
    public String mainPicturePath;


    public long createdDate;

    public void addShare() {
        shareCount++;
    }
}
