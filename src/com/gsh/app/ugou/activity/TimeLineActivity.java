package com.gsh.app.ugou.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.ActivityBase;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.gsh.app.ugou.https.model.ComboOrder;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.android.widget.ViewHolder;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taosj on 15/4/14.
 */
public class TimeLineActivity extends ActivityBase {

    @InjectView
    public ListView list;

    private final List<ComboOrder> timelines = new ArrayList<ComboOrder>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        setTitleBar("套餐配送");
        Injector.self.inject(this);
        list.setAdapter(adapter);

        showProgressDialog();
        execute(new Request(Urls.COMBO_ORDER_LIST), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                dismissProgressDialog();
                if (apiResult != null && apiResult.isOk()) {
                    List<ComboOrder> models = apiResult.getModels(ComboOrder.class);
                    if (models != null && !models.isEmpty()) {
                        timelines.addAll(models);
                        adapter.notifyDataSetChanged();
                    } else {
                        showEmptyPage("您还没有订购套餐");
                    }
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                dismissProgressDialog();
                showErrorPage("数据请求失败");
            }
        });


    }

    private final BaseAdapter adapter = new BaseAdapter() {
        public int getCount() {
            return timelines.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.item_timeline, null);

            TextView name = ViewHolder.get(convertView, R.id.name);
            TextView desc = ViewHolder.get(convertView, R.id.desc);

            final ComboOrder data = timelines.get(position);
            name.setText(data.name);
            desc.setText(data.address);

            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    go.name(TimeLineDetailActivity.class).param("id", data.orderId).go();
                }
            });

            return convertView;
        }
    };
}
