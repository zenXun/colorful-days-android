package com.zhengxunw.colorfuldays.commons;

import android.view.View;
import android.widget.TextView;

import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

public class StatsUtils {

    public static void populateStatsRow(DatabaseHelper db, TaskItem taskItem, View view) {

        int bgColor = taskItem.getColor();
        int txtColor = CustomizedColorUtils.getTextColor(bgColor);
        TextView stripTV = view.findViewById(R.id.task_color_strip);
        TextView taskTV = view.findViewById(R.id.task_name_part);
        TextView hourTV = view.findViewById(R.id.task_hour_part);
        taskTV.setText(taskItem.getTaskName());
        hourTV.setText(TimeUtils.getDisplayHour(taskItem.getTaskHour()));
        hourTV.setTextColor(txtColor);
        hourTV.setBackgroundColor(bgColor);
        stripTV.setBackgroundColor(bgColor);
        TextView startDateTV = view.findViewById(R.id.task_start_date_tv);
        TextView lastingDaysTV = view.findViewById(R.id.task_days_tv);
        int taskId = taskItem.getId();
        String firstDate = db.getFirstTransactionDate(taskId);
        if (firstDate != null) {
            startDateTV.setText("Started from: " + firstDate);
            int insistedDays = db.getUniqueTransactionsDays(taskId);
            if (insistedDays > 0) {
                lastingDaysTV.setText("Insisted for " + insistedDays + " days");
            }
        }
    }

}
