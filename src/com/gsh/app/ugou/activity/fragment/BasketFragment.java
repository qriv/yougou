package com.gsh.app.ugou.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.fragment.FragmentBase;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M4;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.gsh.app.ugou.activity.BasketDetailActivity;
import com.gsh.app.ugou.https.model.M43;
import com.gsh.app.ugou.https.model.M51;
import com.litesuits.common.utils.FileUtil;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Tan Chunmao
 */
public class BasketFragment extends FragmentBase {

    private int typeAHeight;
    private int typeBSize;
    private List<View> viewList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
            return layout;
        }
        layout = inflater.inflate(R.layout.ugou_fragment_package, container, false);
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        typeAHeight = (width - 2 * getResources().getDimensionPixelOffset(R.dimen.ui_margin_d)) * 9 / 16;
        typeBSize = (width - 3 * getResources().getDimensionPixelOffset(R.dimen.ui_margin_d)) / 2;
        int[] basketIds = {R.id.basket_a, R.id.basket_b, R.id.basket_c, R.id.basket_d, R.id.basket_e, R.id.basket_f, R.id.basket_g, R.id.basket_h, R.id.basket_i, R.id.basket_j, R.id.basket_k};
        viewList = new ArrayList<View>();
        for (int i : basketIds) {
            viewList.add(findViewById(i));
        }

        setLong(viewList.get(0));
        setLong(viewList.get(1));
        setSquare(viewList.get(2));
        setSquare(viewList.get(3));
        setLong(viewList.get(4));
        setLong(viewList.get(5));
        setSquare(viewList.get(6));
        setSquare(viewList.get(7));
        setLong(viewList.get(8));
        setSquare(viewList.get(9));
        setSquare(viewList.get(10));
//        ((TextView) findViewById(R.id.notice)).setText("英联邦片区于每周三、周六下午6点-8点配送到家，请各位业主查收菜篮子。");

//        fillBaskets();
        fetchData();
        return layout;
    }

    private void setLong(View view) {
        view.findViewById(R.id.picture).getLayoutParams().height = typeAHeight;
        view.setTag(R.id.tag_key_first, true);
    }

    private void setSquare(View view) {
        view.setTag(R.id.tag_key_first, false);
        view.findViewById(R.id.picture).getLayoutParams().height = typeBSize;
        view.findViewById(R.id.picture).getLayoutParams().width = typeBSize;

    }

    private void fetchData() {
        activity.showProgressDialog();
        hideContent();
        activity.execute(new Request(Urls.COMBO_LIST),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        activity.dismissProgressDialog();

                        if (apiResult != null && apiResult.isOk()) {
                            hideErrorPage();
                            List<M43> list = apiResult.getModels(M43.class);
                            fillBaskets(list);
                        } else {
                            onRequestError();
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        activity.dismissProgressDialog();
                        onRequestError();
                    }
                });
    }



    public void onErrorPageClick() {
        fetchData();
    }

    private void fillBaskets(List<M43> list) {
        /*String json = FileUtil.getLocalJson(Constant.Cache.BASKET_LIST);
        List<M43> list = null;
        if (TextUtils.isEmpty(json)) {
            list = prepareData();
        } else {
            list = JSONObject.parseArray(json, M43.class);
        }*/

        showContent();
        for (int i = 0; i < viewList.size(); i++) {
            if (i < list.size()) {
                View view = viewList.get(i);
                view.setOnClickListener(basketOnClickListener);
                view.setVisibility(View.VISIBLE);
                M43 m43 = list.get(i);
                view.setTag(R.id.tag_key_second, m43);
                ((TextView) view.findViewById(R.id.type)).setText((char) ('A' + i) + "");
                ((TextView) view.findViewById(R.id.desc)).setText(m43.name);
//                ((TextView) view.findViewById(R.id.desc)).setText("绿色有机套餐绿色有机套餐");
                ((TextView) view.findViewById(R.id.money)).setText((int) m43.price + "");
//                ((TextView) view.findViewById(R.id.money)).setText("8888");
                Boolean big = (Boolean) view.getTag(R.id.tag_key_first);
                if (big) {
                    activity.loadLongImage((ImageView) view.findViewById(R.id.picture), m43.mainPicturePath);
                } else {
                    activity.loadImage((ImageView) view.findViewById(R.id.picture), m43.mainPicturePath);
                }
//                ((ImageView) view.findViewById(R.id.picture)).setImageResource(m43.localPicture);
            }
        }
    }

    private View.OnClickListener basketOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            M43 m43 = (M43) v.getTag(R.id.tag_key_second);
            String basketId = String.valueOf(m43.id);
            startActivity(new Intent(activity, BasketDetailActivity.class).putExtra(String.class.getName(), basketId));
        }
    };



    private Random random = new Random();

    private M43 temp(long time, String summary, double price, String name, String picturePath, int saleCount, int commentCount, int localPicture) {
        List<M4> goods = new ArrayList<M4>();
        long now = System.currentTimeMillis();
        goods.add(new M4(now++, "蔬菜", Constant.TestData.fruit_pictures[0], "100g", 5, 7));
        goods.add(new M4(now++, "猪肉", Constant.TestData.fruit_pictures[1], "100g", 12, 15));
        goods.add(new M4(now++, "鸡蛋", Constant.TestData.fruit_pictures[2], "1个", 2, 3));
        M51 m51 = new M51();
        m51.goods = goods;
        List<M51> baskets = new ArrayList<M51>();
        for (int i = 0; i < 8; i++) {
            baskets.add(m51);
        }
        M43 m = new M43(time, summary, price, name, picturePath, saleCount, commentCount);
        m.localPicture = localPicture;
        m.baskets = baskets;
        m.star = random.nextInt(500) / 100.0;
        m.saleCount = random.nextInt(10000);
        m.commentCount = m.saleCount / 2;
        m.saleTip = "有效期：2015.01.20至2015.04.21";
        return m;
    }

    @Override
    public void refresh() {

    }
}
