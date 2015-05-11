package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.alipay.sdk.app.PayTask;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.alipay.PayResult;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M34;
import com.gsh.app.client.mall.https.model.M35;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by taosj on 15/3/10.
 */
public class RechargeActivity extends ActivityBase {

    @InjectView
    View rechargeType;
    @InjectView
    EditText rechargeAmount;
    @InjectView
    Button recharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rechange);
        setTitleBar("账户充值");
        Injector.self.inject(this);
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = rechargeAmount.getText().toString();
                if (TextUtils.isEmpty(input) || input.startsWith("0")) {
                    toast("请输入合法的金额");
                } else {
                    submit(input.trim());
                }
            }
        });
    }

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
                        onBackPressed();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            toast("支付渠道原因或者系统原因还在等待支付结果确认");
//                            onBackPressed();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            toast("支付失败");

                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(RechargeActivity.this, "检查结果为：" + msg.obj,
                            Toast.LENGTH_SHORT).show();
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

    public void submit(String price) {
        showProgressDialog();
        if (MallApplication.test) {
            price = "0.01";
        }
        final Request reqTrackNo = new Request(Urls.PAY_PREPAID).addUrlParam("number", price);
        execute(reqTrackNo, new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                if (apiResult.isOk()) {
                    M34 data = apiResult.getModel(M34.class);
                    Request reqAlipa = new Request(Urls.PAY_ALI).addUrlParam("number", data.payNo);
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
                                        PayTask alipay = new PayTask(RechargeActivity.this);
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
                } else {
                    dismissProgressDialog();
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
}
