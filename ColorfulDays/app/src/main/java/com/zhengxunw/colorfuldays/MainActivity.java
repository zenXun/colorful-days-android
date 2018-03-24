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

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Fragment homeFragment, statsFragment, colorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // clear DB
        getApplicationContext().deleteDatabase(DatabaseHelper.DATABASE_NAME);
        DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
        Calendar c = Calendar.getInstance();
        c.set(2018, 2, 10, 0, 0);
        db.appendColor(HomeFragment.dateKeyFormat.format(c.getTime()), Color.BLACK);
        c.set(2018, 2, 11, 0, 0);
        db.appendColor(HomeFragment.dateKeyFormat.format(c.getTime()), Color.BLUE);

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
        colorFragment = ColorFragment.newInstance();

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
