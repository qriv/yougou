package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.*;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.*;

/**
 * *@author Tan Chunmao
 */
public class OrderListActivity extends PayActivity {

    @InjectView
    private ListView list;
    private MyAdapter adapter;
    private Random random = new Random();
    private int currentTabId;


    public static enum OrderStatus {
        toPay("待付款"), //待付款
        toShip("待收货"), //待发货
        toReceive("待收货"),//待收货
        done("交易成功"),//交易完成
        returnProcess("退货中"), //退货中
        returnProcessed("交易成功"), //退货已处理
        closed("交易关闭");//交易关闭
        public String display;

        private OrderStatus(String display) {
            this.display = display;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filter = getIntent().getStringExtra(String.class.getName());
        setContentView(R.layout.activity_my_order);
        setTitleBar("我的订单");
        Injector.self.inject(this);
        list.setVerticalScrollBarEnabled(false);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener(onItemClickListener);
        View all = findViewById(R.id.all);
        all.setOnClickListener(onClickListener);
        View toPay = findViewById(R.id.to_pay);
        toPay.setOnClickListener(onClickListener);
        View toReceive = findViewById(R.id.to_receive);
        toReceive.setOnClickListener(onClickListener);
        View toComment = findViewById(R.id.to_comment);
        toComment.setOnClickListener(onClickListener);

        currentTabId = -1;
        if (filter.equals("all"))
            all.performClick();
        else if (filter.equals("to_pay"))
            toPay.performClick();
        else if (filter.equals("to_receive"))
            toReceive.performClick();
        else if (filter.equals("to_comment"))
            toComment.performClick();
//        adapter.addData(prepareData(), true);
    }

    private void changeTab(int id) {
        if (id != currentTabId) {
            currentTabId = id;
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            map.put(R.id.all, R.id.all_indicator);
            map.put(R.id.to_pay, R.id.to_pay_indicator);
            map.put(R.id.to_receive, R.id.to_receive_indicator);
            map.put(R.id.to_comment, R.id.to_comment_indicator);

            int[] indicators = {R.id.all, R.id.to_pay, R.id.to_receive, R.id.to_comment};
            for (int indicator : indicators) {
                if (id == indicator)
                    findViewById(map.get(indicator)).setVisibility(View.VISIBLE);
                else
                    findViewById(map.get(indicator)).setVisibility(View.INVISIBLE);
            }
            if (R.id.all == id)
                filter = "all";
            else if (R.id.to_pay == id)
                filter = "to_pay";
            else if (R.id.to_receive == id)
                filter = "to_receive";
            else if (R.id.to_comment == id)
                filter = "to_comment";
            fetchData();
        }
    }

    private String filter;

