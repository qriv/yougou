package com.litesuits.common.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by taosj on 15/3/19.
 */
public class XmlCacheModel extends CacheModel {
    @Override
    public <T> void save() {
        XmlCache.getInstance().putString(this.getClass().getName(), JSON.toJSONString(this));
    }

    public static <T> T load(Class<T> tClass) {
        String jsonString = XmlCache.getInstance().getString(tClass.getName());
        T t = null;
        if (jsonString != null && jsonString.length() > 0) {
            t = JSONObject.parseObject(jsonString, tClass);
        } else {
            try {
                t = tClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return t;
    }
}
