package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M19;
import com.gsh.app.client.mall.https.model.M27;
import com.gsh.app.client.mall.https.model.M39;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
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
public class DeliverStatusActivity extends ActivityBase {

    private M19 m19;
    @InjectView
    private TextView status, orderNo, price;//header
    @InjectView
    private TextView worker_name, worker_sex, worker_mobile;//worker
    private List<M39> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m19 = (M19) getIntent().getSerializableExtra(M19.class.getName());
        setContentView(R.layout.activity_deliver_status);
        setTitleBar("配送信息");
        Injector.self.inject(this);
        setHeader();
        setWorker();
        fetchData();
    }

    private void fetchData() {
        showProgressDialog();
        execute(new Request(Urls.ORDER_DELIVER).addUrlParam("orderId", String.valueOf(m19.id)),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        dismissProgressDialog();
                        prepareData();
                        fillData();
                    }
                }
        );
    }

    private void prepareData() {
        data = new ArrayList<M39>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int count = 0;
        while (count < 10) {
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            count++;
            data.add(new M39("one", calendar.getTime().getTime()));
        }
    }

    private void fillData() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.delivers_status);
        layout.removeAllViews();
        boolean first = true;
        for (M39 m39 : data) {
            View convertView = getLayoutInflater().inflate(R.layout.item_deliver, null);
            TextView infoView = (TextView) convertView.findViewById(R.id.info);
            TextView timeView = (TextView) convertView.findViewById(R.id.time);
            infoView.setText(m39.deliverInfo);
            timeView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(m39.deliverInfoTime));
            int green = getResources().getColor(R.color.ui_font_green);
            int grey = getResources().getColor(R.color.ui_font_e);
            int roundRadius = getResources().getDimensionPixelOffset(R.dimen.ui_margin_b);
            GradientDrawable gd = new GradientDrawable();
            gd.setCornerRadius(roundRadius);
            int color = grey;
            if (first) {
                first = false;
                color = green;
                convertView.findViewById(R.id.top).setVisibility(View.INVISIBLE);
            } else {
                convertView.findViewById(R.id.top).setVisibility(View.VISIBLE);
            }
            gd.setColor(color);
            infoView.setTextColor(color);
            timeView.setTextColor(color);
            convertView.findViewById(R.id.circle).setBackgroundDrawable(gd);
            layout.addView(convertView);
        }
    }


    private void setHeader() {
        status.setText(OrderListActivity.OrderStatus.valueOf(m19.status).display);
        orderNo.setText(String.format("订单编号：%s", m19.orderNo));
        price.setText(String.format("订单金额：￥%.2f", m19.pay_price));

    }


    private void setWorker() {
        M27 m27 = m19.worker;
        loadAvatar((ImageView) findViewById(R.id.worker_avatar), m27.avatarPath);
        worker_name.setText(String.format("姓名：%s", m27.name));
        worker_sex.setText(String.format("性别：%s", m27.sex));
        worker_mobile.setText(Html.fromHtml(getString(R.string.deliver_mobile, m27.mobile)));
        worker_mobile.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.worker_mobile == v.getId()) {
                call();
            }
        }
    };

    private void call() {
        String uri = String.format("tel://%s", m19.worker.mobile);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
        startActivity(intent);
    }
}
