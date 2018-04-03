package com.zhengxunw.colorfuldays.stats_module;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.commons.TimeUtils;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_ITEM;

public class TaskDetailActivity extends AppCompatActivity {

    private EditText mEditTaskName;
    private EditText mEditTaskInitHour;
    private Button colorSettingBtn;
    private Button deleteTaskBtn;
    private TaskItem taskItem;
    private ColorPicker cp;
    private BarChart barChart;
    private boolean isNewTask = false;
    private Context context;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        context = getApplicationContext();
        db = DatabaseHelper.getInstance(getApplicationContext());
        cp = new ColorPicker(TaskDetailActivity.this);
        mEditTaskName = findViewById(R.id.edit_task_name);
        mEditTaskInitHour = findViewById(R.id.edit_task_init_hour);
        colorSettingBtn = findViewById(R.id.color_setting);
        deleteTaskBtn = findViewById(R.id.delete_task);
        barChart = findViewById(R.id.chart);

        taskItem = getIntent().getParcelableExtra(INTENT_EXTRA_TASK_ITEM);
        
        if (taskItem == null) {
            isNewTask = true;
            deleteTaskBtn.setVisibility(View.INVISIBLE);
            barChart.setVisibility(View.INVISIBLE);
            deleteTaskBtn.setClickable(false);
            taskItem = new TaskItem();
        } else {
            existingTaskSetup();
        }

        mEditTaskName.setText(isNewTask ? "" : taskItem.getTaskName());
        mEditTaskInitHour.setText(String.valueOf(isNewTask ? 0f : taskItem.getTaskHour()));
        colorSettingBtn.setBackgroundColor(isNewTask ? Color.WHITE : taskItem.getColor());

        colorSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cp.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(int color) {
                        taskItem.setColor(color);
                        colorSettingBtn.setBackgroundColor(color);
                        cp.dismiss();
                    }
                });
                cp.show();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void existingTaskSetup() {
        TextView hourTV = findViewById(R.id.task_hour_tv);
        hourTV.setText(R.string.existing_task_hour_tv_hint);

        List<BarEntry> entries = new ArrayList<>();
        String[] labels = new String[7];
        final int taskId = taskItem.getId();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, -6);

        for (int i = 0; i < 7; i++) {
            String dateKey = TimeUtils.getDateKey(cal.getTime());
            Cursor cursor = db.queryHourByDateAndTask(dateKey, taskId);
            cursor.moveToFirst();
            labels[i] = TimeUtils.getWeekday(cal);
            if (cursor.getCount() > 0) {
                float time = DatabaseHelper.getHourInTransTable(cursor);
                entries.add(new BarEntry(i, time));
            } else {
                entries.add(new BarEntry(i, 0));
            }
            cal.add(Calendar.DAY_OF_WEEK, 1);
        }

        BarDataSet barDataSet = new BarDataSet(entries, "time spent (in hour)");
        BarData barData = new BarData(barDataSet);
        barChart.getXAxis().setValueFormatter(new LabelFormatter(labels));
        barChart.setData(barData);
        Description description = new Description();
        description.setText("The most recent week's record.");
        barChart.setDescription(description);

        deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Delete Task", Toast.LENGTH_SHORT).show();
                db.removeTask(taskId);
                db.removeTaskTransactions(taskId);
                onBackPressed();
            }
        });
    }

    private class LabelFormatter implements IAxisValueFormatter {
        private final String[] mLabels;

        public LabelFormatter(String[] labels) {
            mLabels = labels;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mLabels[(int) value];
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cp.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cp.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_add_task, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_task_action:
                String newTaskName = mEditTaskName.getText().toString();
                String newTaskHourText = mEditTaskInitHour.getText().toString();
                float newTaskHour = newTaskHourText.isEmpty() ? 0 : Float.parseFloat(newTaskHourText);

                if (!isNewTask) {
                    taskItem.setTaskName(newTaskName);
                    taskItem.setTaskHour(newTaskHour);
                    db.updateTask(taskItem);
                    break;
                }

                if (newTaskName.isEmpty()) {
                    Toast.makeText(context, R.string.empty_task_name_msg, Toast.LENGTH_SHORT).show();
                    return false;
                }

                TaskItem newTask = new TaskItem(taskItem.getId(), newTaskName, newTaskHour, taskItem.getColor(), TaskItem.IDLE);
                if (db.addNewTask(newTask)) {
                    Toast.makeText(context, R.string.add_task_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.add_task_failure, Toast.LENGTH_SHORT).show();
                }
        }
        onBackPressed();
        return true;
    }
}
