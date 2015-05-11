package com.gsh.app.client.mall;

import android.text.TextUtils;
import com.gsh.app.client.mall.https.model.M0;
import com.litesuits.common.cache.XmlCacheModel;

/**
 * @author Tan Chunmao
 */
public class User extends XmlCacheModel {
    private long id;
    private long communityId;
    private String nickname;
    private String avatarPath;
    private String token;
    private String localAvatarPath;
    private boolean hasSetPaymentPassword;
    private boolean paymentPasswordOn;
    private boolean notified;


    public long getStationId() {
        return stationId;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
    }

    private long stationId;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    private String stationName;

    public User() {
        reset();
    }

    public void reset() {
        id = -1;
        communityId = -1;
        nickname = "";
        avatarPath = "";
        token = "";
        stationName = "";
        localAvatarPath = "";
    }

    public void update(M0 m0) {
        id = m0.id;
        communityId = m0.communityId;
        nickname = m0.nickname;
        avatarPath = m0.avatarPath;
        token = m0.token;
        hasSetPaymentPassword = m0.hasSetPaymentPassword;
        paymentPasswordOn = m0.paymentPasswordOn;
        stationId = m0.stationId;
        stationName = m0.stationName;
        if (TextUtils.isEmpty(nickname)) {
            nickname = "人民优购用户_" + id;
        }
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCommunityId(long communityId) {
        this.communityId = communityId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public long getCommunityId() {
        return communityId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public String getToken() {
        return token;
    }

    public boolean communityChoosed() {
        return communityId != -1;
    }

    public boolean loggedIn() {
        return id != -1 && !TextUtils.isEmpty(token);
    }

    public String getLocalAvatarPath() {
        return localAvatarPath;
    }

    public void setLocalAvatarPath(String localAvatarPath) {
        this.localAvatarPath = localAvatarPath;
    }

    public boolean isHasSetPaymentPassword() {
        return hasSetPaymentPassword;
    }

    public void setHasSetPaymentPassword(boolean hasSetPaymentPassword) {
        this.hasSetPaymentPassword = hasSetPaymentPassword;
    }

    public boolean isPaymentPasswordOn() {
        return paymentPasswordOn;
    }

    public void setPaymentPasswordOn(boolean paymentPasswordOn) {
        this.paymentPasswordOn = paymentPasswordOn;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }
}