    private void fetchData() {
        showProgressDialog();
        adapter.addData(new ArrayList<M19>(), true);
        execute(new Request(Urls.ORDER_LIST).addUrlParam("type", filter),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult.isOk()) {
                            List<M19> data = apiResult.getModels(M19.class);
                            adapter.addData(data, true);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        dismissProgressDialog();
//                        prepareData();
                    }
                }
        );
    }

    long idSeed = System.currentTimeMillis();

    private List<M19> prepareData() {
        final List<M19> data = new ArrayList<M19>();

        int i = 0;
        while (data.size() < OrderStatus.values().length) {
            List<M10> goods = new ArrayList<M10>();
            goods.add(produceGood());
            goods.add(produceGood());
            M27 m27 = produceWorker();
            String status = OrderStatus.values()[i].name();
            if (filter.equals("to_pay"))
                status = OrderStatus.toPay.name();
            else if (filter.equals("to_receive"))
                status = OrderStatus.toReceive.name();
            else if (filter.equals("to_comment"))
                status = OrderStatus.done.name();
            i++;
            idSeed++;
            M19 m19 = new M19(idSeed, String.valueOf(idSeed), status, goods, m27);
            m19.address = prodduceAddress();
            m19.total_price = 100;
            m19.coupon = 20;
            m19.score = 10;
            m19.pay_price = m19.total_price - m19.coupon - m19.score;
            data.add(m19);
        }
        return data;
    }

    private M18 prodduceAddress() {
        M18 m18 = new M18();
        m18.name = "张三";
        m18.mobile = "18012345678";
        m18.address = "朝阳小区5栋3单元1201";
        return m18;
    }

    private M27 produceWorker() {
        M27 m27 = new M27();
        m27.id = idSeed++;
        m27.name = Constant.TestData.people_names[random.nextInt(Constant.TestData.people_names.length)];
        m27.no = String.valueOf(idSeed);
        m27.mobile = "18012345678";
        boolean male = random.nextBoolean();
        m27.avatarPath = male ? Constant.TestData.avatar_male : Constant.TestData.avatar_female;
        m27.sex = male ? "男" : "女";
        return m27;
    }

    private M10 produceGood() {
        M10 m10 = new M10();
        m10.name = Constant.TestData.fruit_names[random.nextInt(Constant.TestData.fruit_names.length)];
        m10.picturePath = Constant.TestData.fruit_pictures[random.nextInt(Constant.TestData.fruit_pictures.length)];
        m10.price = random.nextFloat() * 90 + 10;
        m10.description = "4斤";
        m10.number = 1;
        return m10;
    }

    private class MyAdapter extends BaseAdapter {
        List<M19> data;

        public MyAdapter() {
            data = new ArrayList<M19>();
        }

        public void addData(List<M19> data, boolean refresh) {
            if (refresh)
                this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public M19 getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void remove(M19 m19) {
            data.remove(m19);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_my_order, null);
                viewHolder = new ViewHolder();
                viewHolder.statusView = (TextView) convertView.findViewById(R.id.status);
                viewHolder.dealtIcon = convertView.findViewById(R.id.icon_dealt);
                viewHolder.moneyView = (TextView) convertView.findViewById(R.id.money);
                viewHolder.buttonA = (TextView) convertView.findViewById(R.id.button_a);
                viewHolder.buttonA.setOnClickListener(onClickListener);
                viewHolder.deleteView = convertView.findViewById(R.id.delete);
                viewHolder.deleteView.setOnClickListener(onClickListener);
                viewHolder.deleteIndicator = convertView.findViewById(R.id.delete_indicator);
                viewHolder.goodsView = (LinearLayout) convertView.findViewById(R.id.goods);
                viewHolder.buttonB = (TextView) convertView.findViewById(R.id.button_b);
                viewHolder.buttonB.setOnClickListener(onClickListener);
                viewHolder.actionDivider = convertView.findViewById(R.id.action_divider);
                viewHolder.actionLayout = convertView.findViewById(R.id.action);
                viewHolder.goodsCountView = (TextView) convertView.findViewById(R.id.goods_count);
                viewHolder.orderNoView = (TextView) convertView.findViewById(R.id.orderNo);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            final M19 orderCommand = getItem(position);
            viewHolder.buttonA.setTag(orderCommand);
            viewHolder.buttonB.setTag(orderCommand);
            viewHolder.deleteView.setTag(orderCommand);
            viewHolder.orderNoView.setText(String.format("订单号：%s", orderCommand.orderNo));
            setStatus(viewHolder, orderCommand);
            fillGoods(viewHolder, orderCommand);
            return convertView;
        }

        private void setStatus(ViewHolder viewHolder, M19 orderCommand) {

            final OrderStatus orderStatus = OrderStatus.valueOf(orderCommand.status);
            String status = orderStatus.display;
            if (MallApplication.test) {
                status = status + "_" + orderStatus;
            }
            viewHolder.statusView.setText(status);
            viewHolder.actionDivider.setVisibility(orderStatus != OrderStatus.returnProcess ? View.VISIBLE : View.GONE);
            viewHolder.actionLayout.setVisibility(orderStatus != OrderStatus.returnProcess ? View.VISIBLE : View.GONE);
            if (OrderStatus.toPay == orderStatus) {
                viewHolder.buttonA.setVisibility(View.VISIBLE);
                viewHolder.buttonA.setText("立即支付");
                viewHolder.buttonB.setVisibility(View.VISIBLE);
                viewHolder.buttonB.setText("取消订单");
            } else if (OrderStatus.toReceive == orderStatus || OrderStatus.toShip == orderStatus) {
                viewHolder.buttonA.setVisibility(View.VISIBLE);
                viewHolder.buttonA.setText("再次购买");
                viewHolder.buttonB.setVisibility(View.GONE);
            } else if (OrderStatus.done == orderStatus) {
                orderCommand.commentPermitted = true;
                orderCommand.deletePermitted = true;
                viewHolder.buttonA.setVisibility(View.VISIBLE);
                viewHolder.buttonA.setText("再次购买");
                viewHolder.buttonB.setVisibility(View.VISIBLE);
                viewHolder.buttonB.setText("申请退货");
            } else if (OrderStatus.returnProcess == orderStatus) {

            } else if (OrderStatus.returnProcessed == orderStatus) {
                orderCommand.commentPermitted = true;
                orderCommand.deletePermitted = true;
                viewHolder.buttonA.setVisibility(View.VISIBLE);
                viewHolder.buttonA.setText("再次购买");
                viewHolder.buttonB.setVisibility(View.GONE);
            } else if (OrderStatus.closed == orderStatus) {
                orderCommand.deletePermitted = true;
                viewHolder.buttonA.setVisibility(View.VISIBLE);
                viewHolder.buttonA.setText("再次购买");
                viewHolder.buttonB.setVisibility(View.GONE);
            }

            viewHolder.deleteView.setVisibility(orderCommand.deletePermitted ? View.VISIBLE : View.GONE);
            viewHolder.deleteIndicator.setVisibility(orderCommand.deletePermitted ? View.VISIBLE : View.GONE);
        }

        private void fillGoods(ViewHolder viewHolder, M19 order) {
            List<M10> goods = order.goods;
            LinearLayout layout = viewHolder.goodsView;
            layout.removeAllViews();
            ViewGroup.LayoutParams verticalGapLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.ui_size_one_dp));
            View gap = null;
            double totalMoney = 0;
            for (M10 m10 : goods) {
                totalMoney += m10.price;
                View convertView = getLayoutInflater().inflate(R.layout.item_order_goods_comment, null);
                loadImage((ImageView) convertView.findViewById(R.id.pic), m10.picturePath);
                String name = m10.name;
                if (MallApplication.test) {
                    name = name + "_" + m10.id;
                }
                ((TextView) convertView.findViewById(R.id.title)).setText(name);
                convertView.findViewById(R.id.currentPrice).setVisibility(View.GONE);
                convertView.findViewById(R.id.quality).setVisibility(View.GONE);
                convertView.findViewById(R.id.status).setVisibility(View.GONE);
                View commentView = convertView.findViewById(R.id.comment);
                commentView.setVisibility(order.commentPermitted && !m10.commented ? View.VISIBLE : View.GONE);
                commentView.setTag(R.id.tag_key_first, m10);
                commentView.setTag(R.id.tag_key_second, String.valueOf(order.id));
                commentView.setOnClickListener(onClickListener);
                /*((TextView) convertView.findViewById(R.id.currentPrice)).setText(String.format("￥%.2f", m10.price));
                ((TextView) convertView.findViewById(R.id.quality)).setText(String.format("X%d", m10.number));
                ((TextView) convertView.findViewById(R.id.status)).setText(m10.description);*/
                if (gap != null)
                    layout.addView(gap);
                layout.addView(convertView);
                gap = new View(OrderListActivity.this);
                gap.setLayoutParams(verticalGapLayoutParams);
                gap.setBackgroundResource(R.color.ui_divider_bg);
            }
            viewHolder.goodsCountView.setText(String.format("共有%d件商品", goods.size()));
            viewHolder.moneyView.setText(String.format("实付款:￥%.2f", totalMoney));
        }
    }

    class ViewHolder {
        TextView statusView;
        TextView moneyView;
        LinearLayout goodsView;
        View deleteView;
        View deleteIndicator;
        View dealtIcon;
        TextView buttonA;
        TextView buttonB;
        View actionDivider;
        View actionLayout;
        TextView goodsCountView;
        TextView orderNoView;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.comment == v.getId()) {
                M10 goods = (M10) v.getTag(R.id.tag_key_first);
                String orderId = (String) v.getTag(R.id.tag_key_second);
                comment(goods, orderId);
            } else {
                final M19 orderCommand = (M19) v.getTag();
                if (R.id.delete == v.getId()) {
                    delete(orderCommand);
                } else if (R.id.all == v.getId() || R.id.to_pay == v.getId() || R.id.to_receive == v.getId() || R.id.to_comment == v.getId()) {
                    changeTab(v.getId());
                } else if (R.id.button_a == v.getId()) {
                    OrderStatus orderStatus = OrderStatus.valueOf(orderCommand.status);
                    if (orderStatus == OrderStatus.toPay) {
                        pay(orderCommand);
                    } else {
                        buyAgain(orderCommand);
                    }
                } else if (R.id.button_b == v.getId()) {
                    OrderStatus orderStatus = OrderStatus.valueOf(orderCommand.status);
                    if (orderStatus == OrderStatus.toPay) {
                        cancelOrder(orderCommand);
                    } else {
                        returnOrder(orderCommand);
                    }
                }
            }
        }
    };

    private void delete(final M19 orderCommand) {
        notice(new CallBack() {
            @Override
            public void call() {
                showProgressDialog();
                execute(new Request(Urls.MEMBER_ORDER_DELETE).addUrlParam("orderId", String.valueOf(orderCommand.id)),
                        new HttpResultHandler() {
                            @Override
                            protected void onSuccess(ApiResult apiResult) {
                                dismissProgressDialog();
                                if (apiResult.isOk()) {
                                    adapter.remove(orderCommand);
                                }
                            }

                            @Override
                            protected void onFailure(Response res, HttpException e) {
                                super.onFailure(res, e);
                                dismissProgressDialog();
                            }
                        });
            }
        }, "确定删除当前订单？", "删除后将无法恢复");
    }

    private void pay(final M19 orderCommand) {
        execute(new Request(Urls.MEMBER_WALLET_REMAIN_SUM),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult.isOk()) {
                            M16 m16 = apiResult.getModel(M16.class);
                            choosePayMethod(orderCommand.payNo, orderCommand.pay_price, m16.remain);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);

                    }
                }
        );
    }

    private void buyAgain(M19 m19) {
        showProgressDialog();
        execute(new Request(Urls.ORDER_REBUY).addUrlParam("orderId", String.valueOf(m19.id)),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult.isOk()) {
                            M11 m11 = apiResult.getModel(M11.class);
                            go.name(OrderConfirmActivity.class).param(M11.class.getName(), m11).go();
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        dismissProgressDialog();
                    }
                }
        );
    }

    private void cancelOrder(final M19 m19) {
        notice(new CallBack() {
            @Override
            public void call() {
                showProgressDialog();
                execute(new Request(Urls.ORDER_CANCEL).addUrlParam("orderId", String.valueOf(m19.id)),
                        new HttpResultHandler() {
                            @Override
                            protected void onSuccess(ApiResult apiResult) {
                                dismissProgressDialog();
                                if (apiResult.isOk()) {
                                    toast("订单取消成功");
                                    fetchData();
                                }
                            }

                            @Override
                            protected void onFailure(Response res, HttpException e) {
                                super.onFailure(res, e);
                                dismissProgressDialog();
                            }
                        }
                );
            }
        }, "确定取消当前订单？");
    }

    private void returnOrder(final M19 m19) {

    }

    private void comment(M10 goods, String orderId) {
        go.name(CommentActivity.class).param(M10.class.getName(), goods).param("orderId", orderId).go();
    }

    private static final int REQUEST_DETAIL = 2038;
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            M19 m19 = adapter.getItem(position);
            go.name(OrderDetailActivity.class).param(M19.class.getName(), m19).goForResult(REQUEST_DETAIL);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_DETAIL) {
                fetchData();
            }
        }
    }

    @Override
    protected void warnNotPay() {
        notice(new CallBack() {
            @Override
            public void call() {
                payDialog.dismiss();
            }
        }, "是否放弃付款？", null, false);
    }

    protected void payByMolinSuccess() {
        fetchData();
    }

    protected void payByAlipaySuccess() {
        super.payByAlipaySuccess();
        fetchData();
    }
}
