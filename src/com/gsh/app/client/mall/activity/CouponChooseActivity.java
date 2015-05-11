package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.model.M15;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.android.widget.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taosj on 15/3/13.
 */
public class CouponChooseActivity extends ActivityBase {

    @InjectView
    ListView list;
    @InjectView
    private View checkAll, ok;
    private boolean allSelected;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        data.addAll((ArrayList<M15>) getIntent().getSerializableExtra(M15.class.getName()));
        List<M15> pass = (ArrayList<M15>) getIntent().getSerializableExtra(M15.class.getName());
        setContentView(R.layout.activity_choose_coupon);
        setTitleBar("选择代金券");
        Injector.self.inject(this);
        list.setVerticalScrollBarEnabled(false);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        adapter.changeData(pass);
        checkAll.setOnClickListener(onClickListener);
        checkAll.performClick();
        ok.setOnClickListener(onClickListener);
    }

    private class MyAdapter extends BaseAdapter {
        private List<M15> data;
        private List<M15> checkedItems;

        public MyAdapter() {
            data = new ArrayList<M15>();
            checkedItems = new ArrayList<M15>();
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

        public void checkAll(boolean on) {
            for (M15 m15 : data) {
                m15.checked = on;
            }
            if (on)
                checkedItems.addAll(data);
            else
                checkedItems.clear();
            notifyDataSetChanged();
        }

        public List<M15> getCheckedItems() {
            return checkedItems;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.item_coupon, null);

            View leftView = ViewHolder.get(convertView, R.id.left);
            View rightView = ViewHolder.get(convertView, R.id.right);
            TextView usedValueView = ViewHolder.get(convertView, R.id.use_value);
            leftView.setBackgroundColor(Color.parseColor("#0e9d2f"));
            rightView.setBackgroundColor(Color.parseColor("#0ad75c"));
            usedValueView.setTextColor(getResources().getColor(R.color.ui_font_c));

            TextView valueView = ViewHolder.get(convertView, R.id.value);
            TextView titleView = ViewHolder.get(convertView, R.id.title);
            TextView timeView = ViewHolder.get(convertView, R.id.time);
            final M15 m15 = getItem(position);
            valueView.setText(getString(R.string.voucher_value, m15.number));
            usedValueView.setText(m15.constraint);

            titleView.setText(String.format("%s元代金券", chinese(m15.number)));
            timeView.setText(getString(R.string.voucher_time, sdf.format(m15.beginOn), sdf.format(m15.endOn)));
            ViewHolder.get(convertView, R.id.check).setSelected(m15.checked);
            View container = ViewHolder.get(convertView, R.id.container);
            container.setTag(m15);
            container.setOnClickListener(onClickListener);
            return convertView;
        }

        public void check(M15 m15) {
            m15.checked = !m15.checked;
            if (m15.checked && !checkedItems.contains(m15)) {
                checkedItems.add(m15);
            } else if (!m15.checked && checkedItems.contains(m15)) {
                checkedItems.remove(m15);
            }

            allSelected = checkedItems.size() == data.size();
            checkAll.setSelected(allSelected);
            notifyDataSetChanged();
        }
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

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.container == v.getId()) {
                M15 m15 = (M15) v.getTag();
                adapter.check(m15);
            } else if (R.id.checkAll == v.getId()) {
                allSelected = !allSelected;
                v.setSelected(allSelected);
                adapter.checkAll(allSelected);
            } else if (R.id.ok == v.getId()) {
                Intent intent = new Intent();
                intent.putExtra(M15.class.getName(), new ArrayList<M15>(adapter.getCheckedItems()));
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    };
}
