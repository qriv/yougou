package com.gsh.app.client.mall.pickerview.lib;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Numeric Wheel adapter.
 */
public class WeekdayAdapter implements WheelAdapter {
    private List<String> weekdays;
    final String[] week_day = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};

    /**
     * The default min value
     */
    public static final int DEFAULT_MAX_VALUE = 9;

    /**
     * The default max value
     */
    private static final int DEFAULT_MIN_VALUE = 0;

    // Values
    private int minValue;
    private int maxValue;

    // format
    private String format;


    public WeekdayAdapter(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        String[] s = {"一", "二", "三", "四", "五", "六", "日"};
        weekdays = new ArrayList<String>();
        int id = 2000;
        for (String s1 : s) {
            weekdays.add(id++ + "");
        }
    }

    @Override
    public String getItem(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = minValue + index;
            return weekdays.get(index);
        }
        return null;
    }

    @Override
    public int getItemsCount() {
        return maxValue - minValue + 1;
    }

    @Override
    public int getMaximumLength() {
        int max = Math.max(Math.abs(maxValue), Math.abs(minValue));
        int maxLen = Integer.toString(max).length();
        if (minValue < 0) {
            maxLen++;
        }
        return maxLen;
    }
}
