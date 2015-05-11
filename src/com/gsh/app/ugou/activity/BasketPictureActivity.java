package com.gsh.app.ugou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.ActivityBase;
import com.gsh.app.client.mall.activity.AddressEditActivity;
import com.gsh.app.client.mall.activity.LoginActivity;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M4;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.gsh.app.ugou.https.model.Combo;
import com.gsh.app.ugou.https.model.M45;
import com.gsh.app.ugou.https.model.M50;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.List;

/**
 * Created by Administrator on 2015/4/15.
 */
public class BasketPictureActivity extends ActivityBase {
    private Combo combo;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        combo = (Combo) getIntent().getSerializableExtra(Combo.class.getName());
        setContentView(R.layout.ugou_activity_package_picture);
        setTitleBar("图文详情");
        ((TextView) findViewById(R.id.money)).setText((int) combo.price + "");
        findViewById(R.id.buy).setOnClickListener(onClickListener);
        webView = (WebView) findViewById(R.id.web);
        Log.i("description",combo.description);
        webView.loadDataWithBaseURL(null, combo.description, "text/html", "utf-8", null);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.buy == v.getId()) {
                if (!MallApplication.user.loggedIn()) {
                    go.name(LoginActivity.class).go();
                } else {
                    createOrder();
                }
            }
        }
    };

    private void createOrder() {
        go.name(BasketOrderConfirmActivity.class).param(Combo.class.getName(), combo).go();
        /*showProgressDialog();
        execute(new Request(Urls.EXTERN_DELIVER_ORDER_CREATE).addUrlParam("packageId", String.valueOf(combo.id)),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult != null) {
                            if (apiResult.isOk()) {
                                M50 m50 = apiResult.getModel(M50.class);
                                go.name(BasketOrderConfirmActivity.class).param(M50.class.getName(), m50).param(Combo.class.getName(), combo).go();
                            } else if (apiResult.code.equals(Urls.CODE_ADDRESS_EMPTY)) {
                                dismissProgressDialog();
                                setAddress();
                            }
                        } else {
                            dismissProgressDialog();
                            toast("订单获取失败");
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        dismissProgressDialog();
                        toast("订单获取失败");
                    }
                });*/
    }

    private static final int REQUEST_ADDRESS_ADD = 2041;

    private void setAddress() {
        notice(
                new CallBack() {
                    @Override
                    public void call() {
                        go.name(AddressEditActivity.class).param(String.class.getName(), "add").goForResult(REQUEST_ADDRESS_ADD);
                    }
                },
                new CallBack() {
                    @Override
                    public void call() {
                        finish();
                    }
                },
                "请先添加收货地址", "", false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADDRESS_ADD) {
                createOrder();
            }
        }
    }
}
