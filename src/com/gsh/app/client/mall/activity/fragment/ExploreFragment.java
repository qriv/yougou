package com.gsh.app.client.mall.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gsh.app.client.mall.R;

/**
 * @author Tan Chunmao
 */
public class ExploreFragment extends FragmentBase {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
            return layout;
        }
        layout = inflater.inflate(R.layout.fragment_explore, container, false);
        return layout;
    }

    @Override
    public void refresh(){
    }
}
