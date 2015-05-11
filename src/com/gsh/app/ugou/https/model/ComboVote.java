package com.gsh.app.ugou.https.model;

import java.io.Serializable;

/**
 * Created by taosj on 15/4/28.
 */
public class ComboVote implements Serializable {
    public String voterName;
    public long votingTime;
    public int star;
    public String content;
    public String avatarPath;
}
