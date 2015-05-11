package com.gsh.app.client.mall.https.result;

import com.litesuits.http.data.HttpStatus;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.response.Response;
import com.litesuits.http.response.handler.HttpResponseHandler;

/**
 * Created by taosj on 15/2/27.
 */
public abstract class HttpResultHandler extends HttpResponseHandler {

    @Override
    protected void onSuccess(Response res, HttpStatus status, NameValuePair[] headers) {
        res.printInfo();
    }

    @Override
    protected void onFailure(Response res, HttpException e) {
        res.printInfo();
    }

    protected abstract void onSuccess(ApiResult apiResult);

    @Override
    public HttpResponseHandler handleResponse(Response res) {
        if (res != null) {
            HttpException e = res.getException();
            if (e == null) {
                onSuccess(res, res.getHttpStatus(), res.getHeaders());
                onSuccess(res.getObject(ApiResult.class));
            } else {
                onFailure(res, e);
            }
        }
        return this;
    }
}
