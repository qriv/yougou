package com.gsh.app.client.mall.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M9;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.android.widget.ViewHolder;
import com.litesuits.common.utils.CMUtil;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.List;


/**
 * Created by taosj on 15/3/16.
 */
public class MyFavoritesActivity extends ActivityBase {

    @InjectView
    private View notEmptyView, dealView, editView, delete;
    @InjectView
    private ListView list;
    @InjectView
    private View checkAll, checkAll4Edit;
    @InjectView
    private TextView total, totalEdit;
    @InjectView
    private Button addToCart;
    private boolean checkAlled;
    private boolean checkAll4Edited;
    private MyAdapter adapter;


    private boolean isEditMode = false;

    private final List<M9> data = CMUtil.getArrayList();
    private final List<M9> shouldBeEdited = CMUtil.getArrayList();
    private final List<M9> shouldBeBuy = CMUtil.getArrayList();
    private final List<M9> availableGoods = CMUtil.getArrayList();

    private void fetchData() {
        showProgressDialog();
        hideContent();
        execute(new Request(Urls.MEMBER_FAVORITES), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                showContent();
                dismissProgressDialog();
                List<M9> favoriteGoods = apiResult.getModels(M9.class);
                if (!apiResult.isOk()) {
                    toast(apiResult.message + "");
                    return;
                }
                if (favoriteGoods == null || favoriteGoods.size() == 0) {
                    setTitleBar(false, "我的收藏", RightAction.NONE, "");
                    showEmptyPage("收藏夹是空的");
                } else {
                    notEmptyView.setVisibility(View.VISIBLE);
                    data.clear();
                    data.addAll(favoriteGoods);
                    availableGoods.clear();
                    for (M9 m9 : favoriteGoods) {
                        if (m9.isAvailable()) {
                            availableGoods.add(m9);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    setTitleBar(false, "我的收藏", RightAction.TEXT, "编辑");
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                setTitleBar(false, "我的收藏", RightAction.TEXT, "");
                showEmptyPage("收藏夹是空的");
                notEmptyView.setVisibility(View.GONE);
                dismissProgressDialog();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);
        Injector.self.inject(this);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        if (isEditMode) {
            editView.setVisibility(View.VISIBLE);
            dealView.setVisibility(View.GONE);
        } else {
            editView.setVisibility(View.GONE);
            dealView.setVisibility(View.VISIBLE);
        }

        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAll.performClick();
            }
        });

        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAlled = !checkAlled;
                checkAll.setSelected(checkAlled);
                adapter.checkAll(checkAlled);
            }
        });

        checkAll4Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAll4Edited = !checkAll4Edited;
                checkAll4Edit.setSelected(checkAll4Edited);
                adapter.checkAll(checkAll4Edited);
            }
        });


        totalEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAll4Edit.performClick();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldBeEdited.size() == 0)
                    return;
                StringBuilder sbIds = new StringBuilder();
                for (M9 item : shouldBeEdited) {
                    sbIds.append(item.id + ",");
                }
                showProgressDialog();
                execute(new Request(Urls.MEMBER_FAVORITE_DELETE).addUrlParam("favoriteGoodsId", sbIds.toString()), new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult.isOk()) {
                            if (shouldBeEdited.size() == data.size()) {
                                showEmptyPage("收藏夹是空的");
                                setTitleBar(false, "我的收藏", RightAction.NONE, "");
                                notEmptyView.setVisibility(View.GONE);
                            }
                            data.removeAll(shouldBeBuy);
                            shouldBeEdited.clear();
                            adapter.notifyDataSetChanged();
                        } else {
                            toast("删除失败");
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        dismissProgressDialog();
                        toast("删除失败");
                    }
                });

            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldBeBuy.size() == 0)
                    return;
                StringBuilder sbIds = new StringBuilder();
                for (M9 item : shouldBeBuy) {
                    sbIds.append(item.id + ",");
                }
                showProgressDialog();
                execute(new Request(Urls.MEMBER_FAVORITE_MOVE_CART).addUrlParam("favoriteGoodsId", sbIds.toString()), new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult.isOk()) {
                            if (shouldBeBuy.size() == data.size()) {
                                showEmptyPage("收藏夹是空的");
                                setTitleBar(false, "我的收藏", RightAction.NONE, "");
                                notEmptyView.setVisibility(View.GONE);
                            }
                            data.removeAll(shouldBeBuy);
                            shouldBeBuy.clear();
                            adapter.notifyDataSetChanged();
                            dismissProgressDialog();
                        } else {
                            toast("移除失败");
                            dismissProgressDialog();
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        toast("移除失败");
                        dismissProgressDialog();
                    }
                });
            }
        });

        fetchData();
    }

    @Override
    protected void onRightActionPressed() {
        isEditMode = !isEditMode;
        shouldBeBuy.clear();
        shouldBeEdited.clear();
        checkAll.setSelected(false);
        checkAll4Edit.setSelected(false);
        if (isEditMode) {
            setTitleBar(false, "我的收藏", RightAction.TEXT, "完成");
            editView.setVisibility(View.VISIBLE);
            dealView.setVisibility(View.GONE);
        } else {
            setTitleBar(false, "我的收藏", RightAction.TEXT, "编辑");
            editView.setVisibility(View.GONE);
            dealView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void checkAll(boolean on) {
            if (isEditMode) {
                shouldBeEdited.clear();
                if (on) {
                    shouldBeEdited.addAll(data);
                }
            } else {
                shouldBeBuy.clear();
                if (on) {
                    shouldBeBuy.addAll(availableGoods);
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.item_cart, null);
            convertView.findViewById(R.id.quality).setVisibility(View.GONE);
            ((LinearLayout) convertView.findViewById(R.id.wrapper)).setGravity(Gravity.CENTER_VERTICAL);
            final ImageView selector = (ImageView) ViewHolder.get(convertView, R.id.selector);
            ImageView pic = (ImageView) ViewHolder.get(convertView, R.id.pic);
            TextView title = (TextView) ViewHolder.get(convertView, R.id.title);
            TextView status = (TextView) ViewHolder.get(convertView, R.id.status);
            TextView currentPrice = (TextView) ViewHolder.get(convertView, R.id.currentPrice);
            TextView price = (TextView) ViewHolder.get(convertView, R.id.price);
            price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);


            final M9 model = data.get(position);
            String path = "";
            if (!TextUtils.isEmpty(model.picturePath)) {
                path = model.picturePath;
            } else {
                if (model.picturePaths != null && !model.picturePaths.isEmpty()) {
                    path = model.picturePaths.get(0);
                }
            }
            loadImage(pic, path);
            title.setText(model.name);

            if (model.isUndercarriage) {
                status.setVisibility(View.VISIBLE);
                status.setText("无效商品");
                title.setVisibility(View.VISIBLE);
                convertView.setBackgroundColor(getResources().getColor(R.color.ui_bg_grey));
            } else if (model.stock == 0) {
                status.setVisibility(View.VISIBLE);
                status.setText("没有库存");
                title.setVisibility(View.VISIBLE);
                convertView.setBackgroundColor(getResources().getColor(R.color.ui_bg_grey));
            } else {
                status.setVisibility(View.GONE);
                convertView.setBackgroundColor(getResources().getColor(R.color.ui_bg_white));
            }

            currentPrice.setText(String.format("￥%1$.2f", model.price));
            price.setText(Html.fromHtml(String.format("￥%1$.2f", model.orginPrice)));
            if (model.orginPrice == model.price) {
                price.setVisibility(View.INVISIBLE);
            } else {
                price.setVisibility(View.VISIBLE);
            }

            if (isEditMode) {
                if (shouldBeEdited.contains(model)) {
                    selector.setImageResource(R.drawable.ui_checked);
                    selector.setTag("checked");
                } else {
                    selector.setImageResource(R.drawable.ui_unchecked);
                    selector.setTag("unchecked");
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("unchecked".equals(selector.getTag())) {
                            selector.setTag("checked");
                            selector.setImageResource(R.drawable.ui_check);
                            if (!shouldBeEdited.contains(model))
                                shouldBeEdited.add(model);
                        } else {
                            shouldBeEdited.remove(model);
                            selector.setTag("unchecked");
                            selector.setImageResource(R.drawable.ui_unchecked);
                        }
                        checkAll4Edited = shouldBeEdited.size() == data.size();
                        checkAll4Edit.setSelected(checkAll4Edited);
                        adapter.notifyDataSetChanged();
                    }
                });
            } else {
                if (shouldBeBuy.contains(model)) {
                    selector.setImageResource(R.drawable.ui_checked);
                    selector.setTag("checked");
                } else {
                    selector.setImageResource(R.drawable.ui_unchecked);
                    selector.setTag("unchecked");
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.isUndercarriage || model.stock == 0) {
                            selector.setTag("unchecked");
                            selector.setImageResource(R.drawable.ui_unchecked);
                        } else if ("unchecked".equals(selector.getTag())) {
                            selector.setTag("checked");
                            selector.setImageResource(R.drawable.ui_check);
                            if (!shouldBeBuy.contains(model))
                                shouldBeBuy.add(model);
                        } else {
                            shouldBeBuy.remove(model);
                            selector.setTag("unchecked");
                            selector.setImageResource(R.drawable.ui_unchecked);
                        }
                        checkAlled = shouldBeBuy.size() == availableGoods.size();
                        checkAll.setSelected(checkAlled);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            return convertView;
        }
    }

    ;
}
