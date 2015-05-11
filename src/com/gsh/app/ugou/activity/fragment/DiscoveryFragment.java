package com.gsh.app.ugou.activity.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.fragment.FragmentBase;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.gsh.app.ugou.activity.DiscoveryDetailsActivity;
import com.gsh.app.ugou.https.model.Discovery;
import com.litesuits.android.widget.ViewHolder;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taosj on 15/4/15.
 */
public class DiscoveryFragment extends FragmentBase {

    private ListView list;
    private static final int REQUEST_DETAIL = 2038;


    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private MyAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
            return layout;
        }
        layout = inflater.inflate(R.layout.ugo_fragment_discovery, container, false);
        list = (ListView) layout.findViewById(R.id.list);
        list.setOnItemClickListener(onItemClickListener);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        fetchData();
        return layout;
    }

    @Override
    public void refresh() {

    }

    private void fetchData() {
        activity.showProgressDialog();
        activity.execute(new Request(Urls.DISCOVERY_LIST), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                activity.dismissProgressDialog();
                if (apiResult != null && apiResult.isOk()) {
                    hideErrorPage();
                    List<Discovery> list = apiResult.getModels(Discovery.class);
                    adapter.setData(list);
                } else {
                    onRequestError();
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                activity.dismissProgressDialog();
                onRequestError();
            }
        });
    }


    public void onErrorPageClick() {
        fetchData();
    }

    private class MyAdapter extends BaseAdapter {

        private List<Discovery> data;
        private int typeAHeight;

        public MyAdapter() {
            data = new ArrayList<Discovery>();
            int width = activity.getWindowManager().getDefaultDisplay().getWidth();
            typeAHeight = (width - 2 * getResources().getDimensionPixelOffset(R.dimen.ui_margin_d)) * 9 / 16;
        }

        public int getCount() {
//            return discovries.size();
            return data.size();
        }

        public Discovery getItem(int position) {
            return data.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_discovery, null);


            Discovery discovery = getItem(position);

            ImageView pic = ViewHolder.get(convertView, R.id.pic);
            pic.getLayoutParams().height = typeAHeight;
            activity.loadLongImage(pic, discovery.mainPicturePath);
//            pic.setImageResource(R.drawable.sample_exp);
            TextView name = ViewHolder.get(convertView, R.id.name);
            TextView desc = ViewHolder.get(convertView, R.id.desc);
            desc.setText(discovery.summary);
            name.setText(discovery.title);
            TextView fee = ViewHolder.get(convertView, R.id.fee);
            fee.setText(discovery.fee);
            TextView date = ViewHolder.get(convertView, R.id.date);
            date.setText(sdf.format(discovery.createdDate));
            TextView likeCount = ViewHolder.get(convertView, R.id.likeCount);
            likeCount.setText(discovery.likeCount + "");
            TextView reviewCount = ViewHolder.get(convertView, R.id.reviewCount);
            reviewCount.setText(discovery.reviewCount + "");
            return convertView;
        }

        public void setData(List<Discovery> list) {
            data.clear();
            data.addAll(list);
            notifyDataSetChanged();
        }

        public void updateItem(Discovery discovery) {
            for (Discovery temp : data) {
                if (temp.id == discovery.id) {
                    temp.reviewCount = discovery.reviewCount;
                    temp.likeCount = discovery.likeCount;
                    notifyDataSetChanged();
                    break;
                }
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_DETAIL) {
                Discovery discovery = (Discovery) data.getSerializableExtra(Discovery.class.getName());
                adapter.updateItem(discovery);
            }
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String discoveryId = String.valueOf(adapter.getItem(position).id);
            Intent intent = new Intent(activity, DiscoveryDetailsActivity.class);
            intent.putExtra(String.class.getName(), discoveryId);
            startActivityForResult(intent, REQUEST_DETAIL);
        }
    };
}
