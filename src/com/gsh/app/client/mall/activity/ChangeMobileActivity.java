package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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
public class ChangeMobileActivity extends ActivityBase {
    @InjectView
    private EditText mobile, captcha, password;
    @InjectView
    private TextView agreement_molin, agreement_alipay, get_captcha;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mobile);
        Injector.self.inject(this);
        setTitleBar("修改手机号", "提交");
        get_captcha.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(get_captcha)) {
                getCaptcha();
            }
        }
    };

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        next();
    }

    private void getCaptcha() {
        final String mobileText = mobile.getText().toString();
        if (!StringUtils.checkPhoneNo(mobileText)) {
            toast("请输入您的手机号码");
        } else {
            showProgressDialog();
            execute(new Request(Urls.MEMBER_MOBILE_CHANGE__CAPTCHA).addUrlParam("mobile", mobileText),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if (apiResult.isOk()) {
                                timer();
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

    private void timer() {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                get_captcha.setClickable(Boolean.FALSE);
                get_captcha.setText("(" + l / 1000 + ")秒后获取");
            }

            @Override
            public void onFinish() {
                get_captcha.setClickable(Boolean.TRUE);
                get_captcha.setText("点击获取");
            }
        }.start();
    }

    private void next() {
        final String mobileText = mobile.getText().toString();
        final String captchaText = captcha.getText().toString();
        final String passwordText = password.getText().toString();
        if (!StringUtils.checkPhoneNo(passwordText)) {
            toast("请输入您的密码");
        } else if (!StringUtils.checkPhoneNo(mobileText)) {
            toast("请输入您的手机号码");
        } else if (!StringUtils.checkCaptcha(captchaText)) {
            toast("请输入您收到的验证码");
        } else {
            showProgressDialog();
            execute(new Request(Urls.MEMBER_MOBILE_CHANGE).addUrlParam("password", passwordText).addUrlParam("newMobile", mobileText).addUrlParam("validateCode", captchaText),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if (apiResult.isOk()) {

                                toast("修改手机号码成功，请重新登录");
                                go.name(LoginActivity.class).goAndFinishCurrent();
                            } else {
                                toast("修改失败");
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
