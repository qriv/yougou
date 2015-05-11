package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.gsh.app.client.mall.R;

/**
 * @author Tan Chunmao
 */
public class QrCodeInputActivity extends ActivityBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_input);
        setTitleBar("输入二维码/条形码");
        findViewById(R.id.scan).setOnClickListener(onClickListener);
        findViewById(R.id.ok).setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.scan == v.getId()) {
                finish();
            } else if (R.id.ok == v.getId()) {
                String text = ((EditText) findViewById(R.id.input)).getText().toString();
                if (TextUtils.isEmpty(text)) {
                    toast("请输入二维码或者条形码");
                } else {
                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", text);
                    resultIntent.putExtras(bundle);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        }
    };
}
