package com.gsh.app.client.mall.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.ActivityBase;
import com.gsh.app.client.mall.activity.ActivityBase.RightAction;
import com.litesuits.common.utils.DensityUtil;

/**
 * Created by taosj on 15/2/2.
 */
public abstract class FragmentBase extends Fragment {


    private RightAction rightAction;
    protected ActivityBase activity;
    protected View layout;

    public abstract void refresh();

    private boolean contentLoaded;

    protected void hideContent() {
        contentLoaded = false;
        findViewById(R.id.content).setVisibility(View.INVISIBLE);
    }


    public void onRequestError(){
        showErrorPage("数据获取失败，点击重新获取");
    }

    private void showPage(String tag, String message) {
        ViewGroup rootView = getRootView();

        if (rootView.findViewWithTag(tag) != null)
            return;

        Context context = activity;
        RelativeLayout view = new RelativeLayout(context);
        view.setLayoutParams(rootView.getLayoutParams());
        view.setBackgroundResource(android.R.color.transparent);

        int titleHeight = DensityUtil.dip2px(context, 48);
        FrameLayout fl = new FrameLayout(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(rootView.getWidth(), rootView.getHeight() - titleHeight);
        lp.setMargins(0, titleHeight, 0, 0);
        fl.setLayoutParams(lp);
        fl.setBackgroundResource(R.color.ui_bg_white);

        ImageView iv = new ImageView(context);
        int width = DensityUtil.dip2px(context, 80);
        int height = DensityUtil.dip2px(context, 80);
        FrameLayout.LayoutParams ivlp = new FrameLayout.LayoutParams(width, height);

        int parentHeight = rootView.getHeight() - titleHeight;
        int parentWidth = rootView.getWidth();

        int x = parentWidth / 2 - width / 2;
        int y = parentHeight / 2 - height;
        ivlp.setMargins(x, y, 0, 0);
        iv.setLayoutParams(ivlp);
        iv.setImageResource(R.drawable.kong);
        fl.addView(iv);

        view.addView(fl);

        TextView tv = new TextView(context);
        FrameLayout.LayoutParams tvlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvlp.setMargins(0, y + height + 40, 0, 0);
        tv.setLayoutParams(tvlp);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getResources().getColor(R.color.ui_font_d));
        tv.setText(message);
        fl.addView(tv);
        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onErrorPageClick();
            }
        });
        view.setTag(tag);
        rootView.addView(view);
    }

    protected void showContent() {
        if (contentLoaded)
            return;
        contentLoaded = true;
        findViewById(R.id.content).setVisibility(View.VISIBLE);
    }

    protected void setTitleBar(View layout, String title) {
        setTitleBar(layout, false, title, RightAction.NONE, -1);
    }

    protected void setTitleBar(View layout, int titleId) {
        setTitleBar(layout, false, titleId, RightAction.NONE, -1);
    }

    protected void setTitleBar(View layout, boolean hideBack, int title, RightAction rightAction, int actionRes) {
        setTitleBar(layout, hideBack, getString(title), rightAction, actionRes);
    }

    protected void setTitleBar(View layout, boolean hideBack, int title, RightAction rightAction, String actionText) {
        setTitleBar(layout, hideBack, getString(title), rightAction, actionText);
    }

    protected void setTitleBar(View layout, boolean hideBack, String title, RightAction rightAction, String actionText) {
        /*left button*/
        if (hideBack) {
            layout.findViewById(R.id.btn_title_back).setClickable(false);
            layout.findViewById(R.id.image_back).setVisibility(View.GONE);
            int padding = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
            layout.findViewById(R.id.text_title).setPadding(padding, 0, 0, 0);
        } else {
            layout.findViewById(R.id.image_back).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.btn_title_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLeftActionPressed();
                }
            });
        }

        /*title*/
        ((TextView) layout.findViewById(R.id.text_title)).setText(title);

        /*right button*/
        this.rightAction = rightAction;
        final View iconAction = layout.findViewById(R.id.title_bar_icon_action);
        final View textAction = layout.findViewById(R.id.title_bar_text_action);
        if (rightAction == RightAction.NONE) {
            iconAction.setVisibility(View.GONE);
            textAction.setVisibility(View.GONE);
        } else {
            iconAction.setVisibility(RightAction.ICON == rightAction ? View.VISIBLE : View.GONE);
            textAction.setVisibility(RightAction.TEXT == rightAction ? View.VISIBLE : View.GONE);
            if (RightAction.ICON == rightAction) {
                iconAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightActionPressed();
                    }
                });
            } else if (RightAction.TEXT == rightAction) {
                ((TextView) layout.findViewById(R.id.title_bar_action_text)).setText(actionText);
                textAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightActionPressed();
                    }
                });
            }
        }
        enableRightAction(layout, isRightActionEnabled());
    }

    protected void setTitleBar(View layout, boolean hideBack, String title, RightAction rightAction, int actionRes) {
        /*left button*/
        if (hideBack) {
            layout.findViewById(R.id.btn_title_back).setClickable(false);
            layout.findViewById(R.id.image_back).setVisibility(View.GONE);
            int padding = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
            layout.findViewById(R.id.text_title).setPadding(padding, 0, 0, 0);
        } else {
            layout.findViewById(R.id.image_back).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.btn_title_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLeftActionPressed();
                }
            });
        }

        /*title*/
        ((TextView) layout.findViewById(R.id.text_title)).setText(title);

        /*right button*/
        this.rightAction = rightAction;
        final View iconAction = layout.findViewById(R.id.title_bar_icon_action);
        final View textAction = layout.findViewById(R.id.title_bar_text_action);
        if (rightAction == RightAction.NONE) {
            iconAction.setVisibility(View.GONE);
            textAction.setVisibility(View.GONE);
        } else {
            iconAction.setVisibility(RightAction.ICON == rightAction ? View.VISIBLE : View.GONE);
            textAction.setVisibility(RightAction.TEXT == rightAction ? View.VISIBLE : View.GONE);
            if (RightAction.ICON == rightAction) {
                ((ImageView) layout.findViewById(R.id.title_bar_action_image)).setImageResource(actionRes);
                iconAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightActionPressed();
                    }
                });
            } else if (RightAction.TEXT == rightAction) {
                ((TextView) layout.findViewById(R.id.title_bar_action_text)).setText(actionRes);
                textAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightActionPressed();
                    }
                });
            }
        }
        enableRightAction(layout, isRightActionEnabled());
    }

    protected void onLeftActionPressed() {

    }

    protected void onRightActionPressed() {

    }

    protected boolean isRightActionEnabled() {
        return true;
    }

    private void enableRightAction(View layout, boolean enable) {
        if (rightAction == RightAction.TEXT) {
            layout.findViewById(R.id.title_bar_text_action).setEnabled(enable);
            layout.findViewById(R.id.title_bar_action_text).setEnabled(enable);
        } else if (rightAction == RightAction.ICON) {
            layout.findViewById(R.id.title_bar_icon_action).setEnabled(enable);
            layout.findViewById(R.id.title_bar_action_image).setEnabled(enable);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (ActivityBase) getActivity();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected View findViewById(int id) {
        return layout.findViewById(id);
    }

    protected ViewGroup getRootView() {
        return (ViewGroup) layout;
    }



    public void onErrorPageClick() {

    }

    public void showErrorPage(String message) {
        ViewGroup rootView = getRootView();

        if (rootView.findViewWithTag(TAG_ERROR) != null)
            return;

        RelativeLayout view = new RelativeLayout(getActivity());
        view.setLayoutParams(rootView.getLayoutParams());
        view.setBackgroundResource(android.R.color.transparent);

        FrameLayout fl = new FrameLayout(getActivity());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(rootView.getWidth(), rootView.getHeight());
        lp.setMargins(0, 0, 0, 0);
        fl.setLayoutParams(lp);
        fl.setBackgroundResource(R.color.ui_bg_white);

        ImageView iv = new ImageView(getActivity());
        int width = DensityUtil.dip2px(getActivity(), 80);
        int height = DensityUtil.dip2px(getActivity(), 80);
        FrameLayout.LayoutParams ivlp = new FrameLayout.LayoutParams(width, height);

        int parentHeight = rootView.getHeight();
        int parentWidth = rootView.getWidth();

        int x = parentWidth / 2 - width / 2;
        int y = parentHeight / 2 - height;
        ivlp.setMargins(x, y, 0, 0);
        iv.setLayoutParams(ivlp);
        iv.setImageResource(R.drawable.shibai);
        fl.addView(iv);

        view.addView(fl);

        TextView tv = new TextView(getActivity());
        FrameLayout.LayoutParams tvlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvlp.setMargins(0, y + height + 40, 0, 0);
        tv.setLayoutParams(tvlp);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getResources().getColor(R.color.ui_font_d));
        tv.setText(message);
        fl.addView(tv);
        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onErrorPageClick();
            }
        });
        view.setTag(TAG_ERROR);
        rootView.addView(view, 1);
    }

    private final String TAG_ERROR = "error";
    public void hideErrorPage() {
        if (getRootView().findViewWithTag(TAG_ERROR) != null) {
            getRootView().removeView(getRootView().findViewWithTag(TAG_ERROR));
        }
    }

}
