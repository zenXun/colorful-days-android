package com.zhengxunw.colorfuldays.commons;

import android.view.View;
import android.widget.TextView;

import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.database.TaskItem;

public class StatsUtils {

    public static void populateStatsRow(TaskItem taskItem, View view) {
        int bgColor = taskItem.getColor();
        int txtColor = CustomizedColorUtils.getTextColor(bgColor);
        TextView stripTV = view.findViewById(R.id.task_color_strip);
        TextView taskTV = view.findViewById(R.id.task_name_part);
        TextView hourTV = view.findViewById(R.id.task_hour_part);
        taskTV.setText(taskItem.getTaskName());
        float taskHour = taskItem.getTaskHour();
        hourTV.setText(TimeUtils.getDisplayHourVertical(taskHour));
        hourTV.setTextColor(txtColor);
        hourTV.setBackgroundColor(bgColor);
        stripTV.setBackgroundColor(bgColor);
    }

}
