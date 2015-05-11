package com.gsh.app.ugou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;

import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.ShareApp;
import com.gsh.app.client.mall.activity.ActivityBase;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.gsh.app.ugou.https.model.Discovery;
import com.gsh.app.ugou.https.model.DiscoveryDetails;
import com.litesuits.android.log.Log;
import com.litesuits.android.widget.ViewHolder;
import com.litesuits.common.utils.StringUtils;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taosj on 15/4/15.
 */
public class DiscoveryDetailsActivity extends ActivityBase {
    private static final String PREF_LIKED = "discovery_";

    private Discovery discovery;
    private TextView date, desc, fee;
    private ListView recommandListView;

    private TextView shareCount, textLike, readCount;
    private View like;
    private boolean liked;
    private int likedCount;
    private DiscoveryDetails discoveryDetails;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery_details);
        setTitleBar(false, "发现生活详情", RightAction.ICON, R.drawable.action_share);
        showRightAction(false);
        fetchData();
        review();
    }

    private void review() {
        String discoveryId = getIntent().getStringExtra(String.class.getName());
        execute(new Request(Urls.DISCOVERY_REVIEW).addUrlParam("discoveryId", discoveryId),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult != null && apiResult.isOk()) {
                            Log.d("test", "review success");
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        Log.d("test", "review failure");
                    }
                });

    }

    private void setContent() {
        showContent();
        like = findViewById(R.id.like);
        shareCount = (TextView) findViewById(R.id.shareCount);
        readCount = (TextView) findViewById(R.id.readCount);
        textLike = (TextView) findViewById(R.id.text_like);
        liked = preferences.getBoolean(PREF_LIKED + discovery.id, false);
        like.setSelected(liked);
        like.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                like();
            }
        });
        textLike.setText(likedCount + "");
        readCount.setText(discovery.reviewCount + "");
        shareCount.setText(discovery.shareCount + "分享");
        WebView webView = (WebView) findViewById(R.id.web);
        webView.loadDataWithBaseURL(null, discovery.description, "text/html", "utf-8", null);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
//            discoveryListView.addFooterView(footer);
//            discoveryListView.setAdapter(discoveryAdapter);
//            setListViewHeightBasedOnChildren(discoveryListView);

        recommandListView = (ListView) findViewById(R.id.recommend_list);
        adapter = new MyAdapter();
        recommandListView.setAdapter(adapter);
        adapter.setData(discoveryDetails.recommends);
        setListViewHeightBasedOnChildren(recommandListView);
        setHeader();
    }

    private void setHeader() {
        View header = findViewById(R.id.header);
        ((TextView) header.findViewById(R.id.title)).setText(discovery.title);
        ((TextView) header.findViewById(R.id.summary)).setText(discovery.summary);
        ((TextView) header.findViewById(R.id.time)).setText(sdf.format(discovery.createdDate));
        ((TextView) header.findViewById(R.id.shareCount)).setText(discovery.shareCount + "");
    }

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        addShare();
        showShareDialog();
    }

    @Override
    public void onBackPressed() {
        if (discovery != null) {
            Intent intent = new Intent();
            intent.putExtra(Discovery.class.getName(), discovery);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    @Override
    protected void share(ShareApp shareApp) {
        shareContent = new ShareContent();
//        shareContent.url = data.shareUrl;
        String url = "http://" + getString(R.string.app_address) + getString(R.string.share_discovery);
        String discoveryId = getIntent().getStringExtra(String.class.getName());
        shareContent.url = String.format(url, discoveryId);
        if (!TextUtils.isEmpty(discovery.mainPicturePath)) {
            shareContent.path = StringUtils.getPicturePath(discovery.mainPicturePath);
        } else {
            shareContent.path = Urls.IMAGE_LOGO;
        }

        Platform.ShareParams sp = new Platform.ShareParams();
        String appName = getString(R.string.app_name);
        sp.setTitle(appName);
        sp.setTitleUrl(shareContent.url);
        String text = getString(R.string.share_test, shareContent.url, discovery.title,appName);
        sp.setText(text);
        if (shareApp != ShareApp.wechat)
            sp.setImageUrl(shareContent.path);
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform plat = ShareSDK.getPlatform(shareApp.getPlatformName());
        //plat.setPlatformActionListener(this);
        plat.share(sp);
        super.share(shareApp);
    }

    private void like() {
        showProgressDialog();
        String discoveryId = getIntent().getStringExtra(String.class.getName());
        execute(new Request(Urls.DISCOVERY_LIKE).addUrlParam("discoveryId", discoveryId).addUrlParam("isLike", String.valueOf(!liked)),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult != null && apiResult.isOk()) {

                        }
                        handleLikeResult();
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        dismissProgressDialog();
                    }
                });
    }

    private void fetchData() {
        showProgressDialog();
        hideContent();
        String discoveryId = getIntent().getStringExtra(String.class.getName());
        execute(new Request(Urls.DISCOVERY_DETAILS).addUrlParam("discoveryId", discoveryId),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult != null && apiResult.isOk()) {
                            hideErrorPage();
                            showRightAction(true);
                            discoveryDetails = apiResult.getModel(DiscoveryDetails.class);
                            discovery = discoveryDetails.discovery;
                            likedCount = discoveryDetails.discovery.likeCount;
                            setContent();
                        } else {
                            onRequestError();
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        dismissProgressDialog();
                        onRequestError();
                    }
                });
    }

    public void onErrorPageClick() {
        fetchData();
    }

    private void addShare() {
        String discoveryId = getIntent().getStringExtra(String.class.getName());
        execute(new Request(Urls.DISCOVERY_SHARE).addUrlParam("discoveryId", discoveryId),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (discovery != null) {
                            discovery.addShare();
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                    }
                });
    }

    private void handleLikeResult() {
        liked = !liked;
        if (liked) {
            likedCount++;
        } else {
            likedCount--;
        }
        discovery.likeCount = likedCount;
        textLike.setText(likedCount + "");
        like.setSelected(liked);
        preferences.edit().putBoolean(PREF_LIKED + discovery.id, liked).apply();
    }

    private class MyAdapter extends BaseAdapter {
        private List<Discovery> data;

        public MyAdapter() {
            data = new ArrayList<Discovery>();
        }

        public void setData(List<Discovery> data) {
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        public int getCount() {
//            return recommands.size();
            return data.size();
        }

        public Discovery getItem(int position) {
            return data.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.item_recommand, null);
            ImageView pic = ViewHolder.get(convertView, R.id.pic);
            TextView title = ViewHolder.get(convertView, R.id.title);
            TextView likeCount = ViewHolder.get(convertView, R.id.likeCount);
            TextView reviewCount = ViewHolder.get(convertView, R.id.reviewCount);
            TextView desc = ViewHolder.get(convertView, R.id.desc);
            TextView date = ViewHolder.get(convertView, R.id.date);

            Discovery di = getItem(position);
            loadImage(pic, di.mainPicturePath);
            title.setText(di.title);
            likeCount.setText(di.likeCount + "");
            reviewCount.setText(di.reviewCount + "");
            desc.setText(di.summary);
            date.setText(sdf.format(di.createdDate));

            return convertView;
        }

    }

    private void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}

