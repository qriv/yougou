package com.gsh.app.client.mall.pickerview.lib;

import android.content.Context;
import android.view.View;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.pickerview.TimePopupWindow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class WheelTime {
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    public static final int START_HOUR = 9;
    public static final int END_HOUR = 21;
    public static final int START_MINUTE = 0;
    public static final int END_MINUTE_NORMAL = 59;
    public static final int END_MINUTE_HALF = 30;
    private View view;
    private WheelView wv_year;
    private WheelView wv_month;
    private WheelView wv_day;
    private WheelView wv_hours;
    private WheelView wv_mins;
    public int screenheight;

    private TimePopupWindow.Type type;
    private static int START_YEAR = 1990, END_YEAR = 2100;

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public static int getSTART_YEAR() {
        return START_YEAR;
    }

    public static void setSTART_YEAR(int sTART_YEAR) {
        START_YEAR = sTART_YEAR;
    }

    public static int getEND_YEAR() {
        return END_YEAR;
    }

    public static void setEND_YEAR(int eND_YEAR) {
        END_YEAR = eND_YEAR;
    }

    public WheelTime(View view) {
        super();
        this.view = view;
        type = TimePopupWindow.Type.ALL;
        setView(view);
    }

    public WheelTime(View view, TimePopupWindow.Type type) {
        super();
        this.view = view;
        this.type = type;
        setView(view);
    }

    boolean current;//当天发货

    public WheelTime(View view, TimePopupWindow.Type type, boolean current, long startTime) {
        super();
        this.view = view;
        this.type = type;
        this.current = current;
        this.startTime = startTime;
        setView(view);
    }

    public void setPicker(int year, int month, int day) {
        this.setPicker(year, month, day, 0, 0);
    }

    private int currentDayIndex;
    private int currentHourIndex;
    private int startHour;
    private int startMinute;
    private int endMinute;

    /**
     */
    public void setPicker(final int year, final int month, final int day, final int hour, final int minute) {
        Context context = view.getContext();
        setYearAndMonth(year, month, context);


        // 日
        wv_day = (WheelView) view.findViewById(R.id.day);
        int c = 190000;//magic number
        wv_day.setAdapter(new NumericWheelAdapter(c, c + 3, "", current));
        wv_day.setCurrentItem(0);

        wv_hours = (WheelView) view.findViewById(R.id.hour);
        if (current) {
            startHour = hour;
        } else {
            startHour = START_HOUR;
        }
        wv_hours.setAdapter(new NumericWheelAdapter(startHour, END_HOUR));
        wv_hours.setLabel(context.getString(R.string.hours));// 添加文字

        if (current) {
            wv_hours.setCurrentItem(hour);
        } else {
            wv_hours.setCurrentItem(0);
        }

        wv_mins = (WheelView) view.findViewById(R.id.min);
        if (current) {
            startMinute = minute;
        } else {
            startMinute = START_MINUTE;
        }
        if (hour == END_HOUR) {
            endMinute = END_MINUTE_HALF;
        } else {
            endMinute = END_MINUTE_NORMAL;
        }
        wv_mins.setAdapter(new NumericWheelAdapter(startMinute, endMinute));

        wv_mins.setLabel(context.getString(R.string.minutes));// 添加文字
        wv_mins.setCurrentItem(0);


        // 添加"日"监听
        OnWheelChangedListener wheelListener_day = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                currentDayIndex = newValue;
                if (current && newValue == 0) {
                    startHour = hour;
                } else {
                    startHour = START_HOUR;
                }
                wv_hours.setAdapter(new NumericWheelAdapter(startHour, 21));
                changeMinute(hour, minute);
            }
        };
        wv_day.addChangingListener(wheelListener_day);

        // 添加"小时"监听
        OnWheelChangedListener wheelListener_hour = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                currentHourIndex = newValue;
                changeMinute(hour, minute);
            }
        };

        wv_hours.addChangingListener(wheelListener_hour);

        // 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
        int textSize = 0;
        switch (type) {

            case WEEKDAY_HOUR_MIN:
                textSize = (screenheight / 100) * 4;
                wv_year.setVisibility(View.GONE);
                wv_month.setVisibility(View.GONE);
                break;
        }

        wv_day.TEXT_SIZE = textSize;
        wv_hours.TEXT_SIZE = textSize;
        wv_mins.TEXT_SIZE = textSize;
    }

    private void setYearAndMonth(int year, int month, Context context) {
        // 年
        wv_year = (WheelView) view.findViewById(R.id.year);
        wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
        wv_year.setLabel(context.getString(R.string.year));// 添加文字
        wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

        // 月
        wv_month = (WheelView) view.findViewById(R.id.month);
        wv_month.setAdapter(new NumericWheelAdapter(1, 12));
        wv_month.setLabel(context.getString(R.string.month));
        wv_month.setCurrentItem(month);
    }

    private void changeMinute(final int h, final int m) {
        int hourCount = wv_hours.getAdapter().getItemsCount();
        if (current && currentHourIndex == 0 && currentDayIndex == 0) {
            startMinute = m;
        } else {
            startMinute = START_MINUTE;
        }
        if (currentHourIndex == hourCount - 1) {
            endMinute = END_MINUTE_HALF;
        } else {
            endMinute = END_MINUTE_NORMAL;
        }
        wv_mins.setAdapter(new NumericWheelAdapter(startMinute, endMinute));
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public void setCyclic(boolean cyclic) {
        wv_year.setCyclic(cyclic);
        wv_month.setCyclic(cyclic);
        wv_day.setCyclic(cyclic);
        wv_hours.setCyclic(cyclic);
        wv_mins.setCyclic(cyclic);
    }

    private long startTime;

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }


    public long getTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        int day = wv_day.getCurrentItem();
        if (!current)
            day++;
        calendar.add(Calendar.DAY_OF_YEAR, day);
        calendar.set(Calendar.HOUR_OF_DAY, wv_hours.getCurrentItem() + startHour);
        calendar.set(Calendar.MINUTE, wv_mins.getCurrentItem() + startMinute);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    public String getTime() {
        StringBuffer sb = new StringBuffer();
        sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-")
                .append((wv_month.getCurrentItem() + 1)).append("-")
                .append((wv_day.getCurrentItem() + 1)).append(" ")
                .append(wv_hours.getCurrentItem()).append(":")
                .append(wv_mins.getCurrentItem());
        return sb.toString();
    }
}
