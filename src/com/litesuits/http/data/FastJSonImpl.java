package com.litesuits.http.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by taosj on 15/2/27.
 */
public class FastJSonImpl extends Json {


    @Override
    public String toJson(Object src) {
        return JSON.toJSONString(src);
    }

    @Override
    public <T> T toObject(String json, Class<T> claxx) {
        return JSONObject.parseObject(json, claxx);
    }

    @Override
    public <T> T toObject(byte[] bytes, Class<T> claxx) {
        return null;
    }

    @Override
    public <T> List<T> toObjects(String json, Class<T> claxx) {
        return JSONObject.parseArray(json,claxx);
    }
}
