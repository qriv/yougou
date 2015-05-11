package com.gsh.app.client.mall.https.result;

import com.google.gson.JsonElement;
import com.gsh.app.client.mall.https.Urls;
import com.litesuits.http.data.FastJSonImpl;
import com.litesuits.http.data.Json;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/1/30.
 */
public final class ApiResult implements Serializable {

    private static Json json = new FastJSonImpl();

    private ApiResult() {
    }

    public String api;
    public JsonElement data;
    public String message;
    public String code;

    public <T> T getModel(Class<T> clazz) {
        return new FastJSonImpl().toObject(data.toString(), clazz);
    }

    public <T> List<T> getModels(Class<T> clazz) {
        return json.toObjects(data.toString(), clazz);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [api=" + api + ", result="
                + ("[message="
                + message + ",code=" + code + "]" + "]");
    }

    public boolean isOk() {
        return code.equals("1");
    }

    public boolean isTokenInvalid() {
        return code.equals(Urls.CODE_TOKEN_INVALID);
    }
}

