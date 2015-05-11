package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M0;
import com.gsh.app.client.mall.https.model.M10;
import com.gsh.app.client.mall.https.model.M4;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.async.AsyncTask;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.android.widget.ImageUtils;
import com.litesuits.android.widget.ViewUtils;
import com.litesuits.common.io.FileUtils;
import com.litesuits.common.utils.CMUtil;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.request.content.MultipartBody;
import com.litesuits.http.request.content.multi.FilePart;
import com.litesuits.http.response.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Tan Chunmao
 */
public class CommentActivity extends ActivityBase implements View.OnClickListener, View.OnLongClickListener {
    private static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 2038;
    @InjectView
    private EditText input;
    @InjectView
    private TextView title;
    @InjectView
    private ImageView pic;
    @InjectView
    private View quality;
    private M10 m10;

    public final Map<Integer, ImgAction> imgActions = CMUtil.getHashMap();
    private final List<String> images = new ArrayList<String>(4);
    private ViewGroup.LayoutParams imageLayoutParams;
    private ImageView img1, img2, img3, img4;
    private List<File> files;
    private static Uri outputFileUri;


    private final List<ImageView> qualityStars = CMUtil.getArrayList();


    private View[] ratingStars;
    private Integer rate;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m10 = (M10) getIntent().getSerializableExtra(M10.class.getName());
        orderId = getIntent().getStringExtra("orderId");
        setContentView(R.layout.activity_comment);
        setTitleBar(false, "发表评论", RightAction.TEXT, "提交");
        Injector.self.inject(this);
        initViews();
    }

    private void initViews() {
        loadImage(pic, m10.picturePath);
        title.setText(m10.name);
        files = new ArrayList<File>();
        initImageSize();
        prepareImages();

        initStars();
    }

    private void initStars() {
        ratingStars = new View[]{findViewById(R.id.rating_a), findViewById(R.id.rating_b), findViewById(R.id.rating_c), findViewById(R.id.rating_d), findViewById(R.id.rating_e)};
        Integer score = 1;
        for (View view : ratingStars) {
            view.setOnClickListener(this);
            view.setTag(score);
            view.setSelected(true);
            score++;
        }
        rate = 5;
    }


    private void prepareImages() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.images);
        images.add("");
        images.add("");
        images.add("");
        images.add("");

        imgActions.put(1, ImgAction.hide);
        imgActions.put(2, ImgAction.hide);
        imgActions.put(3, ImgAction.hide);
        imgActions.put(4, ImgAction.hide);


        initImages(linearLayout);

    }

    private void initImageSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int maxWidth = metrics.widthPixels;
        int imageGap = getResources().getDimensionPixelSize(R.dimen.ui_margin_d);
        int edgePadding = getResources().getDimensionPixelSize(R.dimen.ui_margin_f);
        int imageSize = (int) (((float) maxWidth - imageGap * (Constant.CountLimit.SHARE_PICTURE - 1) - 2 * edgePadding) / Constant.CountLimit.SHARE_PICTURE);
        imageLayoutParams = new RelativeLayout.LayoutParams(imageSize, imageSize);
    }

    private void initImages(LinearLayout layout) {
        List<ImageView> views = new ArrayList<ImageView>();
        LayoutInflater inflater = getLayoutInflater();
        {
            ImageView addView = (ImageView) inflater.inflate(R.layout.image, null);
            addView.setLayoutParams(imageLayoutParams);
            addView.setImageResource(R.drawable.button_post_add_picture);
            addView.setOnClickListener(this);
            addView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView.setOnLongClickListener(this);
            addView.setId(R.id.img1);
            img1 = addView;
            imgActions.put(1, ImgAction.add);
            views.add(addView);
        }
        {
            ImageView addView = (ImageView) inflater.inflate(R.layout.image, null);
            addView.setLayoutParams(imageLayoutParams);
            addView.setImageResource(R.drawable.button_post_add_picture);
            addView.setOnClickListener(this);
            addView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView.setOnLongClickListener(this);
            addView.setId(R.id.img2);
            addView.setVisibility(View.INVISIBLE);
            img2 = addView;
            views.add(addView);
        }
        {
            ImageView addView = (ImageView) inflater.inflate(R.layout.image, null);
            addView.setLayoutParams(imageLayoutParams);
            addView.setImageResource(R.drawable.button_post_add_picture);
            addView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView.setOnClickListener(this);
            addView.setOnLongClickListener(this);
            addView.setId(R.id.img3);
            addView.setVisibility(View.INVISIBLE);
            img3 = addView;
            views.add(addView);
        }
        {
            ImageView addView = (ImageView) inflater.inflate(R.layout.image, null);
            addView.setLayoutParams(imageLayoutParams);
            addView.setImageResource(R.drawable.button_post_add_picture);
            addView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView.setOnClickListener(this);
            addView.setOnLongClickListener(this);
            addView.setId(R.id.img4);
            addView.setVisibility(View.INVISIBLE);
            img4 = addView;
            views.add(addView);
        }
        int gapWidth = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
        int otherViewWidth = getResources().getDimensionPixelOffset(R.dimen.ui_margin_f);
        ViewUtils.addViews(this, layout, views, imageLayoutParams.width, Gravity.CENTER, gapWidth, otherViewWidth);
    }


    @Override
    public boolean onLongClick(View view) {
        if (view == img4) {
            if (imgActions.get(4) == ImgAction.display) {
                notice(new CallBack() {
                    @Override
                    public void call() {
                        imgActions.put(4, ImgAction.add);
                        images.set(3, "");
                        img4.setImageResource(R.drawable.button_post_add_picture);
                    }
                }, "确定要删除图片？");


            }
        }
        if (view == img3) {
            if (imgActions.get(3) == ImgAction.display) {
                notice(new CallBack() {
                    @Override
                    public void call() {
                        images.set(2, images.get(3));
                        images.set(3, "");

                        if (img4.getDrawable() instanceof BitmapDrawable) {
                            BitmapDrawable bd = (BitmapDrawable) img4.getDrawable();
                            img3.setImageBitmap(bd.getBitmap());
                        } else {
                            img3.setImageResource(R.drawable.button_post_add_picture);
                        }

                        img4.setImageResource(R.drawable.button_post_add_picture);
                        img4.setVisibility(View.INVISIBLE);
                        imgActions.put(4, ImgAction.hide);
                        imgActions.put(3, ImgAction.add);
                    }
                }, "确定要删除图片？");
            }
        }
        if (view == img2) {
            if (imgActions.get(2) == ImgAction.display) {
                notice(new CallBack() {
                    @Override
                    public void call() {
                        images.set(1, images.get(2));
                        images.set(2, images.get(3));
                        images.set(3, "");

                        if (img3.getDrawable() instanceof BitmapDrawable) {
                            BitmapDrawable bd = (BitmapDrawable) img3.getDrawable();
                            img2.setImageBitmap(bd.getBitmap());
                        } else {
                            img2.setImageResource(R.drawable.button_post_add_picture);
                        }

                        if (img4.getDrawable() instanceof BitmapDrawable) {
                            BitmapDrawable bd = (BitmapDrawable) img4.getDrawable();
                            img3.setImageBitmap(bd.getBitmap());
                        } else {
                            img3.setImageResource(R.drawable.button_post_add_picture);
                        }

                        img4.setImageResource(R.drawable.button_post_add_picture);

                        if (imgActions.get(4) == ImgAction.display) {
                            imgActions.put(4, ImgAction.add);
                        } else if (imgActions.get(4) == ImgAction.add) {
                            img4.setVisibility(View.INVISIBLE);
                            imgActions.put(4, ImgAction.hide);
                            imgActions.put(3, ImgAction.add);
                        } else {
                            if (imgActions.get(3) == ImgAction.display) {
                                imgActions.put(3, ImgAction.add);
                            } else if (imgActions.get(3) == ImgAction.add) {
                                img3.setVisibility(View.INVISIBLE);
                                imgActions.put(3, ImgAction.hide);
                                imgActions.put(2, ImgAction.add);
                            } else {
                                imgActions.put(2, ImgAction.add);
                            }
                        }
                    }
                }, "确定要删除图片？");
            }
        }
        if (view == img1) {
            if (imgActions.get(1) == ImgAction.display) {
                notice(new CallBack() {
                    @Override
                    public void call() {
                        images.set(0, images.get(1));
                        images.set(1, images.get(2));
                        images.set(2, images.get(3));
                        images.set(3, "");

                        if (img2.getDrawable() instanceof BitmapDrawable) {
                            BitmapDrawable bd = (BitmapDrawable) img2.getDrawable();
                            img1.setImageBitmap(bd.getBitmap());
                        } else {
                            img1.setImageResource(R.drawable.button_post_add_picture);
                        }

                        if (img3.getDrawable() instanceof BitmapDrawable) {
                            BitmapDrawable bd = (BitmapDrawable) img3.getDrawable();
                            img2.setImageBitmap(bd.getBitmap());
                        } else {
                            img2.setImageResource(R.drawable.button_post_add_picture);
                        }

                        if (img4.getDrawable() instanceof BitmapDrawable) {
                            BitmapDrawable bd = (BitmapDrawable) img4.getDrawable();
                            img3.setImageBitmap(bd.getBitmap());
                        } else {
                            img3.setImageResource(R.drawable.button_post_add_picture);
                        }

                        img4.setImageResource(R.drawable.button_post_add_picture);

                        if (imgActions.get(4) == ImgAction.display) {
                            imgActions.put(4, ImgAction.add);
                        } else if (imgActions.get(4) == ImgAction.add) {
                            img4.setVisibility(View.INVISIBLE);
                            imgActions.put(4, ImgAction.hide);
                            imgActions.put(3, ImgAction.add);
                        } else {
                            if (imgActions.get(3) == ImgAction.display) {
                                imgActions.put(3, ImgAction.add);
                            } else if (imgActions.get(3) == ImgAction.add) {
                                img3.setVisibility(View.INVISIBLE);
                                imgActions.put(3, ImgAction.hide);
                                imgActions.put(2, ImgAction.add);
                            } else {
                                if (imgActions.get(2) == ImgAction.display) {
                                    imgActions.put(2, ImgAction.add);
                                } else if (imgActions.get(2) == ImgAction.add) {
                                    img2.setVisibility(View.INVISIBLE);
                                    imgActions.put(2, ImgAction.hide);
                                    imgActions.put(1, ImgAction.add);
                                }
                            }
                        }
                    }
                }, "确定要删除图片？");
            }
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        if (view == img1) {
            selectPicture(1);
        } else if (view == img2) {
            selectPicture(2);
        } else if (view == img3) {
            selectPicture(3);
        } else if (view == img4) {
            selectPicture(4);
        } else if (R.id.rating_a == view.getId() || R.id.rating_b == view.getId() || R.id.rating_c == view.getId() || R.id.rating_d == view.getId() || R.id.rating_e == view.getId()) {
            rate = (Integer) view.getTag();
            rating();
        }
    }

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        checkInput();
    }

    private void rating() {
        for (View view : ratingStars) {
            Integer score = (Integer) view.getTag();
            view.setSelected(score <= rate);
        }
    }


    private void checkInput() {
        final String text = input.getText().toString();
        if (TextUtils.isEmpty(text)) {
            toast("请输入评论内容");
        }
        fillPhotos();
    }

    private void fillPhotos() {
        files.clear();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (String item : images) {
                    if (item.length() > 0) {
                        File file = FileUtils.compressPicture(new File(item), Constant.CountLimit.UPLOAD_PICTURE_SIZE);
                        if (file.exists())
                            files.add(file);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        submit();
                    }
                });
            }
        });
    }

    private void submit() {
        final String text = input.getText().toString();
        if (TextUtils.isEmpty(text)) {
            toast("请输入评论内容");
        } else {
            MultipartBody body = new MultipartBody();
            int count = 0;
            for (File file : files) {
                body.addPart(new FilePart("file", file, "image/jpeg"));
                count++;
            }
            showProgressDialog();
            execute(new Request(Urls.GOODS_COMMENT).addUrlParam("goodsId", String.valueOf(m10.id)).addUrlParam("text", text).addUrlParam("rate", String.valueOf(rate)).addUrlParam("orderId", orderId).setHttpBody(body),
                    new HttpResultHandler() {
                        @Override
                        protected void onSuccess(ApiResult apiResult) {
                            dismissProgressDialog();
                            if (apiResult.isOk()) {
                                toast("感谢您的评价");
                                finish();
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

    private void selectPicture(int index) {
        // Determine Uri of camera image to save.
        File file = null;
        try {
            file = FileUtils.createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        final Intent chooserIntent = Intent.createChooser(galleryIntent, "请选择图片");
        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{captureIntent});
        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE + index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            deleteTempFile();
            switch (requestCode) {
                case YOUR_SELECT_PICTURE_REQUEST_CODE + 1:
                    handleSelectedImage(data, 1);
                    break;
                case YOUR_SELECT_PICTURE_REQUEST_CODE + 2:
                    handleSelectedImage(data, 2);
                    break;
                case YOUR_SELECT_PICTURE_REQUEST_CODE + 3:
                    handleSelectedImage(data, 3);
                    break;
                case YOUR_SELECT_PICTURE_REQUEST_CODE + 4:
                    handleSelectedImage(data, 4);
                    break;
            }
        }
    }


    private void deleteTempFile() {
        if (outputFileUri != null) {
            File file = new File(outputFileUri.getPath());
            if (file.exists() && file.length() == 0) {
                file.delete();
            }
        }
    }

    private void handleSelectedImage(Intent data, int index) {
        try {
            if (data != null) {
                outputFileUri = data.getData();
            }
            Bitmap bitmap = null;
            if (outputFileUri != null)
                bitmap = ImageUtils.getThumbnail(this, outputFileUri, imageLayoutParams.width);
            if (bitmap == null) {
                toast("您选择的图片不存在！");
                return;
            }
            String path = "";
            if (outputFileUri != null) {
                //从相册中选
                path = FileUtils.getRealPathFromURI(this, outputFileUri);
                if (path == null)
                    //拍照
                    path = outputFileUri.getPath();
            }
            images.set(index - 1, path);
            switch (index) {
                case 1:
                    img1.setImageBitmap(bitmap);
                    imgActions.put(1, ImgAction.display);
                    img2.setVisibility(View.VISIBLE);
                    imgActions.put(2, ImgAction.add);
                    break;
                case 2:
                    img2.setImageBitmap(bitmap);
                    imgActions.put(2, ImgAction.display);
                    img3.setVisibility(View.VISIBLE);
                    imgActions.put(3, ImgAction.add);
                    break;
                case 3:
                    img3.setImageBitmap(bitmap);
                    imgActions.put(3, ImgAction.display);
                    img4.setVisibility(View.VISIBLE);
                    imgActions.put(4, ImgAction.add);

                    break;
                case 4:
                    img4.setImageBitmap(bitmap);
                    imgActions.put(4, ImgAction.display);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
