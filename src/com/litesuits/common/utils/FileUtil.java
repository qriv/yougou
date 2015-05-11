package com.litesuits.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import com.gsh.app.client.mall.Constant;
import com.gsh.app.client.mall.MallApplication;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

/**
 * @author MaTianyu
 * @date 2014-08-10
 */
public class FileUtil {

    public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            FileChannel in = fi.getChannel();//得到对应的文件通道
            FileChannel out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fo != null) fo.close();
                if (fi != null) fi.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static String formatFileSizeToString(long fileLen) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileLen < 1024) {
            fileSizeString = df.format((double) fileLen) + "B";
        } else if (fileLen < 1048576) {
            fileSizeString = df.format((double) fileLen / 1024) + "K";
        } else if (fileLen < 1073741824) {
            fileSizeString = df.format((double) fileLen / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileLen / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 根据路径删除图片
     */
    public static boolean deleteFile(File file) throws IOException {
        return file != null && file.delete();
    }

    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return 返回文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    private static final String EXTERNAL_CACHE = MallApplication.context.getExternalCacheDir().getAbsolutePath();
    private static final String CACHE_JSON = EXTERNAL_CACHE + "/json";
    public static final String IMAGE = EXTERNAL_CACHE + "/image";

    public static void createDirectories() {
        File[] dirs = {new File(CACHE_JSON), new File(IMAGE)};
        for (File file : dirs) {
            if (!file.exists())
                file.mkdir();
        }
    }

    public static String getLocalJson(String fileName) {
        String result = "";
        try {
            File file = new File(CACHE_JSON + "/" + fileName);
            if (file.exists()) {
                FileInputStream is = new FileInputStream(file);
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                is.close();
                result = new String(bytes);
            }
        } catch (Exception e) {

        }
        return result;
    }

    public static void writeJson(String content, String fileName) {
        try {
            File directory = new File(CACHE_JSON);
            if (!directory.exists()) {
                directory.mkdir();
            }
            FileOutputStream fos = new FileOutputStream(CACHE_JSON + "/" + fileName);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            System.out.println();
        }
    }

    public static void saveBitmap(Bitmap bmp, String filename) {
        FileOutputStream out = null;
        try {
            File directory = new File(CACHE_JSON);
            if (!directory.exists()) {
                directory.mkdir();
            }
            out = new FileOutputStream(CACHE_JSON + "/" + filename);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean pictureSampleSaved() {
        return new File(CACHE_JSON, Constant.Cache.PICTURE_SAMPLES[0]).exists();
    }

    public static String getPicturePath(String fileName) {
        return CACHE_JSON + "/" + fileName;
    }

    public static boolean jsonExist(String fileName) {
        return new File(CACHE_JSON + "/" + fileName).exists();
    }

    public static String getAssetText(Context context, String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
