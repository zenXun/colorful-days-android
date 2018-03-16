package com.zhengxunw.colorfuldays;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TaskDetailActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private EditText mEditTaskName;
    private EditText mEditTaskInitHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = DatabaseHelper.getmInstance(getApplicationContext());

        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mEditTaskName = (EditText) findViewById(R.id.edit_task_name);
        mEditTaskInitHour = (EditText) findViewById(R.id.edit_task_init_hour);

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
                } else {
                    db.addData(name, hour.isEmpty() ? 0 : Float.parseFloat(hour));
                }
                break;
        }
        onBackPressed();
        return true;
    }
}
