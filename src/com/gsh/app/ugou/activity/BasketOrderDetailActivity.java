package com.gsh.app.ugou.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.ActivityBase;
import com.gsh.app.client.mall.https.model.M4;
import com.gsh.app.ugou.https.model.M49;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class BasketOrderDetailActivity extends ActivityBase {


    private M49 m49;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m49 = (M49) getIntent().getSerializableExtra(M49.class.getName());
        prepareData();
        setContentView(R.layout.ugou_activity_order_detail);
        setTitleBar("订单详情");
        fillGoods();
    }

    private void prepareData() {
        m49 = new M49();
        long now = System.currentTimeMillis();
        m49.id = now;
        m49.orderNo = String.valueOf(now);
        List<M4> goods = new ArrayList<M4>();
        goods.add(new M4(now++, "猪肉", Constant.TestData.fruit_pictures[0], "1kg装", 5, 7));
        goods.add(new M4(now++, "空心菜", Constant.TestData.fruit_pictures[1], "2kg装", 12, 15));
        goods.add(new M4(now++, "大白菜", Constant.TestData.fruit_pictures[2], "2kg装", 2, 3));
        goods.add(new M4(now++, "番茄", Constant.TestData.fruit_pictures[2], "2kg装", 2, 3));
        m49.goods = goods;
    }


    private void fillGoods() {
        int[] goodsPictures = {R.drawable.goods_pork, R.drawable.goods_water_spinach, R.drawable.goods_chinese_cabbage, R.drawable.goods_potato};
        LinearLayout items = (LinearLayout) findViewById(R.id.container_goods);
        int one = getResources().getDimensionPixelOffset(R.dimen.ui_size_one_dp);
        for (int i = 0; i < m49.goods.size(); i++) {
            View gap = new View(this);
            gap.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, one));
            gap.setBackgroundResource(R.color.ui_divider_bg);
            items.addView(gap);
            fillItem(items, goodsPictures[i], m49.goods.get(i));
        }
    }


    private void fillItem(LinearLayout layout, int picture, M4 m4) {
        View container = getLayoutInflater().inflate(R.layout.ugou_items_order_goods, null);
        ((ImageView) container.findViewById(R.id.icon)).setImageResource(picture);
        ((TextView) container.findViewById(R.id.name)).setText(m4.name);
        ((TextView) container.findViewById(R.id.desc)).setText(m4.unit);
        ((TextView) container.findViewById(R.id.money)).setText((int) m4.price + "元");
        layout.addView(container);
    }


}
