package com.zhengxunw.colorfuldays.commons;

import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.util.Range;

import java.util.Map;

/**
 * Created by wukey on 3/26/18.
 */

public class CustomizedColorUtils {

    public static int getComplementaryColor(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return android.graphics.Color.rgb(255 - r, 255 - g, 255 - b);
    }

    public static boolean isLightColor(int color) {
        return ColorUtils.calculateLuminance(color) >= 0.5;
    }

    public static boolean isBgTransparent(int color) {
        return (color >> 24 & 0xff) <= 30;
    }

    public static int mixColors(Map<Integer, Float> colorToHour) {

        final byte ALPHA_CHANNEL = 24;
        final byte RED_CHANNEL   = 16;
        final byte GREEN_CHANNEL =  8;

        float totalHour = 0;
        for (Float hour : colorToHour.values()) {
            totalHour += hour;
        }
        int a = 0, r = 0, g = 0, b = 0;
        for (Map.Entry<Integer, Float> entry : colorToHour.entrySet()) {
            int color = entry.getKey();
            float hour = entry.getValue();
            r += (int)((float)(color >> RED_CHANNEL & 0xff) * hour / totalHour);
            g += (int)((float)(color >> GREEN_CHANNEL & 0xff) * hour / totalHour);
            b += (int)((float)(color & 0xff) * hour / totalHour);
        }
        int finalColor = getAlphaByHour(totalHour) << ALPHA_CHANNEL | r << RED_CHANNEL | g << GREEN_CHANNEL | b;
        return finalColor == 0 ? Color.WHITE : finalColor;
    }

    private static int getAlphaByHour(float hour) {
        if (hour > 24) {
            hour = 24;
        }
        if (hour < 2) {
            return (int) (32 * hour);
        }
        if (hour < 6) {
            return (int) (16 * hour) + 32;
        }
        return (int) (1.12 * Math.log((double) hour + Math.E) + 125.575);
    }
}
