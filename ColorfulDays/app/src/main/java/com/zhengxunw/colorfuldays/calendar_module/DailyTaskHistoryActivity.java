package com.zhengxunw.colorfuldays.calendar_module;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    String date;
    private DatabaseHelper db;

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
        ListView todayTasks = findViewById(R.id.today_tasks);
        ArrayList<TaskItem> tasks = new ArrayList<>();
        Cursor transCursor = db.queryTransactionGroupByTaskByDate(date);
        transCursor.moveToFirst();
        do {
            if (transCursor.getCount() > 0) {
                tasks.add(DatabaseHelper.getTaskItem(transCursor));
            }
        } while (transCursor.moveToNext());
        todayTasks.setAdapter(new todayTasksAdapter(getApplicationContext(), tasks));
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
            ListView taskNotePart = convertView.findViewById(R.id.task_note_parts);
            ArrayList<String> notes = new ArrayList<>();

            Cursor cursor = db.queryTransactionByDateAndTask(date, task.getId());
            int times = cursor.getCount();
            while (cursor.moveToNext()) {
                notes.add((String) DatabaseHelper.getFieldFromCursor(cursor, DatabaseConstants.TRANSACTION_TABLE_NOTE));
            }
            cursor.close();
            int bgColor = task.getColor();
            int txtColor = CustomizedColorUtils.getTextColor(bgColor);
            todayTaskNotesAdapter notesAdapter = new todayTaskNotesAdapter(getContext(), notes, txtColor);
            taskNotePart.setAdapter(notesAdapter);
            adjustNoteListHeight(notesAdapter, taskNotePart);

            // Populate the data into the template view using the data object
            taskPartTV.setText(task.getTaskName());
            hourPartTV.setText(TimeUtils.getDisplayHour(task.getTaskHour()));
            taskTimesTV.setText("Did " + times + " times");
            taskTimesTV.setTextColor(txtColor);
            taskPartTV.setTextColor(txtColor);
            hourPartTV.setTextColor(txtColor);
            convertView.setBackgroundColor(bgColor);
            // Return the completed view to render on screen
            return convertView;
        }
    }

    private void adjustNoteListHeight(todayTaskNotesAdapter notesAdapter, ListView taskNotePart) {
        int totalHeight = 0;
        for (int i = 0; i < notesAdapter.getCount(); i++) {
            View listItem = notesAdapter.getView(i, null, taskNotePart);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = taskNotePart.getLayoutParams();
        params.height = totalHeight + (taskNotePart.getDividerHeight() * (notesAdapter.getCount() - 1));
        taskNotePart.setLayoutParams(params);
        taskNotePart.requestLayout();
    }

    class todayTaskNotesAdapter extends ArrayAdapter<String> {

        private int textColor;

        public todayTaskNotesAdapter(Context context, ArrayList<String> notes, int textColor) {
            super(context, 0, notes);
            this.textColor = textColor;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String note = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(note);
            tv.setTextColor(textColor);
            return convertView;
        }
    }
}
