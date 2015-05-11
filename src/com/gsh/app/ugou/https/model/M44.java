package com.gsh.app.ugou.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/4/15.
 */
public class M44 implements Serializable {
    public long id;
    public String title;
    public long createdDate;
    public String fee;
    public String summary;
    public String description;//html
    public int likeCount;
    public int reviewCount;
    public int shareCount;
    public String shareUrl;
    public String mainPicturePath;
    public String keyword;
    public boolean hot;
    public List<M45> details;
    public String picturePath;
}
