package com.zhengxunw.colorfuldays;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView mTextDate;
    private FragmentManager fragmentManager;
    private Fragment homeFragment, statsFragment, colorFragment;
    private static final DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

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

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        statsFragment = new StatsFragment();
        colorFragment = new ColorFragment();
        fragmentManager.beginTransaction().replace(R.id.content, homeFragment).commit();
    }

    private void displayCurrentDate() {
        Date currentTime = Calendar.getInstance().getTime();
        mTextDate.setText(dateFormat.format(currentTime));
    }

}
