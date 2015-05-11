package com.gsh.app.client.mall.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.ShareApp;
import com.gsh.app.client.mall.User;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.widget.ImageUtils;
import com.litesuits.common.assist.Toastor;
import com.litesuits.common.io.FileUtils;
import com.litesuits.common.utils.AppUtil;
import com.litesuits.common.utils.DensityUtil;
import com.litesuits.common.utils.DialogUtil;
import com.litesuits.common.utils.FileUtil;
import com.litesuits.http.LiteHttpClient;
import com.litesuits.http.async.HttpAsyncExecutor;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by taosj on 15/1/30.
 */
public abstract class ActivityBase extends FragmentActivity {

    private final String TAG_ERROR = "error";
    private final String TAG_EMPTY = "empty";
    protected ActivityBase context;

    private LiteHttpClient client;
    private HttpAsyncExecutor asyncExecutor;

    private final List<FutureTask<Response>> tasks = new ArrayList<FutureTask<Response>>();
    private final static SparseArray<ActivityBase> activities = new SparseArray<ActivityBase>();
    private final static AtomicInteger IDs = new AtomicInteger();
    private volatile int uniqueId;

    private RightAction rightAction;

    public ActivityWantedToGo go;

    private Toastor toast;

    public static ImageLoader imageLoader;
    public static DisplayImageOptions defaultPictureOptions;
    public static DisplayImageOptions longPictureOptions;
    public static DisplayImageOptions avatarOptions;
    public SharedPreferences preferences;
    private MallApplication mallApplication;
    private DialogUtil.ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        context = this;
        mallApplication = (MallApplication) getApplication();
        preferences = MallApplication.preferences;
        progressDialog = DialogUtil.getProgressDialog(this);
        initImageLoader();
        client = LiteHttpClient.newApacheHttpClient(context);

        asyncExecutor = HttpAsyncExecutor.newInstance(client);
        synchronized (activities) {
            activities.put(getUniqueId(), this);
        }
        go = new ActivityWantedToGo();
        toast = new Toastor(context);
    }

    private boolean contentLoaded;

    protected void hideContent() {
        contentLoaded = false;
        findViewById(R.id.content).setVisibility(View.INVISIBLE);
    }

    protected void showContent() {
        if (contentLoaded)
            return;
        contentLoaded = true;
        findViewById(R.id.content).setVisibility(View.VISIBLE);
    }

    private void initImageLoader() {
        if (defaultPictureOptions == null) {
            FileUtil.createDirectories();
            File cacheDir = new File(FileUtil.IMAGE);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .memoryCacheExtraOptions(metrics.widthPixels, metrics.heightPixels)
                    .diskCacheExtraOptions(metrics.widthPixels, metrics.heightPixels, null)
                    .threadPoolSize(5)
                    .threadPriority(Thread.NORM_PRIORITY - 1)
                    .tasksProcessingOrder(QueueProcessingType.FIFO)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                    .memoryCacheSize(2 * 1024 * 1024)
                    .memoryCacheSizePercentage(20)
                    .diskCache(new UnlimitedDiscCache(cacheDir))
                    .diskCacheSize(50 * 1024 * 1024)
                    .diskCacheFileCount(100)
                    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                    .imageDownloader(new BaseImageDownloader(context))
                    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                    .writeDebugLogs()
                    .build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);

            defaultPictureOptions = createImageOption(R.drawable.pic_loading);
            longPictureOptions = createImageOption(R.drawable.pic_loading_long);
            avatarOptions = createImageOption(R.drawable.ui_default_avatar);
        }
    }

    private DisplayImageOptions createImageOption(int loadDrawable) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(loadDrawable) // resource or drawable
