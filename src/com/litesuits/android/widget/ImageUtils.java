package com.litesuits.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.activity.ActivityBase;
import com.litesuits.common.io.FileUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    public static final String SDCARD_MNT = "/mnt/sdcard";
    public static final String SDCARD = "/sdcard";
    public static final String DCIM = "/DCIM/Camera/";
    public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;
    public static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
    public static final int REQUEST_CODE_GETIMAGE_BYCROP = 2;

    /**
     * Get a thumbnail bitmap.
     *
     * @param uri
     * @return a thumbnail bitmap
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static Bitmap getThumbnail(Context context, Uri uri, int thumbnailSize) throws FileNotFoundException, IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;// optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        if (input != null)
            input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > thumbnailSize) ? (originalSize / thumbnailSize) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;// optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        if (input != null)
            input.close();
        return bitmap;
    }

    /**
     * Resolve the best value for inSampleSize attribute.
     *
     * @param ratio
     * @return
     */
    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0)
            return 1;
        else
            return k;
    }

    public static void loadImage(ImageLoader imageLoader, DisplayImageOptions options, ImageView imageView, String imagePath) {
        File file = DiskCacheUtils.findInCache(imagePath, imageLoader.getDiscCache());
        if (file != null) {
            showLocalImage(imageView, file.getAbsolutePath());
        } else {
            imageLoader.displayImage(imagePath, imageView, options);
        }
    }


    //要求显示图片尺寸较小，比如头像
    public static void showLocalImage(final ImageView imageView, final String path) {
        AsyncTask<Object, Object, Bitmap> task = new AsyncTask<Object, Object, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Object... params) {
                return BitmapFactory.decodeFile(path);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                imageView.setImageBitmap(bitmap);
            }
        };
        task.execute();
    }
}
