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

package com.gsh.app.client.mall.https;

import java.io.Serializable;

/**
 * @author Simon Wang
 */
public class HttpModel<T> implements Serializable {
    private static final String CODE_OK = "200";
    private static final String CODE_ERROR = "500";
    private static final String CODE_CONTENT_EMPTY = "501";
    private static final String ERROR_CODE_TOKEN_INVALID = "101";
    private static final String ERROR_CODE_NOT_CAPTCHA = "110";//验证码错误
    private static final String ERROR_CODE_PHONE_REGISTERED = "108";//注册时手机号码已存在
    private static final String EXCEPTION = "exception";

    /*
    * public static int   TOKEN_OVERDUE=101; //token过期
public static int   NOT_UPDATA=102; //用户修改信息不到时间
public static int  NOT_CORRECT =103; //历史密码错误
public static  int MOBILE_NUMBER=104;//手机号码格式不正确
public static  int MOBILE_IS_BINDING=105;//手机号码已经绑定
public static  int NOT_FRIEND=106;//非好友不能添加备注
public static  int NOT_SET_ADDRESS=107;//用户还未设置地址
public static  int NOT_CAPTCHA=110;//验证码错误
public static  int MOBILE_UNSABLE=108;//手机号码已存在*/

    private String code;
    private String message;
    private String errorCode;
    private int size;
    private T data;

    public boolean isOK() {
        return CODE_OK.equals(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {

        return message;
    }

    public boolean tokenInvalid() {
        return ERROR_CODE_TOKEN_INVALID.equals(errorCode);
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isDataReturned() {
        return isOK() && (data != null);
    }

    public boolean isError() {
        return CODE_ERROR.equals(code);
    }


    public static <T extends HttpModel> T getContentEmpty(Class<T> tClass) {
        try {
            T t = tClass.newInstance();
            t.setCode(CODE_CONTENT_EMPTY);
            t.setMessage("");
            return t;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
