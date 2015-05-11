package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.model.M2;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.common.utils.CMUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taosj on 15/3/12.
 */
public class AddressDialogActivity extends ActivityBase {

    @InjectView
    private ListView list;
    private boolean isModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_setting);
        setTitleBar("选择社区");
        Injector.self.inject(this);
        list.setVerticalScrollBarEnabled(false);
        ArrayList<M2> communities = (ArrayList<M2>) getIntent().getSerializableExtra("data");
        data.addAll(communities);
        list.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (!isModify) {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        } else
            super.onBackPressed();
    }

    private final List<M2> data = CMUtil.getArrayList();
    private final BaseAdapter adapter = new BaseAdapter() {
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_community_setting, null);
                viewHolder = new ViewHolder();
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.name);
                viewHolder.addressView = (TextView) convertView.findViewById(R.id.community_address);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final M2 m2 = data.get(position);
            viewHolder.nameView.setText(m2.name);
            viewHolder.addressView.setText(m2.address);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_OK, getIntent().putExtra("selectedItem", data.get(position)));
                    isModify = true;
                    onBackPressed();
                }
            });
            return convertView;
        }
    };

    private class MyAdapter extends BaseAdapter {
        private List<M2> data;

        public MyAdapter() {
            data = new ArrayList<M2>();
        }

        public void addData(List<M2> data) {
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public M2 getItem(int position) {
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
                convertView = getLayoutInflater().inflate(R.layout.item_community_setting, null);
                viewHolder = new ViewHolder();
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.name);
                viewHolder.addressView = (TextView) convertView.findViewById(R.id.community_address);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final M2 m2 = getItem(position);
            viewHolder.nameView.setText(m2.name);
            viewHolder.addressView.setText(m2.address);
            return convertView;
        }
    }

    private class ViewHolder {
        TextView nameView;
        TextView addressView;
    }

    @Override
    protected void onRightActionPressed() {
        go.name(MainActivity.class).goAndFinishCurrent();
    }

}
