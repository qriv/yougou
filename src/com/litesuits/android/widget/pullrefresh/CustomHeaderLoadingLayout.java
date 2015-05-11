package com.litesuits.android.widget.pullrefresh;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import com.gsh.app.client.mall.R;

/**
 * 这个类封装了下拉刷新的布局
 *
 * @author Li Hong
 * @since 2013-7-30
 */
public class CustomHeaderLoadingLayout extends LoadingLayout {
    private LinearLayout mHeaderContainer;

    private View circleOne;
    private View circleTwo;
    private View circleThree;
    private Animation animationOne;
    private Animation animationTwo;
    private Animation animationThree;

    public CustomHeaderLoadingLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public CustomHeaderLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context context
     */
    private void init(Context context) {
        mHeaderContainer = (LinearLayout) findViewById(R.id.still);
        circleOne = findViewById(R.id.circle_one);
        circleTwo = findViewById(R.id.circle_two);
        circleThree = findViewById(R.id.circle_three);

        animationOne = AnimationUtils.loadAnimation(context, R.anim.pull_to_refresh_dot);
        animationTwo = AnimationUtils.loadAnimation(context, R.anim.pull_to_refresh_dot);
        animationThree = AnimationUtils.loadAnimation(context, R.anim.pull_to_refresh_dot);
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {

    }

    @Override
    public int getContentSize() {
        if (null != mHeaderContainer) {
            //return mHeaderContainer.getHeight();
            mHeaderContainer.measure(0, 0);
            return mHeaderContainer.getMeasuredHeight();
        }

        return (int) (getResources().getDisplayMetrics().density * 60);
    }

    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_custom, null);
        return container;
    }

    @Override
    protected void onStateChanged(State curState, State oldState) {


        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        clearAnimations();
        showStill();
    }

    private void clearAnimations() {
        circleOne.clearAnimation();
        circleTwo.clearAnimation();
        circleThree.clearAnimation();
    }

    private void startAnimations() {
        circleOne.startAnimation(animationOne);
        circleThree.startAnimation(animationThree);
        circleTwo.setVisibility(INVISIBLE);
        circleOne.postDelayed(new Runnable() {
            @Override
            public void run() {
                circleTwo.startAnimation(animationTwo);
            }
        }, 500);
    }

    private void showStill() {
        circleOne.setVisibility(VISIBLE);
        circleTwo.setVisibility(VISIBLE);
        circleThree.setVisibility(VISIBLE);
    }

    @Override
    protected void onPullToRefresh() {
        clearAnimations();
        showStill();
    }

    @Override
    protected void onReleaseToRefresh() {
        clearAnimations();
        showStill();
    }

    @Override
    protected void onRefreshing() {
        clearAnimation();
        startAnimations();
    }
}
