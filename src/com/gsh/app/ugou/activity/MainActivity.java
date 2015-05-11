package com.gsh.app.ugou.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.LocationActivity;
import com.gsh.app.client.mall.activity.fragment.FragmentBase;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.gsh.app.ugou.activity.fragment.BasketFragment;
import com.gsh.app.ugou.activity.fragment.DiscoveryFragment;
import com.litesuits.android.log.Log;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

/**
 * @author Tan Chunmao
 */
public class MainActivity extends LocationActivity implements View.OnClickListener {
    public static String curFragmentTag;
    private FragmentManager fragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private long exitTime = 0;
    private Tab currentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ugou_activity_main);
        initTabs();
        fragmentManager = getSupportFragmentManager();
        switchFragment(Tab.basket);
    }

    @Override
    protected void onResume() {
        needLocationWhenStart = !MallApplication.user.loggedIn() && !MallApplication.user.isNotified();
        needLocationWhenStart = false;
        Log.d("test", "user log: " + !MallApplication.user.loggedIn());
        Log.d("test", "user notified: " + !MallApplication.user.isNotified());
        Log.d("test", "main need location: " + needLocationWhenStart);
        super.onResume();
    }

    @Override
    protected void onLocated() {
        checkServiceAvailable();
    }

    private void checkServiceAvailable() {
        String latString = String.valueOf(bdLocation.getLatitude());
        String lonString = String.valueOf(bdLocation.getLongitude());
        execute(new Request(Urls.EXTERN_ONSERVICE).addUrlParam("lat", latString).addUrlParam("lng", lonString),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                       /* notice(new CallBack() {
                            @Override
                            public void call() {

                            }
                        }, "我们将尽快开通您所在小区的服务");*/
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);

                    }
                }
        );
    }


    @Override
    public void onClick(View v) {
        Tab selectedTab = (Tab) v.getTag();
        if (currentTab != selectedTab)
            switchFragment(selectedTab);
    }

    public void switchFragment(Tab selectedTab) {
        currentTab = selectedTab;

        for (Tab tab : Tab.values()) {
            View view = findViewById(tab.containerId);
            boolean isSelected = (tab == selectedTab);
            view.setSelected(isSelected);
//            View line = view.findViewById(R.id.line);
//            if (isSelected)
//                line.setVisibility(View.VISIBLE);
//            else
//                line.setVisibility(View.INVISIBLE);
        }
        String tag = selectedTab.name();
        if (TextUtils.equals(tag, curFragmentTag)) {
            return;
        }
        if (curFragmentTag != null) {
            detachFragment(getFragment(curFragmentTag));

        }
        FragmentBase currentFragment = (FragmentBase) getFragment(tag);
        attachFragment(R.id.content, currentFragment, tag);
        curFragmentTag = tag;
        commitTransactions();
        if (currentFragment.getActivity() != null)
            currentFragment.refresh();
    }

    private void detachFragment(Fragment f) {

        if (f != null && !f.isDetached()) {
            ensureTransaction();
            mFragmentTransaction.detach(f);
        }
    }

    private FragmentTransaction ensureTransaction() {
        if (mFragmentTransaction == null) {
            mFragmentTransaction = fragmentManager.beginTransaction();
            mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }
        return mFragmentTransaction;
    }

    private void attachFragment(int layout, Fragment f, String tag) {
        if (f != null) {
            if (f.isDetached()) {
                ensureTransaction();
                mFragmentTransaction.attach(f);
            } else if (!f.isAdded()) {
                ensureTransaction();
                mFragmentTransaction.add(layout, f, tag);
            }
        }
    }

    private void commitTransactions() {
        if (mFragmentTransaction != null && !mFragmentTransaction.isEmpty()) {
            mFragmentTransaction.commitAllowingStateLoss();
            mFragmentTransaction = null;
        }
    }

    private Fragment getFragment(String tag) {
        Fragment f = fragmentManager.findFragmentByTag(tag);
        if (f == null) {
            if (TextUtils.equals(tag, Tab.basket.name())) {
                f = new BasketFragment();
            } else if (TextUtils.equals(tag, Tab.explore.name())) {
                f = new DiscoveryFragment();
            } else if (TextUtils.equals(tag, Tab.profile.name())) {
                f = new com.gsh.app.ugou.activity.fragment.ProfileFragment();
            }
        }
        return f;
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            toast("再按一次 退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            finishAll();
        }
    }

    private void initTabs() {
        for (Tab tab : Tab.values()) {
            final LinearLayout container = (LinearLayout) findViewById(tab.containerId);
            container.setOnClickListener(this);
            /*if (tab == Tab.basket) {
                container.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            } else if (tab == Tab.profile) {
                container.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            } else if (tab == Tab.explore) {
                container.setGravity(Gravity.CENTER);
            }*/
            container.setTag(tab);
            ((ImageView) container.findViewById(R.id.icon)).setImageResource(tab.iconRid);
            ((TextView) container.findViewById(R.id.label)).setText(tab.labelText);
        }
    }

    public enum Tab {
        basket(R.id.tab_basket, R.drawable.ugou_basket, "优选"),
        explore(R.id.tab_explore, R.drawable.ugou_explore, "发现生活"),
        profile(R.id.tab_profile, R.drawable.ugou_profile, "我的"),;

        int containerId;
        int iconRid;
        String labelText;

        Tab(int containerId, int iconRid, String labelText) {
            this.containerId = containerId;
            this.iconRid = iconRid;
            this.labelText = labelText;
        }
    }
}
