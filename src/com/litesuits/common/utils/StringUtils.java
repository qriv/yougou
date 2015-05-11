/*
 * Copyright (c) 2014 Gangshanghua Information Technologies Ltd.
 * http://www.gangsh.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Gangshanghua Information Technologies ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you
 * entered into with Gangshanghua Information Technologies.
 */

package com.litesuits.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import com.gsh.app.client.mall.https.Urls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Simon Wang
 */
public abstract class StringUtils {
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";

    public static boolean hasText(CharSequence str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String removeBlank(String s) {
        String result = s.replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
        return result;
    }

    public static boolean hasText(String str) {
        return hasText((CharSequence) str);
    }

    public static boolean checkNickname(String s) {
        return s.length() >= 2 && s.length() <= 6;
    }

    public static boolean checkLength(String s, int min, int max) {
        return s != null && s.length() >= min && s.length() <= max;
    }

    public static boolean checkLength(String s, int max) {
        return checkLength(s, 1, max);
    }

    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    public static boolean hasLength(String str) {
        return hasLength((CharSequence) str);
    }

    /**
     * @param noStr
     * @return check mobile phone number in China
     */
    public static boolean checkPhoneNo(String noStr) {
        if (noStr.length() == 11 && noStr.matches("\\d+") && (noStr.startsWith("1") || noStr.startsWith("2"))) {
            switch (noStr.charAt(1)) {
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                    return true;
            }
        }
        return false;
    }

    public static boolean checkTelephone(String str) {
        str = str.replaceAll("-", "").replaceAll("#", "").replaceAll("\\*", "");
        return str.matches("\\d+");
    }

    public static boolean checkCaptcha(String captcha) {
        return captcha.matches("\\d{6}");
    }

    //password length from 6 to 15
    //only contain digits and letters
    public static boolean checkPwd(String pwd) {
        if (pwd.length() < 6 || pwd.length() >= 15) {
            return false;
        } else {
            for (int i = 0; i < pwd.length(); i++) {
                char c = pwd.charAt(i);
                if (c < '0' || (c > '9' && c < 'A') || (c > 'Z' && c < 'a') || c > 'z') {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean moneyValid(String s) {
        try {
            double money = Double.parseDouble(s);
            return money > 0;
        } catch (Exception e) {

        }
        return false;
    }

    public static double getMoney(String s) {
        try {
            double money = Double.parseDouble(s);
            return money;
        } catch (Exception e) {

        }
        return 0.0;
    }

    public static boolean equal(String a, String b) {
        boolean result = false;
        if (a == null && b == null) {
            result = true;
        } else if (a == null || b == null) {
            result = false;
        } else {
            result = a.equals(b);
        }
        return result;
    }

    public static String listToCommaString(List<String> tags) {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (String tag : tags) {
            sb.append(prefix);
            prefix = COMMA;
            sb.append(tag);
        }
        return sb.toString();
    }

    public static List<String> commaStringToList(String s) {
        List<String> result = new ArrayList<String>();
        if (hasText(s)) {
            result.addAll(Arrays.asList(s.split(COMMA)));
        }
        return result;
    }

    static final int ELLIPSIS_UNICODE = 8230;//英文省略号unicode8230

    public static String truncate(String src, int length) {
        String result = src;
        if (hasText(src) && src.length() > length) {
            result = src.substring(0, length) + Character.toString((char) ELLIPSIS_UNICODE);
        }
        return result;
    }

    public static boolean toBool(String b) {
        return Boolean.parseBoolean(b);
    }

    public static long toLong(String obj) {
        return Long.parseLong(obj);
    }

    public static SpannableString toSpannableString(Context context, String text) {
        if (!TextUtils.isEmpty(text)) {
            SpannableString spannableString = new SpannableString(text);
            int start = 0;
            Pattern pattern = Pattern.compile("\\\\ue[a-z0-9]{3}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String faceText = matcher.group();
                String key = faceText.substring(1);
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                        context.getResources().getIdentifier(key, "drawable", context.getPackageName()), options);
                ImageSpan imageSpan = new ImageSpan(context, bitmap);
                int startIndex = text.indexOf(faceText, start);
                int endIndex = startIndex + faceText.length();
                if (startIndex >= 0)
                    spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = (endIndex - 1);
            }

            return spannableString;
        } else {
            return new SpannableString("");
        }
    }

    public static String getPicturePath(String path) {
        if (StringUtils.hasText(path) && !path.startsWith("http")) {
            path = Urls.IMAGE_PREFIX + path;
        }
        return path;
    }

    public static String getBigPicturePath(String path) {
        return getPicturePath(path).replace("_small", "");
    }

    public static String leftAlignThreeDigits(int i) {
        StringBuilder sb = new StringBuilder();
        final int s = i;
        while (i < 100) {
            i *= 10;
            sb.append(" ");
        }
        sb.append(s);
        return sb.toString();
    }

    public static String getJsonFileName(Class tClass) {
        return tClass.getName() + ".txt";
    }

    //支付密码由6位数字组成
    public static boolean checkPayPassword(String s) {
        return s.matches("\\d{6}");
    }
}
