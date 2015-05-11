package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M20;
import com.gsh.app.client.mall.https.model.M21;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class ReservationListActivity extends ActivityBase {

    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        setTitleBar("预售");
        ListView listView = (ListView) findViewById(R.id.list);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
        fetchData();
    }

    private void fetchData() {
        showProgressDialog();
        execute(new Request(Urls.GOODS_PRESALE),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult.isOk()) {
                            List<M21> data = apiResult.getModels(M21.class);
                            if (data != null && data.size() > 0) {
                                List<M21> temp = new ArrayList<M21>();
                                for (M21 m20 : data) {
                                    if (m20 != null)
                                        temp.add(m20);
                                }
                                adapter.addData(temp);
                            } else {
                                showEmptyPage("当前还没有预售商品");
                            }
                        } else {
                            toast(apiResult.message);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        dismissProgressDialog();
                    }
                });
    }

    private class MyAdapter extends BaseAdapter {

        private List<M21> data;

        public MyAdapter() {
            data = new ArrayList<M21>();
        }

        public void addData(List<M21> data) {
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public M21 getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_reservation, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.pic);
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.name);
                viewHolder.falsePriceView = (TextView) convertView.findViewById(R.id.false_price);
                viewHolder.falsePriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.realPriceView = (TextView) convertView.findViewById(R.id.real_price);
                viewHolder.peopleCountView = (TextView) convertView.findViewById(R.id.order);
                viewHolder.allCountView = (TextView) convertView.findViewById(R.id.all);
                viewHolder.remainView = (TextView) convertView.findViewById(R.id.remain);
                viewHolder.action = convertView.findViewById(R.id.action);
//                viewHolder.action.setOnClickListener(onClickListener);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final M21 m21 = getItem(position);
            loadImage(viewHolder.imageView, m21.picturePath);
            viewHolder.nameView.setText(m21.name);
            viewHolder.falsePriceView.setText(String.format("市场价￥%.2f", m21.orginPrice));
            viewHolder.realPriceView.setText(String.format("￥%.2f", m21.price));
            viewHolder.peopleCountView.setText(String.format("已有%d人预订", m21.peopleCount));
            viewHolder.allCountView.setText(String.format("预售%d斤", m21.allCount));
            viewHolder.remainView.setText(String.format("仅剩%d斤", m21.remainCount));
            viewHolder.action.setTag(m21);
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView imageView;
        TextView nameView;
        TextView falsePriceView;
        TextView realPriceView;
        TextView peopleCountView;
        TextView allCountView;
        TextView remainView;
        View action;
    }


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            detail(adapter.getItem(position));
        }
    };


    private void detail(M21 m21) {
        Intent intent = new Intent(this, GoodsDetailActivity.class);
        intent.putExtra(String.class.getName(), "id");
        intent.putExtra("id", m21.goodsId + "");
        startActivity(intent);
    }
}
