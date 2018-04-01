package com.zhengxunw.colorfuldays.stats_module;

import android.content.Context;
import android.content.Intent;
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
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_ITEM;

public class TaskDetailActivity extends AppCompatActivity {

    private EditText mEditTaskName;
    private EditText mEditTaskInitHour;
    private Button colorSettingBtn;
    private Button deleteTaskBtn;
    private TaskItem taskItem;
    private ColorPicker cp;
    private boolean isNewTask = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        cp = new ColorPicker(TaskDetailActivity.this);
        mEditTaskName = findViewById(R.id.edit_task_name);
        mEditTaskInitHour = findViewById(R.id.edit_task_init_hour);
        colorSettingBtn = findViewById(R.id.color_setting);
        deleteTaskBtn = findViewById(R.id.delete_task);

        taskItem = getIntent().getParcelableExtra(INTENT_EXTRA_TASK_ITEM);
        
        if (taskItem == null) {
            isNewTask = true;
            deleteTaskBtn.setVisibility(View.INVISIBLE);
            deleteTaskBtn.setClickable(false);
            taskItem = new TaskItem();
        }

        if (!isNewTask) {
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void existingTaskSetup() {
        TextView hourTV = findViewById(R.id.task_hour_tv);
        hourTV.setText(R.string.existing_task_hour_tv_hint);

        deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Delete Task", Toast.LENGTH_SHORT).show();
                DatabaseHelper.getInstance(getApplicationContext()).removeTask(taskItem.getId());
                DatabaseHelper.getInstance(getApplicationContext()).removeTaskTransactions(taskItem.getId());
                onBackPressed();
            }
        });
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
                Context context = getApplicationContext();
                DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
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
                    Toast.makeText(getApplicationContext(), R.string.empty_task_name_msg, Toast.LENGTH_SHORT).show();
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
