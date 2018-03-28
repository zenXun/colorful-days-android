package com.zhengxunw.colorfuldays;

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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhengxunw.colorfuldays.commons.CustomizedColorUtils;
import com.zhengxunw.colorfuldays.commons.TimeUtils;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;

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
        todayTasks.setAdapter(new todayTasksAdapter(getApplicationContext(),
                DatabaseHelper.getInstance(getApplicationContext()).queryTransactionByDate(dateKey)));

    }



    class todayTasksAdapter extends CursorAdapter {

        todayTasksAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(R.layout.stats_item, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView taskPartTV = view.findViewById(R.id.task_name_part);
            TextView hourPartTV = view.findViewById(R.id.task_hour_part);

            String taskName = DatabaseHelper.getNameInTransTable(cursor);
            Integer color = DatabaseHelper.getInstance(getApplicationContext()).getTaskColor(taskName);
            color = color == null ? Color.WHITE : color;
            float hour = DatabaseHelper.getHourInTransTable(cursor);
            String hourPart = String.format("%.02f", hour) + " hours";
            taskPartTV.setText(taskName);
            hourPartTV.setText(hourPart);
            if (!CustomizedColorUtils.isLightColor(color)) {
                taskPartTV.setTextColor(Color.WHITE);
                hourPartTV.setTextColor(Color.WHITE);
            }
            view.setBackgroundColor(color);
        }
    }
}
