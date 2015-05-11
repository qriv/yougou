package com.gsh.app.client.mall.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M15;
import com.gsh.app.client.mall.https.model.M40;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.widget.ViewHolder;
import com.litesuits.common.utils.CMUtil;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;
import com.zxing.activity.CaptureActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by taosj on 15/2/3.
 */
public class VoucherListActivity extends ActivityBase {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    private static final SimpleDateFormat sdfn = new SimpleDateFormat("yyyyMMdd");
    private static final Random random = new Random();
    private MyAdapter adapter;

    private enum VoucherState {
        unused, used, expired;
    }

    private VoucherState currentVoucherState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_coupon);
        setTitleBar(false, "我的代金券", RightAction.ICON, R.drawable.ui_why);
        findViewById(R.id.title_bar_divider).setVisibility(View.INVISIBLE);
        currentVoucherState = VoucherState.unused;
        findViewById(R.id.unused).setOnClickListener(onClickListener);
        findViewById(R.id.used).setOnClickListener(onClickListener);
        findViewById(R.id.expired).setOnClickListener(onClickListener);
        findViewById(R.id.exchange).setOnClickListener(onClickListener);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setVerticalScrollBarEnabled(false);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        fetchData();
    }

    private void fetchData() {
        showProgressDialog();
        execute(new Request(Urls.MEMBER_WALLET_COUPONS), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                dismissProgressDialog();
                if (apiResult.isOk()) {
                    M40 data = apiResult.getModel(M40.class);
                    sortData(data.coupons);
                    TextView info = (TextView) findViewById(R.id.info);
                    String infoText = String.format("您有%d张代金券即将过期", data.willExpiredCount);
                    info.setText(infoText);
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                dismissProgressDialog();
            }
        });
    }


    public String chinese(int number) {
        int ten = number / 10;
        int digit = number % 10;
        StringBuilder sb = new StringBuilder();
        if (ten > 0) {
            if (ten > 1)
                sb.append(digitToString(ten));
            sb.append(digists.charAt(digists.length() - 1));
        }

        if (digit > 0)
            sb.append(digitToString(digit));
        return sb.toString();
    }

    private char digitToString(int number) {
        return digists.charAt(number);
    }

    private static final String digists = "零一二三四五六七八九十";


    private final List<M15> unusedVouchers = CMUtil.getArrayList();
    private final List<M15> usedVouchers = CMUtil.getArrayList();
    private final List<M15> expiredVouchers = CMUtil.getArrayList();

    private void sortData(List<M15> list) {
        for (M15 voucherCommand : list) {
            if ("expired".equals(voucherCommand.status)) {
                expiredVouchers.add(voucherCommand);
            } else if ("used".equals(voucherCommand.status)) {
                usedVouchers.add(voucherCommand);
            } else {
                unusedVouchers.add(voucherCommand);
            }
        }
        adapter.changeData(unusedVouchers);
    }

    private class MyAdapter extends BaseAdapter {
        private List<M15> data;

        public MyAdapter() {
            data = new ArrayList<M15>();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public M15 getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void changeData(List<M15> list) {
            data.clear();
            data.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.item_voucher_unused, null);

            View leftView = ViewHolder.get(convertView, R.id.left);
            View rightView = ViewHolder.get(convertView, R.id.right);
            TextView usedValueView = ViewHolder.get(convertView, R.id.use_value);
            if (currentVoucherState == VoucherState.unused) {
                leftView.setBackgroundColor(Color.parseColor("#0e9d2f"));
                rightView.setBackgroundColor(Color.parseColor("#0ad75c"));
                usedValueView.setTextColor(getResources().getColor(R.color.ui_font_c));
            } else {
                leftView.setBackgroundColor(Color.parseColor("#00000000"));
                rightView.setBackgroundColor(Color.parseColor("#00000000"));
                usedValueView.setTextColor(getResources().getColor(R.color.ui_font_g));
            }

            TextView valueView = ViewHolder.get(convertView, R.id.value);
            TextView titleView = ViewHolder.get(convertView, R.id.title);
            TextView timeView = ViewHolder.get(convertView, R.id.time);
            final M15 voucherCommand = getItem(position);
            valueView.setText(getString(R.string.voucher_value, voucherCommand.number));
            usedValueView.setText(getString(R.string.voucher_use_value, voucherCommand.constraint));
            titleView.setText(String.format("%s元代金券", chinese(voucherCommand.number)));
            timeView.setText(getString(R.string.voucher_time, sdf.format(voucherCommand.beginOn), sdf.format(voucherCommand.endOn)));
            return convertView;
        }
    }

    private static final int REQUEST_QR_CODE = 2038;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.unused == v.getId()) {
                select(VoucherState.unused);
            } else if (R.id.used == v.getId()) {
                select(VoucherState.used);
            } else if (R.id.expired == v.getId()) {
                select(VoucherState.expired);
            } else if (R.id.exchange == v.getId()) {
                go.name(CaptureActivity.class).goForResult(REQUEST_QR_CODE);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_QR_CODE) {
            final String code = data.getExtras().getString("result");
            exchange(code);
        }
    }

    private void exchange(String code) {
        showProgressDialog();
        execute(new Request(Urls.COUPON_EXCHANGE).addUrlParam("code", code),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult != null && apiResult.isOk()) {
                            fetchData();
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

    private void select(VoucherState voucherState) {
        if (currentVoucherState != voucherState) {
            currentVoucherState = voucherState;
            findViewById(R.id.unused_selection).setVisibility(currentVoucherState == VoucherState.unused ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.unused_selection_).setVisibility(currentVoucherState != VoucherState.unused ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.used_selection).setVisibility(currentVoucherState == VoucherState.used ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.used_selection_).setVisibility(currentVoucherState != VoucherState.used ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.expired_selection).setVisibility(currentVoucherState == VoucherState.expired ? View.VISIBLE : View.INVISIBLE);
            findViewById(R.id.expired_selection_).setVisibility(currentVoucherState != VoucherState.expired ? View.VISIBLE : View.INVISIBLE);

            findViewById(R.id.info).setVisibility(currentVoucherState == VoucherState.unused ? View.VISIBLE : View.GONE);
            findViewById(R.id.exchange).setVisibility(currentVoucherState == VoucherState.unused ? View.VISIBLE : View.GONE);
            findViewById(R.id.header_divider).setVisibility(currentVoucherState != VoucherState.unused ? View.VISIBLE : View.GONE);

            if (voucherState == VoucherState.unused) {
                adapter.changeData(unusedVouchers);
            } else if (voucherState == VoucherState.used) {
                adapter.changeData(usedVouchers);
            } else if (voucherState == VoucherState.expired) {
                adapter.changeData(expiredVouchers);
            }
        }
    }
}
