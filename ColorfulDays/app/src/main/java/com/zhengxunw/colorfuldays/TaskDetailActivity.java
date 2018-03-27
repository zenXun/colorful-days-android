package com.zhengxunw.colorfuldays;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.zhengxunw.colorfuldays.database.DatabaseHelper;

import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_COLOR_KEY;
import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_HOUR_KEY;
import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_NAME_KEY;

public class TaskDetailActivity extends AppCompatActivity {

    private EditText mEditTaskName;
    private EditText mEditTaskInitHour;
    private Button colorSettingBtn;
    private Button deleteTaskBtn;
    private String taskName;
    private float taskHour;
    private int taskColor;
    private ColorPicker cp;
    private boolean isNewTask = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        cp = new ColorPicker(TaskDetailActivity.this);
        mEditTaskName = (EditText) findViewById(R.id.edit_task_name);
        mEditTaskInitHour = (EditText) findViewById(R.id.edit_task_init_hour);
        colorSettingBtn = (Button) findViewById(R.id.color_setting);
        deleteTaskBtn = (Button) findViewById(R.id.delete_task);

        Intent intent = getIntent();

        taskHour = intent.getFloatExtra(INTENT_EXTRA_TASK_HOUR_KEY, 0);
        taskName = intent.getStringExtra(INTENT_EXTRA_TASK_NAME_KEY);
        taskColor = intent.getIntExtra(INTENT_EXTRA_TASK_COLOR_KEY, Color.WHITE);

        if (taskName == null || taskName.trim().equals("")) {
            isNewTask = true;
        }

        if (!isNewTask) {
            mEditTaskInitHour.setInputType(0);
            mEditTaskName.setInputType(0);
            deleteTaskBtn.setClickable(true);
        } else {
            deleteTaskBtn.setClickable(false);
        }
        mEditTaskName.setText(taskName);
        mEditTaskInitHour.setText(String.valueOf(taskHour));
        colorSettingBtn.setBackgroundColor(taskColor);

        colorSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cp.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(int color) {
                        taskColor = color;
                        colorSettingBtn.setBackgroundColor(color);
                        cp.dismiss();
                    }
                });
                cp.show();
            }
        });

        deleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper.getInstance(getApplicationContext()).deleteTask(taskName);
                onBackPressed();
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
                DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
                if (!isNewTask) {
                    TaskItem existingTask = new TaskItem(taskName, taskHour, taskColor);
                    db.updateData(existingTask);
                    break;
                }

                String newTaskName = mEditTaskName.getText().toString();
                String newTaskHour = mEditTaskInitHour.getText().toString();
                if (newTaskName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Task name is required.", Toast.LENGTH_SHORT).show();
                    return false;
                }

                TaskItem newTask = new TaskItem(newTaskName, newTaskHour.isEmpty() ? 0 : Float.parseFloat(newTaskHour), taskColor);
                db.insertData(newTask);
        }
        onBackPressed();
        return true;
    }
}
