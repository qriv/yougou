package com.gsh.app.client.mall.activity;

import android.content.Intent;
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
public class RegisterActivity extends ActivityBase {
    @InjectView
    private EditText mobile, captcha;
    @InjectView
    private TextView agreement_molin, agreement_alipay, get_captcha;
    @InjectView
    private View agreement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Injector.self.inject(this);
        setTitleBar("快速注册", "下一步");
        agreement.setVisibility(View.VISIBLE);
        agreement_alipay.setOnClickListener(onClickListener);
        agreement_molin.setOnClickListener(onClickListener);
        get_captcha.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(get_captcha)) {
                getCaptcha();
            } else if (v.equals(agreement_molin)) {
                go.name(WebActivity.class).param("title", "人民优购服务协议").param("html", "protocol.html").go();
            } else if (v.equals(agreement_alipay)) {
                go.name(WebActivity.class).param("title", "支付宝协议").param("html", "alipay.html").go();
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
            execute(new Request(Urls.MEMBER_REGISTER_CAPTCHA).addUrlParam("mobile", mobileText),
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
                get_captcha.setTextColor(getResources().getColor(R.color.ui_font_e));
                get_captcha.setText("(" + l / 1000 + ")秒后获取");
            }

            @Override
            public void onFinish() {
                get_captcha.setClickable(Boolean.TRUE);
                get_captcha.setText("点击获取");
                get_captcha.setTextColor(getResources().getColor(R.color.ui_font_c));
            }
        }.start();
    }

    private static final int REQUEST_REGISTER = 2038;

    private void next() {
        final String mobileText = mobile.getText().toString();
        final String captchaText = captcha.getText().toString();
        if (!StringUtils.checkPhoneNo(mobileText)) {
            toast("请输入您的手机号码");
        } else if (!StringUtils.checkCaptcha(captchaText)) {
            toast("请输入您收到的验证码");
        } else {
            go.name(SetPasswordActivity.class).param("mobile", mobileText).param("captcha", captchaText).param("url", Urls.MEMBER_REGISTER).goForResult(REQUEST_REGISTER);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_REGISTER) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
