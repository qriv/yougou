package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M2;
import com.gsh.app.client.mall.https.model.M28;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taosj on 15/2/2.
 */
public class CommunitySettingActivity extends LocationActivity {

    @InjectView
    private ListView list;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_setting);
        setTitleBar(true, "选择社区");
        Injector.self.inject(this);
        list.setVerticalScrollBarEnabled(false);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chooseCommunity(adapter.getItem(position).id + "");
            }
        });
    }

    @Override
    protected void onLocated() {
        fetchData();
    }

    @Override
    protected void onLocateFailure() {
        super.onLocateFailure();
        fetchData();
    }


    private void fetchData() {
        execute(new Request(Urls.COMMUNITY_LIST).
                        addUrlParam("Lat", String.valueOf(bdLocation.getLatitude())).
                        addUrlParam("Lng", String.valueOf(bdLocation.getLongitude())),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult.isOk()) {
                            List<M2> data = apiResult.getModels(M2.class);
                            adapter.addData(data);
                        } else {
                            toast(apiResult.message);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        dismissProgressDialog();
                    }
                });
    }

    private void chooseCommunity(final String communityId) {
        showProgressDialog();
        execute(new Request(Urls.COMMUNITY_CHANGE).
                        addUrlParam("communityId", communityId),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult.isOk()) {
                            M28 m28 = apiResult.getModel(M28.class);
                            MallApplication.user.setCommunityId(Long.parseLong(communityId));
                            MallApplication.user.setStationId(m28.id);
                            MallApplication.user.setStationName(m28.name);
                            saveUser();
                            go.name(MainActivity.class).goAndFinishCurrent();
                        } else {
                            toast(apiResult.message);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        dismissProgressDialog();
                        toast("您选择的小区还未完善，请选择其他小区");
                    }
                });
    }

    private class MyAdapter extends BaseAdapter {
        private List<M2> data;

        public MyAdapter() {
            data = new ArrayList<M2>();
        }

        public void addData(List<M2> data) {
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public M2 getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_community_setting, null);
                viewHolder = new ViewHolder();
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.name);
                viewHolder.addressView = (TextView) convertView.findViewById(R.id.community_address);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final M2 m2 = getItem(position);
            viewHolder.nameView.setText(m2.name);
            viewHolder.addressView.setText(m2.address);
            return convertView;
        }
    }

    private class ViewHolder {
        TextView nameView;
        TextView addressView;
    }

    @Override
    protected void onRightActionPressed() {
        go.name(MainActivity.class).goAndFinishCurrent();
    }

    @Override
    public void onBackPressed() {
        notice(new CallBack() {
            @Override
            public void call() {
                finishAll();
            }
        }, "确定要退出程序？", "", false);
    }
}
