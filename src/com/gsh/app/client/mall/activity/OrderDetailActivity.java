package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.*;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class OrderDetailActivity extends PayActivity {
    private M19 m19;
    @InjectView
    private TextView status, orderNo, price;//header
    @InjectView
    private TextView address_name, address_mobile, address_detail;//address
    @InjectView
    private TextView deliver_info, deliver_time;//deliver
    @InjectView
    private TextView worker_name, worker_sex, worker_mobile;//worker
    @InjectView
    private View layout_worker;
    @InjectView
    private TextView total_price, total_coupon, total_point, pay_price;//money
    @InjectView
    private TextView button_a, button_b, button_c;//action
    private OrderListActivity.OrderStatus orderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m19 = (M19) getIntent().getSerializableExtra(M19.class.getName());
        orderStatus = OrderListActivity.OrderStatus.valueOf(m19.status);
        setContentView(R.layout.activity_order_detail);
        setTitleBar("订单详情");
        Injector.self.inject(this);
        setHeader();
        setAddressAndDeliver();
        setGoods();
        setWorker();
        setMoney();
        setActions();
    }

    private void setHeader() {

        String display = OrderListActivity.OrderStatus.valueOf(m19.status).display;
        if (MallApplication.test) {
            display = display + "_" + m19.status;
        }
        status.setText(display);
        orderNo.setText(String.format("订单编号：%s", m19.orderNo));
        price.setText(String.format("订单金额：￥%.2f", m19.total_price));
    }

    private void setAddressAndDeliver() {
        M18 m18 = m19.address;
        address_name.setText(String.format("收货人：%s", m18.name));
        address_mobile.setText(m18.mobile);
        address_detail.setText(String.format("收货地址：%s", m18.address));

        boolean toPay = m19.status.equals(OrderListActivity.OrderStatus.toPay.name());
        dismissView(R.id.button_deliver, toPay);
        dismissView(R.id.deliver_sep, toPay);
        deliver_info.setText(m19.deliverInfo.deliverInfo);
        deliver_time.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(m19.deliverInfo.deliverInfoTime));
        findViewById(R.id.button_deliver).setOnClickListener(onClickListener);
    }

    private void setGoods() {
        List<M10> goods = m19.goods;
        LinearLayout layout = (LinearLayout) findViewById(R.id.goods);
        layout.removeAllViews();
        ViewGroup.LayoutParams verticalGapLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.ui_size_one_dp));
        View gap = null;
        for (M10 m10 : goods) {
            View convertView = getLayoutInflater().inflate(R.layout.item_order_goods_comment, null);
            loadImage((ImageView) convertView.findViewById(R.id.pic), m10.picturePath);
            ((TextView) convertView.findViewById(R.id.title)).setText(m10.name);
            convertView.setOnClickListener(onClickListener);
            convertView.setTag(m10);
            View commentView = convertView.findViewById(R.id.comment);
            commentView.setVisibility(m19.commentPermitted && !m10.commented ? View.VISIBLE : View.GONE);
            commentView.setTag(m10);
            commentView.setOnClickListener(onClickListener);
            convertView.findViewById(R.id.currentPrice).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.quality).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.currentPrice)).setText(String.format("￥%.2f", m10.price));
            ((TextView) convertView.findViewById(R.id.quality)).setText(String.format("X%d", m10.number));
            ((TextView) convertView.findViewById(R.id.status)).setText(m10.description);
            if (gap != null)
                layout.addView(gap);
            layout.addView(convertView);
            gap = new View(OrderDetailActivity.this);
            gap.setLayoutParams(verticalGapLayoutParams);
            gap.setBackgroundResource(R.color.ui_divider_bg);
        }
    }

    private void setWorker() {
        if (orderStatus == OrderListActivity.OrderStatus.toPay
                || orderStatus == OrderListActivity.OrderStatus.toShip) {
            dismissView(layout_worker);
        } else {
            dismissView(layout_worker, false);
            M27 m27 = m19.worker;
            loadAvatar((ImageView) findViewById(R.id.worker_avatar), m27.avatarPath);
            worker_name.setText(String.format("姓名：%s", m27.name));
            worker_sex.setText(String.format("性别：%s", m27.sex));
            worker_mobile.setText(Html.fromHtml(getString(R.string.deliver_mobile, m27.mobile)));
            worker_mobile.setOnClickListener(onClickListener);
        }
    }

    private void setMoney() {
        showMoney(total_price, (float) m19.total_price);
        subMoney(total_point, (float) m19.score);
        subMoney(total_coupon, (float) m19.coupon);
        pay_price.setText(Html.fromHtml(getString(R.string.pay_money, m19.total_price)));
    }

    private void setActions() {
        OrderListActivity.OrderStatus orderStatus = OrderListActivity.OrderStatus.valueOf(m19.status);
        findViewById(R.id.actions).setVisibility(orderStatus == OrderListActivity.OrderStatus.returnProcess ? View.GONE : View.VISIBLE);
        button_a.setOnClickListener(onClickListener);
        button_b.setOnClickListener(onClickListener);
        button_c.setOnClickListener(onClickListener);
        if (OrderListActivity.OrderStatus.toPay == orderStatus) {
            button_a.setVisibility(View.VISIBLE);
            button_a.setText("立即支付");
            button_b.setVisibility(View.VISIBLE);
            button_b.setText("取消订单");
            button_c.setVisibility(View.GONE);
        } else if (OrderListActivity.OrderStatus.toReceive == orderStatus || OrderListActivity.OrderStatus.toShip == orderStatus) {
            button_a.setVisibility(View.VISIBLE);
            button_a.setText("再次购买");
            button_b.setVisibility(View.GONE);
            button_c.setVisibility(View.GONE);
        } else if (OrderListActivity.OrderStatus.done == orderStatus) {
            m19.commentPermitted = true;
            button_a.setVisibility(View.VISIBLE);
            button_a.setText("再次购买");
            button_b.setVisibility(View.VISIBLE);
            button_b.setText("申请退货");
            button_c.setVisibility(View.VISIBLE);
        } else if (OrderListActivity.OrderStatus.returnProcess == orderStatus) {

        } else if (OrderListActivity.OrderStatus.returnProcessed == orderStatus) {
            m19.commentPermitted = true;
            button_a.setVisibility(View.VISIBLE);
            button_a.setText("再次购买");
            button_b.setVisibility(View.GONE);
            button_c.setVisibility(View.VISIBLE);
        } else if (OrderListActivity.OrderStatus.closed == orderStatus) {
            button_a.setVisibility(View.VISIBLE);
            button_a.setText("再次购买");
            button_b.setVisibility(View.GONE);
            button_c.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.button_deliver == v.getId()) {
                deliverDetail();
            } else if (R.id.comment == v.getId()) {
                M10 goods = (M10) v.getTag();
                comment(goods);
            } else if (R.id.worker_mobile == v.getId()) {
                call();
            } else if (R.id.button_a == v.getId()) {
                OrderListActivity.OrderStatus orderStatus = OrderListActivity.OrderStatus.valueOf(m19.status);
                if (orderStatus == OrderListActivity.OrderStatus.toPay) {
                    pay();
                } else {
                    buyAgain();
                }
            } else if (R.id.button_b == v.getId()) {
                OrderListActivity.OrderStatus orderStatus = OrderListActivity.OrderStatus.valueOf(m19.status);
                if (orderStatus == OrderListActivity.OrderStatus.toPay) {
                    cancelOrder();
                } else {
                    returnOrder();
                }
            } else if (R.id.goods == v.getId()) {
                M10 goods = (M10) v.getTag();
                goodsDetail(goods);
            } else if (R.id.button_c == v.getId()) {
                deleteOrder();
            }
        }
    };

    private void deleteOrder() {
        notice(new CallBack() {
            @Override
            public void call() {
                showProgressDialog();
                execute(new Request(Urls.MEMBER_ORDER_DELETE).addUrlParam("orderId", String.valueOf(m19.id)),
                        new HttpResultHandler() {
                            @Override
                            protected void onSuccess(ApiResult apiResult) {
                                dismissProgressDialog();
                                if (apiResult.isOk()) {
                                    toast("订单删除成功");
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            }

                            @Override
                            protected void onFailure(Response res, HttpException e) {
                                super.onFailure(res, e);
                                dismissProgressDialog();
                            }
                        });
            }
        }, "确定删除当前订单？", "删除后将无法恢复");
    }

    private void deliverDetail() {
        go.name(DeliverStatusActivity.class).param(M19.class.getName(), m19).go();
    }

    private void comment(M10 goods) {
        go.name(CommentActivity.class).param(M10.class.getName(), goods).param("orderId", String.valueOf(m19.id)).go();
    }

    private void call() {
        String uri = String.format("tel://%s", m19.worker.mobile);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
        startActivity(intent);
    }


    private void pay() {
        execute(new Request(Urls.MEMBER_WALLET_REMAIN_SUM),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult.isOk()) {
                            M16 m16 = apiResult.getModel(M16.class);
                            choosePayMethod(m19.payNo, m19.pay_price, m16.remain);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        toast("获取人民优购余额失败，请稍候再试");
                    }
                }
        );
    }

    private void buyAgain() {
        showProgressDialog();
        execute(new Request(Urls.ORDER_REBUY).addUrlParam("orderId", String.valueOf(m19.id)),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult.isOk()) {
                            M11 m11 = apiResult.getModel(M11.class);
                            go.name(OrderConfirmActivity.class).param(M11.class.getName(), m11).go();
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

    private void cancelOrder() {
        notice(new CallBack() {
            @Override
            public void call() {
                showProgressDialog();
                execute(new Request(Urls.ORDER_CANCEL).addUrlParam("orderId", String.valueOf(m19.id)),
                        new HttpResultHandler() {
                            @Override
                            protected void onSuccess(ApiResult apiResult) {
                                dismissProgressDialog();
                                if (apiResult.isOk()) {
                                    toast("订单取消成功");

                                    setResult(RESULT_OK);
                                    finish();
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
        }, "确定取消当前订单？");
    }

    private void returnOrder() {

    }

    private void goodsDetail(M10 goods) {
        go.name(GoodsDetailActivity.class).param(String.class.getName(), "id").param("id", String.valueOf(goods.id)).go();
    }

    @Override
    protected void warnNotPay() {
        notice(new CallBack() {
            @Override
            public void call() {
                payDialog.dismiss();
            }
        }, "是否放弃付款？", null, false);
    }

    protected void payByMolinSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    protected void payByAlipaySuccess() {
        super.payByAlipaySuccess();
        setResult(RESULT_OK);
        finish();
    }
}