//                .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
//                .showImageOnFail(R.drawable.ic_error) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(1000)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();
    }

    public void execute(final Request req, final HttpResultHandler UIHandler) {
        User user = MallApplication.user;
//            req.addHeader("communityId", String.valueOf(user.getCommunityId()));
        req.addHeader("stationId", "1");
        req.addHeader("version", "1.0");
        if (user.loggedIn()) {/*
            if (MallApplication.test) {
                try {
                    if (req.getUrl().contains(Urls.MEMBER_WALLET_REMAIN_SUM) || req.getUrl().contains(Urls.MEMBER_WALLET_CREDIT_SUM) || req.getUrl().contains(Urls.MEMBER_WALLET_COUPON_SUM)) {
                        req.addHeader("token", "123456");
                    }
                } catch (HttpClientException e) {
                    e.printStackTrace();
                }
            } else {
            }*/
            req.addHeader("token", user.getToken());
        }
        FutureTask<Response> responseFutureTask = asyncExecutor.execute(req, UIHandler);
        tasks.add(responseFutureTask);
    }

    protected void removeAllHttpTasks() {
        for (FutureTask<Response> item : tasks) {
            if (!item.isCancelled())
                item.cancel(true);
        }
    }

    public synchronized int getUniqueId() {
        if (uniqueId == 0) {
            uniqueId = IDs.incrementAndGet();
        }
        return uniqueId;
    }

    public static void finishAll() {
        synchronized (activities) {
            if (activities.size() > 0)
                for (int i = activities.size() - 1; i >= 0; i--) {
                    int key = activities.keyAt(i);
                    if (activities.get(key) != null)
                        activities.get(key).finish();
                }
            activities.clear();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onDestroy() {
        synchronized (activities) {
            activities.delete(getUniqueId());
        }
        super.onDestroy();
    }

    protected void setTitleBar(String title, String rightAction) {
        setTitleBar(false, title, RightAction.TEXT, rightAction);
    }

    protected void setTitleBar(String title) {
        setTitleBar(false, title, RightAction.NONE, -1);
    }

    protected void setTitleBar(boolean hideBack, String title) {
        setTitleBar(hideBack, title, RightAction.NONE, -1);
    }

    protected void setTitleBar(int titleId) {
        setTitleBar(false, titleId, RightAction.NONE, -1);
    }

    protected void setTitleBar(boolean hideBack, int title, RightAction rightAction, int actionRes) {
        setTitleBar(hideBack, getString(title), rightAction, actionRes);
    }

    protected void setTitleBar(boolean hideBack, int title, RightAction rightAction, String actionText) {
        setTitleBar(hideBack, getString(title), rightAction, actionText);
    }

    protected void setTitleBar(boolean hideBack, String title, RightAction rightAction, String actionText) {
        /*left button*/
        if (hideBack) {
            findViewById(R.id.btn_title_back).setClickable(false);
            findViewById(R.id.image_back).setVisibility(View.GONE);
            int padding = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
            findViewById(R.id.text_title).setPadding(padding, 0, 0, 0);
        } else {
            findViewById(R.id.image_back).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_title_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLeftActionPressed();
                }
            });
        }

        /*title*/
        ((TextView) findViewById(R.id.text_title)).setText(title);

        /*right button*/
        this.rightAction = rightAction;
        final View iconAction = findViewById(R.id.title_bar_icon_action);
        final View textAction = findViewById(R.id.title_bar_text_action);
        if (rightAction == RightAction.NONE) {
            iconAction.setVisibility(View.GONE);
            textAction.setVisibility(View.GONE);
        } else {
            iconAction.setVisibility(RightAction.ICON == rightAction ? View.VISIBLE : View.GONE);
            textAction.setVisibility(RightAction.TEXT == rightAction ? View.VISIBLE : View.GONE);
            if (RightAction.ICON == rightAction) {
                iconAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightActionPressed();
                    }
                });
            } else if (RightAction.TEXT == rightAction) {
                ((TextView) findViewById(R.id.title_bar_action_text)).setText(actionText);
                textAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightActionPressed();
                    }
                });
            }
        }
        enableRightAction(isRightActionEnabled());
    }

    public void showRightAction(boolean on) {
        findViewById(R.id.title_bar_icon_action).setVisibility(on ? View.VISIBLE : View.INVISIBLE);
    }

    protected void setTitleBar(boolean hideBack, String title, RightAction rightAction, int actionRes) {
        /*left button*/
        if (hideBack) {
            findViewById(R.id.btn_title_back).setClickable(false);
            findViewById(R.id.image_back).setVisibility(View.GONE);
            int padding = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
            findViewById(R.id.text_title).setPadding(padding, 0, 0, 0);
        } else {
            findViewById(R.id.image_back).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_title_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLeftActionPressed();
                }
            });
        }

        /*title*/
        ((TextView) findViewById(R.id.text_title)).setText(title);

        /*right button*/
        this.rightAction = rightAction;
        final View iconAction = findViewById(R.id.title_bar_icon_action);
        final View textAction = findViewById(R.id.title_bar_text_action);
        if (rightAction == RightAction.NONE) {
            iconAction.setVisibility(View.GONE);
            textAction.setVisibility(View.GONE);
        } else {
            iconAction.setVisibility(RightAction.ICON == rightAction ? View.VISIBLE : View.GONE);
            textAction.setVisibility(RightAction.TEXT == rightAction ? View.VISIBLE : View.GONE);
            if (RightAction.ICON == rightAction) {
                ((ImageView) findViewById(R.id.title_bar_action_image)).setImageResource(actionRes);
                iconAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightActionPressed();
                    }
                });
            } else if (RightAction.TEXT == rightAction) {
                ((TextView) findViewById(R.id.title_bar_action_text)).setText(actionRes);
                textAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRightActionPressed();
                    }
                });
            }
        }
        enableRightAction(isRightActionEnabled());
    }

    protected boolean isRightActionEnabled() {
        return true;
    }

    private void enableRightAction(boolean enable) {
        if (rightAction == RightAction.TEXT) {
            findViewById(R.id.title_bar_text_action).setEnabled(enable);
            findViewById(R.id.title_bar_action_text).setEnabled(enable);
        } else if (rightAction == RightAction.ICON) {
            findViewById(R.id.title_bar_icon_action).setEnabled(enable);
            findViewById(R.id.title_bar_action_image).setEnabled(enable);
        }
    }

    protected void onLeftActionPressed() {
        onBackPressed();
    }

    protected void onRightActionPressed() {

    }

    public enum RightAction {
        ICON, TEXT, NONE
    }

    public class ActivityWantedToGo {

        private Intent intent;

        public ActivityWantedToGo name(Class<?> cls) {
            intent = new Intent(context, cls);
            return this;
        }

        public ActivityWantedToGo param(String name, String value) {
            if (intent != null)
                intent.putExtra(name, value);
            return this;
        }

        public ActivityWantedToGo param(String name, int value) {
            if (intent != null)
                intent.putExtra(name, value);
            return this;
        }

        public ActivityWantedToGo param(String name, long value) {
            if (intent != null)
                intent.putExtra(name, value);
            return this;
        }

        public ActivityWantedToGo param(String name, float value) {
            if (intent != null)
                intent.putExtra(name, value);
            return this;
        }

        public ActivityWantedToGo param(String name, double value) {
            if (intent != null)
                intent.putExtra(name, value);
            return this;
        }

        public ActivityWantedToGo param(String name, Serializable value) {
            if (intent != null)
                intent.putExtra(name, value);
            return this;
        }

        public void go()

        {
            if (intent != null) {
                startActivity(intent);
                intent = null;
            }
        }

        public void goForResult(int requestCode) {
            if (intent != null) {
                startActivityForResult(intent, requestCode);
                intent = null;
            }
        }

        public void goAndFinishCurrent() {
            if (intent != null) {
                startActivity(intent);
                intent = null;
                finish();
            }
        }
    }

    //region ui_item_down_arrow
    public void showMoney(TextView textView, float money) {
        textView.setText(getString(R.string.money, money));
    }

    public void showMoney(TextView textView, double money) {
        textView.setText(getString(R.string.money, money));
    }

    public void subMoney(TextView textView, float money) {
        textView.setText(getString(R.string.sub_money, money));
    }

    public void toast(String s) {
        toast.getSingletonToast(s).show();
    }

    public void toast(int s) {
        toast.getSingletonToast(s).show();
    }

    public void testLog(String s) {
        Log.d("test", s);
    }

    public void testLog(int s) {
        Log.d("test", getString(s));
    }

    public final void showProgressDialog() {
        if (!isFinishing()) {
            progressDialog.show();
        }
    }

    public void serverError() {
        toast("服务器错误");
    }

    public void hideView(View view, boolean on) {
        view.setVisibility(on ? View.INVISIBLE : View.VISIBLE);
    }

    public void dismissView(int viewId, boolean on) {
        dismissView(findViewById(viewId), on);
    }

    public void dismissView(View view, boolean on) {
        view.setVisibility(on ? View.GONE : View.VISIBLE);
    }

    public void dismissView(View view) {
        dismissView(view, true);
    }

    public void dismissProgressDialog() {
        if (!isFinishing()) {
            progressDialog.dismiss();
        }
    }


    public interface CallBack {
        void call();
    }

    public void notice(final CallBack callBack, String title) {
        notice(callBack, title, null);
    }

    public void notice(final CallBack callBack, String title, String noticeContent) {
        notice(callBack, title, noticeContent, true);
    }

    public void notice(final CallBack callBack, String title, String noticeContent, boolean cancelable) {
        notice(callBack, null, title, noticeContent, cancelable);
    }

    public void notice(final CallBack callBack, final CallBack noCallBack, String title, String noticeContent, boolean cancelable, String ok, String cancel) {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.setCancelable(cancelable);
        View layout = getLayoutInflater().inflate(R.layout.dialog_notice, null);
        dialog.setContentView(layout);
        ((TextView) layout.findViewById(R.id.title)).setText(title);
        if (!TextUtils.isEmpty(ok)) {
            ((TextView) layout.findViewById(R.id.yes)).setText(ok);
        }
        if (!TextUtils.isEmpty(cancel)) {
            ((TextView) layout.findViewById(R.id.no)).setText(cancel);
        }
        TextView contentView = (TextView) layout.findViewById(R.id.content);
        if (TextUtils.isEmpty(noticeContent)) {
            contentView.setVisibility(View.GONE);
        } else {
            contentView.setVisibility(View.VISIBLE);
            contentView.setText(noticeContent);
        }
        layout.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callBack.call();
            }
        });

        layout.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (noCallBack != null) {
                    noCallBack.call();
                }
            }
        });
        Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.90);
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(p);
        dialog.show();
    }

    public void notice(final CallBack callBack, final CallBack noCallBack, String title, String noticeContent, boolean cancelable) {
        notice(callBack, noCallBack, title, noticeContent, cancelable, "是", "否");
    }

    public void hideKeyboard() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void action(View v) {
        toast(((TextView) v).getText().toString());
    }


