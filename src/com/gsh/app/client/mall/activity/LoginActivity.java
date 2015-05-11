package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M0;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

/**
 * @author Tan Chunmao
 */
public class LoginActivity extends ActivityBase {
    @InjectView
    private EditText mobile, password;
    @InjectView
    private TextView login, reset_password, register;
    private String action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        action = getIntent().getAction();
        setContentView(R.layout.activity_login);
        Injector.self.inject(this);
        setTitleBar(getString(R.string.app_name, "账户登录"));
        login.setOnClickListener(onClickListener);
        register.setOnClickListener(onClickListener);
        reset_password.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(login)) {
                login();
            } else if (v.equals(register)) {
                register();
            } else if (v.equals(reset_password)) {
                resetPassword();
            }
        }
    };


    private void login() {
        final String mobileText = mobile.getText().toString();
        final String passwordText = password.getText().toString();
        if (TextUtils.isEmpty(mobileText) || TextUtils.isEmpty(passwordText)) {
            toast("请输入用户名和密码");
        } else {
            showProgressDialog();
            execute(new Request(Urls.MEMBER_LOGIN).addUrlParam("mobile", mobileText).addUrlParam("password", passwordText),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if (apiResult.isOk()) {
                                M0 m0 = apiResult.getModel(M0.class);
                                onLoginSuccess(m0);
                            } else if (apiResult.code.equals(Urls.CODE_PASSWORD_WRONG)) {
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

    private void onLoginSuccess(M0 m0) {
        MallApplication.user.update(m0);
        saveUser();
        if (action != null && action.equals("recharge")) {
            go.name(RechargeActivity.class).go();
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    private static final int REQUEST_REGISTER = 2038;

    private void register() {
        go.name(RegisterActivity.class).goForResult(REQUEST_REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_REGISTER) {
                M0 m0 = (M0) data.getSerializableExtra(M0.class.getName());
                onLoginSuccess(m0);
            }
        }
    }

    private void resetPassword() {
        go.name(ResetPasswordActivity.class).go();
    }
}
