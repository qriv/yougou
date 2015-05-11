package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.https.Urls;
import com.gsh.app.client.mall.https.model.M12;
import com.gsh.app.client.mall.https.model.M14;
import com.gsh.app.client.mall.https.model.M16;
import com.gsh.app.client.mall.https.result.ApiResult;
import com.gsh.app.client.mall.https.result.HttpResultHandler;
import com.litesuits.android.inject.InjectView;
import com.litesuits.android.inject.Injector;
import com.litesuits.android.log.Log;
import com.litesuits.http.request.Request;

/**
 * Created by taosj on 15/2/3.
 */
public class WalletActivity extends ActivityBase {

    @InjectView
    private View cash_coupon;
    @InjectView
    private View score;
    @InjectView
    private View amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setTitleBar(R.string.title_wallet);
        Injector.self.inject(this);
        initItems();
        loadCouponSum();
        loadCreditSum();
        loadRemainSum();
    }


    private void loadRemainSum() {
        execute(new Request(Urls.MEMBER_WALLET_REMAIN_SUM), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                if (apiResult != null && apiResult.isOk()) {
                    M16 data = apiResult.getModel(M16.class);
                    if (data != null) {
                        TextView value = (TextView) amount.findViewById(R.id.value);
                        value.setTextColor(getResources().getColor(R.color.ui_font_green));
                        value.setText(String.format("%.2f元", data.remain));
                        if (MallApplication.test) {
                            if (data.remain == 0.0) {
                                charge();
                            }
                        }
                    }
                }
            }
        });
    }

    private void charge() {
        execute(new Request(Urls.PAY_TEST),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        if (apiResult.isOk()) {
                            Log.d("response", "charge success");
                        }
                    }
                }
        );
    }


    private void loadCreditSum() {
        execute(new Request(Urls.MEMBER_WALLET_CREDIT_SUM), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                if (apiResult.isOk()) {
                    M12 data = apiResult.getModel(M12.class);
                    TextView value = (TextView) score.findViewById(R.id.value);
                    value.setText(String.format("%s个积分", data.score));
                }
            }
        });
    }

    private void loadCouponSum() {
        execute(new Request(Urls.MEMBER_WALLET_COUPON_SUM), new HttpResultHandler() {
            @Override
            protected void onSuccess(ApiResult apiResult) {
                if (apiResult.isOk()) {
                    M14 data = apiResult.getModel(M14.class);
                    TextView value = (TextView) cash_coupon.findViewById(R.id.value);
                    value.setText(String.format("%s张代金券", data.number));
                }
            }
        });
    }


    private void initItems() {
        TextView cash_coupon_lbl = (TextView) cash_coupon.findViewById(R.id.label);
        cash_coupon_lbl.setText("人民优购代金券");
        cash_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go.name(VoucherListActivity.class).go();
            }
        });

        TextView score_lbl = (TextView) score.findViewById(R.id.label);
        score_lbl.setText("人民优购积分");
        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go.name(ScoreListActivity.class).go();
            }
        });

        TextView amount_lbl = (TextView) amount.findViewById(R.id.label);
        amount_lbl.setText("人民优购余额");
        amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go.name(PaymentListActivity.class).go();
            }
        });
    }
}