//endregion ui_item_down_arrow

    //    region image
    public void loadImage(ImageView imageView, String path) {
        if (!TextUtils.isEmpty(path)) {
            if (!path.startsWith("http"))
                path = Urls.IMAGE_PREFIX + path;
            ImageUtils.loadImage(imageLoader, defaultPictureOptions, imageView, path);
        } else {
            imageView.setImageResource(R.drawable.pic_loading);
        }
    }

    public void loadLongImage(ImageView imageView, String path) {
        if (!TextUtils.isEmpty(path)) {
            if (!path.startsWith("http"))
                path = Urls.IMAGE_PREFIX + path;
            ImageUtils.loadImage(imageLoader, longPictureOptions, imageView, path);
        } else {
            imageView.setImageResource(R.drawable.pic_loading_long);
        }
    }


    public void saveAvatar(final ImageView imageView, String path) {
        path = Urls.IMAGE_PREFIX + path;
        imageLoader.loadImage(path, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                System.out.println();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                System.out.println();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imageView.setImageBitmap(loadedImage);
                File temp = FileUtils.convertBitmapToFile(context, loadedImage);
                MallApplication.user.setLocalAvatarPath(temp.getAbsolutePath());
                saveUser();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                System.out.println();
            }
        });
    }

    public void loadAvatar(ImageView imageView, String path) {
        if (!TextUtils.isEmpty(path)) {
            if (!path.startsWith("http")) {
                path = Urls.IMAGE_PREFIX + path;
            }
            ImageUtils.loadImage(imageLoader, avatarOptions, imageView, path);
        } else {
            imageView.setImageResource(R.drawable.ui_default_avatar);
        }

    }

    public void setGalleryTag(ImageView imageView, List<String> imagePaths, int position) {
        imageView.setOnClickListener(imageListener);
        imageView.setTag(R.id.tag_key_first, imagePaths);
        imageView.setTag(R.id.tag_key_second, position);
    }

    private void gallery(View v) {
        Intent intent = new Intent(this, GalleryActivity.class);
        List<String> paths = (List<String>) v.getTag(R.id.tag_key_first);
        Integer index = (Integer) v.getTag(R.id.tag_key_second);
        intent.putExtra(String.class.getName(), new ArrayList<String>(paths)).putExtra(Integer.class.getName(), index);
        startActivity(intent);
    }

    private View.OnClickListener imageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gallery(v);
        }
    };
