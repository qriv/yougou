package com.gsh.app.ugou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;

import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.ShareApp;
import com.gsh.app.client.mall.activity.ActivityBase;
import com.gsh.app.client.mall.activity.AddressEditActivity;
import com.gsh.app.client.mall.activity.LoginActivity;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.gsh.app.ugou.https.model.*;
import com.gsh.app.ugou.widget.MyScrollView;
import com.litesuits.android.log.Log;
import com.litesuits.android.widget.RatingStars;
import com.litesuits.common.utils.FileUtil;
import com.litesuits.common.utils.StringUtils;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Tan Chunmao
 */
public class BasketDetailActivity extends ActivityBase implements MyScrollView.OnScrollListener {
    /**
     * 自定义的MyScrollView
     */
    private MyScrollView myScrollView;
    /**
     * 在MyScrollView里面的购买布局
     */
    private LinearLayout mBuyLayout;
    /**
     * 位于顶部的购买布局
     */
    private LinearLayout mTopBuyLayout;
    private View divider;

    private Combo combo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ugou_activity_basket_detail);
        setTitleBar(false, "套餐详情", RightAction.ICON, R.drawable.action_share);
        showRightAction(false);
        myScrollView = (MyScrollView) findViewById(R.id.content);
        mBuyLayout = (LinearLayout) findViewById(R.id.in_buy);
        mBuyLayout.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
        mTopBuyLayout = (LinearLayout) findViewById(R.id.top_buy_layout);
        divider = mTopBuyLayout.findViewById(R.id.divider);
        myScrollView.setOnScrollListener(this);
        //当布局的状态或者控件的可见性发生改变回调的接口
        findViewById(R.id.parent_layout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                //这一步很重要，使得上面的购买布局和下面的购买布局重合
                onScroll(myScrollView.getScrollY());
            }
        });
        fetchData();
//        getPackages();
        findViewById(R.id.picture).setOnClickListener(onClickListener);
