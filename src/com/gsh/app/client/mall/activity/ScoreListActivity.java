package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M13;
import com.gsh.app.client.mall.https.model.M41;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.android.widget.ViewHolder;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class ScoreListActivity extends ActivityBase {

    @InjectView
    private TextView score;
    @InjectView
    private ListView list;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        Injector.self.inject(this);
        setTitleBar(false, "人民优购积分", RightAction.ICON, R.drawable.ui_why);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        list.setVerticalScrollBarEnabled(false);
        prepareData();
    }


    private void prepareData() {
        showProgressDialog();
        execute(new Request(Urls.MEMBER_WALLET_CREDITS), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                dismissProgressDialog();
                if(apiResult.isOk()){
                    M41 data = apiResult.getModel(M41.class);
                    final String string = getString(R.string.label_balance_score, data.currentScore);
                    score.setText(Html.fromHtml(string));
                    adapter.addData(data.items,true);
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                dismissProgressDialog();
            }
        });
    }


    private class MyAdapter extends BaseAdapter {
        List<M13> data;

        public MyAdapter() {
            data = new ArrayList<M13>();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public M13 getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addData(List<M13> data, boolean refresh) {
            if (refresh)
                this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_score, null);
            }
            TextView title = (TextView) ViewHolder.get(convertView, R.id.title);
            TextView value = (TextView) ViewHolder.get(convertView, R.id.value);
            final M13 pointRecordCommand = getItem(position);
            title.setText(pointRecordCommand.item);
            if (pointRecordCommand.isConsume) {
                value.setText("-" + pointRecordCommand.number);
                value.setTextColor(getResources().getColor(R.color.ui_font_red));
            } else {
                value.setText("+" + pointRecordCommand.number);
                value.setTextColor(getResources().getColor(R.color.ui_font_green));
            }

            return convertView;
        }
    }
}