//    endregion image

    //region share
    protected Dialog shareDialog;
    protected ShareContent shareContent;
    private View dialogContainer;

    protected final void showShareDialog() {
        if (shareDialog == null) {
            createShareDialog();
        }
        shareDialog.show();
    }

    private void createShareDialog() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        shareDialog = new Dialog(context, R.style.DialogSlideAnim);
        dialogContainer = inflater.inflate(R.layout.dialog_share, null);
        entry();
        final View cancelView = dialogContainer.findViewById(R.id.cancel);
        cancelView.setOnClickListener(shareClickListener);
        shareDialog.setContentView(dialogContainer);

        Window dialogWindow = shareDialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = d.getWidth();
        dialogWindow.setAttributes(p);
        dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

    private void entry() {
        setEntry(R.id.entry_weibo, ShareApp.weibo);
        setEntry(R.id.entry_tencent_weibo, ShareApp.tencent_weibo);
        setEntry(R.id.entry_qq, ShareApp.tencent_qq);
        setEntry(R.id.entry_qzone, ShareApp.qzone);
        setEntry(R.id.entry_wechat, ShareApp.wechat);
        setEntry(R.id.entry_moment, ShareApp.wechat_moments);
    }

    private void setEntry(int layoutId, ShareApp shareApp) {
        final View entry = dialogContainer.findViewById(layoutId);
        int color = getResources().getColor(shareApp.getBackgroundRid());
        int roundRadius = getResources().getDimensionPixelOffset(R.dimen.ui_size_icon_f_half);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(roundRadius);
        entry.findViewById(R.id.icon_container).setBackgroundDrawable(gd);
        entry.setOnClickListener(shareClickListener);
        ((ImageView) entry.findViewById(R.id.icon)).setImageResource(shareApp.getIconRid());
        ((TextView) entry.findViewById(R.id.label)).setText(shareApp.getLabelRid());
        ((TextView) entry.findViewById(R.id.label)).setTextColor(getResources().getColor(R.color.ui_font_c));
    }

    protected void share(ShareApp shareApp) {
        shareDialog.dismiss();
    }

    //分享到第三方平台的内容
    protected class ShareContent {
        String text;
        public String path;
        public String url;

        public ShareContent(String text, String path, String url) {
            this.text = text;
            this.path = path;
            this.url = url;
        }

        public ShareContent() {
        }
    }

    @Override
    public void onBackPressed() {
        removeAllHttpTasks();
        imageLoader.stop();
        super.onBackPressed();
    }

    private void checkAppInstall(ShareApp shareApp) {
        boolean installed = AppUtil.appInstalledOrNot(shareApp.getPackageName(), this);
        if (!installed) {
            String appName = getString(shareApp.getAppName());
            String text = String.format("请安装%s客户端后再试！", appName);
            toast(text);
        } else {
            share(shareApp);
        }
    }

    private View.OnClickListener shareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.cancel == v.getId()) {
                shareDialog.dismiss();
            } else if (R.id.entry_weibo == v.getId()) {
                checkAppInstall(ShareApp.weibo);
            } else if (R.id.entry_tencent_weibo == v.getId()) {
                share(ShareApp.tencent_weibo);
            } else if (R.id.entry_qq == v.getId()) {
                share(ShareApp.tencent_qq);
            } else if (R.id.entry_qzone == v.getId()) {
                share(ShareApp.qzone);
            } else if (R.id.entry_wechat == v.getId()) {
                checkAppInstall(ShareApp.wechat);
            } else if (R.id.entry_moment == v.getId()) {
                checkAppInstall(ShareApp.wechat_moments);
            }
        }
    };
