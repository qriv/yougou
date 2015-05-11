package com.gsh.app.client.mall.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.ShareApp;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M31;
import com.gsh.app.client.mall.https.model.M32;
import com.gsh.app.client.mall.https.model.M4;
import com.gsh.app.client.mall.https.model.M5;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.log.Log;
import com.litesuits.android.widget.RatingStars;
import com.litesuits.android.widget.ViewUtils;
import com.litesuits.android.widget.vertical.VerticalPagerAdapter;
import com.litesuits.android.widget.vertical.VerticalViewPager;
import com.litesuits.common.utils.StringUtils;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Tan Chunmao
 */
public class GoodsDetailActivity extends ActivityBase {
    private static final int REQUEST_LOGIN = 2038;
    private List<View> verticalViews1;
    private VerticalAdapter vAdapter1;
    private VerticalViewPager verticalPager;
    private View detailView;
    private View expandView;

    private cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager imagePager;
    private ImageView[] dots;
    private ImagePagerAdapter imagePagerAdapter;

    private String type;//二维码，id
    private Request request;
    private M4 m4;
    private Boolean collected;
    private Integer shoppingCarCount;
    Map<String, String> htmlMap;
    private LinearLayout.LayoutParams imageParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wait = -1;
        htmlMap = new HashMap<String, String>();
        htmlMap.put(Urls.GOODS_PICTURE, "");
        htmlMap.put(Urls.GOODS_PARAMETER, "");
        htmlMap.put(Urls.GOODS_SERVICE, "");
        type = getIntent().getStringExtra(String.class.getName());
        if (type.equals("qr_code")) {
            String id = getIntent().getStringExtra("qr_code");
            if (MallApplication.test)
                id = "14c2588f7be";
            request = new Request(Urls.GOODS_QR_CODE).addUrlParam("barcode", id);
        } else if (type.equals("id")) {
            String id = getIntent().getStringExtra("id");
            request = new Request(Urls.GOODS_ITEM).addUrlParam("id", id);
        }
        setContentView(R.layout.activity_good);
        setTitleBar(false, R.string.title_goods_detail, RightAction.ICON, R.drawable.share);
        findViewById(R.id.content).setVisibility(View.INVISIBLE);
        fetchData();
    }

    private void initContent() {
//        createShareDialog();
        verticalPager = (VerticalViewPager) findViewById(R.id.basePager);
        verticalPager.setSpeed(500);
        verticalViews1 = new ArrayList<View>();
        detailView = getLayoutInflater().inflate(R.layout.fragment_good_detail, null);

        verticalViews1.add(detailView);

        expandView = getLayoutInflater().inflate(R.layout.fragment_good_detail_expand, null);

        verticalViews1.add(expandView);
        vAdapter1 = new VerticalAdapter(verticalViews1);
        verticalPager.setOnPageChangeListener(onPageChangeListener);
        verticalPager.setAdapter(vAdapter1);

        findViewById(R.id.collect).setOnClickListener(onClickListener);
        findViewById(R.id.add).setOnClickListener(onClickListener);
        findViewById(R.id.shopping_car).setOnClickListener(onClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (m4 != null && MallApplication.user.loggedIn()) {
            checkShoppingCar();
            checkIfCollected();
        }
        if (imagePager != null)
            imagePager.startAutoScroll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (imagePager != null && m4.picturePaths.size() > 0)
            imagePager.stopAutoScroll();
    }

    @Override
    public void onBackPressed() {
        if (verticalPager.getCurrentItem() == 1) {
            verticalPager.setCurrentItem(0, true);
        } else {
            super.onBackPressed();
        }
    }

    private void fetchData() {
        showProgressDialog();
        execute(request,
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult.isOk()) {
                            m4 = apiResult.getModel(M4.class);
                            setContent();
                            if (MallApplication.user.loggedIn()) {
                                checkShoppingCar();
                                checkIfCollected();
                            }
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

    private void checkIfCollected() {
        execute(new Request(Urls.GOODS_IS_COLLECT).addUrlParam("goodsId", m4.id + ""),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult != null && apiResult.isOk()) {
                            collected = apiResult.getModel(M31.class).isMyfavorite;
//                            collected = apiResult.getModel(Boolean.class);
                            updateCollectIcon();
                            if (shoppingCarCount != null) {
                                handleWait();
                            }
                        }
                    }
                }
        );
    }


    private void setContent() {
        findViewById(R.id.content).setVisibility(View.VISIBLE);
        initContent();
        setDetail();
        setExpand();
    }


    private void checkShoppingCar() {
        execute(new Request(Urls.SHOPPING_CAR_COUNT),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult != null && apiResult.isOk()) {
                            M32 m32 = apiResult.getModel(M32.class);
                            shoppingCarCount = m32.count;
                            updateShoppingCar();
                            if (collected != null) {
                                handleWait();
                            }
                        }
                    }
                }
        );
    }

    private void setDetail() {
        measureScreen();
        initDots();
        initImagePager();
        final TextView nameView = (TextView) detailView.findViewById(R.id.name);
        if (MallApplication.test)
            m4.name = m4.name + "_" + m4.id;
        nameView.setText(m4.name);
        nameView.setOnClickListener(onClickListener);
        ((TextView) detailView.findViewById(R.id.real_price)).setText(String.format("￥%.2f", m4.price));
        final TextView falsePriceView = (TextView) detailView.findViewById(R.id.false_price);
        falsePriceView.setText(String.format("￥%.2f", m4.originPrice));
        falsePriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        ((TextView) detailView.findViewById(R.id.people_count)).setText(String.format("共%d人购买", m4.peopleCount));
        detailView.findViewById(R.id.on_sale).setVisibility(m4.isOnSale ? View.VISIBLE : View.GONE);
        dismissView(detailView.findViewById(R.id.delivery_quick), !m4.isQuickMode);
        dismissView(detailView.findViewById(R.id.delivery_arrive), !m4.isSupportArrivalPay);

        ((TextView) detailView.findViewById(R.id.comment_people)).setText(String.format("用户评价（%d人评价）", m4.commentCount));
        ((TextView) detailView.findViewById(R.id.rate)).setText(NumberFormat.getPercentInstance().format(m4.rate));
        detailView.findViewById(R.id.drawer).setOnClickListener(onClickListener);
        detailView.findViewById(R.id.comments_expand).setOnClickListener(onClickListener);
        List<M5> comments = m4.commentsSummary;
        if (comments.size() > 5) {
            comments = m4.commentsSummary.subList(0, 5);
        }
        if (comments.size() > 0) {
            fillComments(comments);
        }
    }

    private void fillComments(List<M5> comments) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        int imageSize = initImageSize();
        imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        ViewGroup.LayoutParams verticalGapLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.ui_size_one_dp));

        LinearLayout linearLayout = (LinearLayout) detailView.findViewById(R.id.comments);
        for (M5 m5 : comments) {
            View convertView = getLayoutInflater().inflate(R.layout.item_goods_comment, null);
            ((TextView) convertView.findViewById(R.id.name)).setText(m5.nickname);
            ((TextView) convertView.findViewById(R.id.date)).setText(sdf.format(m5.commentOn));
            ((TextView) convertView.findViewById(R.id.info)).setText(m5.comment);
            loadAvatar((ImageView) convertView.findViewById(R.id.avatar), m5.avatarPath);
            if (TextUtils.isEmpty(m5.level))
                m5.level = "注册普卡";
            ((TextView) convertView.findViewById(R.id.level)).setText(m5.level);
            ((TextView) convertView.findViewById(R.id.deal_date)).setText(sdf.format(m5.bargainOn));
            LinearLayout imagesLayout = (LinearLayout) convertView.findViewById(R.id.images);
            if (m5.picturePaths != null && m5.picturePaths.size() > 0) {
                fillImages(imagesLayout, m5.picturePaths);
            }
            RatingStars.rate(convertView.findViewById(R.id.stars), m5.score);
            View gap = new View(GoodsDetailActivity.this);
            gap.setLayoutParams(verticalGapLayoutParams);
            gap.setBackgroundResource(R.color.ui_divider_bg);
            linearLayout.addView(gap);
            linearLayout.addView(convertView);
        }
    }

    private int initImageSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int maxWidth = metrics.widthPixels;
        int edgePadding = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
        int gapWidth = getResources().getDimensionPixelOffset(R.dimen.ui_size_one_dp);
        int columns = Constant.CountLimit.SHARE_PICTURE;
        return (int) (((float) maxWidth - gapWidth * (columns - 1) - edgePadding * 2) / columns);
    }

    private void fillImages(LinearLayout layout, List<String> paths) {
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
        ViewUtils.addViews(GoodsDetailActivity.this, layout, views, imageParams.width, Gravity.LEFT);
    }

    private void initImagePager() {
        imagePager = (cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager) detailView.findViewById(R.id.pager);
        imagePager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        imagePager.setOnPageChangeListener(pageChangeListener);

        if (m4.picturePaths.size() > 0) {
            imagePagerAdapter = new ImagePagerAdapter();
            imagePager.setAdapter(imagePagerAdapter);
            imagePager.startAutoScroll();
        }
    }

    private void measureScreen() {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(metrics.widthPixels, metrics.widthPixels);
        detailView.findViewById(R.id.images).setLayoutParams(params);
    }


    private void initDots() {
        LinearLayout layout = (LinearLayout) detailView.findViewById(R.id.Layout_Retailer_Dots);
        layout.removeAllViews();
        dots = new ImageView[m4.picturePaths.size()];
        // 循环取得小点图片
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
        for (int i = 0; i < m4.picturePaths.size(); i++) {
            dots[i] = new ImageView(GoodsDetailActivity.this);
            dots[i].setLayoutParams(layoutParams);
            dots[i].setBackgroundResource(R.drawable.circle_index);
            if (i == 0) {
                dots[i].setSelected(true);
            } else {
                dots[i].setSelected(false);
            }
            layout.addView(dots[i]);
            if (i < m4.picturePaths.size() - 1) {
                View gapView = new View(GoodsDetailActivity.this);
                gapView.setLayoutParams(layoutParams);
                layout.addView(gapView);
            }
        }
    }

    private WebView webView;

    private void setExpand() {
        webView = (WebView) expandView.findViewById(R.id.web);
        expandView.findViewById(R.id.picture).setOnClickListener(onClickListener);
        expandView.findViewById(R.id.parameter).setOnClickListener(onClickListener);
        expandView.findViewById(R.id.service).setOnClickListener(onClickListener);
    }

    private class VerticalAdapter extends VerticalPagerAdapter {
        List<View> mListViews;

        public VerticalAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((VerticalViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((VerticalViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.name == v.getId() || R.id.drawer == v.getId()) {
                verticalPager.setCurrentItem(1, true);
            } else if (R.id.picture == v.getId() || R.id.parameter == v.getId() || R.id.service == v.getId()) {
                changeTab(v.getId());
            } else if (R.id.comments_expand == v.getId()) {
                go.name(CommentListActivity.class).param(String.class.getName(), String.valueOf(m4.id)).go();
            } else if (R.id.collect == v.getId()) {
                if (checkLoginStatus("收藏", WAIT_COLLECT))
                    collect();
            } else if (R.id.add == v.getId()) {
                if (checkLoginStatus("购物车", WAIT_ADD_TO_SHOPPING_CAR)) {
                    addToShoppingCar();
                }
            } else if (R.id.shopping_car == v.getId()) {
                if (checkLoginStatus("购物车", WAIT_SHOPPING_CAR))
                    shoppingCar();
            }
        }
    };

    private int wait;
    private final static int WAIT_COLLECT = 0;
    private final static int WAIT_SHOPPING_CAR = 1;
    private final static int WAIT_ADD_TO_SHOPPING_CAR = 2;

    private boolean checkLoginStatus(String function, int wait) {
        boolean loggedIn = MallApplication.user.loggedIn();
        if (!loggedIn) {
            toast(String.format("使用%s功能需要先进行登录", function));
            this.wait = wait;
            go.name(LoginActivity.class).goForResult(REQUEST_LOGIN);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOGIN) {
            }
        }
    }

    private void handleWait() {
        if (wait == WAIT_COLLECT) {
            collect();
        } else if (wait == WAIT_SHOPPING_CAR) {
            shoppingCar();
        } else if (wait == WAIT_ADD_TO_SHOPPING_CAR) {
            addToShoppingCar();
        }
        wait = -1;
    }

    private void addToShoppingCar() {
        if (m4 != null) {
            showProgressDialog();
            execute(new Request(Urls.MEMBER_CART_GOODS_ADD).addUrlParam("goodsId", m4.id + ""),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if (apiResult != null && apiResult.isOk()) {
                                toast("成功添加商品到购物车");
                                if (shoppingCarCount == null) {
                                    shoppingCarCount = 0;
                                }
                                shoppingCarCount++;
                                updateShoppingCar();
                            }
                        }

                        @Override
                        protected void onFailure(Response res, HttpException e) {
                            super.onFailure(res, e);
                            dismissProgressDialog();
                        }
                    });
        }
    }


    private void updateShoppingCar() {
        TextView shop = (TextView) findViewById(R.id.shop);
        if (shoppingCarCount > 0) {
            shop.setVisibility(View.VISIBLE);
            shop.setText(shoppingCarCount + "");
        } else {
            shop.setVisibility(View.GONE);
        }
    }

    private void collect() {
        if (collected != null) {
            String url = Urls.MEMBER_FAVORITE_ADD;
            if (collected)
                url = Urls.MEMBER_FAVORITE_DELETE;
            showProgressDialog();
            execute(new Request(url).addUrlParam("favoriteGoodsId", m4.id + ""),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if (apiResult.isOk()) {
                                collected = !collected;
                                if (collected)
                                    toast("收藏成功");
                                else
                                    toast("取消收藏成功");
                                updateCollectIcon();
                            }
                        }

                        @Override
                        protected void onFailure(Response res, HttpException e) {
                            super.onFailure(res, e);
                            dismissProgressDialog();
                            toast(e.toString());
                        }
                    }
            );
        }
    }

    private void updateCollectIcon() {
        findViewById(R.id.collect_icon).setBackgroundResource(collected ? R.drawable.collected : R.drawable.collect);
    }

    private void shoppingCar() {
        startActivity(new Intent(this, ShoppingCarActivity.class));
    }

    private void changeTab(int id) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(R.id.picture, R.id.picture_indicator);
        map.put(R.id.parameter, R.id.parameter_indicator);
        map.put(R.id.service, R.id.service_indicator);
        int[] indicators = {R.id.picture, R.id.parameter, R.id.service};
        for (int indicator : indicators) {
            if (id == indicator)
                expandView.findViewById(map.get(indicator)).setVisibility(View.VISIBLE);
            else
                expandView.findViewById(map.get(indicator)).setVisibility(View.INVISIBLE);
        }

        String url = Urls.GOODS_PICTURE;
        if (R.id.parameter == id)
            url = Urls.GOODS_PARAMETER;
        else if (R.id.service == id)
            url = Urls.GOODS_SERVICE;
        if (TextUtils.isEmpty(htmlMap.get(url))) {
            fetchHtml(url);
        } else {
            webView.loadDataWithBaseURL(null, htmlMap.get(url), "text/html", "utf-8", null);
        }
    }

    final ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            selectPage(arg0);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void selectPage(int page) {
        for (int i = 0; i < m4.picturePaths.size(); i++) {
            if (i == page % m4.picturePaths.size()) {
                dots[i].setSelected(true);
            } else {
                dots[i].setSelected(false);
            }
        }
    }

    @Override
    protected void onRightActionPressed() {
        showShareDialog();
    }

    private List<View> images = new ArrayList<View>();

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        private ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewGroup) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View view) {

        }

        @Override
        public int getCount() {
            return 3000;
        }

        @Override
        public void startUpdate(View view) {

        }

        @Override
        public View instantiateItem(View view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_goods_image, (ViewGroup) view, false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            setGalleryTag(imageView, m4.picturePaths, position);
            loadImage(imageView, m4.picturePaths.get(position % m4.picturePaths.size()));
            ((ViewGroup) view).addView(imageLayout, 0);
            images.add(imageLayout);
            return imageLayout;

          /*  View view1 = images.get(position);
            if (view1 != null) {
                return view1;
            } else {
                View imageLayout = inflater.inflate(R.layout.item_goods_image, (ViewGroup) view, false);
                assert imageLayout != null;
                ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
                setGalleryTag(imageView, m4.picturePaths, position);
                loadImage(imageView, m4.picturePaths.get(position % m4.picturePaths.size()));
                ((ViewGroup) view).addView(imageLayout, 0);
                images.add(imageLayout);
                return imageLayout;
            }*/
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    protected final void share(ShareApp shareApp) {
        shareContent = new ShareContent();
        shareContent.url = TextUtils.isEmpty(m4.shareUrl) ? getString(R.string.portal_address) : m4.shareUrl;
        shareContent.path = TextUtils.isEmpty(m4.picturePath) ? Urls.IMAGE_LOGO : m4.picturePath;

        Platform.ShareParams sp = new Platform.ShareParams();
        String appName = getString(R.string.app_name);
        sp.setTitle(appName);
        sp.setTitleUrl(shareContent.url);
        String text = getString(R.string.share_goods, m4.name, appName, shareContent.url);
        sp.setText(text);
        if (shareApp != ShareApp.wechat)
            sp.setImageUrl(shareContent.path);
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform plat = ShareSDK.getPlatform(shareApp.getPlatformName());
        //plat.setPlatformActionListener(this);
        plat.share(sp);
        super.share(shareApp);
    }

    private VerticalViewPager.OnPageChangeListener onPageChangeListener = new VerticalViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 1 && TextUtils.isEmpty(htmlMap.get(Urls.GOODS_PICTURE)))
                fetchHtml(Urls.GOODS_PICTURE);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void fetchHtml(final String url) {
        execute(new Request(url).addUrlParam("goodsId", String.valueOf(m4.id)),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult.isOk()) {
                            String html = apiResult.getModel(String.class);
                            htmlMap.put(url, html);
                            webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

                        }
                    }
                });
    }
}
