package com.zhengxunw.colorfuldays.commons;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zhengxunw on 3/24/18.
 */

public class TimeUtils {

    public static final DateFormat DATE_FORMAT_HOME = new SimpleDateFormat("EEE, d MMM yyyy");
    public static final DateFormat DATE_FORMAT_AS_KEY = new SimpleDateFormat("dd-MMM-yyyy");
    public static final DateFormat DATE_FORMAT_CALENDAR_TITLE = new SimpleDateFormat("MMM yyyy");

    public static String getWeekday(Calendar calendar) {
        DateFormat weekdayFormat = new SimpleDateFormat("E");
        return weekdayFormat.format(calendar.getTime());
    }

    public static Date getTodayDate() {
        return Calendar.getInstance(Locale.US).getTime();
    }

    public static Calendar getCurrentCalendar() {
        return Calendar.getInstance(Locale.US);
    }

    public static String getCurrentDateKey() {
        return DATE_FORMAT_AS_KEY.format(getTodayDate());
    }

    public static String getDateKey(Date date) {
        return DATE_FORMAT_AS_KEY.format(date);
    }

    public static String getCountingTime(long millis) {
        return getCountingTime(TimeUtils.getMinute(millis), TimeUtils.getSecond(millis));
    }

    public static String getCountingTime(int minutes, int seconds) {
        return String.format(Locale.US, "%d:%02d", minutes, seconds);
    }

    public static String getDisplayHour(float hour) {
        return String.format(Locale.US, "%.02f", hour) + "\n hours";
    }

    public static float millisToHour(long duration) {
        return (float) duration / 3600000;
    }

    public static int getHour(float timeInHour) {
        return (int) timeInHour;

    }

    public static int getMinute(float timeInHour) {
        return (int) ((timeInHour - getHour(timeInHour)) * 60);
    }

    public static int getHour(long timeInMillis) {
        return (int) timeInMillis / 3600000;
    }

    public static int getMinute(long timeInMillis) {
        return (int) (timeInMillis / 1000) / 60;
    }

    public static int getSecond(long timeInMillis) {
        return (int) (timeInMillis / 1000) % 60;
    }

    public static String toDateStr(Calendar calendar) {
        return TimeUtils.DATE_FORMAT_AS_KEY.format(calendar.getTime());
    }

    public static Calendar toCalendar(String dateStr) {
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(TimeUtils.DATE_FORMAT_AS_KEY.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
