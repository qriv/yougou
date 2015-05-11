package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.*;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.android.log.Log;
import com.litesuits.common.utils.TimeUtil;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class OrderConfirmActivityNew extends PayActivity {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final int REQUEST_ADDRESS = 2038;
    private static final int REQUEST_DELIVER = 2039;
    private static final int REQUEST_COUPON = 2040;
    private static final int REQUEST_ADDRESS_ADD = 2041;
    private M11 m11;
    private M33 m33;

    @InjectView
    private TextView name, mobile, address;//address
    @InjectView
    private View layout_time_order, layout_time_normal;
    @InjectView
    private TextView pay_method, deliver_method, order_deliver_time, normal_deliver_time;//deliver
    @InjectView
    private TextView coupon_label, coupon_used, point_label, point_used;//coupon and point
    @InjectView
    private View layout_point, point_check;
    @InjectView
    private TextView total_price, total_coupon, total_point;//price
    @InjectView
    private TextView value;//需要支付的金额

    private List<Wrap> wrapList;


    private int score;
    private int usedScore;
    private boolean scoreUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        M11 m11 = (M11) getIntent().getSerializableExtra(M11.class.getName());
        setContentView(R.layout.activity_order_confirm_new);
        Injector.self.inject(this);
        setTitleBar("确认订单");
        hideContent();
        findViewById(R.id.layout_person_information).setOnClickListener(onClickListener);
        findViewById(R.id.add).setOnClickListener(onClickListener);
        findViewById(R.id.order_two).setVisibility(View.GONE);
        wrapList = new ArrayList<Wrap>();
        if (m11 != null) {
            wrapList.add(new Wrap(m11));
            fetchCouponAndScore(wrapList.get(0));
        } else {
            fetchData();
        }
    }


    private void fetchCouponAndScore(final Wrap wrap) {
        showProgressDialog();
        execute(new Request(Urls.ORDER_COUPON).addUrlParam("orderId", String.valueOf(wrap.m11.id)),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult != null && apiResult.isOk()) {
                            wrap.m11.m33 = apiResult.getModel(M33.class);
                            if (score == 0) {
                                score = wrap.m11.m33.score;
                                if (MallApplication.test) {
                                    score = 300;
                                }
                                usedScore = 0;
                            }
                            boolean allFetched = true;
                            for (Wrap w : wrapList) {
                                if (w.m11.m33 == null) {
                                    allFetched = false;
                                    break;
                                }
                            }
                            if (allFetched) {
                                fillContent();
                            }
                            return;
                        }
                        orderFailure();
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        dismissProgressDialog();
//                        fillContent();
                        orderFailure();
                    }
                }
        );
    }

    private void fillContent() {
        showContent();
        updateAddress();
        for (Wrap wrap : wrapList) {
            TextView goodsLabel = (TextView) wrap.container.findViewById(R.id.goods_label);
            if (wrap.m11.saleType == 0) {
                goodsLabel.setText("预售商品");
            } else {
                goodsLabel.setText("普通商品");
            }
            fillGoods(wrap);
            View deliver = wrap.container.findViewById(R.id.deliver);
            deliver.setTag(wrap);
            deliver.setOnClickListener(onClickListener);
            updateDeliver(wrap);
            updateCoupon(wrap);
//            updateScore(wrap);
        }
        updateScore();
        updatePrice();
    }

    private void updateCoupon(Wrap wrap) {
        int availableCouponSize = wrap.m11.m33.courpons.size();
        TextView coupon_label = (TextView) wrap.container.findViewById(R.id.coupon_label);
        coupon_label.setText(String.format("%d张可用", availableCouponSize));
        View couponLayout = wrap.container.findViewById(R.id.coupon);
        couponLayout.setTag(wrap);
        couponLayout.setOnClickListener(onClickListener);
        int usedCouponSize = wrap.submit.coupon.list.size();
        int black = getResources().getColor(R.color.ui_font_a);
        int orange = getResources().getColor(R.color.ui_font_orange);
        TextView coupon_used = (TextView) wrap.container.findViewById(R.id.coupon_used);
        if (usedCouponSize == 0) {
            coupon_used.setText("未使用");
            coupon_used.setTextColor(black);
        } else {
            coupon_used.setText(String.format("使用%d张代金券", usedCouponSize));
            coupon_used.setTextColor(orange);
        }
    }


    private void updateScore() {
        TextView point_label = (TextView) findViewById(R.id.point_label);
        point_label.setText(String.format("%d个积分", score));
        hideView(point_used, score == 0);
        hideView(point_check, score == 0);
        layout_point.setOnClickListener(onClickListener);
    }

    private void fillGoods(Wrap wrap) {
        ViewGroup.LayoutParams verticalGapLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.ui_size_one_dp));
        LinearLayout layout = (LinearLayout) wrap.container.findViewById(R.id.goods);
        for (M10 m10 : wrap.m11.goods) {
            View convertView = getLayoutInflater().inflate(R.layout.item_order_goods, null);
            loadImage((ImageView) convertView.findViewById(R.id.pic), m10.picturePath);
            ((TextView) convertView.findViewById(R.id.title)).setText(m10.name);
            ((TextView) convertView.findViewById(R.id.currentPrice)).setText(String.format("￥%.2f", m10.price));
            ((TextView) convertView.findViewById(R.id.quality)).setText(String.format("X%d", m10.number));
            ((TextView) convertView.findViewById(R.id.status)).setText(m10.description);

            View gap = new View(OrderConfirmActivityNew.this);
            gap.setLayoutParams(verticalGapLayoutParams);
            gap.setBackgroundResource(R.color.ui_divider_bg);
            layout.addView(gap);
            layout.addView(convertView);
        }
    }

    private void updateDeliver(Wrap wrap) {
        ((TextView) wrap.container.findViewById(R.id.pay_method)).setText(wrap.submit.deliver.payMethod);
        ((TextView) wrap.container.findViewById(R.id.deliver_method)).setText(wrap.submit.deliver.deliverMethod);
        TextView payTime = (TextView) wrap.container.findViewById(R.id.pay_time);
        if (wrap.submit.deliver.isSelf()) {
            payTime.setVisibility(View.INVISIBLE);
        } else {
            payTime.setVisibility(View.VISIBLE);
            payTime.setText(sdf.format(wrap.submit.deliver.normalTime));
        }
    }

    private void fetchData() {
        showProgressDialog();
        String ids = getIntent().getStringExtra(String.class.getName());
        execute(new Request(Urls.ORDER_CREATE).addUrlParam("cartItemId", ids),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult.isOk()) {
                            List<M11> data = apiResult.getModels(M11.class);
                            if (data != null && !data.isEmpty()) {
                                wrapList.add(new Wrap(data.get(0)));
                                if (data.size() == 2) {
                                    findViewById(R.id.order_two).setVisibility(View.VISIBLE);
                                    wrapList.add(new Wrap(data.get(1), R.id.order_two));
                                }
                                for (Wrap wrap : wrapList) {
                                    fetchCouponAndScore(wrap);
                                }
                            } else {
                                dismissProgressDialog();
                                orderFailure();
                            }
                        } else if (apiResult.code.equals(Urls.CODE_ADDRESS_EMPTY)) {
                            dismissProgressDialog();
                            setAddress();
                        } else {
                            dismissProgressDialog();
                            orderFailure();
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        dismissProgressDialog();
                        orderFailure();
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

    private void orderFailure() {
        toast("订单获取失败");
    }


//region address

    private void updateAddress() {
        M18 m18 = wrapList.get(0).m11.address;
        for (Wrap wrap : wrapList) {
            wrap.submit.addressId = String.valueOf(m18.id);
        }
        name.setText(m18.name);
        mobile.setText(m18.mobile);
        address.setText(m18.addressDetail);
    }

    private void pickAddress() {
        go.name(AddressListActivity.class).param(String.class.getName(), "need").goForResult(REQUEST_ADDRESS);
    }

    //endregion address
    private void fillGoods(final int layoutRid, final List<M10> goods, final int labelRid) {
        if (goods == null || goods.size() == 0) {
            findViewById(labelRid).setVisibility(View.GONE);
        } else {
            findViewById(labelRid).setVisibility(View.VISIBLE);
            LinearLayout layout = (LinearLayout) findViewById(layoutRid);
            ViewGroup.LayoutParams verticalGapLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.ui_size_one_dp));
            for (M10 m10 : goods) {
                View convertView = getLayoutInflater().inflate(R.layout.item_order_goods, null);

                loadImage((ImageView) convertView.findViewById(R.id.pic), m10.picturePath);
                ((TextView) convertView.findViewById(R.id.title)).setText(m10.name);
                ((TextView) convertView.findViewById(R.id.currentPrice)).setText(String.format("￥%.2f", m10.price));
                ((TextView) convertView.findViewById(R.id.quality)).setText(String.format("X%d", m10.number));
                ((TextView) convertView.findViewById(R.id.status)).setText(m10.description);

                View gap = new View(OrderConfirmActivityNew.this);
                gap.setLayoutParams(verticalGapLayoutParams);
                gap.setBackgroundResource(R.color.ui_divider_bg);
                layout.addView(gap);
                layout.addView(convertView);
            }
        }
    }

    //    region deliver
    private void updateDeliver() {
        deliver_method.setText(submit.deliver.deliverMethod);
        pay_method.setText(submit.deliver.payMethod);

        dismissView(layout_time_normal, m11.goods.size() == 0);
        if (m11.goods.size() > 0) {
            normal_deliver_time.setText(sdf.format(submit.deliver.normalTime));
        }

        dismissView(layout_time_order, m11.preGoods.size() == 0);
        if (m11.preGoods.size() > 0) {
            order_deliver_time.setText(sdf.format(submit.deliver.orderTime));
        }
    }


    private Wrap deliverWrap;

    private void setDeliverInfo(View view) {
        Wrap wrap = (Wrap) view.getTag();
        deliverWrap = wrap;
        Intent intent = new Intent(this, OrderDeliverActivityNew.class);
        intent.putExtra(OrderDeliverActivityNew.Deliver.class.getName(), wrap.submit.deliver);
        intent.putExtra(Integer.class.getName(), wrap.m11.saleType);
        startActivityForResult(intent, REQUEST_DELIVER);
    }

    //endregion deliver
    private void updatePrice() {
        double totalPrice = 0;
        double couponMoney = 0;
        for (Wrap wrap : wrapList) {
            totalPrice += wrap.m11.totalPrice;
            couponMoney += wrap.submit.coupon.money;
        }

        showMoney(total_price, totalPrice);
        subMoney(total_coupon, (float) couponMoney);
        subMoney(total_point, usedScore);
        double lastPrice = totalPrice - couponMoney - usedScore;
        showMoney(value, lastPrice);
    }

    private void updateCouponAndPoint() {
        if (m33 == null) {
            m33 = new M33();
            m33.courpons = new ArrayList<M15>();
            m33.score = 0;
        }
        int availableCouponSize = m33.courpons.size();
        coupon_label.setText(String.format("%d张可用", availableCouponSize));
        findViewById(R.id.coupon).setOnClickListener(onClickListener);
        int usedCouponSize = submit.coupon.list.size();
        int black = getResources().getColor(R.color.ui_font_a);
        int orange = getResources().getColor(R.color.ui_font_orange);
        if (usedCouponSize == 0) {
            coupon_used.setText("未使用");
            coupon_used.setTextColor(black);
        } else {
            coupon_used.setText(String.format("使用%d张代金券", usedCouponSize));
            coupon_used.setTextColor(orange);
        }

        point_label.setText(String.format("%d个积分", m33.score));
        hideView(point_used, m33.score == 0);
        hideView(point_check, m33.score == 0);
        layout_point.setOnClickListener(onClickListener);
    }


    private Wrap couponWrap;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.layout_person_information == v.getId()) {
                pickAddress();
            } else if (R.id.deliver == v.getId()) {
                setDeliverInfo(v);
            } else if (R.id.add == v.getId()) {
                submit();
//                testSubmit();
            } else if (R.id.coupon == v.getId()) {
                couponWrap = (Wrap) v.getTag();
                M33 m33 = couponWrap.m11.m33;

                //test
                couponWrap.submit.coupon.money = 2;
                refreshScore();
                updatePrice();
//                if (m33.courpons == null || m33.courpons.isEmpty()) {
//                    toast("您没有可在当前订单中使用的代金券");
//                } else {
//                    startActivityForResult(new Intent(OrderConfirmActivityNew.this, CouponChooseActivity.class).putExtra(M15.class.getName(), new ArrayList<M15>(m33.courpons)), REQUEST_COUPON);
//                }
            } else if (R.id.layout_point == v.getId()) {
                point();
            }
        }
    };

    private void point() {
        if (score == 0) {
            toast("您当前没有积分可以使用");
        } else {
            scoreUsed = !scoreUsed;
            point_check.setSelected(scoreUsed);
            refreshScore();
            updatePrice();
        }
    }

    private void refreshScore() {
        if (scoreUsed) {
            double totalPrice = 0;
            int couponMoney = 0;
            for (Wrap wrap : wrapList) {
                totalPrice += wrap.m11.totalPrice;
                couponMoney += wrap.submit.coupon.money;
            }
            usedScore = Math.min(score, (int) Math.round(totalPrice - couponMoney));
            point_used.setText(String.format("使用%d个积分", usedScore));
        } else {
            usedScore = 0;
            point_used.setText("不使用积分");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADDRESS) {
                m11.address = (M18) data.getSerializableExtra(M18.class.getName());
                updateAddress();
            } else if (requestCode == REQUEST_DELIVER) {
                deliverWrap.submit.deliver = (OrderDeliverActivityNew.Deliver) data.getSerializableExtra(OrderDeliverActivityNew.Deliver.class.getName());
                updateDeliver(deliverWrap);
            } else if (requestCode == REQUEST_COUPON) {
                List<M15> pass = (ArrayList<M15>) data.getSerializableExtra(M15.class.getName());
            } else if (requestCode == REQUEST_ADDRESS_ADD) {
                fetchData();
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

    //    region submit
    private class Coupon {
        List<M15> list;
        float money;
        String idList;

        public void updateCoupon(List<M15> coupons) {
            list = coupons;
            money = 0;
            StringBuilder sb = new StringBuilder();
            String separator = "";
            for (M15 m15 : coupons) {
                if (!TextUtils.isEmpty(separator))
                    sb.append(separator);
                sb.append(m15.id);
                separator = ",";
                money += m15.number;
            }
            idList = sb.toString();
        }
    }

    public class Wrap implements Serializable {
        public M11 m11;
        public Submit submit;
        public View container;

        public Wrap(M11 m11) {
            this(m11, R.id.order_one);
        }

        public Wrap(M11 m11, int rId) {
            this.m11 = m11;
            submit = new Submit(m11);
            container = findViewById(rId);
        }
    }

    class Post implements Serializable {
        public String orderId;
        public String addressId;
        public String paymentType;
        public String deliverType;
        public String deliverTime;
        public String isUseScore;
        public String couponIds;

        public Post(Submit s) {
            orderId = s.orderId;
            addressId = s.addressId;
            paymentType = s.deliver.getPayMethodInteger();
            deliverType = s.deliver.getDeliverMethodInteger();
            deliverTime = String.valueOf(s.deliver.normalTime);
            isUseScore = String.valueOf(scoreUsed);
            couponIds = s.coupon.idList;
        }
    }

    public class Submit {
        String orderId;
        String addressId;
        public OrderDeliverActivityNew.Deliver deliver;
        boolean useScore;
        Coupon coupon;
        int score;//使用积分

        public Submit(M11 m11) {
            orderId = String.valueOf(m11.id);
            if (m11.address != null)
                addressId = String.valueOf(m11.address.id);
            long time = System.currentTimeMillis();
            deliver = OrderDeliverActivityNew.Deliver.getInstance(m11);
            if (m11.saleType == 0) {
                deliver.normalTime = getOrderDeliverTime(m11, time);
            } else {
                deliver.normalTime = getNormalDeliverTime(m11, time);
            }
            useScore = false;
            score = 0;
            coupon = new Coupon();
            coupon.updateCoupon(new ArrayList<M15>());
        }

        public Request getRequest() {
            Request request = new Request(Urls.ORDER_SUBMIT).
                    addUrlParam("orderId", String.valueOf(m11.id)).
                    addUrlParam("addressId", addressId).
                    addUrlParam("paymentType", deliver.getPayMethodInteger()).
                    addUrlParam("deliverType", deliver.getDeliverMethodInteger()).
                    addUrlParam("deliverTime", String.valueOf(deliver.normalTime)).
                    addUrlParam("preDeliverTime", String.valueOf(deliver.orderTime)).
                    addUrlParam("isUseScore", String.valueOf(useScore));

            if (!TextUtils.isEmpty(coupon.idList)) {
                coupon.idList = "";
            }
            request.addUrlParam("couponIds", coupon.idList);
            return request;
        }
    }

    private long getNormalDeliverTime(M11 m11, long now) {
        long result = 0;
        if (!m11.goods.isEmpty()) {
            boolean current = TimeUtil.inWorkTime(now);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(now);
            if (current) {
                calendar.add(Calendar.MINUTE, TimeUtil.DELIVER_MAX);
            } else {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 9);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
            }
            result = calendar.getTimeInMillis();
        }
        return result;
    }

    private long getOrderDeliverTime(M11 m11, long now) {
        long result = 0;
        if (!m11.goods.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(now);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            result = calendar.getTimeInMillis();
        }
        return result;
    }

    private Submit submit;

    private void submit() {
        Request request = new Request(Urls.ORDER_SUBMIT);

        List<Post> posts = new ArrayList<Post>();
        for (Wrap wrap : wrapList) {
            posts.add(new Post(wrap.submit));
        }
        String json = JSONObject.toJSONString(posts);
        request.addUrlParam("order", json);
        showProgressDialog();
        execute(request,
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult.isOk()) {
                            if (submit.deliver.payMethod.equals(OrderDeliverActivity.PAY_ARRIVAL)) {
                                toast("订单提交成功");
                                finish();
                            } else {
                                M34 m34 = apiResult.getModel(M34.class);
                                getMolinBalance(m34);
                            }
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

    private void testSubmit() {
    }

    private void getMolinBalance(final M34 m34) {
        execute(new Request(Urls.MEMBER_WALLET_REMAIN_SUM),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult.isOk()) {
                            M16 m16 = apiResult.getModel(M16.class);
                            choosePayMethod(m34.payNo, m34.price, m16.remain);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                    }
                }
        );
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
                OrderConfirmActivityNew.super.onBackPressed();
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
}
