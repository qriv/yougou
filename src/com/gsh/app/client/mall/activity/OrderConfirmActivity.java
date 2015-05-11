package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.*;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.common.utils.TimeUtil;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class OrderConfirmActivity extends PayActivity {
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
    private View layout_address, layout_time_order, layout_time_normal;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m11 = (M11) getIntent().getSerializableExtra(M11.class.getName());
        setContentView(R.layout.activity_order_confirm);
        Injector.self.inject(this);
        setTitleBar("确认订单");
        hideContent();
        findViewById(R.id.layout_person_information).setOnClickListener(onClickListener);
        findViewById(R.id.deliver).setOnClickListener(onClickListener);
        findViewById(R.id.add).setOnClickListener(onClickListener);
//        prepareData();

//        fillContent();
        if (m11 != null) {
            fetchCouponAndScore();
        } else {
            fetchData();
        }
    }

    private void fetchCouponAndScore() {
        showProgressDialog();
        execute(new Request(Urls.ORDER_COUPON).addUrlParam("orderId", String.valueOf(m11.id)),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult.isOk()) {
                            m33 = apiResult.getModel(M33.class);
                            fillContent();
                            return;
                        }
                        orderFailure();
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        dismissProgressDialog();
                        fillContent();
//                        orderFailure();
                    }
                }
        );
    }

    private void fillContent() {
        submit = new Submit();
        showContent();
        updateAddress();
        fillGoods(R.id.normal_goods, m11.goods, R.id.normal_goods_label);
        fillGoods(R.id.order_goods, m11.preGoods, R.id.order_goods_label);
        updateDeliver();
        updateCouponAndPoint();
        updatePrice();
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
                            m11 = data.get(0);

                            fetchCouponAndScore();
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

    private void prepareData() {
        //address
        m11 = new M11();
        M18 m18 = new M18();
        m18.address = "中德英伦联邦4栋2单元1403";
        m18.name = "张三";
        m18.mobile = "18012345678";
        m11.address = m18;

        //goods
        List<M10> goods = new ArrayList<M10>();
        goods.add(new M10(0, Constant.TestData.fruit_names[0], Constant.TestData.fruit_pictures[0], 10.00, 2, "4.5斤"));
        goods.add(new M10(1, Constant.TestData.fruit_names[1], Constant.TestData.fruit_pictures[1], 8.50, 3, "1升"));
        m11.goods = new ArrayList<M10>();
        m11.preGoods = goods;

        //deliver

        m11.payType = "在线支付";
        m11.deliverType = "配送";

        //coupon
        List<M15> coupons = new ArrayList<M15>();
        Calendar calendarA = Calendar.getInstance();
        calendarA.setTimeInMillis(System.currentTimeMillis());
        long time = calendarA.getTime().getTime();
        coupons.add(new M15(0, 5, "满100元可使用", time, time));
        coupons.add(new M15(1, 10, "满200元可使用", time, time));
        m33 = new M33();
        m33.courpons = new ArrayList<M15>();
        m33.score = 0;
    }

//region address

    private void updateAddress() {
        layout_address.setVisibility(View.VISIBLE);
        M18 m18 = m11.address;
        submit.addressId = String.valueOf(m18.id);
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

                View gap = new View(OrderConfirmActivity.this);
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


    private void setDeliverInfo() {
        Intent intent = new Intent(this, OrderDeliverActivity.class);
        intent.putExtra(OrderDeliverActivity.Deliver.class.getName(), submit.deliver);
        startActivityForResult(intent, REQUEST_DELIVER);
    }

    //endregion deliver
    private void updatePrice() {

        showMoney(total_price, m11.totalPrice);
        subMoney(total_coupon, submit.coupon.money);
        subMoney(total_point, submit.score);
        double lastPrice = m11.totalPrice - submit.coupon.money - submit.score;
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


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.layout_person_information == v.getId()) {
                pickAddress();
            } else if (R.id.deliver == v.getId()) {
                setDeliverInfo();
            } else if (R.id.add == v.getId()) {
                submit();
            } else if (R.id.coupon == v.getId()) {
                if (m33.courpons == null || m33.courpons.isEmpty()) {
                    toast("您没有可在当前订单中使用的代金券");
                } else {
                    startActivityForResult(new Intent(OrderConfirmActivity.this, CouponChooseActivity.class).putExtra(M15.class.getName(), new ArrayList<M15>(m33.courpons)), REQUEST_COUPON);
                }
            } else if (R.id.layout_point == v.getId()) {
                point();
            }
        }
    };

    private void point() {
        if (m33.score == 0) {
            toast("您当前没有积分可以使用");
        } else {
            submit.useScore = !submit.useScore;
            point_check.setSelected(submit.useScore);
            if (submit.useScore) {
                int score = Math.min(m33.score, (int) Math.round(m11.totalPrice));
                point_used.setText(String.format("使用%d个积分", score));
            } else {
                point_used.setText("不使用积分");
            }
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
                submit.deliver = (OrderDeliverActivity.Deliver) data.getSerializableExtra(OrderDeliverActivity.Deliver.class.getName());
                updateDeliver();
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

    private class Submit {
        String addressId;
        OrderDeliverActivity.Deliver deliver;
        boolean useScore;
        Coupon coupon;
        int score;//使用积分

        public Submit() {
            if (m11.address != null)
                addressId = String.valueOf(m11.address.id);

            long time = System.currentTimeMillis();
            deliver = OrderDeliverActivity.Deliver.getInstance(m11);
            deliver.normalTime = getNormalDeliverTime(time);
            deliver.orderTime = getOrderDeliverTime(time);
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

    private long getNormalDeliverTime(long now) {
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

    private long getOrderDeliverTime(long now) {
        long result = 0;
        if (!m11.preGoods.isEmpty()) {
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
        if (m11.address == null) {
            notice(new CallBack() {
                @Override
                public void call() {
                    pickAddress();
                }
            }, "请填写收货地址", "", false);
        } else {
            showProgressDialog();
            execute(submit.getRequest(),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if (apiResult.isOk()) {
                                if (submit.deliver.payMethod.equals(OrderDeliverActivity.PAY_ARRIVAL)) {
                                    if (submit.deliver.deliverMethod.equals(OrderDeliverActivity.DELIVER_TRANSFER)) {
                                        toast("订单提交成功，我们将尽快安排配送");
                                    } else {
                                        toast("订单提交成功，请尽快到服务站提取商品");
                                    }
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
                OrderConfirmActivity.super.onBackPressed();
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
