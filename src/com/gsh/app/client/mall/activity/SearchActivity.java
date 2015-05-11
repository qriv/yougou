package com.gsh.app.client.mall.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M4;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.Injector;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class SearchActivity extends ActivityBase {


    private MyAdapter adapter;
    private String keyword;
    private EditText input;
    private View title_bar_text_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_list);
        Injector.self.inject(this);
        ListView listView = (ListView) findViewById(R.id.list);
        findViewById(R.id.btn_title_back).setOnClickListener(onClickListener);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
        title_bar_text_action = findViewById(R.id.title_bar_text_action);
        title_bar_text_action.setOnClickListener(onClickListener);
        input = (EditText) findViewById(R.id.input);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                }
                return false;
            }
        });
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                title_bar_text_action.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
            }
        });
//        findViewById(R.id.search).setOnClickListener(onClickListener);
    }

    private void fetchData() {
        execute(new Request(Urls.GOODS_SEARCH).addUrlParam("keyword", keyword),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult.isOk()) {
                            List<M4> data = apiResult.getModels(M4.class);
                            if (data != null && data.size() > 0) {
                                hideEmptyPage();
                                title_bar_text_action.setVisibility(View.INVISIBLE);
                                adapter.addData(data);
                            } else {
                                showEmptyPage("没有找到符合条件的商品");
                            }
                        } else {
                            toast(apiResult.message);
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {

                    }
                });
    }


    private class MyAdapter extends BaseAdapter {
        private List<M4> data;

        public MyAdapter() {
            data = new ArrayList<M4>();
        }

        public void addData(List<M4> data) {
            this.data.clear();
            this.data.addAll(data);
            notifyDataSetChanged();
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_cart, null);
                viewHolder = new ViewHolder();
                convertView.findViewById(R.id.selector).setVisibility(View.GONE);
                convertView.findViewById(R.id.status).setVisibility(View.GONE);
                convertView.findViewById(R.id.quality).setVisibility(View.GONE);

                ((LinearLayout) convertView.findViewById(R.id.wrapper)).setGravity(Gravity.CENTER_VERTICAL);

                viewHolder.picView = (ImageView) convertView.findViewById(R.id.pic);
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.title);
                viewHolder.priceView = (TextView) convertView.findViewById(R.id.currentPrice);
                viewHolder.falsePriceView = (TextView) convertView.findViewById(R.id.price);
                viewHolder.falsePriceView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final M4 m4 = getItem(position);
            loadImage(viewHolder.picView, m4.picturePath);
            if (MallApplication.test)
                viewHolder.nameView.setText(m4.name + "_" + m4.id);
            else
                viewHolder.nameView.setText(m4.name);
            viewHolder.priceView.setText(String.format("￥%.2f", m4.price));
            viewHolder.falsePriceView.setText(String.format("￥%.2f", m4.originPrice));
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView picView;
        TextView nameView;
        TextView priceView;
        TextView falsePriceView;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.title_bar_text_action == v.getId()) {
                search();
            } else if (R.id.btn_title_back == v.getId()) {
                onBackPressed();
            }
        }
    };

    private void search() {
        String temp = input.getText().toString();
        if (!TextUtils.isEmpty(temp)) {
            title_bar_text_action.requestFocus();
            keyword = temp;
            hideKeyboard();
            fetchData();
        } else {
            toast("输入商品名称");
        }
    }


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            detail(adapter.getItem(position));
        }
    };

    private void detail(M4 m4) {
        go.name(GoodsDetailActivity.class).param(String.class.getName(), "id").param("id", m4.id + "").go();
        /*Intent intent = new Intent(this, GoodDetailActivity.class);
        intent.putExtra(String.class.getName(), "id");
        intent.putExtra("id", m4.id + "");
        startActivity(intent);*/
    }
}
