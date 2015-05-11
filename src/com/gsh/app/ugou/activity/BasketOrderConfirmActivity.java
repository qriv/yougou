package com.gsh.app.ugou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.AddressEditActivity;
import com.gsh.app.client.mall.activity.AddressListActivity;
import com.gsh.app.client.mall.activity.PayActivity;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M18;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.gsh.app.ugou.https.model.AlipayOrder;
import com.gsh.app.ugou.https.model.Combo;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.List;

/**
 * @author Tan Chunmao
 */
public class BasketOrderConfirmActivity extends PayActivity {
    private static final int REQUEST_ADDRESS = 2038;
    private static final int REQUEST_ADDRESS_ADD = 2041;

    @InjectView
    private TextView name, mobile, address, notice;//address
    //    private M50 m50;
    private Combo combo;
    private M18 m18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        m50 = (M50) getIntent().getSerializableExtra(M50.class.getName());
        combo = (Combo) getIntent().getSerializableExtra(Combo.class.getName());
        setContentView(R.layout.ugou_activity_order_confirm);
        Injector.self.inject(this);
        setTitleBar("确认订单");
        findViewById(R.id.layout_person_information).setOnClickListener(onClickListener);
//        updateAddress();
        hideContent();
        loadData();
        findViewById(R.id.buy).setOnClickListener(onClickListener);
    }

    private void loadData() {
        showProgressDialog();
        execute(new Request(Urls.MEMBER_ADDRESS), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                dismissProgressDialog();
                if (apiResult.isOk()) {
                    List<M18> addresses = apiResult.getModels(M18.class);
                    if (addresses != null && !addresses.isEmpty()) {
                        for (M18 m : addresses) {
                            if (m.isDefault) {
                                m18 = m;
                                break;
                            }
                        }
                    }/*
                    if (MallApplication.test) {
                        m18 = new M18();
                        m18.id = 1;
                        m18.name = "Jack";
                        m18.mobile = "18012345675";
                        m18.addressDetail = "中和镇";
                    }*/
                    if (m18 != null) {
                        showContent();
                        fillPackage();
                        updateAddress();
                    } else {
                        setAddress();
                    }
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                dismissProgressDialog();
                System.out.println();
            }
        });
    }

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

    private void fillPackage() {
        View container = findViewById(R.id.basket);
        dismissView(container.findViewById(R.id.arrow));
        loadImage((ImageView) container.findViewById(R.id.icon), combo.mainPicturePath);
        ((TextView) container.findViewById(R.id.name)).setText(combo.name);
        ((TextView) container.findViewById(R.id.money)).setText(String.format("%d", (int) combo.price));

        ((TextView) findViewById(R.id.money_)).setText((int) combo.price + "");
    }


//region address

    private void updateAddress() {
//        M18 m18 = m50.address;
        name.setText(m18.name);
        mobile.setText(m18.mobile);
        address.setText(m18.addressDetail);
        notice.setText(m18.deliveryDescription);
    }

    private void pickAddress() {
        go.name(AddressListActivity.class).param(String.class.getName(), "need").goForResult(REQUEST_ADDRESS);
    }

    //endregion address

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADDRESS) {
                m18 = (M18) data.getSerializableExtra(M18.class.getName());
                showContent();
                updateAddress();
                fillPackage();
            } else if (requestCode == REQUEST_ADDRESS_ADD) {
                loadData();
            }
        }
    }

    @Override
    protected void warnNotPay() {
        notice(new CallBack() {
            @Override
            public void call() {
                payDialog.dismiss();
                finish();
            }
        }, "是否放弃付款？", null, false);
    }


    private void submit() {
        showProgressDialog();
        String orderId = String.valueOf(combo.id);
        String addressId = String.valueOf(m18.id);
        execute(new Request(Urls.COMBO_ORDER_CREATE).addUrlParam("comboId", orderId).addUrlParam("deliveryAddressId", addressId),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult != null && apiResult.isOk()) {
                            AlipayOrder alipayOrder = apiResult.getModel(AlipayOrder.class);
                            payByAlipay(alipayOrder);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        dismissProgressDialog();
                        toast("提交订单失败");
                    }
                });
    }
//    endregion submit

    @Override
    protected void onLeftActionPressed() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        notice(new CallBack() {
            @Override
            public void call() {
                BasketOrderConfirmActivity.super.onBackPressed();
            }
        }, "是否放弃当前订单？", null, false);
    }

    protected void payByMolinSuccess() {
        toast("订单支付成功");
        finish();
    }

    protected void payByAlipaySuccess() {
        super.payByAlipaySuccess();
        finish();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.layout_person_information == v.getId()) {
                pickAddress();
            } else if (R.id.buy == v.getId()) {
                submit();
            }
        }
    };
}
