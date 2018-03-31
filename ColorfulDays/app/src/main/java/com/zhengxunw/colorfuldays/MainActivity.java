package com.zhengxunw.colorfuldays;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.zhengxunw.colorfuldays.calendar_module.CalendarFragment;
import com.zhengxunw.colorfuldays.database.TaskItem;
import com.zhengxunw.colorfuldays.stats_module.TaskDetailActivity;
import com.zhengxunw.colorfuldays.database.DatabaseConstants;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.stats_module.StatsFragment;
import com.zhengxunw.colorfuldays.today_module.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Fragment homeFragment, statsFragment, colorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // clear DB
//        getApplicationContext().deleteDatabase(DatabaseConstants.DATABASE_NAME);

        DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
//        db.addNewTask(new TaskItem(1, "Reading", 25.36f, Color.parseColor("#005DFF"), TaskItem.IDLE));
//        db.addNewTask(new TaskItem(2, "Workout", 30, Color.parseColor("#F78800"), TaskItem.IDLE));
//        db.addNewTask(new TaskItem(3, "Android", 28.08f, Color.parseColor("#FF2C00"), TaskItem.IDLE));
//        db.addNewTask(new TaskItem(4, "Guitar", 5.2f, Color.parseColor("#5A6754"), TaskItem.IDLE));
//        db.addNewTask(new TaskItem(5, "Finance", 20.92f, Color.parseColor("#C6005D"), TaskItem.IDLE));
//        db.addNewTask(new TaskItem(6, "Blockchain", 4.1f, Color.parseColor("#E69FC4"), TaskItem.IDLE));
//        db.addNewTask(new TaskItem(7, "Data Engineer", 29.47f, Color.parseColor("#00E5CF"), TaskItem.IDLE));
//
//
//        Calendar c = TimeUtils.getCurrentCalendar();
//        c.set(2018, 2, 10, 0, 0);
//        db.appendCalendarEntry(TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), Color.BLACK);
//        c.set(2018, 2, 11, 0, 0);
//        db.appendCalendarEntry(TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), Color.BLUE);
//        c.set(2018, 2, 11, 0, 0);
//        db.appendTransaction(TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), "Reading", 1);
//        c.set(2018, 2, 12, 0, 0);
//        db.appendTransaction(TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), "Workout", 1);
//        c.set(2018, 2, 13, 0, 0);
//        db.appendTransaction(TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), "Android", 1);
//        c.set(2018, 2, 14, 0, 0);
//        db.appendTransaction(TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), "Finance", 1);
//        c.set(2018, 2, 15, 0, 0);
//        db.appendTransaction(TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), "Blockchain", 1);


        db.populateColorTable();


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.navigation_today:
                        transaction.replace(R.id.content, homeFragment).commit();
                        return true;
                    case R.id.navigation_single_stats:
                        transaction.replace(R.id.content, statsFragment).commit();
                        return true;
                    case R.id.navigation_total_stats:
                        transaction.replace(R.id.content, colorFragment).commit();
                        return true;
                }
                return false;
            }

        });

        fragmentManager = getSupportFragmentManager();
        homeFragment = HomeFragment.newInstance();
        statsFragment = StatsFragment.newInstance();
        colorFragment = CalendarFragment.newInstance();

        // show home fragment
        fragmentManager.beginTransaction().replace(R.id.content, homeFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_add_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_task:
                Intent intent = new Intent(MainActivity.this, TaskDetailActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
