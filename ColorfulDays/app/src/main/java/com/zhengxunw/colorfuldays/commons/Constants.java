package com.zhengxunw.colorfuldays.commons;

import android.graphics.Color;

import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

/**
 * Created by zhengxunw on 3/24/18.
 */

public class Constants {

    public static final String IDLE_TASK_TAG = "idle task";
    public static final String WORKING_TASK_TAG = "working task";
    public static final String LOCAL_STORAGE_TASK_TO_STARTTIME_KEY = "startTime";
    public static final String LOCAL_STORAGE_TASK_TO_STARTTIME_SEPARATOR = "_";

    public static final String INTENT_EXTRA_TASK_ITEM = "task item";


    public static final SlidrConfig slidrConfig = new SlidrConfig.Builder()
            .position(SlidrPosition.LEFT)
            .sensitivity(1f)
            .scrimColor(Color.WHITE)
            .scrimStartAlpha(1f)
            .scrimEndAlpha(0f)
            .velocityThreshold(2400)
            .distanceThreshold(0.25f)
            .edge(true)
            .edgeSize(0.18f) // The % of the screen that counts as the edge, default 18%
            .build();

}
