package com.litesuits;

import android.app.Application;

/**
 * Created by taosj on 15/3/19.
 */
public class ApplicationBase extends Application {

    public static ApplicationBase instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
