package com.gsh.app.ugou.activity.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.*;
import com.gsh.app.client.mall.activity.fragment.FragmentBase;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M0;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.gsh.app.ugou.activity.TimeLineActivity;
import com.litesuits.android.widget.ImageUtils;
import com.litesuits.android.widget.RoundCornerImageView;
import com.litesuits.common.io.FileUtils;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.request.content.MultipartBody;
import com.litesuits.http.request.content.multi.FilePart;
import com.litesuits.http.response.Response;

import java.io.File;

/**
 * @author Tan Chunmao
 */
public class ProfileFragment extends FragmentBase {

    private static final int REQUEST_LOGIN = 2038;
    private static final int REQUEST_QR_CODE = 2039;
    private static final int REQUEST_EDIT = 2040;
    private static final int REQUEST_CROP = 2041;
    private static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 2042;
    private static final int REQUEST_ACCOUNT = 2043;
    private static final int REQUEST_QUIT = 2044;
    private Button login;
    private View unlogined;
    private View logined;
    private View bigArrow;
    private RoundCornerImageView avatar;

    private static Uri outputFileUri;
    private File avatarFile;
    private Bitmap photo;

    @Override
    public void refresh() {

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
            return layout;
        }
        layout = inflater.inflate(R.layout.ugou_fragment_profile, container, false);
//        findViewById(R.id.avatar).setOnClickListener(onClickListener);
//        findViewById(R.id.nickname).setOnClickListener(onClickListener);
        initItems();
        return layout;
    }

    private void initTitle() {
        login = (Button) layout.findViewById(R.id.login);
        unlogined = layout.findViewById(R.id.unlogined);
        logined = layout.findViewById(R.id.logined);
        avatar = (RoundCornerImageView) layout.findViewById(R.id.avatar);
        bigArrow = layout.findViewById(R.id.big_arrow);
        if (!MallApplication.user.loggedIn()) {
            unlogined.setVisibility(View.VISIBLE);
            logined.setVisibility(View.GONE);
            bigArrow.setVisibility(View.GONE);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(activity, LoginActivity.class), REQUEST_LOGIN);
//                    activity.go.name(LoginActivity.class).goForResult(REQUEST_LOGIN);
                }
            });
        } else {
            onLogin();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            onLogin();
        } else if (requestCode == REQUEST_QR_CODE && resultCode == Activity.RESULT_OK) {
            final String code = data.getExtras().getString("result");
            exchange(code);
        } else if (requestCode == REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            handleCroppedPicture(data);
        } else if (requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE) {
            deleteEmptyFile();
            if (resultCode == Activity.RESULT_OK)
                cropPicture(data);
        } else if (requestCode == REQUEST_EDIT && resultCode == Activity.RESULT_OK) {
            EditTextActivity.EditableText editableText = (EditTextActivity.EditableText) data.getSerializableExtra(EditTextActivity.EditableText.class.getName());
            String value = editableText.getValue();
            changeNickname(value);
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_ACCOUNT) {
            updateProfile();
        }
    }

    private void exchange(String code) {
        activity.showProgressDialog();
        activity.execute(new Request(Urls.COUPON_EXCHANGE).addUrlParam("code", code),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        activity.dismissProgressDialog();
                        if (apiResult != null && apiResult.isOk()) {
                            activity.go.name(VoucherListActivity.class).go();
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        activity.dismissProgressDialog();
                    }
                }
        );
    }

    private void onLogin() {
        unlogined.setVisibility(View.GONE);
        logined.setVisibility(View.VISIBLE);
        findViewById(R.id.header).setOnClickListener(onClickListener);
        bigArrow.setVisibility(View.VISIBLE);
        updateProfile();
    }

    private void initItems() {
        initItem(R.id.layout_package, R.drawable.ui_order, "已购套餐", Color.parseColor("#d05836"));
        initItem(R.id.layout_address, R.drawable.ui_my_favorite, "收货地址", Color.parseColor("#eaa422"));
        initItem(R.id.layout_settings_, R.drawable.ui_my_favorite, "设置", Color.parseColor("#eaa422"));
    }

    @Override
    public void onResume() {
        super.onResume();
        initTitle();
//        hideStatusBar();
    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT < 16) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = activity.getWindow().getDecorView();
// Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null)
                actionBar.hide();
        }
    }

    private void updateProfile() {
        String nickname = MallApplication.user.getNickname();
        if (!TextUtils.isEmpty(nickname)) {
            ((TextView) findViewById(R.id.nickname)).setText(MallApplication.user.getNickname());
        } else {
            ((TextView) findViewById(R.id.nickname)).setText("人民优购用户_" + MallApplication.user.getId());
        }
        String localAvatarPath = MallApplication.user.getLocalAvatarPath();
        if (!TextUtils.isEmpty(localAvatarPath)) {
            ImageUtils.showLocalImage(avatar, localAvatarPath);
        } else {
            String avatarPath = MallApplication.user.getAvatarPath();
            if (!TextUtils.isEmpty(avatarPath)) {
                activity.loadAvatar(avatar, avatarPath);
            } else {
                avatar.setImageResource(R.drawable.ui_default_avatar);
            }
        }
    }

    private void initItem(int item, int iconRes, String title, int bg) {
        View container = layout.findViewById(item);
        ImageView icon = (ImageView) container.findViewById(R.id.icon);
        icon.setImageResource(iconRes);
        View iconContainer = container.findViewById(R.id.icon_container);
        GradientDrawable gradientDrawable = (GradientDrawable) iconContainer.getBackground();
        gradientDrawable.setColor(bg);
//        iconContainer.setBackgroundResource(bg);
        TextView label = (TextView) container.findViewById(R.id.label);
        container.findViewById(R.id.icon_container).setVisibility(View.GONE);
        label.setText(title);
        container.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.layout_settings_ == v.getId()) {
                activity.go.name(SettingsActivity.class).go();
            } else if (!MallApplication.user.loggedIn()) {
                activity.go.name(LoginActivity.class).go();
            } else {
                if (R.id.layout_package == v.getId()) {
                    ActivityBase base = (ActivityBase) getActivity();
                    base.go.name(TimeLineActivity.class).go();
                } else if (R.id.layout_address == v.getId()) {
                    ActivityBase base = (ActivityBase) getActivity();
                    base.go.name(AddressListActivity.class).go();
                } else if (R.id.avatar == v.getId()) {
                    selectPicture();
                } else if (R.id.nickname == v.getId()) {
                    setNickname();
                } else if (R.id.header == v.getId()) {
                    startActivityForResult(new Intent(activity, SettingsAccountActivity.class), REQUEST_ACCOUNT);
                }
            }
        }
    };

    private void selectPicture() {
        // Determine Uri of camera image to save.
        File file = FileUtils.createTempFile();
        if (file == null) {
            activity.toast("请插入SD卡！");
            return;
        }
        outputFileUri = Uri.fromFile(file);

        // Camera.
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        // Filesystems
        // galleryIntent.setAction(Intent.ACTION_GET_CONTENT); // To allow file managers or any other app that are not gallery app.

        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Image");
        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{captureIntent});
        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }

    private void setNickname() {
        Intent intent = new Intent(activity, EditTextActivity.class);
        EditTextActivity.EditableText editableText = new EditTextActivity.EditableText();
        editableText.setMax(10);
        editableText.setTitle("昵称");
        intent.putExtra(EditTextActivity.EditableText.class.getName(), editableText);
        startActivityForResult(intent, REQUEST_EDIT);
    }


    private void handleCroppedPicture(Intent data) {

        Bundle bundle = data.getExtras();
        if (bundle != null) {
            photo = bundle.getParcelable("data");
            avatarFile = FileUtils.convertBitmapToFile(activity, photo, avatarFile);
            changeAvatar();
        }
    }


    private void deleteEmptyFile() {
        if (outputFileUri != null) {
            File file = new File(outputFileUri.getPath());
            if (file.exists() && file.length() == 0) {
                file.delete();
            }
        }
    }

    private void cropPicture(Intent data) {
        // List<String> picturePaths = data.getStringArrayListExtra(Constant.Send.SELECTED_PICTURES);
        Intent intent = new Intent("com.android.camera.action.CROP");
        // intent.setDataAndType(Uri.fromFile(new File(picturePaths.get(0))), "image/*");
        if (data != null) {
            outputFileUri = data.getData();
        }

        intent.setDataAndType(outputFileUri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", Constant.CountLimit.AVATAR_SIZE);
        intent.putExtra("outputY", Constant.CountLimit.AVATAR_SIZE);

        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        avatarFile = new File(FileUtils.LOCAL_AVATAR);
        intent.putExtra("output", Uri.fromFile(avatarFile));  // 专入目标文件
        intent.putExtra("outputFormat", "JPEG"); //输入文件格式
        startActivityForResult(intent, REQUEST_CROP);
    }

    private void changeNickname(final String nicknameText) {
        activity.showProgressDialog();
        activity.execute(new Request(Urls.MEMBER_PROFILE_UPDATE).addUrlParam("nickname", nicknameText),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        activity.dismissProgressDialog();
                        if (apiResult.isOk()) {
                            M0 m0 = apiResult.getModel(M0.class);
                            MallApplication.user.setNickname(m0.nickname);
                            ((TextView) findViewById(R.id.nickname)).setText(nicknameText);
                            activity.saveUser();
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        activity.dismissProgressDialog();
                    }
                }
        );
    }

    private void changeAvatar() {
        activity.showProgressDialog();
        MultipartBody body = new MultipartBody();
        body.addPart(new FilePart("avatar", avatarFile, "image/jpeg"));
        activity.execute(new Request(Urls.MEMBER_PROFILE_UPDATE).setHttpBody(body),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        activity.dismissProgressDialog();
                        if (apiResult.isOk()) {
                            MallApplication.user.setLocalAvatarPath(avatarFile.getAbsolutePath());
                            avatar.setImageBitmap(photo);
                            activity.saveUser();
                        }
                    }

                    @Override
                    protected void onFailure(Response res, HttpException e) {
                        super.onFailure(res, e);
                        activity.dismissProgressDialog();
                    }
                }
        );
    }
}
