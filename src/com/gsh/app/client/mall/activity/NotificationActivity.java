package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M23;
import com.gsh.app.client.mall.https.model.M43;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class NotificationActivity extends ActivityBase {
    private WebView webView;
    private String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_notification_list);
        setTitleBar(getString(R.string.app_name,"头条"));
        webView = (WebView) findViewById(R.id.web);
        fetchData();
    }

    private void fetchData() {
        String messageId = getIntent().getStringExtra(String.class.getName());
        showProgressDialog();
        execute(new Request(Urls.MEMBER_MESSAGE_DETAIL).addUrlParam("id", messageId),
                new HttpResultHandler() {

                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult != null && apiResult.isOk()) {
                            M43 m43 = apiResult.getModel(M43.class);
                            webView.loadDataWithBaseURL(null, m43.content, "text/html", "utf-8", null);
                        } else {
                            showErrorPage("网页加载失败");
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        dismissProgressDialog();
                        showErrorPage("网页加载失败");
                    }
                });
    }


    class MyAdapter extends BaseAdapter {
        private List<M23> data;

        public MyAdapter() {
            data = new ArrayList<M23>();
        }

        public void addData(List<M23> data) {
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public M23 getItem(int position) {
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
                convertView = getLayoutInflater().inflate(R.layout.item_notification, null);
                viewHolder = new ViewHolder();
                viewHolder.notificationView = (TextView) convertView.findViewById(R.id.notification);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final M23 m23 = getItem(position);
            viewHolder.notificationView.setText(m23.content);
            return convertView;
        }
    }

    class ViewHolder {
        TextView notificationView;
    }
}