//        findViewById(R.id.back).setOnClickListener(onClickListener);
        findViewById(R.id.comments).setOnClickListener(onClickListener);
        mBuyLayout.findViewById(R.id.buy).setOnClickListener(onClickListener);
        mTopBuyLayout.findViewById(R.id.buy).setOnClickListener(onClickListener);

        findViewById(R.id.to_comment).setOnClickListener(onClickListener);
    }

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        showShareDialog();
    }

    @Override
    protected final void share(ShareApp shareApp) {
        shareContent = new ShareContent();
        String url = "http://" + getString(R.string.app_address) + getString(R.string.share_combo);
        shareContent.url = String.format(url, combo.id);
        String picPath = StringUtils.getPicturePath(combo.mainPicturePath);
        if (!TextUtils.isEmpty(picPath)) {
            shareContent.path = picPath;
        } else {
            shareContent.path = Urls.IMAGE_LOGO;
        }

        Platform.ShareParams sp = new Platform.ShareParams();
        String appName = getString(R.string.app_name);
        sp.setTitle(appName);
        sp.setTitleUrl(shareContent.url);
        String text = getString(R.string.share_test, shareContent.url, combo.name, appName);
        sp.setText(text);
        if (shareApp != ShareApp.wechat)
            sp.setImageUrl(shareContent.path);
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform plat = ShareSDK.getPlatform(shareApp.getPlatformName());
        //plat.setPlatformActionListener(this);
        plat.share(sp);
        super.share(shareApp);

    }

    private void fetchData() {
        showProgressDialog();
        hideContent();
        String basketId = getIntent().getStringExtra(String.class.getName());
        execute(new Request(Urls.COMBO_DETAILS).addUrlParam("comboId", basketId),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult != null && apiResult.isOk()) {
                            hideErrorPage();
                            showRightAction(true);
                            combo = apiResult.getModel(Combo.class);
                            showContent();
                            setImage();
                            setHeader();
                            setGoods();
                            setNotice();
                            fillPackages();
                            myScrollView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myScrollView.fullScroll(ScrollView.FOCUS_UP);
                                }
                            }, 100);
                        } else {
                            onRequestError();
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        dismissProgressDialog();
                        onRequestError();
                    }
                });
    }


    private void setGoods() {
        String string = renderBasketDescription(combo);
        WebView webView = (WebView) findViewById(R.id.web);
        Log.d("test", "html: " + string);
        webView.loadDataWithBaseURL(null, string, "text/html", "utf-8", null);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/20 Safari/537.31");
        /*webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webView里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });*/
    }


    public void onErrorPageClick() {
        fetchData();
    }

    private void setHeader() {
        ((TextView) mTopBuyLayout.findViewById(R.id.money)).setText((int) combo.price + "");
        ((TextView) mBuyLayout.findViewById(R.id.money)).setText((int) combo.price + "");

        ((TextView) mBuyLayout.findViewById(R.id.deliver)).setText(String.format("/月/%s次", combo.deliveryCount));
        ((TextView) mTopBuyLayout.findViewById(R.id.deliver)).setText(String.format("/月/%s次", combo.deliveryCount));


        ((TextView) findViewById(R.id.name)).setText(combo.name);
        loadLongImage((ImageView) findViewById(R.id.image), combo.mainPicturePath);
        ((TextView) findViewById(R.id.desc)).setText(combo.summary);
        ((TextView) findViewById(R.id.sale)).setText(String.format("%d人购买", combo.saleCount));
        if (combo.commentCount > 0) {
            findViewById(R.id.comments).setVisibility(View.VISIBLE);
            findViewById(R.id.no_comment).setVisibility(View.GONE);
            RatingStars.rate(findViewById(R.id.stars), (float) combo.star);
            ((TextView) findViewById(R.id.comment_people)).setText(String.format("%d人评价", combo.commentCount));
            ((TextView) findViewById(R.id.score)).setText(String.format("%.1f分", combo.star));
        } else {
            findViewById(R.id.comments).setVisibility(View.GONE);
            findViewById(R.id.no_comment).setVisibility(View.VISIBLE);
        }
    }

    private void setNotice() {
        TextView noticeView = (TextView) findViewById(R.id.notice);
        String notice = FileUtil.getAssetText(this, "notice.html");
        noticeView.setText(Html.fromHtml(notice));
//        ((TextView) findViewById(R.id.notice)).setText(combo.saleTip);

    }


    private void fillPackages() {
        if (combo.relatedCombos != null && !combo.relatedCombos.isEmpty()) {
            findViewById(R.id.other_packages).setVisibility(View.VISIBLE);
            LinearLayout layout = (LinearLayout) findViewById(R.id.packages);
            for (RelatedCombo com : combo.relatedCombos) {
                fillPackage(layout, com);
            }
        } else {
            findViewById(R.id.other_packages).setVisibility(View.GONE);
        }
    }

    private void fillPackage(LinearLayout layout, RelatedCombo relatedCombo) {
        View container = getLayoutInflater().inflate(R.layout.ugou_item_basket_b, null);
        container.setOnClickListener(onClickListener);
        container.setTag(relatedCombo);
        loadImage((ImageView) container.findViewById(R.id.icon), relatedCombo.mainPicturePath);
//        ((ImageView) container.findViewById(R.id.icon)).setImageResource(combo.localPicture);
        ((TextView) container.findViewById(R.id.name)).setText(relatedCombo.name);
        ((TextView) container.findViewById(R.id.sale_count)).setText(String.format("%d人购买", relatedCombo.saleCount));
        ((TextView) container.findViewById(R.id.money)).setText(String.format("%d", (int) relatedCombo.price));
        ((TextView) container.findViewById(R.id.comment_count)).setText(String.format("%d人评论", relatedCombo.commentCount));
        layout.addView(container);

        if (combo.relatedCombos.indexOf(relatedCombo) != combo.relatedCombos.size() - 1) {
            View gap = new View(this);
            gap.setBackgroundResource(R.color.ui_divider_bg);
            int height = getResources().getDimensionPixelOffset(R.dimen.ui_size_one_dp);
            gap.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            layout.addView(gap);
        }
    }


    private void setImage() {
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = width * 9 / 16;

        findViewById(R.id.image_container).getLayoutParams().height = height;
        findViewById(R.id.image).getLayoutParams().height = height;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.picture == v.getId()) {
                go.name(BasketPictureActivity.class).param(Combo.class.getName(), combo).go();
            } else if (R.id.comments == v.getId()) {
                go.name(BasketCommentListActivity.class).param("comboId", combo.id + "").go();
            } else if (R.id.buy == v.getId()) {
                if (!MallApplication.user.loggedIn()) {
                    go.name(LoginActivity.class).go();
                } else {
                    createOrder();
                }
            } else if (R.id.item_package == v.getId()) {
                RelatedCombo combo = (RelatedCombo) v.getTag();
                String basketId = String.valueOf(combo.comboId);
                go.name(BasketDetailActivity.class).param(String.class.getName(), basketId).go();
            }
        }
    };

    private void createOrder() {
        try {
            if (MallApplication.test) {
                combo.sellTime = new SimpleDateFormat("yyyyMMdd").parse("20150612").getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (combo.isSellBegin()) {
            go.name(BasketOrderConfirmActivity.class).param(Combo.class.getName(), combo).go();
        } else {
            String date = new SimpleDateFormat("MM月dd日").format(combo.sellTime);
            String title = String.format("将在%s正式上线，现在可以预订！", date);
            notice(new CallBack() {
                @Override
                public void call() {
                    go.name(BasketOrderConfirmActivity.class).param(Combo.class.getName(), combo).go();
                }
            }, null, title, null, false, "预订", "取消");
        }
    }

    private static final int REQUEST_ADDRESS_ADD = 2041;

    private void setAddress() {
        notice(
                new CallBack() {
                    @Override
                    public void call() {
                        go.name(AddressEditActivity.class).param(String.class.getName(), "add").goForResult(REQUEST_ADDRESS_ADD);
                    }
                },
                new CallBack() {
                    @Override
                    public void call() {
                        finish();
                    }
                },
                "请先添加收货地址", "", false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADDRESS_ADD) {
                createOrder();
            }
        }
    }

    @Override
    public void onScroll(int scrollY) {
        int mBuyLayout2ParentTop = Math.max(scrollY, mBuyLayout.getTop());
        mTopBuyLayout.layout(0, mBuyLayout2ParentTop, mTopBuyLayout.getWidth(), mBuyLayout2ParentTop + mTopBuyLayout.getHeight());
        if (scrollY > mBuyLayout.getTop()) {
            divider.setVisibility(View.VISIBLE);
        } else {
            divider.setVisibility(View.INVISIBLE);
        }
    }

    public static String renderBasketDescription(Combo combo) {

        if (combo == null || combo.baskets == null)
            return "";

        StringBuilder html = new StringBuilder();


        html.append(renderPrefixHtml());

        for (Basket basket : combo.baskets) {

            if (combo.baskets.size() > 1)
                html.append("<p>第" + basket.axisSerial + "次配送</p>");

            html.append("<table>" +
                    "<tr>" +
                    "<td style=\"width:25px\">分类</td>" +
                    "<td style=\"width:58px\">名称</td>" +
                    "<td style=\"width:30px\">数量</td>" +
                    "<td>备注</td>" +
                    "</tr>");

            for (BasketGoodsGroup basketGoodsGroup : basket.basketGoodsGroups) {

                for (int i = 0; i < basketGoodsGroup.basketGoodsDTOs.size(); i++) {
                    BasketGoods basketGoods = basketGoodsGroup.basketGoodsDTOs.get(i);
                    if (i == 0) {
                        html.append("<tr>" +
                                "<td rowspan=" + basketGoodsGroup.basketGoodsDTOs.size() + ">" + basketGoodsGroup.categoryName + "</td>" +
                                " <td>" + basketGoods.name + "</td>" +
                                " <td>" + basketGoods.quantity + "</td>" +
                                " <td>" + basketGoods.remark + "</td>" +
                                " </tr>");
                    } else {
                        html.append("<tr>" +
                                " <td>" + basketGoods.name + "</td>" +
                                " <td>" + basketGoods.quantity + "</td>" +
                                " <td>" + basketGoods.remark + "</td>" +
                                " </tr>");
                    }
                }
            }

            html.append("</table>");
        }

        html.append(renderSuffixHtml());

//        Log.i(html);

        return html.toString();

    }

    private static String renderPrefixHtml() {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head lang=zh>" +
                "<meta charset=UTF-8>" +
                renderCss() +
                "</head>" +
                "<body>";
    }

    private static String renderCss() {
        return "<style type=text/css>" +
                "table,th,td{" +
                "border-collapse: collapse;" +
                "border-style: solid;" +
                "border-color: #eeeeee;" +
                "text-align: left;" +
                "border-top-width: 1px;" +
                "border-bottom-width: 1px;" +
                "border-left-width: 1px;" +
                "border-right-width: 1px;" +
                "font-size:12px;padding:10px}" +
                "table {" +
                "width: 100%;" +
                "}" +
                "p {" +
                "font-size:14px;" +
                "}" +
                "</style>";
    }

    private static String renderSuffixHtml() {
        return "</body>" +
                "</html>";
    }
}
