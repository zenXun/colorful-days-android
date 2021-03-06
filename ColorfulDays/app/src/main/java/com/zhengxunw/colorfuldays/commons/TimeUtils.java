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
    public static final DateFormat DATE_FORMAT_AS_KEY = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat DATE_FORMAT_CALENDAR_TITLE = new SimpleDateFormat("MMM yyyy");
    public static final DateFormat MONTH_FORMAT = new SimpleDateFormat("MMM");
    public static final DateFormat WEEK_FORMAT = new SimpleDateFormat("dd-MMM");

    public static String getWeekday(Calendar calendar) {
        DateFormat weekdayFormat = new SimpleDateFormat("E");
        return weekdayFormat.format(calendar.getTime());
    }

    public static String getLabel(Calendar calendar, int graphType) {
        if (graphType == Constants.DAILY_GRAPH) {
            return getWeekday(calendar);
        } else if (graphType == Constants.WEEKLY_GRAPH) {
            return WEEK_FORMAT.format(calendar.getTime());
        }
        return MONTH_FORMAT.format(calendar.getTime());
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

    public static String getWeekDay(String dateKey) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(DATE_FORMAT_AS_KEY.parse(dateKey));
            return new SimpleDateFormat("EE").format(cal.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    public static String getDisplayHourVertical(float hour) {
        return String.format(Locale.US, "%.02f", hour) + "\n hours";
    }

    public static String getDisplayHourHorizontal(float hour) {
        return String.format(Locale.US, "%.02f", hour) + " hours";
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
