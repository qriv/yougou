package com.gsh.app.client.mall.activity.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.ActivityBase;
import com.gsh.app.client.mall.activity.GoodsDetailActivity;
import com.gsh.app.client.mall.activity.SearchActivity;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M3;
import com.gsh.app.client.mall.https.model.M4;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.log.Log;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.*;


/**
 * Created by taosj on 15/2/2.
 */
public class CategoryFragment extends FragmentBase {
    private static final int ORDER_SELL_DOWN = 0;
    private static final int ORDER_SELL_UP = 1;
    private static final int ORDER_PRICE_DOWN = 2;
    private static final int ORDER_PRICE_UP = 3;

    private TextView count, volumeRank, priceRank;
    private ListView category, goods;
    private int orderType;
    Map<M3, List<M4>> map;
    CategoryAdapter categoryAdapter;
    GoodsAdapter goodsAdapter;

    Animation volumeUp;
    Animation volumeDown;
    Animation priceUp;
    Animation priceDown;
    private View sortVolume;
    private View sortPrice;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
            return layout;
        }
        layout = inflater.inflate(R.layout.fragment_category, container, false);
        setTitleBar(layout, true, "商品分类", ActivityBase.RightAction.ICON, R.drawable.ui_search);
        count = (TextView) layout.findViewById(R.id.count);
        volumeRank = (TextView) layout.findViewById(R.id.volumeRank);
        priceRank = (TextView) layout.findViewById(R.id.priceRank);
        volumeRank.setOnClickListener(onClickListener);
        priceRank.setOnClickListener(onClickListener);
        sortVolume = findViewById(R.id.sort_volume);
        sortPrice = findViewById(R.id.sort_price);

        category = (ListView) layout.findViewById(R.id.category);
        category.setVerticalScrollBarEnabled(false);
        category.setHorizontalScrollBarEnabled(false);
        goods = (ListView) layout.findViewById(R.id.goods);
        goodsAdapter = new GoodsAdapter();
        goods.setAdapter(goodsAdapter);
        goods.setVerticalScrollBarEnabled(false);
        goods.setHorizontalScrollBarEnabled(false);
        categoryAdapter = new CategoryAdapter();
        category.setAdapter(categoryAdapter);
        category.setOnItemClickListener(onItemClickListener);
        goods.setOnItemClickListener(onItemClickListener);
        volumeUp = AnimationUtils.loadAnimation(activity, R.anim.rotate);
        priceUp = AnimationUtils.loadAnimation(activity, R.anim.rotate);
        volumeDown = AnimationUtils.loadAnimation(activity, R.anim.rotate_down);
        priceDown = AnimationUtils.loadAnimation(activity, R.anim.rotate_down);
        fetchCategory();
        return layout;
    }

    @Override
    public void refresh() {
        fetchCategory();
    }

    @Override
    public void onResume() {
        super.onResume();
        orderType = ORDER_SELL_DOWN;
        volumeRank.setTextColor(getResources().getColor(R.color.ui_font_red));
        priceRank.setTextColor(getResources().getColor(R.color.ui_font_c));
        if (goodsAdapter != null && goodsAdapter.getCount() > 0) {
            goodsAdapter.sort();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sortPrice.clearAnimation();
        sortVolume.clearAnimation();
    }

    private void fetchCategory() {
        ActivityBase activityBase = (ActivityBase) getActivity();
        activityBase.showProgressDialog();
        activityBase.execute(new Request(Urls.CATEGORY),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult.isOk()) {
                            List<M3> data = apiResult.getModels(M3.class);
                            if (data != null && data.size() > 0) {
                                map = new HashMap<M3, List<M4>>();
                                for (M3 m3 : data) {
                                    map.put(m3, null);
                                }
                                catPosition = 0;
                                categoryAdapter.addData(data);
                                fetchGoods(data.get(0));
                            } else {
                                activity.dismissProgressDialog();
                            }
                        } else {
                            activity.dismissProgressDialog();
                            activity.toast(apiResult.message);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        activity.dismissProgressDialog();
                    }
                });

    }

    private void fetchGoods(final M3 m3) {
        List<M4> list = map.get(m3);
        if (list != null) {
            categoryAdapter.select(catPosition);
            goodsAdapter.addData(list);
        } else {
            activity.execute(new Request(Urls.GOODS_CATEGORY).addUrlParam("categoryId", m3.id + ""),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            activity.dismissProgressDialog();
                            if (apiResult != null) {
                                if (apiResult.isOk()) {
                                    List<M4> data = apiResult.getModels(M4.class);
                                    if (data != null) {
                                        int count = 0;
                                        for (M4 m4 : data) {
                                            m4.saleCount = count;
                                            count++;
                                        }
                                        categoryAdapter.select(catPosition);
                                        map.put(m3, data);
                                        goodsAdapter.addData(data);
                                    }
                                } else {
                                    activity.toast(apiResult.message);
                                }
                            } else {
                                activity.serverError();
                            }
                        }

                        @Override
                        protected void onFailure(Response res, HttpException e) {
                            activity.dismissProgressDialog();
                            activity.serverError();
                        }
                    });
        }
    }

    private class GoodsAdapter extends BaseAdapter {
        List<M4> data;

        public GoodsAdapter() {
            data = new ArrayList<M4>();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public M4 getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addData(List<M4> data) {
            this.data.clear();
            this.data.addAll(data);
            sort();
            notifyDataSetChanged();
            count.setText(String.format("共%d个商品", data.size()));
        }

        public void sort() {
            Collections.sort(data, new Comparator<M4>() {
                @Override
                public int compare(M4 m4, M4 t1) {
                    if (orderType == ORDER_PRICE_DOWN) {
                        return Double.compare(t1.price, m4.price);
                    } else if (orderType == ORDER_PRICE_UP) {
                        return Double.compare(m4.price, t1.price);
                    } else if (orderType == ORDER_SELL_DOWN) {
                        return (m4.saleCount < t1.saleCount ? -1 : (m4.saleCount == t1.saleCount ? 0 : 1)) * (-1);
                    } else if (orderType == ORDER_SELL_UP) {
                        return m4.saleCount < t1.saleCount ? -1 : (m4.saleCount == t1.saleCount ? 0 : 1);
                    }
                    return 0;
                }
            });
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GoodsViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_cart, null);
                viewHolder = new GoodsViewHolder();
                ((LinearLayout) convertView.findViewById(R.id.middle)).setGravity(Gravity.TOP);
                convertView.findViewById(R.id.selector).setVisibility(View.GONE);
                convertView.findViewById(R.id.quality).setVisibility(View.GONE);
                ((LinearLayout) convertView.findViewById(R.id.wrapper)).setGravity(Gravity.CENTER_VERTICAL);
                viewHolder.picView = (ImageView) convertView.findViewById(R.id.pic);
                viewHolder.statusView = (TextView) convertView.findViewById(R.id.status);
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.title);
                viewHolder.nameView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
                viewHolder.statusView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f));
                viewHolder.realPriceView = (TextView) convertView.findViewById(R.id.currentPrice);
                viewHolder.falsePriceView = (TextView) convertView.findViewById(R.id.price);
                viewHolder.falsePriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GoodsViewHolder) convertView.getTag();
            }
            final M4 m4 = getItem(position);
            activity.loadImage(viewHolder.picView, m4.picturePath);
            if (MallApplication.test)
                viewHolder.nameView.setText(m4.name + "_" + m4.id);
            else
                viewHolder.nameView.setText(m4.name);
            viewHolder.realPriceView.setText(String.format("￥%.2f", m4.price));
            viewHolder.falsePriceView.setText(String.format("￥%.2f", m4.originPrice));
            viewHolder.statusView.setText(String.format("销量：%d", m4.saleCount));
            viewHolder.statusView.setTextColor(getResources().getColor(R.color.ui_font_e));
            return convertView;
        }

        public void clear() {
            count.setText(String.format("共%d个商品", data.size()));
            data.clear();
            notifyDataSetChanged();
        }
    }

    private class GoodsViewHolder {
        ImageView picView;
        TextView nameView;
        TextView realPriceView;
        TextView falsePriceView;
        TextView statusView;
    }

    private class CategoryAdapter extends BaseAdapter {
        private List<M3> data;

        public CategoryAdapter() {
            data = new ArrayList<M3>();
        }


        public void addData(List<M3> data) {
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public M3 getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CatViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_category, null);
                viewHolder = new CatViewHolder();
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (CatViewHolder) convertView.getTag();
            }
            final M3 m3 = getItem(position);
            viewHolder.nameView.setSelected(m3.selected);
            viewHolder.nameView.setText(m3.name);
            return convertView;
        }

        public void select(int position) {
            for (M3 m3 : data) {
                m3.selected = false;
            }
            data.get(position).selected = true;
            notifyDataSetChanged();
        }
    }

    private class CatViewHolder {
        TextView nameView;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.volumeRank == v.getId()) {
                if (orderType > ORDER_SELL_UP) {
                    orderType = ORDER_SELL_DOWN;
                    volumeRank.setTextColor(getResources().getColor(R.color.ui_font_red));
                    priceRank.setTextColor(getResources().getColor(R.color.ui_font_c));
                    sortPrice.clearAnimation();
                    sortVolume.clearAnimation();
                } else {
                    orderType = orderType == ORDER_SELL_DOWN ? ORDER_SELL_UP : ORDER_PRICE_DOWN;
                    if (orderType == ORDER_SELL_UP) {
                        sortVolume.startAnimation(volumeUp);
                    } else {
                        sortVolume.startAnimation(volumeDown);
                    }
                }
//                onOrderTypeChanged();
                goodsAdapter.sort();
            } else if (R.id.priceRank == v.getId()) {
                if (orderType < ORDER_PRICE_DOWN) {
                    orderType = ORDER_PRICE_DOWN;
                    volumeRank.setTextColor(getResources().getColor(R.color.ui_font_c));
                    priceRank.setTextColor(getResources().getColor(R.color.ui_font_red));
                    sortPrice.clearAnimation();
                    sortVolume.clearAnimation();
                } else {
                    orderType = orderType == ORDER_PRICE_DOWN ? ORDER_PRICE_UP : ORDER_PRICE_DOWN;
                    if (orderType == ORDER_PRICE_UP) {
                        sortPrice.startAnimation(priceUp);
                    } else {
                        sortPrice.startAnimation(priceDown);
                    }
                }
//                onOrderTypeChanged();
                goodsAdapter.sort();
            }
        }
    };

    private void onOrderTypeChanged() {
//        Drawable down = getResources().getDrawable(R.drawable.ui_item_down_arrow);
//        Drawable up = getResources().getDrawable(R.drawable.ui_up_arrow);
//        if (orderType == ORDER_SELL) {
//            volumeRank.setTextColor(getResources().getColor(R.color.ui_font_red));
//            priceRank.setTextColor(getResources().getColor(R.color.ui_font_c));
//            volumeRank.setCompoundDrawablesWithIntrinsicBounds(null, null, up, null);
//            priceRank.setCompoundDrawablesWithIntrinsicBounds(null, null, down, null);
//        } else if (orderType == ORDER_PRICE) {
//            volumeRank.setTextColor(getResources().getColor(R.color.ui_font_c));
//            priceRank.setTextColor(getResources().getColor(R.color.ui_font_red));
//            volumeRank.setCompoundDrawablesWithIntrinsicBounds(null, null, down, null);
//            priceRank.setCompoundDrawablesWithIntrinsicBounds(null, null, up, null);
//        }
//        goodsAdapter.sort();
    }

    @Override
    protected void onRightActionPressed() {
        startActivity(new Intent(activity, SearchActivity.class));
    }


    private int catPosition;
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (R.id.category == parent.getId()) {
                M3 m3 = categoryAdapter.getItem(position);
                catPosition = position;
                goodsAdapter.clear();
                fetchGoods(m3);
            } else if (R.id.goods == parent.getId()) {
                M4 m4 = goodsAdapter.getItem(position);
                activity.go.name(GoodsDetailActivity.class).param(String.class.getName(), "id").param("id", m4.id + "").go();
                /*
                Intent intent = new Intent(activity, GoodDetailActivity.class);
                intent.putExtra(String.class.getName(), "id");
                intent.putExtra("id", m4.id + "");
                startActivity(intent);*/
            }
        }
    };
}
