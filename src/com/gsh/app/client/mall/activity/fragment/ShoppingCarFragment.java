package com.gsh.app.client.mall.activity.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.*;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M10;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.widget.ViewHolder;
import com.litesuits.common.utils.CMUtil;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.List;

/**
 * Created by taosj on 15/2/2.
 */
public class ShoppingCarFragment extends FragmentBase {

    private boolean isEditMode = false;

    private final List<M10> data = CMUtil.getArrayList();
    private final List<M10> shouldBeEdited = CMUtil.getArrayList();
    private final List<M10> shouldBeBuy = CMUtil.getArrayList();
    private final List<M10> availableGoods = CMUtil.getArrayList();

    private ListView list;

    private View notEmptyView;
    private View notLoginView;

    private View dealView;
    private View checkAll;
    private Button deal;
    private TextView total;

    private View editView;
    private View checkAll4Edit;
    private View moveToFavorites;
    private View delete;
    private TextView totalEdit;

    private TextView rightButton;
    private boolean checkAlled;
    private boolean checkAll4Edited;
    private MyAdapter adapter;


    public static ShoppingCarFragment newInstance(boolean hideBack) {
        ShoppingCarFragment shoppingCarFragment = new ShoppingCarFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("hideBack", hideBack);
        shoppingCarFragment.setArguments(bundle);
        return shoppingCarFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
            return layout;
        }
        layout = inflater.inflate(R.layout.fragment_cart, container, false);
        boolean hideBack = getArguments().getBoolean("hideBack");
        setTitleBar(layout, hideBack, "购物车", ActivityBase.RightAction.TEXT, "编辑");

        rightButton = ((TextView) layout.findViewById(R.id.title_bar_action_text));


        list = (ListView) layout.findViewById(R.id.list);
        list.setVerticalScrollBarEnabled(false);
        adapter = new MyAdapter();
        list.setAdapter(adapter);

        notEmptyView = layout.findViewById(R.id.notEmptyView);
        notLoginView = layout.findViewById(R.id.notLoginView);
        dealView = layout.findViewById(R.id.dealView);
        checkAll = layout.findViewById(R.id.checkAll);
        deal = (Button) layout.findViewById(R.id.deal);
        total = (TextView) layout.findViewById(R.id.total);

        editView = layout.findViewById(R.id.editView);
        checkAll4Edit = layout.findViewById(R.id.checkAll4Edit);
        moveToFavorites = layout.findViewById(R.id.moveToFavorites);
        delete = layout.findViewById(R.id.delete);
        totalEdit = (TextView) layout.findViewById(R.id.totalEdit);
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

                deal.setText(String.format("结算(%d)", shouldBeBuy.size()));
                total.setText(String.format("总计:￥%1$.2f", summary()));
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

        deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder();
            }
        });
        totalEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAll4Edit.performClick();
            }
        });


        moveToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToFavorites();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        return layout;
    }

    @Override
    public void refresh() {
        if (MallApplication.user.loggedIn()) {
            rightButton.setVisibility(View.VISIBLE);
            notEmptyView.setVisibility(View.VISIBLE);
            notLoginView.setVisibility(View.INVISIBLE);
            data.clear();

            isEditMode = false;
            shouldBeBuy.clear();
            shouldBeEdited.clear();
            checkAll4Edit.setSelected(false);
            checkAll.setSelected(false);
            if (isEditMode) {
                rightButton.setText("完成");
                editView.setVisibility(View.VISIBLE);
                dealView.setVisibility(View.GONE);
            } else {
                rightButton.setText("编辑");
                editView.setVisibility(View.GONE);
                dealView.setVisibility(View.VISIBLE);
            }

            fetchData();
        } else {
            notEmptyView.setVisibility(View.INVISIBLE);
            notLoginView.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.INVISIBLE);
            layout.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.go.name(LoginActivity.class).go();
                }
            });
        }
    }

    //    "结算"
    private void createOrder() {
        if (shouldBeBuy.isEmpty()) {
            activity.toast("请选择要购买的商品后再提交");
            return;
        }
        StringBuilder sb = new StringBuilder();
        String separator = null;
        for (M10 m10 : shouldBeBuy) {
            if (separator != null)
                sb.append(separator);
            sb.append(m10.id);
            separator = ",";
        }
        activity.go.name(OrderConfirmActivityNew.class).param(String.class.getName(), sb.toString()).go();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_cart, null);

            final ImageView selector = (ImageView) ViewHolder.get(convertView, R.id.selector);
            selector.setTag("unchecked");
            ImageView pic = (ImageView) ViewHolder.get(convertView, R.id.pic);

            TextView title = (TextView) ViewHolder.get(convertView, R.id.title);

            View editNumberView = ViewHolder.get(convertView, R.id.editNumberView);

            View removeByOne = ViewHolder.get(convertView, R.id.removeByOne);
            final TextView number = (TextView) ViewHolder.get(convertView, R.id.number);
            final View addByOne = ViewHolder.get(convertView, R.id.addByOne);
            final TextView status = (TextView) ViewHolder.get(convertView, R.id.status);
            TextView currentPrice = (TextView) ViewHolder.get(convertView, R.id.currentPrice);
            TextView price = (TextView) ViewHolder.get(convertView, R.id.price);
            price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            final TextView quality = (TextView) ViewHolder.get(convertView, R.id.quality);

            final M10 model = data.get(position);

            activity.loadImage(pic, model.picturePath);
            quality.setText(String.format("x%d", model.number));
            currentPrice.setText(String.format("￥%1$.2f", model.price));
            price.setText(Html.fromHtml(String.format("￥%1$.2f", model.orginPrice)));
            if (model.orginPrice == model.price) {
                price.setVisibility(View.INVISIBLE);
            } else {
                price.setVisibility(View.VISIBLE);
            }

            if (isEditMode) {
                editNumberView.setVisibility(View.VISIBLE);
                number.setText(model.number + "");
                addByOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.number >= model.stock) {
                            activity.toast("库存不够了");
                            return;
                        }
                        setNumber(model.id, model.number + 1, number, quality, position);
                    }
                });
                removeByOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.number <= 1) {
                            activity.toast("不能再少了");
                            return;
                        }
                        if (model.number > model.stock + 1) {
                            number.setText((model.stock) + "");
                            data.get(position).number = model.stock;
                            setNumber(model.id, model.stock, number, quality, position);
                        } else {
                            setNumber(model.id, model.number - 1, number, quality, position);
                            status.setVisibility(View.GONE);
                        }
                    }
                });
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
                editNumberView.setVisibility(View.GONE);
                title.setVisibility(View.VISIBLE);
                title.setText(model.name);

                if (MallApplication.test)
                    title.setText(model.name + "_" + model.googdId);
                else
                    title.setText(model.name);

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
                        if (model.isUndercarriage || model.stock == 0 || model.number > model.stock) {
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
                        deal.setText(String.format("结算(%d)", shouldBeBuy.size()));
                        total.setText(String.format("总计￥%1$.2f", summary()));
                    }
                });


            }

            if (model.isUndercarriage) {
                status.setVisibility(View.VISIBLE);
                status.setText("无效商品");
                title.setVisibility(View.VISIBLE);
                editNumberView.setVisibility(View.GONE);
                convertView.setBackgroundColor(getResources().getColor(R.color.ui_bg_grey));
            } else if (model.stock == 0) {
                status.setVisibility(View.VISIBLE);
                status.setText("没有库存");
                title.setVisibility(View.VISIBLE);
                editNumberView.setVisibility(View.GONE);
                convertView.setBackgroundColor(getResources().getColor(R.color.ui_bg_grey));
            } else if (model.stock < model.number) {
                status.setVisibility(View.VISIBLE);
                status.setText(String.format("库存只有%d", model.number));
                convertView.setBackgroundColor(getResources().getColor(R.color.ui_bg_grey));
            } else {
                status.setVisibility(View.INVISIBLE);
                convertView.setBackgroundColor(getResources().getColor(R.color.ui_bg_white));
            }

            return convertView;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    protected void onRightActionPressed() {
        isEditMode = !isEditMode;
        shouldBeBuy.clear();
        shouldBeEdited.clear();
        checkAll4Edit.setSelected(false);
        if (isEditMode) {
            rightButton.setText("完成");
            editView.setVisibility(View.VISIBLE);
            dealView.setVisibility(View.GONE);
        } else {
            rightButton.setText("编辑");
            editView.setVisibility(View.GONE);
            dealView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void fetchData() {
        ActivityBase activityBase = (ActivityBase) getActivity();
        activityBase.showProgressDialog();
        hideContent();
        activityBase.execute(new Request(Urls.MEMBER_CART_GOODS), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                activity.dismissProgressDialog();
                if (apiResult.isOk()) {
                    List<M10> cartGoods = apiResult.getModels(M10.class);

                    checkAll.setSelected(false);
                    if (cartGoods == null || cartGoods.size() == 0) {
//                        showEmptyPage("购物车还是空的");
                        notEmptyView.setVisibility(View.GONE);
                        rightButton.setVisibility(View.GONE);
                    } else {
//                        hideEmptyPage();
                        showContent();
                        if (MallApplication.test) {
                            for (M10 m10 : cartGoods) {
                                m10.stock = 10;
                            }
                        }
                        rightButton.setVisibility(View.VISIBLE);
                        notEmptyView.setVisibility(View.VISIBLE);
                        data.clear();
                        data.addAll(cartGoods);
                        availableGoods.clear();
                        for (M10 m10 : cartGoods) {
                            if (m10.isAvailable()) {
                                availableGoods.add(m10);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        rightButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    activity.toast(apiResult.message + "");
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
//                showEmptyPage("购物车还是空的");
                rightButton.setVisibility(View.GONE);
                notEmptyView.setVisibility(View.GONE);
                activity.dismissProgressDialog();
            }
        });

    }

    private void setNumber(long cartGoodsId, final int number, final TextView numberView, final TextView quality, final int positon) {
        activity.showProgressDialog();
        activity.execute(new Request(Urls.MEMBER_CART_GOODS_CHANGE).addUrlParam("cartGoodsId", cartGoodsId + "").addUrlParam("number", number + ""), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                if (apiResult.isOk()) {
                    numberView.setText(number + "");
                    quality.setText("x" + number);
                    data.get(positon).number = number;
                    activity.dismissProgressDialog();
                } else {
                    activity.toast("修改失败");
                    activity.dismissProgressDialog();
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                activity.toast("修改失败");
                activity.dismissProgressDialog();
            }
        });
    }

    private void delete() {
        if (shouldBeEdited.size() == 0)
            return;
        StringBuilder sbIds = new StringBuilder();
        String sep = "";
        for (M10 item : shouldBeEdited) {
            if (!TextUtils.isEmpty(sep))
                sbIds.append(sep);
            sbIds.append(item.id);
            sep = ",";
        }
        activity.showProgressDialog();
        activity.execute(new Request(Urls.MEMBER_CART_GOODS_DELETE).addUrlParam("cartGoodsId", sbIds.toString()), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                if (apiResult.isOk()) {
                    if (shouldBeEdited.size() == data.size()) {
//                        showEmptyPage("购物车还是空的");
                        rightButton.setVisibility(View.GONE);
                        notEmptyView.setVisibility(View.GONE);
                    }
                    data.removeAll(shouldBeEdited);
                    shouldBeEdited.clear();
                    adapter.notifyDataSetChanged();
                    activity.dismissProgressDialog();
                } else {
                    activity.toast("删除失败");
                    activity.dismissProgressDialog();
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                activity.toast("删除失败");
                activity.dismissProgressDialog();
            }
        });
    }


    private void moveToFavorites() {
        if (shouldBeEdited.size() == 0)
            return;
        StringBuilder sbIds = new StringBuilder();
        for (M10 item : shouldBeEdited) {
            sbIds.append(item.id + ",");
        }
        activity.showProgressDialog();
        activity.execute(new Request(Urls.MEMBER_CART_MOVE_FAVORITE).addUrlParam("cartGoodsId", sbIds.toString()), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                activity.dismissProgressDialog();
                if (apiResult.isOk()) {
                    if (shouldBeEdited.size() == data.size()) {
//                        showEmptyPage("购物车还是空的");
                        rightButton.setVisibility(View.GONE);
                        notEmptyView.setVisibility(View.GONE);
                    }
                    data.removeAll(shouldBeEdited);
                    shouldBeEdited.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    activity.toast("移除失败");
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                activity.dismissProgressDialog();
                activity.toast("移除失败");
            }
        });
    }

    private double summary() {
        double sum = 0;
        for (M10 item : shouldBeBuy) {
            sum += item.price * (double) item.number;
        }
        return sum;
    }

    @Override
    protected void onLeftActionPressed() {
        super.onLeftActionPressed();
        if (getActivity() instanceof ShoppingCarActivity)
            getActivity().finish();
    }
}
