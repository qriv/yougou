package com.gsh.app.ugou.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.ActivityBase;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.gsh.app.ugou.https.model.ComboVote;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by taosj on 15/4/28.
 */
public class BasketCommentListActivity extends ActivityBase {

    private RecyclerView list;
    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setTitleBar(false, "用户评论");
        list = (RecyclerView) findViewById(R.id.list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter(new ArrayList<ComboVote>(), this);
        list.setAdapter(adapter);

        showProgressDialog();
        execute(new Request(Urls.COMBO_COMMENT_LIST).addUrlParam("comboId", getIntent().getStringExtra("comboId")), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                if (apiResult.isOk()) {
                    dismissProgressDialog();
                    adapter.setData(apiResult.getModels(ComboVote.class));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                dismissProgressDialog();
            }
        });

    }


    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        private final List<ComboVote> mDataset = new ArrayList<ComboVote>();
        private ActivityBase activityBase;

        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        public RecyclerAdapter(List<ComboVote> votes, ActivityBase activityBase) {

            this.activityBase = activityBase;
            mDataset.addAll(votes);
        }

        public void setData(List<ComboVote> votes) {
            mDataset.clear();
            mDataset.addAll(votes);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView avatar, star1, star2, star3, star4, star5;
            public TextView name, commentOn, content;

            public ViewHolder(View itemView) {
                super(itemView);
                avatar = (ImageView) itemView.findViewById(R.id.avatar);
                star1 = (ImageView) itemView.findViewById(R.id.star1);
                star2 = (ImageView) itemView.findViewById(R.id.star2);
                star3 = (ImageView) itemView.findViewById(R.id.star3);
                star4 = (ImageView) itemView.findViewById(R.id.star4);
                star5 = (ImageView) itemView.findViewById(R.id.star5);
                name = (TextView) itemView.findViewById(R.id.name);
                commentOn = (TextView) itemView.findViewById(R.id.commentOn);
                content = (TextView) itemView.findViewById(R.id.content);
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ComboVote vote = mDataset.get(position);
            activityBase.loadAvatar(holder.avatar, vote.avatarPath);
            holder.name.setText(vote.voterName);
            holder.commentOn.setText(sdf.format(new Date(vote.votingTime)));
            holder.content.setText(vote.content);

            if (vote.star >= 1) {
                holder.star1.setImageResource(R.drawable.ui_star_solid);
            } else {
                holder.star1.setImageResource(R.drawable.ui_star_stroke);
            }

            if (vote.star >= 2) {
                holder.star2.setImageResource(R.drawable.ui_star_solid);
            } else {
                holder.star2.setImageResource(R.drawable.ui_star_stroke);
            }

            if (vote.star >= 3) {
                holder.star3.setImageResource(R.drawable.ui_star_solid);
            } else {
                holder.star3.setImageResource(R.drawable.ui_star_stroke);
            }

            if (vote.star >= 4) {
                holder.star4.setImageResource(R.drawable.ui_star_solid);
            } else {
                holder.star4.setImageResource(R.drawable.ui_star_stroke);
            }

            if (vote.star >= 5) {
                holder.star5.setImageResource(R.drawable.ui_star_solid);
            } else {
                holder.star5.setImageResource(R.drawable.ui_star_stroke);
            }
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(),
                    R.layout.item_combo_comment, null);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }
    }
}
