package com.zhengxunw.colorfuldays.commons;

import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;

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
        return ColorUtils.calculateLuminance(color) >= 0.4;
    }
}
