package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.view.View;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;

/**
 * @author Tan Chunmao
 */
public class SettingsActivity extends ActivityBase {
    @InjectView
    private View layout_account, layout_clear_cache, layout_about, next, account_divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Injector.self.inject(this);
        setTitleBar("设置");
        if (MallApplication.user.loggedIn()) {
//            layout_account.setVisibility(View.VISIBLE);
//            account_divider.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
        }
        layout_account.setOnClickListener(onClickListener);
        layout_clear_cache.setOnClickListener(onClickListener);
        layout_about.setOnClickListener(onClickListener);
        next.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(layout_account)) {
                account();
            } else if (v.equals(layout_clear_cache)) {
                notice(new CallBack() {
                    @Override
                    public void call() {
                        clearCache();
                    }
                }, "确定清除缓存");
            } else if (v.equals(layout_about)) {
                about();
            } else if (v.equals(next)) {
                next();
            }
        }
    };

    private void account() {
//        go.name(SettingsAccountActivity.class).go();
    }

    private void clearCache() {

    }

    private void about() {
        go.name(WebActivity.class).param("title", "关于人民优购").param("html", "ugou.html").go();
    }


    private void next() {
        notice(new CallBack() {
            @Override
            public void call() {
                MallApplication.user.reset();
                MallApplication.user.save();
                finish();
            }
        }, "确定退出？");
    }
}
