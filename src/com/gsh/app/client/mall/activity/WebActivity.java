package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.webkit.WebView;
import com.gsh.app.client.mall.R;
import com.litesuits.android.async.AsyncTask;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.common.utils.FileUtil;

/**
 * Created by taosj on 15/3/26.
 */
public class WebActivity extends ActivityBase {

    @InjectView
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Injector.self.inject(this);
        String title = getIntent().getStringExtra("title");
        setTitleBar(title);
        String htmlFile = getIntent().getStringExtra("html");

        setTitleBar(false, title, RightAction.NONE, "");
        String htmlString = FileUtil.getAssetText(this, htmlFile);
        web.loadDataWithBaseURL(null, htmlString, "text/html", "utf-8", null);
    }
}
