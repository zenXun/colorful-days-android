package com.zhengxunw.colorfuldays.commons;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by zhengxunw on 3/24/18.
 */

public class TimeUtils {

    public static final DateFormat DATE_FORMAT_HOME = new SimpleDateFormat("EEE, d MMM yyyy");
    public static final DateFormat DATE_FORMAT_AS_KEY = new SimpleDateFormat("dd-MMM-yyyy");
    public static final DateFormat DATE_FORMAT_CALENDAR_TITLE = new SimpleDateFormat("MMM yyyy");

    public static String getCountingTime(int minutes, int seconds) {
        return String.format(Locale.US, "%d:%02d", minutes, seconds);
    }

    public static float millisToHour(long startTime) {
        return (float)(System.currentTimeMillis() - startTime) / 3600000;
    }

    public static String toDateStr(LocalDate date) {
        return DATE_FORMAT_AS_KEY.format(date);
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
