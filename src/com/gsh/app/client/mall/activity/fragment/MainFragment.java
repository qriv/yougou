package com.gsh.app.client.mall.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.*;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M11;
import com.gsh.app.client.mall.https.model.M29;
import com.gsh.app.client.mall.https.model.M30;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.widget.ViewUtils;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;
import com.zxing.activity.CaptureActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class MainFragment extends FragmentBase {
    private ImageView[] dots;
    private cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager imagePager;
    private ImagePagerAdapter imagePagerAdapter;
    private CountDownTimer countDownTimer;
    private View slide_image_layout;
    private M29 m29;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
            return layout;
        }
        layout = inflater.inflate(R.layout.fragment_home, container, false);

        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int height = width * 5 / 16;

        slide_image_layout = layout.findViewById(R.id.slide_image_layout);
        slide_image_layout.getLayoutParams().width = width;
        slide_image_layout.getLayoutParams().height = height;

        String hostName = MallApplication.user.getStationName() + "";
        setTitleBar(layout, true, hostName, ActivityBase.RightAction.ICON, R.drawable.ui_search);
        fetchData();
        return layout;
    }

    @Override
    public void refresh() {

    }

    private void fetchData() {
        activity.showProgressDialog();
        hideContent();
        activity.execute(new Request(Urls.HOME),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        activity.dismissProgressDialog();
                        if (apiResult.isOk()) {
                            m29 = apiResult.getModel(M29.class);
                            setContent();
                        } else {
                            activity.toast(apiResult.message);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        activity.dismissProgressDialog();
                    }
                });
    }

    private void setContent() {
        showContent();
        initNotification();
        initSlideImages();
        initGrid();
        initSale();
        initBestSeller();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.text_title)).setText(MallApplication.user.getStationName());
        if (imagePager != null)
            imagePager.startAutoScroll();
        if (deadline != 0)
            countDown();
    }

    private void initNotification() {
        final View notification = findViewById(R.id.notification);
        ((TextView) notification).setText(m29.message);
        findViewById(R.id.click_notification).setOnClickListener(onClickListener);
    }

    private void countDown() {
        View soldOut = findViewById(R.id.sold_out);
        View clock = findViewById(R.id.clock);
        long time = deadline - System.currentTimeMillis();
        if (time > 0) {
            countDownTimer = new CountDownTimer(time, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateTimer(millisUntilFinished);
                }

                @Override
                public void onFinish() {

                }
            };
            clock.setVisibility(View.VISIBLE);
            soldOut.setVisibility(View.INVISIBLE);
            countDownTimer.start();
        } else {
            clock.setVisibility(View.INVISIBLE);
            soldOut.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (imagePager != null)
            imagePager.stopAutoScroll();
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    //region best seller
    private void initBestSeller() {
        LinearLayout bestSeller = (LinearLayout) findViewById(R.id.best_seller);
        bestSeller.removeAllViews();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int gapWidth = activity.getResources().getDimensionPixelOffset(R.dimen.ui_margin_a);
        int imageSize = (metrics.widthPixels - 2 * gapWidth) / 3;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
        List<View> views = new ArrayList<View>();
        for (M30 m30 : m29.hotGoods) {
            ImageView imageView = (ImageView) activity.getLayoutInflater().inflate(R.layout.image, null);
            imageView.setLayoutParams(params);
            imageView.setTag(m30);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setOnClickListener(onClickListener);
            activity.loadImage(imageView, m30.picturePath);
            views.add(imageView);
        }
        ViewUtils.addViews(activity, bestSeller, views, params.width, Gravity.LEFT, gapWidth, 0);
    }

    //    endregion best seller
    //region sale
    private long deadline;

    private void initSale() {
        LinearLayout bestSeller = (LinearLayout) findViewById(R.id.off);
        bestSeller.removeAllViews();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int gapWidth = activity.getResources().getDimensionPixelOffset(R.dimen.ui_margin_a);
        int imageSize = (metrics.widthPixels - 2 * gapWidth) / 3;

        ImageView panicView = (ImageView) findViewById(R.id.panic);
        panicView.getLayoutParams().width = imageSize;
        panicView.getLayoutParams().height = imageSize;

        if (m29.limitedGoods != null) {
            activity.loadImage(panicView, m29.limitedGoods.picturePath);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSize, imageSize);
        List<View> views = new ArrayList<View>();

        for (M30 m30 : m29.advanceBookingGoods) {
            ImageView imageView = (ImageView) activity.getLayoutInflater().inflate(R.layout.image, null);
            imageView.setLayoutParams(params);
            imageView.setTag(m30);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setOnClickListener(onClickListener);
            activity.loadImage(imageView, m30.picturePath);
            views.add(imageView);
        }
        ViewUtils.addViews(activity, bestSeller, views, params.width, Gravity.LEFT, gapWidth, 0);
        deadline = m29.endOn;
        countDown();
    }


    private void updateTimer(long millisUntilFinished) {
        long interval = millisUntilFinished / 1000;
        int hour = (int) (interval / 3600);
        int minute = (int) (interval % 3600 / 60);
        int second = (int) (interval % 60);
        ((TextView) findViewById(R.id.hour)).setText(String.format("%02d", hour));
        ((TextView) findViewById(R.id.minute)).setText(String.format("%02d", minute));
        ((TextView) findViewById(R.id.second)).setText(String.format("%02d", second));
    }

    //    endregion sale
    //region grid
    private void initGrid() {
        entry(findViewById(R.id.scan), Color.parseColor("#9a1cac"), R.drawable.scan, "扫一扫");
        entry(findViewById(R.id.sale), Color.parseColor("#0aa3e9"), R.drawable.sale, "限时特惠");
        entry(findViewById(R.id.reservation), Color.parseColor("#26d00b"), R.drawable.reservation_grid, "预售");
        entry(findViewById(R.id.recharge), Color.parseColor("#fc7001"), R.drawable.recharge, "充值");

    }

    private void entry(View container, int color, int icon, String label) {
        container.setOnClickListener(onClickListener);
        int roundRadius = getResources().getDimensionPixelOffset(R.dimen.ui_size_icon_d);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(roundRadius);
        container.findViewById(R.id.icon_container).setBackgroundDrawable(gd);
        ((ImageView) container.findViewById(R.id.icon)).setImageResource(icon);
        ((TextView) container.findViewById(R.id.label)).setText(label);
    }

    //    endregion grid
    //region slide images
    private void initSlideImages() {
        if (m29.barGoods != null && !m29.barGoods.isEmpty()) {
            imagePager = (cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager) findViewById(R.id.pager);
            imagePager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
            imagePager.setOnPageChangeListener(pageChangeListener);
            imagePager.setInterval(3000);
            imagePagerAdapter = new ImagePagerAdapter();
            imagePager.setAdapter(imagePagerAdapter);
            initDots();
        }
    }

    private void initDots() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.Layout_Retailer_Dots);
        layout.removeAllViews();
        dots = new ImageView[m29.barGoods.size()];
        // 循环取得小点图片
        int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int dotSize = 0;
        if (screenWidth <= 540) {
            dotSize = 10;
        } else if (screenWidth >= 1080) {
            dotSize = 20;
        } else {
            dotSize = 15;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dotSize, dotSize);
        for (int i = 0; i < m29.barGoods.size(); i++) {
            dots[i] = new ImageView(activity);
            dots[i].setLayoutParams(layoutParams);
            dots[i].setBackgroundResource(R.drawable.circle_index);
            if (i == 0) {
                dots[i].setSelected(true);
            } else {
                dots[i].setSelected(false);
            }
            layout.addView(dots[i]);
            if (i < m29.barGoods.size() - 1) {
                View gapView = new View(activity);
                gapView.setLayoutParams(layoutParams);
                layout.addView(gapView);
            }
        }
        imagePager.postDelayed(new Runnable() {
            @Override
            public void run() {
                imagePager.startAutoScroll();
            }
        }, 100);
    }

    private void selectPage(int page) {
        for (int i = 0; i < m29.barGoods.size(); i++) {
            if (i == page % m29.barGoods.size()) {
                dots[i].setSelected(true);
            } else {
                dots[i].setSelected(false);
            }
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {


        private ImagePagerAdapter() {
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
        public Object instantiateItem(View view, int position) {
            View imageLayout = activity.getLayoutInflater().inflate(R.layout.item_goods_image, (ViewGroup) view, false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            final M30 m30 = m29.barGoods.get(position % m29.barGoods.size());
            final String path = m30.picturePath;
            imageView.setOnClickListener(onClickListener);
            imageView.setTag(m30);
            activity.loadLongImage(imageView, path);
            ((ViewGroup) view).addView(imageLayout, 0);
            return imageLayout;
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
//    endregion slide images


    protected void onRightActionPressed() {
        final Intent intent = new Intent(activity, SearchActivity.class);
        startActivity(intent);
    }


    private static final int REQUEST_QR_CODE = 2038;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.scan == v.getId()) {
                startActivityForResult(new Intent(activity, CaptureActivity.class), REQUEST_QR_CODE);
            } else if (R.id.sale == v.getId()) {
                Intent intent = new Intent(activity, PanicListActivity.class);
                startActivity(intent);
            } else if (R.id.reservation == v.getId()) {
                Intent intent = new Intent(activity, ReservationListActivity.class);
                startActivity(intent);
            } else if (R.id.image == v.getId()) {
                Intent intent = new Intent(activity, GoodsDetailActivity.class);
                intent.putExtra(String.class.getName(), "id");
                String id = ((M30) v.getTag()).goodsId + "";
                intent.putExtra("id", id);
                startActivity(intent);
            } else if (R.id.click_notification == v.getId()) {
                startActivity(new Intent(activity, NotificationActivity.class).putExtra(String.class.getName(), m29.messageId));
            } else if (R.id.recharge == v.getId()) {
                if (MallApplication.user.loggedIn()) {
                    startActivity(new Intent(activity, RechargeActivity.class));
                } else {
                    startActivity(new Intent(activity, LoginActivity.class).setAction("recharge"));
                }

            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_QR_CODE) {
            final String code = data.getExtras().getString("result");
            Intent intent = new Intent(activity, GoodsDetailActivity.class);
            intent.putExtra(String.class.getName(), "qr_code");
            intent.putExtra("qr_code", code);
            startActivity(intent);
        }
    }
}
