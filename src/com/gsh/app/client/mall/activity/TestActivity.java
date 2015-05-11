package com.gsh.app.client.mall.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.R;
import com.litesuits.android.log.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class TestActivity extends ActivityBase {

    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        List<Item> items = new ArrayList<Item>();
        items.add(new Item(Constant.TestData.pics[0], "apple"));
        items.add(new Item(Constant.TestData.pics[1], "banana"));
        items.add(new Item(Constant.TestData.pics[2], "cherry"));
        items.add(new Item(Constant.TestData.pics[3], "dumpling"));
        items.add(new Item(Constant.TestData.pics[4], "eggplant"));
        items.add(new Item(Constant.TestData.pics[5], "fruit"));
        items.add(new Item(Constant.TestData.pics[6], "google"));
        items.add(new Item(Constant.TestData.pics[7], "hello"));
        adapter = new MyAdapter(items);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    private class Item implements Serializable {
        public int iconRid;
        public String text;

        public Item(int iconRid, String text) {
            this.iconRid = iconRid;
            this.text = text;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public List<Item> data;

        public MyAdapter(List<Item> data) {
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            if (viewType == 0) {
                return new ViewHolderB(getLayoutInflater().inflate(R.layout.item_text, viewGroup, false));
            } else {
                return new ViewHolderA(getLayoutInflater().inflate(R.layout.item_test, viewGroup, false));
            }

//            return new ViewHolderA(getLayoutInflater().inflate(R.layout.item_test, viewGroup, false));
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == getItemCount() - 1) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            Log.d("test", "position: " + position);
            if (position == 0 || position == getItemCount() - 1) {
                ViewHolderB viewHolderB = (ViewHolderB) viewHolder;
                if (position == 0) {
                    viewHolderB.textView.setText("Header");
                    viewHolderB.textView.setTextColor(Color.parseColor("#00ff00"));
                } else if (position == getItemCount() - 1) {
                    viewHolderB.textView.setText("End");
                    viewHolderB.textView.setTextColor(Color.parseColor("#00ffff"));
                }
            } else {
                ViewHolderA viewHolderA = (ViewHolderA) viewHolder;
                Item item = data.get(position - 1);
                viewHolderA.iconView.setImageResource(item.iconRid);
                viewHolderA.textView.setText(item.text);
            }
/*
            ViewHolderA viewHolderA = (ViewHolderA) viewHolder;
            Item item = data.get(position);
            Log.d("test", "position: " + position + " : " + item.text);
            viewHolderA.iconView.setImageResource(item.iconRid);
            viewHolderA.textView.setText(item.text);*/
        }


        @Override
        public int getItemCount() {
            return data.size() + 2;
        }

        public class ViewHolderA extends RecyclerView.ViewHolder {
            // each data item is just a string in this case

            public ImageView iconView;
            public TextView textView;

            public ViewHolderA(View v) {
                super(v);
                iconView = (ImageView) v.findViewById(R.id.icon);
                textView = (TextView) v.findViewById(R.id.text);
            }
        }

        public class ViewHolderB extends RecyclerView.ViewHolder {
            // each data item is just a string in this case

            public TextView textView;

            public ViewHolderB(View v) {
                super(v);
                textView = (TextView) v.findViewById(R.id.text);
            }
        }

    }
}
