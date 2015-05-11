package com.gsh.app.client.mall.activity.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.*;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.widget.ImageUtils;
import com.litesuits.android.widget.RoundCornerImageView;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;
import com.zxing.activity.CaptureActivity;


/**
 * Created by taosj on 15/2/2.
 */
public class ProfileFragment extends FragmentBase implements View.OnClickListener {
    private static final int REQUEST_LOGIN = 2038;
    private static final int REQUEST_QR_CODE = 2039;
    private Button login;
    private View unlogined;
    private View logined;
    private RoundCornerImageView avatar;

    private View setting;

    @Override
    public void refresh() {

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
            return layout;
        }
        layout = inflater.inflate(R.layout.fragment_profile, container, false);
        initItems();
        setting = layout.findViewById(R.id.setting);
        findViewById(R.id.to_pay).setOnClickListener(this);
        findViewById(R.id.to_receive).setOnClickListener(this);
        findViewById(R.id.to_comment).setOnClickListener(this);
        setting.setOnClickListener(this);
        return layout;
    }

    private void initTitle() {
        login = (Button) layout.findViewById(R.id.login);
        unlogined = layout.findViewById(R.id.unlogined);
        logined = layout.findViewById(R.id.logined);
        avatar = (RoundCornerImageView) layout.findViewById(R.id.avatar);
        if (!MallApplication.user.loggedIn()) {
            unlogined.setVisibility(View.VISIBLE);
            logined.setVisibility(View.GONE);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(activity, LoginActivity.class), REQUEST_LOGIN);
//                    activity.go.name(LoginActivity.class).goForResult(REQUEST_LOGIN);
                }
            });
        } else {
            onLogin();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOGIN) {
                onLogin();
            } else if (requestCode == REQUEST_QR_CODE) {
                final String code = data.getExtras().getString("result");
                exchange(code);
            }
        }
    }

    private void exchange(String code) {
        activity.showProgressDialog();
        activity.execute(new Request(Urls.COUPON_EXCHANGE).addUrlParam("code", code),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        activity.dismissProgressDialog();
                        if (apiResult != null && apiResult.isOk()) {
                            activity.go.name(VoucherListActivity.class).go();
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        activity.dismissProgressDialog();
                    }
                }
        );
    }

    private void onLogin() {
        unlogined.setVisibility(View.GONE);
        logined.setVisibility(View.VISIBLE);
        updateProfile();
    }

    private void initItems() {
        initItem(layout, R.id.layout_orders, R.drawable.ui_order, getString(R.string.title_order), Color.parseColor("#d05836"));
//        initItem(layout, R.id.layout_appoint, R.drawable.ui_my_appoint, getString(R.string.title_reservation), Color.parseColor("#27a8e1"));
        findViewById(R.id.layout_appoint).setVisibility(View.GONE);
        findViewById(R.id.appoint_sep).setVisibility(View.GONE);
        initItem(layout, R.id.layout_favorites, R.drawable.ui_my_favorite, getString(R.string.title_bookmark), Color.parseColor("#eaa422"));
        initItem(layout, R.id.layout_wallet, R.drawable.ui_wallet, getString(R.string.title_wallet), Color.parseColor("#3c469b"));
        initItem(layout, R.id.layout_address, R.drawable.ui_address, getString(R.string.title_address), Color.parseColor("#6eb92b"));
        initItem(layout, R.id.layout_exchange, R.drawable.ui_exchange, "兑换代金券", Color.parseColor("#f40a25"));
    }

    @Override
    public void onResume() {
        super.onResume();
        initTitle();
        hideStatusBar();
    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT < 16) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = activity.getWindow().getDecorView();
// Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null)
                actionBar.hide();
        }
    }

    private void updateProfile() {
        String nickname = MallApplication.user.getNickname();
        if (!TextUtils.isEmpty(nickname)) {
            ((TextView) findViewById(R.id.nickname)).setText(MallApplication.user.getNickname());
        } else {
            ((TextView) findViewById(R.id.nickname)).setText("人民优购用户_" + MallApplication.user.getId());
        }
        String localAvatarPath = MallApplication.user.getLocalAvatarPath();
        if (!TextUtils.isEmpty(localAvatarPath)) {
            ImageUtils.showLocalImage(avatar, localAvatarPath);
        } else {
            String avatarPath = MallApplication.user.getAvatarPath();
            if (!TextUtils.isEmpty(avatarPath)) {
                activity.loadAvatar(avatar, avatarPath);
            } else {
                avatar.setImageResource(R.drawable.ui_default_avatar);
            }
        }
    }

    private void initItem(View layout, int item, int iconRes, String title, int bg) {
        View container = layout.findViewById(item);
        ImageView icon = (ImageView) container.findViewById(R.id.icon);
        icon.setImageResource(iconRes);
        View iconContainer = container.findViewById(R.id.icon_container);
        GradientDrawable gradientDrawable = (GradientDrawable) iconContainer.getBackground();
        gradientDrawable.setColor(bg);
//        iconContainer.setBackgroundResource(bg);
        TextView label = (TextView) container.findViewById(R.id.label);
        label.setText(title);
        container.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (R.id.setting == v.getId()) {
            activity.go.name(SettingsActivity.class).go();
        } else if (!MallApplication.user.loggedIn()) {
            activity.go.name(LoginActivity.class).go();
        } else
            switch (v.getId()) {
                case R.id.layout_wallet: {
                    ActivityBase base = (ActivityBase) getActivity();
                    base.go.name(WalletActivity.class).go();
                }
                break;
                case R.id.layout_address: {
                    ActivityBase base = (ActivityBase) getActivity();
                    base.go.name(AddressListActivity.class).go();
                }
                break;
                case R.id.layout_appoint: {
                    ActivityBase base = (ActivityBase) getActivity();
                    base.go.name(EmptyActivity.class).param("title", "我的预约").go();
                }
                break;
                case R.id.layout_favorites: {
                    ActivityBase base = (ActivityBase) getActivity();
                    base.go.name(MyFavoritesActivity.class).go();
                }
                break;
                case R.id.layout_orders: {
                    ActivityBase base = (ActivityBase) getActivity();
                    base.go.name(OrderListActivity.class).param(String.class.getName(), "all").go();
                }
                break;
                case R.id.to_pay: {
                    ActivityBase base = (ActivityBase) getActivity();
                    base.go.name(OrderListActivity.class).param(String.class.getName(), "to_pay").go();

                }
                break;
                case R.id.to_comment: {
                    ActivityBase base = (ActivityBase) getActivity();
                    base.go.name(OrderListActivity.class).param(String.class.getName(), "to_comment").go();
                }
                break;
                case R.id.to_receive: {
                    ActivityBase base = (ActivityBase) getActivity();
                    base.go.name(OrderListActivity.class).param(String.class.getName(), "to_receive").go();
                }
                break;
                case R.id.layout_exchange: {
                    ActivityBase base = (ActivityBase) getActivity();
                    base.go.name(CaptureActivity.class).goForResult(REQUEST_QR_CODE);
                }
            }
    }
}
