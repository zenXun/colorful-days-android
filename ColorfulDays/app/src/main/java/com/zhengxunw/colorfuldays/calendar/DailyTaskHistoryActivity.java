package com.zhengxunw.colorfuldays.calendar;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;
import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.commons.Constants;
import com.zhengxunw.colorfuldays.commons.CustomizedColorUtils;
import com.zhengxunw.colorfuldays.commons.TimeUtils;
import com.zhengxunw.colorfuldays.database.DatabaseConstants;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

import java.util.ArrayList;

/**
 * Created by wukey on 3/28/18.
 */

public class DailyTaskHistoryActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slidr.attach(this, Constants.slidrConfig);
        Context context = getApplicationContext();
        db = DatabaseHelper.getInstance(context);

        setContentView(R.layout.activity_task_daily_history);
        date = getIntent().getStringExtra(CustomizedCalendarView.DAILY_TASK_INTENT_EXTRA_KEY);
        TextView dateTV = findViewById(R.id.date_tv);
        dateTV.setText(date);

        Toolbar toolbar = findViewById(R.id.daily_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        float totalHour = 0;
        ListView todayTasks = findViewById(R.id.today_tasks);
        ArrayList<TaskItem> tasks = new ArrayList<>();
        Cursor transCursor = db.queryTransactionGroupByTaskByDate(date);
        transCursor.moveToFirst();
        do {
            if (transCursor.getCount() > 0) {
                TaskItem taskItem = DatabaseHelper.getTaskItem(transCursor);
                totalHour += taskItem.getTaskHour();
                tasks.add(taskItem);
            }
        } while (transCursor.moveToNext());
        todayTasks.setAdapter(new todayTasksAdapter(getApplicationContext(), tasks));
        TextView totalHourTv = findViewById(R.id.total_hour_tv);
        totalHourTv.setText(TimeUtils.getDisplayHourHorizontal(totalHour));
        totalHourTv.setTextColor(Color.BLACK);
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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.daily_history_row, parent, false);
            }
            // Lookup view for data population
            TextView taskTimesTV = convertView.findViewById(R.id.task_days_tv);
            TextView taskPartTV = convertView.findViewById(R.id.task_name_part);
            TextView hourPartTV = convertView.findViewById(R.id.task_hour_part);
            LinearLayout taskNotePart = convertView.findViewById(R.id.task_note_parts);
            taskNotePart.removeAllViews();

            int bgColor = task.getColor();
            int txtColor = CustomizedColorUtils.getTextColor(bgColor);

            Cursor cursor = db.queryTransactionByDateAndTask(date, task.getId());
            int times = cursor.getCount();
            while (cursor.moveToNext()) {
                String note = (String) DatabaseHelper.getFieldFromCursor(cursor, DatabaseConstants.TRANSACTION_TABLE_NOTE);
                if (note.isEmpty()) {
                    continue;
                }
                View noteView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                TextView tv = noteView.findViewById(android.R.id.text1);
                tv.setText(note);
                tv.setTextColor(txtColor);
                taskNotePart.addView(noteView);
            }
            cursor.close();

            // Populate the data into the template view using the data object
            taskPartTV.setText(task.getTaskName());
            hourPartTV.setText(TimeUtils.getDisplayHourVertical(task.getTaskHour()));
            taskTimesTV.setText("Did " + times + " times");
            taskTimesTV.setTextColor(txtColor);
            taskPartTV.setTextColor(txtColor);
            hourPartTV.setTextColor(txtColor);
            convertView.setBackgroundColor(bgColor);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
