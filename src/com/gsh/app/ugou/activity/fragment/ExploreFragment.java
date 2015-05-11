package com.gsh.app.ugou.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.fragment.FragmentBase;

/**
 * @author Tan Chunmao
 */
public class ExploreFragment extends FragmentBase{

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
            return layout;
        }
        layout = inflater.inflate(R.layout.ugou_fragment_explore, container, false);
        return layout;
    }
    @Override
    public void refresh() {

    }
}
