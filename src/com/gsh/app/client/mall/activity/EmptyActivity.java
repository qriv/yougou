package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import com.gsh.app.client.mall.R;

/**
 * Created by taosj on 15/2/5.
 */
public class EmptyActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        String title = getIntent().getStringExtra("title");
        setTitleBar(title);
    }
}
