package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.model.M11;
import com.gsh.app.client.mall.pickerview.TimeDialog;
import com.gsh.app.client.mall.pickerview.TimePopupWindow;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.android.widget.ViewUtils;
import com.litesuits.common.utils.TimeUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class OrderDeliverActivityNew extends ActivityBase {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final String[] PAY_METHOD = {"在线支付", "货到付款"};
    private static final String[] DELIVER_METHOD = {"配送", "自提"};
    private static final int startTime = 9;
    private static final int endTime = 18;
    private Deliver deliver;
    private int saleType;
    private List<View> payMethodViews;
    private List<View> deliverMethodViews;
    private List<View> deliverTimeViews;
    @InjectView
    private TextView normal_time;
    @InjectView
    private View layout_normal_time, layout_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deliver = (Deliver) getIntent().getSerializableExtra(Deliver.class.getName());
        saleType = getIntent().getIntExtra(Integer.class.getName(), -1);
        setContentView(R.layout.activity_order_deliver_new);
        Injector.self.inject(this);
        setTitleBar(false, "选择支付配送", RightAction.TEXT, "提交");
        fillPayMethod();
        fillDeliverMethod();
        fillDeliverTime();
    }

    private void fillPayMethod() {
        payMethodViews = new ArrayList<View>();
        List<String> stringList = new ArrayList<String>(Arrays.asList(PAY_METHOD));
        fillText(payMethodViews, R.id.pay_method, stringList, R.layout.item_deliver_long, R.dimen.deliver_long);
    }

    private void fillDeliverMethod() {
        deliverMethodViews = new ArrayList<View>();
        List<String> stringList = new ArrayList<String>(Arrays.asList(DELIVER_METHOD));
        fillText(deliverMethodViews, R.id.deliver_method, stringList, R.layout.item_deliver_short, R.dimen.deliver_short);
    }

    private void fillDeliverTime() {
        normal_time.setText(sdf.format(deliver.normalTime));
        findViewById(R.id.change_normal_time).setOnClickListener(onClickListener);
    }

    private void fillText(List<View> views, int layoutRid, List<String> stringList, int textLayout, int textWidthRid) {
        LinearLayout layout = (LinearLayout) findViewById(layoutRid);
        layout.removeAllViews();
        int textWidth = getResources().getDimensionPixelOffset(textWidthRid);
        int textHeight = getResources().getDimensionPixelOffset(R.dimen.ui_margin_k);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(textWidth, textHeight);
        int gapWidth = getResources().getDimensionPixelOffset(R.dimen.ui_margin_g);

        for (String path : stringList) {
            View container = getLayoutInflater().inflate(textLayout, null);
            container.setLayoutParams(layoutParams);
            container.setTag(R.id.tag_key_first, layoutRid);
            container.setTag(R.id.tag_key_second, path);
            container.setOnClickListener(onClickListener);
            TextView textView = (TextView) container.findViewById(R.id.value);
            textView.setText(path);

            boolean selected = false;
            if (layoutRid == R.id.pay_method) {
                selected = path.equals(deliver.payMethod);
            } else if (layoutRid == R.id.deliver_method) {
                selected = path.equals(deliver.deliverMethod);
            } else if (layoutRid == R.id.deliver_time) {
                int hour = Integer.valueOf(path.substring(0, path.length() - 1));
                //selected = hour == deliver.hour;
            }
            container.findViewById(R.id.label).setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
            container.setSelected(selected);
            int selectColor = getResources().getColor(R.color.ui_font_c);
            int unselectColor = getResources().getColor(R.color.ui_font_e);
            textView.setTextColor(selected ? selectColor : unselectColor);
            views.add(container);
        }

        ViewUtils.addViews(OrderDeliverActivityNew.this, layout, views, textWidth, Gravity.LEFT, gapWidth, 0);
    }

    private void setView(View container) {
        Integer layoutRid = (Integer) container.getTag(R.id.tag_key_first);
        String path = (String) container.getTag(R.id.tag_key_second);
        boolean selected = false;
        if (layoutRid == R.id.pay_method) {
            selected = path.equals(deliver.payMethod);
        } else if (layoutRid == R.id.deliver_method) {
            selected = path.equals(deliver.deliverMethod);
        } else if (layoutRid == R.id.deliver_time) {
            int hour = Integer.valueOf(path.substring(0, path.length() - 1));
//            selected = hour == deliver.hour;
        }
        container.findViewById(R.id.label).setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        container.setSelected(selected);
        int selectColor = getResources().getColor(R.color.ui_font_c);
        int unselectColor = getResources().getColor(R.color.ui_font_e);
        TextView textView = (TextView) container.findViewById(R.id.value);
        textView.setTextColor(selected ? selectColor : unselectColor);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.deliver == v.getId()) {
                onTagClicked(v);
            } else if (R.id.change_normal_time == v.getId()) {
//                showTimePopupWindow();
                if (saleType == 0) {
                    showNormalTimeDialog();
                } else {
                    showOrderTimeDialog();
                }
            }
        }
    };

    TimeDialog orderTimeDialog;
    TimeDialog normalTimeDialog;

    private void showNormalTimeDialog() {
        boolean current = TimeUtil.inWorkTime(deliver.normalTime);
        normalTimeDialog = new TimeDialog(context, R.style.DialogSlideAnim, TimePopupWindow.Type.WEEKDAY_HOUR_MIN, current, deliver.normalTime);
        normalTimeDialog.setOnTimeSelectListener(new TimeDialog.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                normal_time.setText(sdf.format(date.getTime()));
                deliver.normalTime = date.getTime();
            }
        });
        normalTimeDialog.show();
    }

    private void showOrderTimeDialog() {
        orderTimeDialog = new TimeDialog(context, R.style.DialogSlideAnim, TimePopupWindow.Type.WEEKDAY_HOUR_MIN, false, deliver.orderTime);
        orderTimeDialog.setOnTimeSelectListener(new TimeDialog.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                normal_time.setText(sdf.format(date.getTime()));
                deliver.orderTime = date.getTime();
            }
        });
        orderTimeDialog.show();
    }


    private void onTagClicked(View view) {
        Integer cat = (Integer) view.getTag(R.id.tag_key_first);
        String value = (String) view.getTag(R.id.tag_key_second);
        if (cat.equals(R.id.pay_method)) {
            deliver.payMethod = value;
            select(payMethodViews, value);
        } else if (cat.equals(R.id.deliver_method)) {
            deliver.deliverMethod = value;
            dismissView(layout_time, deliver.deliverMethod.equals(DELIVER_SELF));

            select(deliverMethodViews, value);
        } else if (cat.equals(R.id.deliver_time)) {
            int hour = Integer.valueOf(value.substring(0, value.length() - 1));
//            deliver.hour = hour;
            select(deliverTimeViews, value);
        }


    }

    private void select(List<View> list, String selectValue) {
        for (View view : list) {
            setView(view);
        }
    }


    public static final String DELIVER_TRANSFER = "配送";
    public static final String DELIVER_SELF = "自提";
    public static final String PAY_ONLINE = "在线支付";
    public static final String PAY_ARRIVAL = "货到付款";

    public static class Deliver implements Serializable {
        public String payMethod;
        public String deliverMethod;
        public long normalTime;
        public long orderTime;

        public static Deliver getInstance(M11 m11) {
            Deliver deliver = new Deliver();
            deliver.payMethod = PAY_ONLINE;
            if (m11.payType.equals("1")) {
                deliver.payMethod = PAY_ARRIVAL;
            }
            deliver.deliverMethod = DELIVER_TRANSFER;
            if (m11.deliverType.equals("1")) {
                deliver.deliverMethod = DELIVER_SELF;
            }
            return deliver;
        }

        public String getPayMethodInteger() {
            String result = "0";
            if (payMethod.equals(PAY_ARRIVAL)) {
                result = "1";
            }
            return result;
        }

        public String getDeliverMethodInteger() {
            String result = "0";
            if (deliverMethod.equals(DELIVER_SELF)) {
                result = "1";
            }
            return result;
        }

        public boolean isSelf() {
            return deliverMethod.equals(DELIVER_SELF);
        }
    }

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        Intent intent = new Intent();
        intent.putExtra(Deliver.class.getName(), deliver);
        setResult(RESULT_OK, intent);
        finish();
    }
}
