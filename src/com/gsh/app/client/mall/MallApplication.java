package com.gsh.app.client.mall;

import android.content.Context;
import android.content.SharedPreferences;
import cn.sharesdk.framework.ShareSDK;
import com.litesuits.ApplicationBase;

/**
 * @author Tan Chunmao
 */
public class MallApplication extends ApplicationBase {
    /*molin release test*/
    //打包前设置成false
    public static final boolean test = false;
    public static Context context;
    public static SharedPreferences preferences;
    public static User user;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        initUser();
        ShareSDK.initSDK(getApplicationContext());//initialize share sdk
    }


    private void initUser() {
        user = User.load(User.class);
    }

    public void saveUser() {
        user.save();
    }
}
