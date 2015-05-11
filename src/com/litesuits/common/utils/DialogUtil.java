package com.litesuits.common.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.ActivityBase;

public class DialogUtil {

    public static AlertDialog.Builder dialogBuilder(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (msg != null) builder.setMessage(msg);
        if (title != null) builder.setTitle(title);
        return builder;
    }

    public static AlertDialog.Builder dialogBuilder(Context context, String title, String msg, int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (msg != null) builder.setMessage(Html.fromHtml(msg));
        if (title != null) builder.setTitle(title);
        return builder;
    }


    public static AlertDialog.Builder dialogBuilder(Context context, int title, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (view != null) builder.setView(view);
        if (title > 0) builder.setTitle(title);
        return builder;
    }

    public static AlertDialog.Builder dialogBuilder(Context context, int titleResId, int msgResId) {
        String title = titleResId > 0 ? context.getResources().getString(titleResId) : null;
        String msg = msgResId > 0 ? context.getResources().getString(msgResId) : null;
        return dialogBuilder(context, title, msg);
    }

    public static Dialog showTips(Context context, String title, String des) {
        AlertDialog.Builder builder = DialogUtil.dialogBuilder(context, title, des);
        builder.setCancelable(true);
        Dialog dialog = builder.show();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog showTips(Context context, int title, int des) {
        return showTips(context, context.getString(title), context.getString(des));
    }

    public static class ProgressDialog {
        private android.app.Dialog dialog;
        private AnimationDrawable animationDrawable;
        private ActivityBase activity;

        public ProgressDialog(android.app.Dialog dialog, AnimationDrawable animationDrawable, ActivityBase activity) {
            this.dialog = dialog;
            this.animationDrawable = animationDrawable;
            this.activity = activity;
        }

        public void show() {
            if (!dialog.isShowing() && activity != null && !activity.isFinishing()) {
                dialog.show();
                animationDrawable.start();
            }
        }

        public void dismiss() {
            if (dialog.isShowing() && activity != null && !activity.isFinishing()) {
                dialog.dismiss();
                animationDrawable.stop();
            }
        }

        public void setCancellable(boolean cancellable) {
            dialog.setCancelable(cancellable);
        }
    }

    public static ProgressDialog getProgressDialog(final ActivityBase activity) {
        android.app.Dialog progressDialog = new android.app.Dialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_loading, null);
        ImageView progressIcon = (ImageView) view.findViewById(R.id.progress_icon);
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        progressIcon.getLayoutParams().width = width/4;
        progressIcon.getLayoutParams().height = width/4;
        progressIcon.setBackgroundResource(R.anim.progress_dialog_);
        AnimationDrawable loadingViewAnim = (AnimationDrawable) progressIcon.getBackground();
        progressDialog.setContentView(view);
        return new ProgressDialog(progressDialog, loadingViewAnim, activity);
    }
}
