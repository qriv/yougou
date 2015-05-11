package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import com.gsh.app.client.mall.activity.fragment.ShoppingCarFragment;

/**
 * @author Tan Chunmao
 */
public class ShoppingCarActivity extends ActivityBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_shopping_car);
        ShoppingCarFragment details = ShoppingCarFragment.newInstance(false);
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
    }
}
