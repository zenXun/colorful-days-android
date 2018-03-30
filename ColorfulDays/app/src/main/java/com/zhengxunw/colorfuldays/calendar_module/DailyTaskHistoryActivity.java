package com.zhengxunw.colorfuldays.calendar_module;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.commons.CustomizedColorUtils;
import com.zhengxunw.colorfuldays.commons.TimeUtils;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wukey on 3/28/18.
 */

public class DailyTaskHistoryActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.daily_task_history);
        ListView todayTasks = (ListView) findViewById(R.id.today_tasks);
        String dateKey = getIntent().getStringExtra("date");

        Set<Integer> taskIds = new HashSet<>();
        Cursor cursor = DatabaseHelper.getInstance(getApplicationContext()).queryTransactionByDate(dateKey);
        cursor.moveToFirst();
        do {
            if (cursor.getCount() > 0) {
                int taskId = DatabaseHelper.getTaskIdFromTransTable(cursor);
                taskIds.add(taskId);
            }
        } while (cursor.moveToNext());
        ArrayList<TaskItem> tasks = new ArrayList<>();
        for (int id : taskIds) {
            tasks.add(DatabaseHelper.getTaskItemInTaskTable(DatabaseHelper.getInstance(getApplicationContext()).getTaskById(id)));
        }
        todayTasks.setAdapter(new todayTasksAdapter(getApplicationContext(), tasks));

    }

    class todayTasksAdapter extends ArrayAdapter<TaskItem> {
        public todayTasksAdapter(Context context, ArrayList<TaskItem> tasks) {
            super(context, 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            TaskItem task = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.stats_item, parent, false);
            }
            // Lookup view for data population
            TextView taskPartTV = (TextView) convertView.findViewById(R.id.task_name_part);
            TextView hourPartTV = (TextView) convertView.findViewById(R.id.task_hour_part);
            // Populate the data into the template view using the data object
            taskPartTV.setText(task.getTaskName());
            hourPartTV.setText(TimeUtils.getDisplayHour(task.getTaskHour()));

            int color = task.getColor();
            if (!CustomizedColorUtils.isLightColor(color)) {
                taskPartTV.setTextColor(Color.WHITE);
                hourPartTV.setTextColor(Color.WHITE);
            }
            convertView.setBackgroundColor(color);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
