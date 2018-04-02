package com.zhengxunw.colorfuldays.calendar_module;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
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

    String date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.daily_task_history);
        ListView todayTasks = findViewById(R.id.today_tasks);
        date = getIntent().getStringExtra(CustomizedCalendarView.DAILY_TASK_INTENT_EXTRA_KEY);
        TextView dateTV = findViewById(R.id.date_tv);
        dateTV.setText(date);

        ArrayList<TaskItem> tasks = new ArrayList<>();
        Cursor transCursor = DatabaseHelper.getInstance(getApplicationContext()).queryTransactionGroupByTaskByDate(date);
        transCursor.moveToFirst();
        do {
            if (transCursor.getCount() > 0) {
                tasks.add(DatabaseHelper.getTaskItemFromTransJoinTaskTable(transCursor));
            }
        } while (transCursor.moveToNext());
        todayTasks.setAdapter(new todayTasksAdapter(getApplicationContext(), tasks));

        Toolbar toolbar = findViewById(R.id.daily_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
            TextView taskTimesTV = convertView.findViewById(R.id.task_days_tv);
            TextView taskPartTV = (TextView) convertView.findViewById(R.id.task_name_part);
            TextView hourPartTV = (TextView) convertView.findViewById(R.id.task_hour_part);

            Cursor cursor = DatabaseHelper.getInstance(getContext()).queryTransactionByDateAndTask(date, task.getId());
            cursor.moveToFirst();
            int times = cursor.getCount();
            cursor.close();
            // Populate the data into the template view using the data object
            taskPartTV.setText(task.getTaskName());
            hourPartTV.setText(TimeUtils.getDisplayHour(task.getTaskHour()));
            taskTimesTV.setText("Did " + times + " times");


            int bgColor = task.getColor();
            int txtColor = CustomizedColorUtils.getTextColor(bgColor);
            taskTimesTV.setTextColor(txtColor);
            taskPartTV.setTextColor(txtColor);
            hourPartTV.setTextColor(txtColor);
            convertView.setBackgroundColor(bgColor);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
