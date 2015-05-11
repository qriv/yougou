package com.gsh.app.client.mall.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.alipay.sdk.app.PayTask;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.alipay.PayResult;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M1;
import com.gsh.app.client.mall.https.model.M35;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.gsh.app.ugou.https.model.AlipayOrder;
import com.litesuits.common.utils.StringUtils;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

/**
 * @author Tan Chunmao
 */
public abstract class PayActivity extends ActivityBase {
    protected Dialog payDialog;

    private String payNo;
    private double price;
    private double remain;

    protected void choosePayMethod(final String payNo, final double price, final double remain) {
        this.payNo = payNo;
        this.price = price;
        this.remain = remain;
        payDialog = new Dialog(this, R.style.MyDialog);
        View layout = getLayoutInflater().inflate(R.layout.dialog_pay, null);
        payDialog.setContentView(layout);
        payDialog.setCancelable(false);
        final EditText editText = (EditText) layout.findViewById(R.id.input);
        editText.setText("");
        ((TextView) layout.findViewById(R.id.money)).setText(String.format("%.2f元", price));
        layout.findViewById(R.id.wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payByWechat(payNo);
            }
        });
        layout.findViewById(R.id.alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payByAlipay(payNo);
            }
        });
        if (remain < price) {
            TextView molin = (TextView) layout.findViewById(R.id.molin);
            molin.setText(String.format("账户余额不足（%.2f元）", remain));
            molin.setTextColor(getResources().getColor(R.color.ui_font_e));
            layout.findViewById(R.id.actions).setVisibility(View.GONE);
            layout.findViewById(R.id.action_a).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.no_a).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    warnNotPay();
                }
            });
        } else {
            if (!MallApplication.user.isHasSetPaymentPassword()) {
                checkPassword(payNo, price, remain);
                return;
            }
            layout.findViewById(R.id.actions).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.action_a).setVisibility(View.GONE);
            TextView molin = (TextView) layout.findViewById(R.id.molin);
            molin.setText(String.format("账户余额：%.2f元", remain));
            final boolean passwordOn = MallApplication.user.isPaymentPasswordOn();
            layout.findViewById(R.id.input).setVisibility(passwordOn ? View.VISIBLE : View.GONE);
            layout.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (passwordOn) {
                        String input = editText.getText().toString();
                        if (StringUtils.checkPayPassword(input)) {
                            payByMolin(input, payNo);
                        } else {
                            toast("请确保您的密码是由6位数字组成");
                        }
                    } else {
                        payByMolin("", payNo);
                    }

                }
            });

            layout.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    warnNotPay();
                }
            });
        }

        Window dialogWindow = payDialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.90);
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(p);
        payDialog.show();
    }

    protected abstract void warnNotPay();

    private void checkPassword(final String payNo, final double price, final double remain) {
        showProgressDialog();
        execute(new Request(Urls.MEMBER_PAYMENT_PASSWORD_VALID),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult.isOk()) {
                            M1 m1 = apiResult.getModel(M1.class);
                            MallApplication.user.setHasSetPaymentPassword(m1.hasSet);
                            saveUser();
                            if (MallApplication.user.isHasSetPaymentPassword()) {
                                choosePayMethod(payNo, price, remain);
                                return;
                            }
                        }
                        setPassword();
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        dismissProgressDialog();
                        super.onFailure(res, e);
                        setPassword();
                    }
                }
        );
    }

    private static final int REQUEST_PASSWORD = 2037;

    private void setPassword() {
        go.name(ChangePayPasswordActivity.class).param(String.class.getName(), "open").goForResult(REQUEST_PASSWORD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PASSWORD) {
                choosePayMethod(payNo, price, remain);
            }
        }
    }

    //    region alipay
    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    Log.d("memo", payResult.getMemo());
                    Log.d("result", payResult.getResult());
                    Log.d("resultStatus", payResult.getResultStatus());
                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        toast("支付成功");
                        payByAlipaySuccess();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            toast("支付渠道原因或者系统原因还在等待支付结果确认");
                            payByAlipaySuccess();
//                            onBackPressed();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            if (MallApplication.test) {
                                toast("测试支付成功");
                                payByAlipaySuccess();
                            } else {
                                toast("支付失败");
                            }
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    toast("检查结果为：" + msg.obj);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    /**
     * call alipay sdk pay. 调用SDK支付
     */

    protected void payByAlipay(AlipayOrder alipayOrder) {
        final String payInfo = alipayOrder.key + "&sign=\"" + alipayOrder.value + "\"&"
                + getSignType();
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    protected void payByAlipay(String payNo) {
        Request reqAlipa = new Request(Urls.PAY_ALI).addHeader("Token", "57F99611A75C1A6A54B49C42E14A4D44").addUrlParam("number", payNo);
        execute(reqAlipa, new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                dismissProgressDialog();
                if (apiResult.isOk()) {
                    M35 data = apiResult.getModel(M35.class);
                    final String payInfo = data.key + "&sign=\"" + data.value + "\"&"
                            + getSignType();
                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            // 构造PayTask 对象
                            PayTask alipay = new PayTask(PayActivity.this);
                            // 调用支付接口，获取支付结果
                            String result = alipay.pay(payInfo);

                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };

                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                } else {
                    toast("提交订单失败");
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                dismissProgressDialog();
                toast("提交订单失败");
            }
        });
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }
//    endregion alipay

    //    region wechat pay
    protected void payByWechat(String payNo) {
//
    }
//    endregion wechat pay

    //    region molin pay
    protected void payByMolin(String password, String payNo) {
        execute(new Request(Urls.MEMBER_ORDER_PAYMENT_REMAIN).addUrlParam("paymentPassword", password).addUrlParam("payNo", payNo),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult.isOk()) {
                            payDialog.dismiss();
                            toast("付款成功");
                            payByMolinSuccess();
                        } else if (apiResult.code.equals(Urls.CODE_PASSWORD_WRONG)) {
                            toast("支付密码错误");
                        } else {
                            payDialog.dismiss();
                            toast(apiResult.message);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        payDialog.dismiss();
                        toast("付款失败");
                    }
                }
        );
    }

    protected abstract void payByMolinSuccess();

    protected void payByAlipaySuccess() {
//        payDialog.dismiss();

    }
//    endregion molin pay
}
