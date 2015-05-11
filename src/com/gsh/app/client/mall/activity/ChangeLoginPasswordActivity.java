package com.gsh.app.client.mall.activity;

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
public class ChangeLoginPasswordActivity extends ActivityBase {
    @InjectView
    private EditText origin_password, new_password, new_password_confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_login_password);
        Injector.self.inject(this);
        setTitleBar("修改登录密码", "提交");
    }

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        next();
    }

    private void next() {
        final String originPassword = origin_password.getText().toString();
        final String passwordText = new_password.getText().toString();
        final String confirmPasswordText = new_password_confirm.getText().toString();
        if (TextUtils.isEmpty(passwordText) || !passwordText.equals(confirmPasswordText)) {
            toast("请确保再次输入的密码一致");
        } else {
            showProgressDialog();
            execute(new Request(Urls.MEMBER_PASSWORD_CHANGE).addUrlParam("password", originPassword).addUrlParam("newPassword", passwordText),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if (apiResult.isOk()) {
                                toast("修改密码成功，请重新登录");
                                finish();
                            } else {
                                toast(apiResult.message);
                            }
                        }

                        @Override
                        protected void onFailure(Response res, HttpException e) {
                            super.onFailure(res, e);
                            dismissProgressDialog();
                            toast("修改失败");
                        }
                    }
            );
        }
    }
}
