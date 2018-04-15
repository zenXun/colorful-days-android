package com.zhengxunw.colorfuldays.commons;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.r0adkll.slidr.Slidr;
import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_ITEM;

public class TaskSettingActivity extends AppCompatActivity implements ColorPickerDialogListener {

    @BindView(R.id.edit_task_name) EditText taskNameEV;
    @BindView(R.id.edit_task_init_hour) EditText taskInitHourEV;
    @BindView(R.id.btn_color_setting) Button colorSettingBtn;
    @BindView(R.id.btn_delete_task) Button deleteTaskBtn;
    @BindView(R.id.task_goal_ev) EditText goalHourEV;
    @BindView(R.id.goal_frequency) Spinner goalFreqSpinner;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private TaskItem taskItem;
    private boolean isNewTask = false;
    private Context context;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);
        ButterKnife.bind(this);
        Slidr.attach(this, Constants.slidrConfig);
        context = getApplicationContext();
        db = DatabaseHelper.getInstance(getApplicationContext());

        taskItem = getIntent().getParcelableExtra(INTENT_EXTRA_TASK_ITEM);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.goal_frequency, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        goalFreqSpinner.setAdapter(adapter);

        if (taskItem == null) {
            newTaskSetup();
        } else {
            existingTaskSetup();
        }

        colorSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setAllowCustom(true)
                        .setAllowPresets(false)
                        .setColor(Color.BLACK)
                        .show(TaskSettingActivity.this);
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void newTaskSetup() {
        isNewTask = true;
        deleteTaskBtn.setVisibility(View.INVISIBLE);
        deleteTaskBtn.setClickable(false);
        taskItem = new TaskItem();

        taskNameEV.setText("");
        taskInitHourEV.setText("0");
        colorSettingBtn.setBackgroundColor(Color.WHITE);
    }

    private void existingTaskSetup() {
        TextView hourTV = findViewById(R.id.task_hour_tv);
        hourTV.setText(R.string.existing_task_hour_tv_hint);

        taskNameEV.setText(taskItem.getTaskName());
        taskInitHourEV.setText(String.valueOf(taskItem.getTaskHour()));
        goalHourEV.setText(String.valueOf(taskItem.getGoal()));
        goalFreqSpinner.setSelection(taskItem.getGoalType());
        colorSettingBtn.setBackgroundColor(taskItem.getColor());

        final int taskId = taskItem.getId();
        deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Delete Task", Toast.LENGTH_SHORT).show();
                db.deleteTask(taskId);
                onBackPressed();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_setting_done, menu);
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
                String newTaskName = taskNameEV.getText().toString();
                String newTaskHourText = taskInitHourEV.getText().toString();
                float newTaskHour = newTaskHourText.isEmpty() ? 0 : Float.parseFloat(newTaskHourText);
                String goalHourStr = goalHourEV.getText().toString();
                int goalHour = goalHourStr == null || goalHourStr.isEmpty() ? 0 : Integer.valueOf(goalHourStr);
                int goalType = goalFreqSpinner.getSelectedItemPosition();

                if (newTaskName.isEmpty()) {
                    Toast.makeText(context, R.string.empty_task_name_msg, Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (!isNewTask) {
                    taskItem.setTaskName(newTaskName);
                    taskItem.setTaskHour(newTaskHour);
                    taskItem.setGoal(goalHour);
                    taskItem.setGoalType(goalType);
                    db.updateTask(taskItem);
                    break;
                }

                TaskItem newTask = new TaskItem(taskItem.getId(), newTaskName, newTaskHour, taskItem.getColor(), TaskItem.IDLE, goalHour, goalType);
                if (db.addNewTask(newTask)) {
                    Toast.makeText(context, R.string.add_task_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.add_task_failure, Toast.LENGTH_SHORT).show();
                }
        }
        onBackPressed();
        return true;
    }


    @Override
    public void onDialogDismissed(int dialogId) {

    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        taskItem.setColor(color);
        colorSettingBtn.setBackgroundColor(color);
        colorSettingBtn.setTextColor(CustomizedColorUtils.getTextColor(color));
    }



}
