package com.zhengxunw.colorfuldays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

public class TaskDetailActivity extends AppCompatActivity {

    private EditText mEditTaskName;
    private EditText mEditTaskInitHour;
    private Button mButton;
    private String taskName;
    private float taskHour;
    private int taskColor;
    private ColorPicker cp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cp = new ColorPicker(TaskDetailActivity.this);

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String taskNameKey = "taskName";
        String colorKey = "taskColor";
        String hourKey = "taskHour";

        taskHour = intent.getFloatExtra(hourKey, 0);
        taskName = intent.getStringExtra(taskNameKey);
        taskColor = intent.getIntExtra(colorKey, 0);

        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mEditTaskName = (EditText) findViewById(R.id.edit_task_name);
        mEditTaskName.setText(taskName);
        mEditTaskInitHour = (EditText) findViewById(R.id.edit_task_init_hour);
        mEditTaskInitHour.setText(String.valueOf(taskHour));
        mButton = (Button) findViewById(R.id.button);
        mButton.setBackgroundColor(taskColor);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cp.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(int color) {
                        taskColor = color;
                        mButton.setBackgroundColor(color);
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
                String newTaskHour = mEditTaskInitHour.getText().toString();
                if (newTaskName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Task name is required.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
                TaskItem taskItem = new TaskItem(newTaskName, newTaskHour.isEmpty() ? 0 : Float.parseFloat(newTaskHour), taskColor);
                if (newTaskName.equals(taskName)) {
                    db.updateData(taskItem);
                } else {
                    if (taskName != null) {
                        db.removeTask(taskName);
                    }
                    db.insertData(taskItem);
                }
                break;
        }
        onBackPressed();
        return true;
    }
}
