package com.gsh.app.client.mall;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import com.litesuits.common.utils.AppUtil;

/**
 * @author Tan Chunmao
 */
public enum ShareApp {
    weibo(R.color.ui_weibo, R.drawable.ui_weibo_transparent, R.string.app_sina_weibo, SinaWeibo.NAME, AppUtil.WEIBO_PACKAGE),
    tencent_weibo(R.color.ui_tencent_weibo, R.drawable.ui_tweibo_transparent, R.string.app_tencent_weibo, TencentWeibo.NAME, ""),
    tencent_qq(R.color.ui_qq, R.drawable.ui_qq_trasparent, R.string.app_qq, QQ.NAME, AppUtil.QQ_PACKAGE),
    qzone(R.color.ui_qzone, R.drawable.ui_qzone_transparent, R.string.app_qzone, QZone.NAME, ""),
    wechat(R.color.ui_wechat, R.drawable.ui_wechat_trasparent, R.string.app_wechat, Wechat.NAME, AppUtil.WECHAT_PACKAGE),
    wechat_moments(R.color.ui_wechat_moments, R.drawable.ui_moment_transparent, R.string.label_share_wechat_circle, WechatMoments.NAME, AppUtil.WECHAT_PACKAGE),;
    private int backgroundRid;
    private int iconRid;
    private int labelRid;
    private String platformName;
    private String packageName;
    private boolean installed;

    private ShareApp(int backgroundRid, int iconRid, int labelRid, String platformName, String packageName) {
        this.backgroundRid = backgroundRid;
        this.iconRid = iconRid;
        this.labelRid = labelRid;
        this.platformName = platformName;
        this.packageName = packageName;
        this.installed = false;
    }

    public int getIconRid() {
        return iconRid;
    }

    public int getBackgroundRid() {
        return backgroundRid;
    }

    public void setIconRid(int iconRid) {
        this.iconRid = iconRid;
    }

    public int getLabelRid() {
        return labelRid;
    }

    public void setLabelRid(int labelRid) {
        this.labelRid = labelRid;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public int getAppName() {
        return wechat_moments == this ? R.string.app_wechat : labelRid;
    }
}
