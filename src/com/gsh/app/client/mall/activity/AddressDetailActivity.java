package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M18;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.request.Request;
import com.litesuits.http.response.Response;

/**
 * @author Tan Chunmao
 */
public class AddressDetailActivity extends ActivityBase {
    private M18 m18;
    private boolean hasModify;
    @InjectView
    private View action_delete, action_set_default_address;
    private static final int REQUEST_EDIT = 2038;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m18 = (M18) getIntent().getSerializableExtra(M18.class.getName());
        setContentView(R.layout.activity_address_detail);
        Injector.self.inject(this);
        setTitleBar(false, R.string.title_address, RightAction.TEXT, R.string.action_edit);
        loadData();
    }

    private void loadData() {
        String name = getString(R.string.label_address_name, m18.name);
        ((TextView) findViewById(R.id.label_name)).setText(Html.fromHtml(name));
        String mobile = getString(R.string.label_address_mobile, m18.mobile);
        ((TextView) findViewById(R.id.label_mobile)).setText(Html.fromHtml(mobile));
        String address = getString(R.string.label_address_address, m18.communityName + m18.address);
        ((TextView) findViewById(R.id.label_address)).setText(Html.fromHtml(address));

        dismissView(action_delete, m18.isDefault);
        dismissView(action_set_default_address, m18.isDefault);
        action_delete.setOnClickListener(onClickListener);
        action_set_default_address.setOnClickListener(onClickListener);
    }


    @Override
    public void onBackPressed() {
        if (this.hasModify)
            setResult(RESULT_OK);
        else
            setResult(RESULT_CANCELED);
        super.onBackPressed();
    }


    @Override
    protected void onRightActionPressed() {
        go.name(AddressEditActivity.class).param(M18.class.getName(), m18).goForResult(REQUEST_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_EDIT) {
            m18 = (M18) data.getSerializableExtra(M18.class.getName());
            loadData();
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.action_delete == v.getId()) {
                deleteAddress(v);
            } else if (R.id.action_set_default_address == v.getId()) {
                setDefaultAddress(v);
            }
        }
    };

    private void deleteAddress(View v) {
        showProgressDialog();
        execute(new Request(Urls.MEMBER_ADDRESS_DELETE).addUrlParam("id", m18.id + ""), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                dismissProgressDialog();
                if (apiResult.isOk()) {
                    hasModify = true;
                    onBackPressed();
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                dismissProgressDialog();
            }
        });

    }

    private void setDefaultAddress(View v) {
        showProgressDialog();
        execute(new Request(Urls.MEMBER_ADDRESS_DEFAULT).addUrlParam("id", m18.id + ""), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                dismissProgressDialog();
                if (apiResult.isOk()) {
                    hasModify = true;
                    onBackPressed();
                }
            }

            @Override
            protected void onFailure(Response res, HttpException e) {
                dismissProgressDialog();
            }
        });

    }
}
