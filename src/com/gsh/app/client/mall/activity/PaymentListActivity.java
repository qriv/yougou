package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M17;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.android.widget.ViewHolder;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taosj on 15/2/3.
 */
public class PaymentListActivity extends ActivityBase {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @InjectView
    private ListView list;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        setTitleBar(false, "收支明细", RightAction.ICON, R.drawable.ui_why);
        Injector.self.inject(this);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        prepareData();
    }


    private void prepareData() {
        showProgressDialog();
        execute(new Request(Urls.MEMBER_WALLET_REMAINS), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                dismissProgressDialog();
                if (apiResult.isOk()) {
                    adapter.addData(apiResult.getModels(M17.class), true);
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                dismissProgressDialog();
            }
        });
    }


    private class MyAdapter extends BaseAdapter {
        private List<M17> data;

        public MyAdapter() {
            data = new ArrayList<M17>();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public M17 getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_payment_detail, null);
            }
            TextView title = (TextView) ViewHolder.get(convertView, R.id.title);
            TextView order = (TextView) ViewHolder.get(convertView, R.id.order);
            TextView date = (TextView) ViewHolder.get(convertView, R.id.date);
            TextView value = (TextView) ViewHolder.get(convertView, R.id.value);

            final M17 paymentCommand = getItem(position);

            title.setText(paymentCommand.isConsume ? "充值" : "支出");
            if (!paymentCommand.isConsume) {
                order.setVisibility(View.VISIBLE);
                String orderId = getString(R.string.label_order_id, paymentCommand.orderNo);
                order.setText(orderId);
                value.setText("-" + String.format("%.2f", paymentCommand.number));
                value.setTextColor(getResources().getColor(R.color.ui_font_red));
            } else {
                order.setVisibility(View.INVISIBLE);
                value.setText("+" + String.format("%.2f", paymentCommand.number));
                value.setTextColor(getResources().getColor(R.color.ui_font_green));
            }
            date.setText(sdf.format(paymentCommand.createOn));
            return convertView;
        }

        public void addData(List<M17> data, boolean refresh) {
            if (refresh)
                this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }
    }


}
