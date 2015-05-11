package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.common.utils.StringUtils;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

/**
 * @author Tan Chunmao
 */
public class ChangePayPasswordActivity extends ActivityBase {
    @InjectView
    private EditText origin_password, new_password, new_password_confirm;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = getIntent().getStringExtra(String.class.getName());

        setContentView(R.layout.activity_change_login_password);
        Injector.self.inject(this);
        String title = "";
        if (mode.equals("open")) {
            title = "启用支付密码";
            dismissView(origin_password);
            new_password.setHint("输入支付密码");
            new_password_confirm.setHint("再次输入支付密码");
        } else if (mode.equals("close")) {
            title = "关闭支付密码";
            origin_password.setHint("输入支付密码");
            dismissView(new_password);
            dismissView(new_password_confirm);
        } else if (mode.equals("set")) {
            title = "修改支付密码";
            origin_password.setHint("输入旧支付密码");
            new_password.setHint("输入新支付密码");
            new_password_confirm.setHint("再次输入新支付密码");
        }
        setTitleBar(title, "提交");
    }

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        if (mode.equals("open")) {
            open();
        } else if (mode.equals("close")) {
            close();
        } else if (mode.equals("set")) {
            set();
        }

    }


    private void open() {
        final String passwordText = new_password.getText().toString();
        final String confirmPasswordText = new_password_confirm.getText().toString();
        if (TextUtils.isEmpty(passwordText) || !passwordText.equals(confirmPasswordText)) {
            toast("请确保两次输入的密码一致");
        } else if (!StringUtils.checkPayPassword(passwordText)) {
            toast("请确保您的密码是由6位数字组成");
        } else {
            showProgressDialog();
            execute(new Request(Urls.MEMBER_PAYMENT_PASSWORD_ON).addUrlParam("password", passwordText),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if (apiResult.isOk()) {
                                toast("启用支付密码成功");
                                boolean temp = MallApplication.user.isHasSetPaymentPassword();
                                MallApplication.user.setHasSetPaymentPassword(true);
                                MallApplication.user.setPaymentPasswordOn(true);
                                saveUser();
                                if (!temp) {
                                    setResult(RESULT_OK);
                                }
                                finish();
                            } else {
                                toast(apiResult.message);

                            }
                        }

                        @Override
                        protected void onFailure(Response res, HttpException e) {
                            super.onFailure(res, e);
                            dismissProgressDialog();
                            toast("启用支付密码失败");
                        }
                    }
            );
        }
    }

    private void close() {
        final String originPassword = origin_password.getText().toString();
        if (!StringUtils.checkPayPassword(originPassword)) {
            toast("请确保您的密码是由6位数字组成");
        } else {
            showProgressDialog();
            execute(new Request(Urls.MEMBER_PAYMENT_PASSWORD_CLOSE).addUrlParam("password", originPassword),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if (apiResult.isOk()) {
                                toast("关闭支付密码成功");
                                MallApplication.user.setPaymentPasswordOn(false);
                                saveUser();
                                finish();
                            } else if (apiResult.code.equals(Urls.CODE_PASSWORD_WRONG)) {
                                toast("支付密码不正确");
                            } else {
                                toast(apiResult.message);
                            }
                        }

                        @Override
                        protected void onFailure(Response res, HttpException e) {
                            super.onFailure(res, e);
                            dismissProgressDialog();
                            toast("关闭支付密码失败");
                        }
                    }
            );
        }
    }

    private void set() {
        final String originPassword = origin_password.getText().toString();
        final String passwordText = new_password.getText().toString();
        final String confirmPasswordText = new_password_confirm.getText().toString();
        if (TextUtils.isEmpty(passwordText) || !passwordText.equals(confirmPasswordText)) {
            toast("请确保两次输入的新密码一致");
        } else if (!StringUtils.checkPayPassword(originPassword) || !StringUtils.checkPayPassword(passwordText)) {
            toast("请确保您的密码是由6位数字组成");
        } else {
            showProgressDialog();
            execute(new Request(Urls.MEMBER_PAYMENT_PASSWORD_CHANGE).addUrlParam("password", originPassword).addUrlParam("newPassword", passwordText),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if (apiResult.isOk()) {
                                toast("修改支付密码成功");
                                finish();
                            } else if (apiResult.code.equals(Urls.CODE_PASSWORD_WRONG)) {
                                toast("原支付密码不正确");
                            } else {
                                toast(apiResult.message);
                            }
                        }

                        @Override
                        protected void onFailure(Response res, HttpException e) {
                            super.onFailure(res, e);
                            dismissProgressDialog();
                            toast("修改支付密码失败");
                        }
                    }
            );
        }
    }
}