//endregion share


    public ViewGroup getRootView() {
        ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
        return view;
    }

    public void saveUser() {
        mallApplication.saveUser();
    }

    public void showErrorPage(String message) {
        showPage(TAG_ERROR, message);
    }

    public void hideErrorPage() {
        if (getRootView().findViewWithTag(TAG_ERROR) != null) {
            getRootView().removeView(getRootView().findViewWithTag(TAG_ERROR));
        }
    }

    public void onErrorPageClick() {

    }

    private void showPage(String tag, String message) {
        ViewGroup rootView = getRootView();

        if (rootView.findViewWithTag(tag) != null)
            return;

        RelativeLayout view = new RelativeLayout(context);
        view.setLayoutParams(rootView.getLayoutParams());
        view.setBackgroundResource(android.R.color.transparent);

        int titleHeight = DensityUtil.dip2px(context, 48);
        FrameLayout fl = new FrameLayout(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(rootView.getWidth(), rootView.getHeight() - titleHeight);
        lp.setMargins(0, titleHeight, 0, 0);
        fl.setLayoutParams(lp);
        fl.setBackgroundResource(R.color.ui_bg_white);

        ImageView iv = new ImageView(context);
        int width = DensityUtil.dip2px(context, 80);
        int height = DensityUtil.dip2px(context, 80);
        FrameLayout.LayoutParams ivlp = new FrameLayout.LayoutParams(width, height);

        int parentHeight = rootView.getHeight() - titleHeight;
        int parentWidth = rootView.getWidth();

        int x = parentWidth / 2 - width / 2;
        int y = parentHeight / 2 - height;
        ivlp.setMargins(x, y, 0, 0);
        iv.setLayoutParams(ivlp);
        if (tag.equals(TAG_ERROR)) {
            iv.setImageResource(R.drawable.shibai);
        } else if (tag.equals(TAG_EMPTY)) {
            iv.setImageResource(R.drawable.kong);
        }
        fl.addView(iv);

        view.addView(fl);

        TextView tv = new TextView(context);
        FrameLayout.LayoutParams tvlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvlp.setMargins(0, y + height + 40, 0, 0);
        tv.setLayoutParams(tvlp);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getResources().getColor(R.color.ui_font_d));
        tv.setText(message);
        fl.addView(tv);
        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onErrorPageClick();
            }
        });
        view.setTag(tag);
        rootView.addView(view);
    }

    public void showEmptyPage(String message) {
        showPage(TAG_EMPTY, message);
    }

    public void hideEmptyPage() {
        if (getRootView().findViewWithTag(TAG_EMPTY) != null) {
            getRootView().removeView(getRootView().findViewWithTag(TAG_EMPTY));
        }
    }

    public void onRequestError() {
        showErrorPage("数据获取失败，点击重新获取");
    }

    public void onEmptyPageClick() {

    }
}
