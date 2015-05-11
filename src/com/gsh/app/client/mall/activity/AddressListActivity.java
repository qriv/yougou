package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M18;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.widget.ViewHolder;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taosj on 15/2/3.
 */
public class AddressListActivity extends ActivityBase {

    private final List<M18> data = new ArrayList<M18>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        setTitleBar(false, R.string.title_address, RightAction.TEXT, "添加地址");
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        showProgressDialog();
        execute(new Request(Urls.MEMBER_ADDRESS), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                dismissProgressDialog();
                if (apiResult.isOk()) {
                    data.clear();
                    List<M18> addresses = apiResult.getModels(M18.class);
                    if (addresses != null && !addresses.isEmpty()) {
                        hideEmptyPage();
                        M18 defaultAddress = null;
                        for (M18 m18 : addresses) {
                            if (m18.isDefault)
                                defaultAddress = m18;
                        }
                        if (defaultAddress != null) {
                            addresses.remove(defaultAddress);
                            addresses.add(0, defaultAddress);
                        }
                        data.addAll(addresses);
                        adapter.notifyDataSetChanged();
                    } else {
                        showEmptyPage("您还没有添加地址");
                    }
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                dismissProgressDialog();
            }
        });
    }

    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_address, null);
            }
            M18 item = data.get(position);
            View icon = ViewHolder.get(convertView, R.id.icon);

            TextView name = (TextView) ViewHolder.get(convertView, R.id.name);
            TextView tel = (TextView) ViewHolder.get(convertView, R.id.tel);
            TextView address = (TextView) ViewHolder.get(convertView, R.id.addr);
            name.setSelected(item.isDefault);
            tel.setSelected(item.isDefault);
            address.setSelected(item.isDefault);
            if (item.isDefault) {
                convertView.setBackgroundResource(R.color.ui_bg_address_default);
                icon.setVisibility(View.VISIBLE);
            } else {
                convertView.setBackgroundResource(R.color.ui_bg_white);
                icon.setVisibility(View.INVISIBLE);
            }
            if (MallApplication.test) {
                name.setText(item.name + "_" + item.id);
            } else {
                name.setText(item.name);
            }

            tel.setText(item.mobile);
            address.setText(item.communityName + item.address);
            return convertView;
        }
    };

    @Override
    protected void onRightActionPressed() {
        go.name(AddressEditActivity.class).goForResult(0x1943);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String action = getIntent().getStringExtra(String.class.getName());
            if (TextUtils.isEmpty(action)) {
                go.name(AddressDetailActivity.class).param(M18.class.getName(), data.get(position)).goForResult(0x9527);
            } else {
                setResult(RESULT_OK, new Intent().putExtra(M18.class.getName(), data.get(position)));
                finish();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            loadData();
        }
    }
}
