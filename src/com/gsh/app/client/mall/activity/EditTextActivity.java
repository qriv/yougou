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

package com.gsh.app.client.mall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.gsh.app.client.mall.R;
import com.litesuits.android.widget.CustomEditText;
import com.litesuits.common.utils.StringUtils;

import java.io.Serializable;

/**
 * 编辑文本
 *
 * @author Tan Chunmao
 */
public class EditTextActivity extends ActivityBase {
    private CustomEditText editText;
    private boolean toasted;
    private EditableText editableText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitleBar(savedInstanceState);
        initInput();
    }

    private void initInput() {
        editText = (CustomEditText) findViewById(R.id.edit);

        editText.setHint(String.format("不超过%d个字符！", editableText.getMax()));
        editText.setDrawableClickListener(new CustomEditText.DrawableClickListener() {
            public void onClick(View view, DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        editText.setText("");
                        break;
                    default:
                        break;
                }
            }
        });

        editText.addTextChangedListener(new InputTextChange());
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (StringUtils.hasText(editText.getText().toString()))
                        returnResult();
                    return true;
                }
                return false;
            }
        });

        String originText = editableText.getValue();
        editText.setText(originText);
    }

    private void initTitleBar(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_text);
        if (null != savedInstanceState) {
            editableText = (EditableText) savedInstanceState.getSerializable(EditableText.class.getName());
        } else {
            editableText = (EditableText) getIntent().getSerializableExtra(EditableText.class.getName());
        }

        String titleText = String.format("编辑%s", editableText.getTitle());
        setTitleBar(titleText, "提交");
    }

    private void returnResult() {
        String editValue = editText.getText().toString();
        editValue = StringUtils.removeBlank(editValue);
        String originValue = editableText.getValue();
        if (!StringUtils.equal(editValue, originValue)) {
            editableText.setValue(editValue);
            Intent intent = new Intent();
            intent.putExtra(EditableText.class.getName(), editableText);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EditableText.class.getName(), editableText);
    }

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        returnResult();
    }

    private class InputTextChange implements TextWatcher {
        public InputTextChange() {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = editText.getText().toString().replaceAll(" ", "");
            if (text.contains(",")) {
                text = text.replaceAll(",", "");
                editText.setText(text);
                editText.setSelection(editText.getText().length());
            }

            if (text.length() > editableText.getMax() && !toasted) {
                toasted = true;
                toast(String.format("不超过%d个字符！", editableText.getMax()));
            }
            if (StringUtils.hasText(text)) {
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.button_erase, 0);
                findViewById(R.id.title_bar_text_action).setEnabled(true);
                findViewById(R.id.title_bar_action_text).setEnabled(true);
            } else {
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                findViewById(R.id.title_bar_text_action).setEnabled(false);
                findViewById(R.id.title_bar_action_text).setEnabled(false);
            }
        }
    }

    public static class EditableText implements Serializable {
        private String title;
        private String value;
        private int layoutId;
        private int min;
        private int max;

        public EditableText() {
            setMin(1);
            setMax(1);
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }


        public int getLayoutId() {
            return layoutId;
        }

        public void setLayoutId(int layoutId) {
            this.layoutId = layoutId;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }
    }

}
