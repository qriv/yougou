package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M20;
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
public class PanicListActivity extends ActivityBase {
    private MyAdapter adapter;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic);
        setTitleBar("限时特惠");
        ListView listView = (ListView) findViewById(R.id.list);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
        fetchData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        countDown();
    }

    private void countDown() {
        countDownTimer = new CountDownTimer(1000 * 3600 * 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                adapter.tick();
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    private void fetchData() {
        showProgressDialog();
        execute(new Request(Urls.GOODS_PANIC_BUYING),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        dismissProgressDialog();
                        if (apiResult.isOk()) {
                            List<M20> data = apiResult.getModels(M20.class);
                            if (data != null && data.size() > 0) {
                                List<M20> temp = new ArrayList<M20>();
                                for (M20 m20 : data) {
                                    if (m20 != null)
                                        temp.add(m20);
                                }
                                adapter.addData(temp);
                                countDown();
                            }
                        } else {
                            toast(apiResult.message);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        dismissProgressDialog();
                        toast(e.toString());
                    }
                });
    }

    private class MyAdapter extends BaseAdapter {

        private List<M20> data;

        public MyAdapter() {
            data = new ArrayList<M20>();
        }

        public void addData(List<M20> data) {
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public M20 getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void tick() {
            long now = System.currentTimeMillis();
            List<M20> finish = new ArrayList<M20>();
            for (M20 m20 : data) {
                m20.remain = (m20.endOn - now) / 1000;
                if (m20.remain <= 0)
                    finish.add(m20);
            }
            data.removeAll(finish);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_panic, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.pic);
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.name);
                viewHolder.falsePriceView = (TextView) convertView.findViewById(R.id.false_price);
                viewHolder.falsePriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.realPriceView = (TextView) convertView.findViewById(R.id.real_price);
                viewHolder.peopleCountView = (TextView) convertView.findViewById(R.id.order);
                viewHolder.action = (TextView) convertView.findViewById(R.id.action);
                viewHolder.timer = (TextView) convertView.findViewById(R.id.timer);
//                viewHolder.action.setOnClickListener(onClickListener);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final M20 m20 = getItem(position);
            loadImage(viewHolder.imageView, m20.picturePath);
            viewHolder.nameView.setText(m20.name);
            viewHolder.falsePriceView.setText(String.format("原价￥%.2f", m20.orginPrice));
            viewHolder.realPriceView.setText(String.format("￥%.2f", m20.price));
            viewHolder.peopleCountView.setText(String.format("已有%d人抢购", m20.saleCount));
            if (m20.time > 0) {
                viewHolder.action.setText("抢光了");
                viewHolder.action.setEnabled(false);
            } else {
                viewHolder.action.setEnabled(true);
                viewHolder.action.setText("马上抢");
                viewHolder.action.setTag(m20);
            }
            updateTimer(viewHolder.timer, m20);
            return convertView;
        }

        private void updateTimer(TextView timer, M20 m20) {
            if (m20.time > 0) {
                timer.setText(String.format("%1$d秒抢光%2$d件", m20.time, m20.saleCount));
            } else {
                int hour = (int) (m20.remain / 3600);
                int minute = (int) (m20.remain % 3600 / 60);
                int second = (int) (m20.remain % 60);
                String text = getString(R.string.count_down_timer, hour, minute, second);
                timer.setText(Html.fromHtml(text));
            }
        }
    }

    private class ViewHolder {
        ImageView imageView;
        TextView nameView;
        TextView falsePriceView;
        TextView realPriceView;
        TextView peopleCountView;
        TextView timer;
        TextView action;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.action == v.getId()) {
                buy((M20) v.getTag());
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            detail(adapter.getItem(position));
        }
    };

    private void buy(M20 m20) {
        toast("buy: " + m20.goodsId);
    }

    private void detail(M20 m20) {
        Intent intent = new Intent(this, GoodsDetailActivity.class);
        intent.putExtra(String.class.getName(), "id");
        intent.putExtra("id", m20.id + "");
        startActivity(intent);
    }

}
