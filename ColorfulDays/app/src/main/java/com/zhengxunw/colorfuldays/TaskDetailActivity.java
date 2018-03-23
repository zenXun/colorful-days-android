package com.zhengxunw.colorfuldays;

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

public class TaskDetailActivity extends AppCompatActivity {

    private EditText mEditTaskName;
    private EditText mEditTaskInitHour;
    private int taskColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mEditTaskName = (EditText) findViewById(R.id.edit_task_name);
        mEditTaskInitHour = (EditText) findViewById(R.id.edit_task_init_hour);
        final Button mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ColorPicker cp = new ColorPicker(TaskDetailActivity.this);
                cp.show();
                cp.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(int color) {
                        taskColor = color;
                        mButton.setBackgroundColor(color);
                        cp.cancel();
                    }
                });
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
                String name = mEditTaskName.getText().toString();
                String hour = mEditTaskInitHour.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Task name is required.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                DatabaseHelper db = DatabaseHelper.getmInstance(getApplicationContext());
                db.addData(new TaskItem(name, hour.isEmpty() ? 0 : Float.parseFloat(hour)));
                break;
        }
        onBackPressed();
        return true;
    }
}
