package com.gsh.app.ugou.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.ActivityBase;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.gsh.app.ugou.https.model.ComboOrderDelivery;
import com.gsh.app.ugou.https.model.ComboOrderDetails;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.android.widget.ViewHolder;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by taosj on 15/4/14.
 */
public class TimeLineDetailActivity extends ActivityBase {

    @InjectView
    private ListView list;

    private View header;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    @InjectView
    private TextView name, mobile, address;//address
    private ComboOrderDetails comboOrderDetails;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_detail);
        setTitleBar("套餐配送信息", "评价");
        hideView(findViewById(R.id.title_bar_text_action), true);
        Injector.self.inject(this);
        list.setVerticalScrollBarEnabled(false);
        header = getLayoutInflater().inflate(R.layout.timeline_detail_header_2, null);


        list.addHeaderView(header);
        list.addFooterView(getLayoutInflater().inflate(R.layout.item_timeline_end_node, null));
        adapter = new MyAdapter();
        list.setAdapter(adapter);
//        list.setOnItemClickListener(onItemClickListener);

        if (getIntent().getLongExtra("id", -1) != -1) {
            showProgressDialog();
            execute(new Request(Urls.COMBO_ORDER_DETAILS).addUrlParam("orderId", getIntent().getLongExtra("id", -1) + ""), new HttpResultHandler() {
                @Override
                protected void onSuccess(ApiResult apiResult) {
                    dismissProgressDialog();
                    comboOrderDetails = apiResult.getModel(ComboOrderDetails.class);
                    hideView(findViewById(R.id.title_bar_text_action), comboOrderDetails.isVote);
                    adapter.setData(comboOrderDetails.delivery);
                    fillPackage();
                    updateAddress();
                 /*   name.setText(m47.combo.name);
                    desc.setText(m47.combo.desc);
                    loadImage(pic, m47.combo.picturePath);
                    addrname.setText(m47.address.communityName);
                    addrdesc.setText(m47.address.addressDetail);
                    nodes.addAll(m47.nodes);*/
                }

                @Override
                protected void onFailure(Response res, HttpException e) {
                    dismissProgressDialog();
                }
            });
        }
    }

    private void fillPackage() {
        View container = findViewById(R.id.basket);
        container.setOnClickListener(onClickListener);
        loadImage((ImageView) container.findViewById(R.id.icon), comboOrderDetails.mainPicturePath);
        ((TextView) container.findViewById(R.id.name)).setText(comboOrderDetails.comboName);
        ((TextView) container.findViewById(R.id.money)).setText(String.format("%d", (int) comboOrderDetails.price));
    }

  /*  private void fillPackage() {
        loadImage((ImageView) findViewById(R.id.icon), comboOrderDetails.mainPicturePath);
//        ((ImageView) header.findViewById(R.id.icon)).setImageResource(R.drawable.basket_sample_16);
        ((TextView) header.findViewById(R.id.package_name)).setText(comboOrderDetails.comboName);
//        ((TextView) findViewById(R.id.desc)).setText(m42.desc);
        ((TextView) header.findViewById(R.id.money)).setText((int) comboOrderDetails.price + "元");
    }
*/

    private void updateAddress() {
//        M18 m18 = m50.address;
        ((TextView) header.findViewById(R.id.name)).setText(comboOrderDetails.receiverName);
        ((TextView) header.findViewById(R.id.mobile)).setText(comboOrderDetails.mobile);
        ((TextView) header.findViewById(R.id.address)).setText(comboOrderDetails.address);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            go.name(BasketOrderDetailActivity.class).go();
        }
    };

    private static final int REQUEST_COMMENT = 2039;

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        go.name(BasketCommentActivity.class).param(ComboOrderDetails.class.getName(), comboOrderDetails).goForResult(REQUEST_COMMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_COMMENT) {
                hideView(findViewById(R.id.title_bar_text_action), true);
            }
        }
    }

    private class MyAdapter extends BaseAdapter {
        private List<ComboOrderDelivery> data;

        public MyAdapter() {
            data = new ArrayList<ComboOrderDelivery>();
        }

        public int getCount() {

            return data.size();
        }

        public void setData(List<ComboOrderDelivery> data) {
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        public ComboOrderDelivery getItem(int position) {
            return data.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.item_timeline_node, null);
            ComboOrderDelivery comboOrderDelivery = getItem(position);
            View order = ViewHolder.get(convertView, R.id.order);
            TextView orderNumber = ViewHolder.get(convertView, R.id.orderNumber);
            TextView orderDate = ViewHolder.get(convertView, R.id.orderDate);
            TextView orderNo = ViewHolder.get(convertView, R.id.orderNo);
            TextView status = ViewHolder.get(convertView, R.id.status);
            TextView action = ViewHolder.get(convertView, R.id.action);
            action.setOnClickListener(onClickListener);
            action.setTag(comboOrderDelivery);

            int yellow = Color.parseColor("#e8a433");
            int green = Color.parseColor("#19bc28");
            int gray = getResources().getColor(R.color.ui_font_a);
            int color = -1;
            if (!comboOrderDelivery.signAvailable()) {
                action.setVisibility(View.INVISIBLE);
                convertView.setBackgroundColor(android.graphics.Color.parseColor("#fdfdfd"));
                if (comboOrderDelivery.deliveryState.equalsIgnoreCase("wait")) {
                    status.setText("未配送");
                } else if (comboOrderDelivery.deliveryState.equalsIgnoreCase("over")) {
                    status.setText("已签收");
                }

            } else {
                action.setVisibility(View.VISIBLE);
                status.setText("待签收");
                convertView.setBackgroundColor(android.graphics.Color.parseColor("#ffffff"));
            }


            orderNo.setText(comboOrderDelivery.sn + "");
            orderNumber.setText((data.indexOf(comboOrderDelivery)+1) + "");
            orderDate.setText(sdf.format(new Date(comboOrderDelivery.sendTime)).toString());
            setColor(gray, convertView);
            return convertView;
        }

        private void setColor(int color, View convertView) {
            GradientDrawable gradientDrawable = (GradientDrawable) ViewHolder.get(convertView, R.id.order).getBackground();
            gradientDrawable.setStroke(1, color);
            ((TextView) ViewHolder.get(convertView, R.id.orderNumber)).setTextColor(color);
            ((TextView) ViewHolder.get(convertView, R.id.orderNo)).setTextColor(color);
            ((TextView) ViewHolder.get(convertView, R.id.orderDate)).setTextColor(color);
//            ((TextView) ViewHolder.get(convertView, R.id.status)).setTextColor(color);
        }

        public void sign(String deliveryId) {
            for (ComboOrderDelivery c : data) {
                if (c.sn.equals(deliveryId)) {
                    c.deliveryState = "over";
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.action == v.getId()) {
                ComboOrderDelivery comboOrderDelivery = (ComboOrderDelivery) v.getTag();
                sign(comboOrderDelivery.sn);
            } else if (R.id.basket == v.getId()) {
                String basketId = String.valueOf(comboOrderDetails.comboId);
                go.name(BasketDetailActivity.class).param(String.class.getName(), basketId).go();
            }
        }
    };

    private void sign(final String deliveryId) {
        showProgressDialog();
        execute(new Request(Urls.COMBO_ORDER_DELIVERY_SIGN).addUrlParam("deliveryId", deliveryId),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        adapter.sign(deliveryId);
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        dismissProgressDialog();
                    }
                }
        );
    }
}
