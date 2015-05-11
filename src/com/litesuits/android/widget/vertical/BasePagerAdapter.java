package com.litesuits.android.widget.vertical;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * Created by taosj on 14/12/15.
 */
public class BasePagerAdapter extends PagerAdapter {
    List<View> mListViews;

    public BasePagerAdapter(List<View> mListViews) {
        this.mListViews = mListViews;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(mListViews.get(arg1));
    }


    @Override
    public int getCount() {
        return mListViews.size();
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {
        ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
        return mListViews.get(arg1);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == (arg1);
    }
}