package com.gsh.app.client.mall.pickerview.lib;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter implements WheelAdapter {

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

    /**
     * Default constructor
     */
    public NumericWheelAdapter() {
        this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }

    /**
     * Constructor
     *
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public NumericWheelAdapter(int minValue, int maxValue) {
        this(minValue, maxValue, null, false);
    }

    /**
     * Constructor
     *
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param format   the format string
     */
    public NumericWheelAdapter(int minValue, int maxValue, String format, boolean current) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
        weekdays = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int count = 0;
        while (count < 4) {
            if (current) {
                weekdays.add(week_day[(dayInWeek + count - 1) % week_day.length]);
            } else {
                weekdays.add(week_day[(dayInWeek + count) % week_day.length]);
            }
            count++;
        }

    }

    List<String> weekdays;
    final String[] week_day = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    @Override
    public String getItem(int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = minValue + index;
            return format != null ? weekdays.get(index) : Integer.toString(value);
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
