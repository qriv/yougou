package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.fragment.*;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.log.Log;
import com.litesuits.http.request.Request;

/**
 * Created by taosj on 15/2/2.
 */
public class MainActivity extends ActivityBase implements View.OnClickListener {

    public static String curFragmentTag;
    private FragmentManager fragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private long exitTime = 0;
    private Tab currentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTabs();
        fragmentManager = getSupportFragmentManager();
        switchFragment(Tab.main);
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
            if (TextUtils.equals(tag, Tab.main.name())) {
                f = new MainFragment();
            } else if (TextUtils.equals(tag, Tab.category.name())) {
                f = new CategoryFragment();
            } else if (TextUtils.equals(tag, Tab.explore.name())) {
                f = new ExploreFragment();
            } else if (TextUtils.equals(tag, Tab.cart.name())) {
                f = ShoppingCarFragment.newInstance(true);
            } else if (TextUtils.equals(tag, Tab.profile.name())) {
                f = new ProfileFragment();
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
            final View container = findViewById(tab.containerId);
            container.setOnClickListener(this);
            container.setTag(tab);
            ((ImageView) container.findViewById(R.id.icon)).setImageResource(tab.iconRid);
            ((TextView) container.findViewById(R.id.label)).setText(tab.labelText);
        }
        findViewById(R.id.layout_tab_explore).setVisibility(View.GONE);
    }

    public enum Tab {
        main(R.id.layout_tab_main, R.drawable.ui_home, "首页"),

        category(R.id.layout_tab_category, R.drawable.ui_category, "类目"),

        explore(R.id.layout_tab_explore, R.drawable.ui_explore, "发现"),

        cart(R.id.layout_tab_cart, R.drawable.ui_cart, "购物车"),

        profile(R.id.layout_tab_profile, R.drawable.ui_profile, "我的"),;

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
