package com.zhengxunw.colorfuldays.commons;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by zhengxunw on 3/24/18.
 */

public class TimeUtils {

    public static final DateFormat DATE_FORMAT_HOME = new SimpleDateFormat("EEE, d MMM yyyy");
    public static final DateFormat DATE_FORMAT_AS_KEY = new SimpleDateFormat("dd-MMM-yyyy");
    public static final DateTimeFormatter LOCALDATE_FORMAT_AS_KEY = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    public static final DateFormat DATE_FORMAT_CALENDAR_TITLE = new SimpleDateFormat("MMM yyyy");

    public static String getCountingTime(int minutes, int seconds) {
        return String.format(Locale.US, "%d:%02d", minutes, seconds);
    }

    public static float millisToHour(long startTime) {
        return (float)(System.currentTimeMillis() - startTime) / 3600000;
    }

    public static LocalDate toLocalDate(String dateKey) {
        return TimeUtils.toLocalDate(TimeUtils.toCalendar(dateKey));
    }

    public static String toDateStr(LocalDate date) {
        return TimeUtils.LOCALDATE_FORMAT_AS_KEY.format(date);
    }

    private static LocalDate toLocalDate(Calendar calendar) {
        return LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    private static Calendar toCalendar(String dateStr) {
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(TimeUtils.DATE_FORMAT_AS_KEY.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
