package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M24;
import com.gsh.app.client.mall.https.model.M4;
import com.gsh.app.client.mall.https.model.M5;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.widget.RatingStars;
import com.litesuits.android.widget.ViewUtils;
import com.litesuits.android.widget.pullrefresh.PullToRefreshBase;
import com.litesuits.android.widget.pullrefresh.PullToRefreshListView;
import com.litesuits.common.utils.StringUtils;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class CommentListActivity extends ActivityBase {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private PullToRefreshListView mPullListView;
    private ListView listView;
    private MyAdapter adapter;
    private LinearLayout.LayoutParams imageParams;
    private boolean end;//已经到了最后一页
    private int page;
    private boolean warnLast;//是否提醒最后一页
    private M24 m24;
    private String goodsId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goodsId = getIntent().getStringExtra(String.class.getName());
        setContentView(R.layout.acitivity_user_comments);
        setTitleBar(R.string.title_comment_list);
        hideContent();
        mPullListView = (PullToRefreshListView) findViewById(R.id.list);
        mPullListView.setPullLoadEnabled(false);
        mPullListView.setPullRefreshEnabled(true);
        mPullListView.setScrollLoadEnabled(false);
        mPullListView.setOnRefreshListener(onRefreshListener);

        listView = mPullListView.getRefreshableView();
        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setDivider(getResources().getDrawable(R.color.ui_bg_grey));
        listView.setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.ui_margin_d));

        tabs = new ArrayList<Tab>();
        tabs.add(new Tab(R.id.all, R.id.all_label, R.id.all_count, true, "ALL"));
        tabs.add(new Tab(R.id.good, R.id.good_label, R.id.good_count, false, "HIGH"));
        tabs.add(new Tab(R.id.normal, R.id.normal_label, R.id.normal_count, false, "MEDIUM"));
        tabs.add(new Tab(R.id.bad, R.id.bad_label, R.id.bad_count, false, "BAD"));
        tabs.add(new Tab(R.id.picture, R.id.picture_label, R.id.picture_count, false, "HAS"));
        for (Tab tab : tabs) {
            findViewById(tab.container).setOnClickListener(onClickListener);
        }
        findViewById(tabs.get(0).container).performClick();
    }

    private void fetchData() {
        execute(new Request(Urls.GOODS_COMMENT_LIST).
                        addUrlParam("goodsId", goodsId + "").
                        addUrlParam("pageIndex", page + "").
                        addUrlParam("pageSize", Constant.CountLimit.PAGE_SIZE + "")
                        .addUrlParam("type", filter)
                ,
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        mPullListView.onPullDownRefreshComplete();
                        if (apiResult.isOk()) {
                            m24 = apiResult.getModel(M24.class);
                            showContent();
                            updateCount();
                            List<M5> comments = m24.comments;
                            end = comments == null || comments.size() == 0;
                            if (comments != null && comments.size() > 0) {
                                adapter.addData(comments, page == 0);
                            }
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        mPullListView.onPullDownRefreshComplete();
                        super.onFailure(res, e);
                    }
                }
        );
    }

    private void updateCount() {
        ((TextView) findViewById(R.id.all_count)).setText(m24.count + "");
        ((TextView) findViewById(R.id.good_count)).setText(m24.countA + "");
        ((TextView) findViewById(R.id.normal_count)).setText(m24.countB + "");
        ((TextView) findViewById(R.id.bad_count)).setText(m24.countC + "");
        ((TextView) findViewById(R.id.picture_count)).setText(m24.countHasPicture + "");
    }

    List<Tab> tabs;

    @Override
    protected void showContent() {
        super.showContent();
        int imageSize = initImageSize();
        imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);

        adapter = new MyAdapter();
        listView.setAdapter(adapter);
    }

    private int initImageSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int maxWidth = metrics.widthPixels;
        int edgePadding = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
        int gapWidth = getResources().getDimensionPixelOffset(R.dimen.ui_size_one_dp);
        int columns = Constant.CountLimit.SHARE_PICTURE;
        return (int) (((float) maxWidth - gapWidth * (columns - 1) - edgePadding * 2) / columns);
    }

    private class MyAdapter extends BaseAdapter {
        private List<M5> data;

        public MyAdapter() {
            data = new ArrayList<M5>();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public M5 getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addData(List<M5> data, boolean refresh) {
            if (refresh)
                this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_goods_comment, null);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
                viewHolder.avatarView = (ImageView) convertView.findViewById(R.id.avatar);
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.name);
                viewHolder.dateView = (TextView) convertView.findViewById(R.id.date);
                viewHolder.infoView = (TextView) convertView.findViewById(R.id.info);
                viewHolder.levelView = (TextView) convertView.findViewById(R.id.level);
                viewHolder.stars = convertView.findViewById(R.id.stars);
                viewHolder.imagesLayout = (LinearLayout) convertView.findViewById(R.id.images);
                viewHolder.dealDateView = (TextView) convertView.findViewById(R.id.deal_date);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final M5 m5 = getItem(position);
            loadAvatar(viewHolder.avatarView, m5.avatarPath);
            String name = m5.nickname;
            if (MallApplication.test) {
                name = name + "_" + m5.id;
            }
            viewHolder.nameView.setText(name);
            viewHolder.dateView.setText(sdf.format(m5.commentOn));
            viewHolder.infoView.setText(m5.comment);
            viewHolder.levelView.setText(m5.level);
            viewHolder.dealDateView.setText(String.format("购买日期：%s", sdf.format(m5.bargainOn)));
            fillImages(viewHolder.imagesLayout, m5.picturePaths);
            RatingStars.rate(viewHolder.stars, m5.score);
            return convertView;
        }

        private void fillImages(LinearLayout layout, List<String> paths) {
            if (paths != null && paths.size() > 0) {
                List<ImageView> views = new ArrayList<ImageView>();
                layout.removeAllViews();
                for (String path : paths) {
                    ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.image, null);
                    imageView.setLayoutParams(imageParams);
                    setGalleryTag(imageView, paths, paths.indexOf(path));
                    String imagePath = StringUtils.getPicturePath(path);
                    loadImage(imageView, imagePath);
                    views.add(imageView);
                }
                ViewUtils.addViews(CommentListActivity.this, layout, views, imageParams.width, Gravity.LEFT);
            }
        }
    }

    class ViewHolder {
        ImageView avatarView;
        TextView nameView;
        TextView levelView;
        TextView dateView;
        TextView infoView;
        View stars;
        LinearLayout imagesLayout;
        TextView dealDateView;
    }

    private PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            page = 0;
            fetchData();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {

        }
    };

    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                if (view.getCount() >= Constant.CountLimit.PAGE_SIZE && view.getLastVisiblePosition() == view.getCount() - 1) {
                    if (end) {
                        if (!warnLast) {
                            toast(R.string.toast_list_end);
                            warnLast = true;
                        }
                    } else {
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

    String filter;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (tabs.contains(new Tab(v.getId()))) {
                for (Tab tab : tabs) {
                    if (v.getId() == tab.container) {
                        ((TextView) findViewById(tab.label)).setTextColor(getResources().getColor(R.color.ui_font_red));
                        ((TextView) findViewById(tab.count)).setTextColor(getResources().getColor(R.color.ui_font_red));
                        filter = tab.filter;
                    } else {
                        ((TextView) findViewById(tab.label)).setTextColor(getResources().getColor(R.color.ui_font_c));
                        ((TextView) findViewById(tab.count)).setTextColor(getResources().getColor(R.color.ui_font_c));
                    }
                }
                page = 0;
                fetchData();
            }
        }
    };

    private class Tab {
        public Tab(int container) {
            this.container = container;
        }

        public Tab(int container, int label, int count, boolean selected, String filter) {
            this.container = container;
            this.label = label;
            this.count = count;
            this.selected = selected;
            this.filter = filter;
        }

        int container;
        int label;
        int count;
        boolean selected;
        String filter;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tab tab = (Tab) o;

            if (container != tab.container) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return container;
        }
    }
}
