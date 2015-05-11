package com.gsh.app.client.mall.pickerview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.view.View.OnClickListener;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.pickerview.lib.ScreenInfo;
import com.gsh.app.client.mall.pickerview.lib.WheelTime;
import com.litesuits.common.utils.TimeUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * 时间选择器
 *
 * @author Sai
 */
public class TimeDialog extends Dialog implements OnClickListener {


    private View rootView; // 总的布局
    WheelTime wheelTime;
    private View btnSubmit, btnCancel;
    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";
    private OnTimeSelectListener timeSelectListener;

    public TimeDialog(Context context, int style, TimePopupWindow.Type type, boolean current, long time) {
        super(context, style);

        Window dialogWindow = getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = d.getWidth();
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(p);
        dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        rootView = mLayoutInflater.inflate(R.layout.dialog_time, null);
        // -----确定和取消按钮
        btnSubmit = rootView.findViewById(R.id.btnSubmit);
        btnSubmit.setTag(TAG_SUBMIT);
        btnCancel = rootView.findViewById(R.id.btnCancel);
        btnCancel.setTag(TAG_CANCEL);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        // ----时间转轮
        final View timepickerview = rootView.findViewById(R.id.timepicker);
        ScreenInfo screenInfo = new ScreenInfo((Activity) context);
        wheelTime = new WheelTime(timepickerview, type, current, time);

        wheelTime.screenheight = screenInfo.getHeight();

        //默认选中当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelTime.setPicker(year, month, day, hours, minute);

        setContentView(rootView);
    }

    /**
     * 设置可以选择的时间范围
     *
     * @param START_YEAR
     * @param END_YEAR
     */
    public void setRange(int START_YEAR, int END_YEAR) {
        WheelTime.setSTART_YEAR(START_YEAR);
        WheelTime.setEND_YEAR(END_YEAR);
    }



    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public void setCyclic(boolean cyclic) {
        wheelTime.setCyclic(cyclic);
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (tag.equals(TAG_CANCEL)) {
            dismiss();
            return;
        } else {
            if (timeSelectListener != null) {
                Date date = new Date(wheelTime.getTimeInMillis());
                timeSelectListener.onTimeSelect(date);
            }
            dismiss();
            return;
        }
    }

    public interface OnTimeSelectListener {
        public void onTimeSelect(Date date);
    }

    public void setOnTimeSelectListener(OnTimeSelectListener timeSelectListener) {
        this.timeSelectListener = timeSelectListener;
    }

}
