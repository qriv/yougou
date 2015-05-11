package com.gsh.app.client.mall.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M0;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
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
public class SetProfileActivity extends ActivityBase {
    private static final int REQUEST_CROP = 2038;
    private static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 2039;
    private static final int REQUEST_EDIT = 2040;

    @InjectView
    private View layout_avatar, layout_nickname, layout_address;
    @InjectView
    private RoundCornerImageView avatar;
    @InjectView
    private TextView nickname, next;
    private static Uri outputFileUri;
    private File avatarFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);
        Injector.self.inject(this);
        setTitleBar("设置资料", "跳过");
        layout_avatar.setOnClickListener(onClickListener);
        layout_nickname.setOnClickListener(onClickListener);
        layout_address.setOnClickListener(onClickListener);
        next.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(layout_avatar)) {
                setAvatar();
            } else if (v.equals(layout_nickname)) {
                setNickname();
            } else if (v.equals(layout_address)) {
                setAddress();
            } else if (v.equals(next)) {
                next();
            }
        }
    };

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        go.name(LoginActivity.class).goAndFinishCurrent();
    }

    private void setAvatar() {
        selectPicture();
    }

    private void setNickname() {
        Intent intent = new Intent(this, EditTextActivity.class);
        EditTextActivity.EditableText editableText = new EditTextActivity.EditableText();
        editableText.setMax(10);
        editableText.setTitle("昵称");
        intent.putExtra(EditTextActivity.EditableText.class.getName(), editableText);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    private void setAddress() {

    }

    private void next() {
        final String nicknameText = (String) nickname.getTag();
        if (avatarFile == null) {
            toast("请上传头像！");
        } else if (TextUtils.isEmpty(nicknameText)) {
            toast("请输入昵称");
        } else {
            MultipartBody body = new MultipartBody();
            body.addPart(new FilePart("avatar", avatarFile, "image/jpeg"));
            showProgressDialog();
            execute(new Request(Urls.MEMBER_PROFILE_UPDATE).addUrlParam("nickname", nicknameText).setHttpBody(body),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if(apiResult.isOk()) {
                                M0 m0 = apiResult.getModel(M0.class);
                                MallApplication.user.setNickname(m0.nickname);
                                MallApplication.user.setAvatarPath(m0.avatarPath);
                                saveUser();
                            }
                        }

                        @Override
                        protected void onFailure(Response res, HttpException e) {
                            super.onFailure(res, e);
                            dismissProgressDialog();
                        }
                    }
            );
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CROP && resultCode == RESULT_OK) {
            handleCroppedPicture(data);
        } else if (requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE) {
            deleteEmptyFile();
            if (resultCode == Activity.RESULT_OK)
                cropPicture(data);
        } else if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            EditTextActivity.EditableText editableText = (EditTextActivity.EditableText) data.getSerializableExtra(EditTextActivity.EditableText.class.getName());
            String value = editableText.getValue();
            nickname.setTag(value);
            nickname.setText(value);
        }
    }

    private void selectPicture() {
        // Determine Uri of camera image to save.
        File file = FileUtils.createTempFile();
        if (file == null) {
            toast("请插入SD卡！");
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


    private void deleteEmptyFile() {
        if (outputFileUri != null) {
            File file = new File(outputFileUri.getPath());
            if (file.exists() && file.length() == 0) {
                file.delete();
            }
        }
    }

    private void handleCroppedPicture(Intent data) {

        Bundle bundle = data.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            avatar.setImageBitmap(photo);
            avatarFile = FileUtils.convertBitmapToFile(this, photo, avatarFile);
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

}
