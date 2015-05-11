package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.view.View;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;

/**
 * Created by Administrator on 2015/3/21.
 */
public class SetPayPasswordActivity extends ActivityBase {
    @InjectView
    private View layout_open, layout_close, layout_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pay_password);
        Injector.self.inject(this);
        setTitleBar("设置支付密码");
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean passwordOn = MallApplication.user.isPaymentPasswordOn();
        dismissView(layout_open, passwordOn);
        dismissView(layout_close, !passwordOn);
        dismissView(layout_set, !passwordOn);
        layout_open.setOnClickListener(onClickListener);
        layout_close.setOnClickListener(onClickListener);
        layout_set.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(layout_open)) {
                open();
            } else if (v.equals(layout_close)) {
                close();
            } else if (v.equals(layout_set)) {
                set();
            }
        }
    };

    private void open() {
        go.name(ChangePayPasswordActivity.class).param(String.class.getName(), "open").go();
    }


    private void close() {
        go.name(ChangePayPasswordActivity.class).param(String.class.getName(), "close").go();
    }

    private void set() {
        go.name(ChangePayPasswordActivity.class).param(String.class.getName(), "set").go();
    }
}
