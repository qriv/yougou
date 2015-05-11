package com.litesuits.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Tan Chunmao
 */
public final class TimeUtil {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static Date generate(String s) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(s));
            date = calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    public static boolean isExpired(Date date) {
        return Long.parseLong(sdf.format(date)) > Long.parseLong(sdf.format(new Date()));
    }

    public static final String WORK_TIME_START = "09:00";//工作开始时间
    public static final String WORK_TIME_END = "21:30";//工作结束时间
    public static final int DELIVER_MAX = 30;//最大送货时间

    public static boolean inWorkTime(long time) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            long startTime = sdf2.parse(sdf1.format(time) + " " + WORK_TIME_START).getTime();
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTimeInMillis(startTime);
            startCalendar.add(Calendar.MINUTE, DELIVER_MAX * -1);

            long endTime = sdf2.parse(sdf1.format(time) + " " + WORK_TIME_END).getTime();
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTimeInMillis(endTime);
            endCalendar.add(Calendar.MINUTE, DELIVER_MAX * -1);
            return startCalendar.getTimeInMillis()<= time && time <= endCalendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
