package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M0;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.impl.apache.MyLayeredSocketFactory;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

/**
 * @author Tan Chunmao
 */
public class SetPasswordActivity extends ActivityBase {
    @InjectView
    private EditText password, password_confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        Injector.self.inject(this);
        setTitleBar("设置密码", "提交");
    }

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        next();
    }

    private void next() {
        final String url = getIntent().getStringExtra("url");
        final String passwordText = password.getText().toString();
        final String confirmPasswordText = password_confirm.getText().toString();
        if (TextUtils.isEmpty(passwordText) || !passwordText.equals(confirmPasswordText)) {
            toast("请确保再次输入的密码一致");
        } else {
            final String mobileText = getIntent().getStringExtra("mobile");
            final String captchaText = getIntent().getStringExtra("captcha");
            Request request = new Request(url);
            request.addUrlParam("validateCode", captchaText);
            request.addUrlParam("mobile", mobileText).addUrlParam("password", passwordText);
            showProgressDialog();
            execute(request,
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if (apiResult.isOk()) {
                                if (url.equals(Urls.MEMBER_REGISTER)) {
                                    toast("注册成功");
                                    M0 m0 = apiResult.getModel(M0.class);
                                    setResult(RESULT_OK, new Intent().putExtra(M0.class.getName(), m0));
                                    finish();
                                } else if (url.equals(Urls.MEMBER_PASSWORD_FIND)) {
                                    toast("重置密码成功，请重新登录");
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            } else if (apiResult.code.equals(Urls.CODE_USER_EXIST)) {
                                toast("用户已存在");
                            } else if (apiResult.code.equals(Urls.CODE_CAPCHA_WRONG)) {
                                toast("验证码错误");
                            } else {
                                toast(apiResult.message);
                            }
                        }

                        @Override
                        protected void onFailure(Response res, HttpException e) {
                            super.onFailure(res, e);
                            dismissProgressDialog();
                        }
                    }
            );
        }
    }
}
