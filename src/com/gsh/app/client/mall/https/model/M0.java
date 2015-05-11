package com.gsh.app.client.mall.https.model;

import java.io.Serializable;

/**
 * Created by taosj on 15/2/27.
 */
//User 用户信息
//    member/login 返回

public class M0 implements Serializable {
    public long id;
    public long communityId;
    public String mobile;
    public String nickname;
    public String avatarPath;
    public boolean hasSetPaymentPassword;
    public boolean paymentPasswordOn;
    public String token;
    public long stationId;
    public String stationName;
}
