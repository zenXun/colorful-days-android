package com.zhengxunw.colorfuldays;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zhengxunw.colorfuldays.calendar_module.CalendarFragment;
import com.zhengxunw.colorfuldays.commons.TaskSettingActivity;
import com.zhengxunw.colorfuldays.commons.TimeUtils;
import com.zhengxunw.colorfuldays.database.DatabaseConstants;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;
import com.zhengxunw.colorfuldays.database.TransactionItem;
import com.zhengxunw.colorfuldays.stats_module.StatsFragment;
import com.zhengxunw.colorfuldays.today_module.HomeFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private Fragment homeFrag, statsFrag, calendarFrag;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.content);
        homeFrag = HomeFragment.newInstance();
        statsFrag = StatsFragment.newInstance();
        calendarFrag = CalendarFragment.newInstance();

//        backupDB();
//        restoreDB();
//        getApplicationContext().deleteDatabase(DatabaseConstants.DATABASE_NAME);
//        DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
//        db.addNewTask(new TaskItem(1, "Reading", 25.36f, Color.parseColor("#005DFF"), TaskItem.IDLE, 0, 0));
//        db.addNewTask(new TaskItem(2, "Workout", 30, Color.parseColor("#F78800"), TaskItem.IDLE, 0, 0));
//        db.addNewTask(new TaskItem(3, "Android", 28.08f, Color.parseColor("#FF2C00"), TaskItem.IDLE, 0, 0));
//        db.addNewTask(new TaskItem(4, "Guitar", 5.2f, Color.parseColor("#5A6754"), TaskItem.IDLE, 0, 0));
//        db.addNewTask(new TaskItem(5, "Finance", 20.92f, Color.parseColor("#C6005D"), TaskItem.IDLE, 0, 0));
//        db.addNewTask(new TaskItem(6, "Blockchain", 4.1f, Color.parseColor("#E69FC4"), TaskItem.IDLE, 0, 0));
//        db.addNewTask(new TaskItem(7, "Data Engineer", 29.47f, Color.parseColor("#00E5CF"), TaskItem.IDLE, 0, 0));
//
//        Calendar c = TimeUtils.getCurrentCalendar();
//        c.set(2018, 2, 11, 0, 0);
//        db.appendTransaction(new TransactionItem(1, TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), 1, "kasd"));
//        c.set(2018, 2, 12, 0, 0);
//        db.appendTransaction(new TransactionItem(2,TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), 2, "sdfads"));
//        c.set(2018, 2, 13, 0, 0);
//        db.appendTransaction(new TransactionItem(3,TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), 3, "asdfas"));
//        c.set(2018, 2, 14, 0, 0);
//        db.appendTransaction(new TransactionItem(4, TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), 4, "haha"));
//        c.set(2018, 2, 15, 0, 0);
//        db.appendTransaction(new TransactionItem(5, TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), 5, "sladjl"));
//        c.set(2018, 2, 16, 0, 0);
//        db.appendTransaction(new TransactionItem(1, TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), 6, "sdafsd"));
//        c.set(2018, 2, 21, 0, 0);
//        db.appendTransaction(new TransactionItem(5, TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), 0.5f, "Asf"));
//        c.set(2018, 2, 21, 0, 0);
//        db.appendTransaction(new TransactionItem(4, TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), 1, "adsf"));
//        c.set(2018, 2, 21, 0, 0);
//        db.appendTransaction(new TransactionItem(3, TimeUtils.DATE_FORMAT_AS_KEY.format(c.getTime()), 2, "asdf"));

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
                        break;
                }
                mPager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelected(false);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_today:
                        mPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_single_stats:
                        mPager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_total_stats:
                        mPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }

        });
    }

    private void backupDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = String.format("//data//%s//databases//%s", "com.zhengxunw.colorfuldays", DatabaseConstants.DATABASE_NAME);
                String backupDBPath = DatabaseConstants.DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }

    private void restoreDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = String.format("//data//%s//databases//%s", "com.zhengxunw.colorfuldays", DatabaseConstants.DATABASE_NAME);
                String backupDBPath = DatabaseConstants.DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (backupDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            Log.e("s", "restoreDB: " + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_addition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_task:
                Intent intent = new Intent(MainActivity.this, TaskSettingActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return homeFrag;
                case 1:
                    return statsFrag;
                case 2:
                    return calendarFrag;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public int getItemPosition(Object obj) {
            return POSITION_NONE;
        }
    }

    private class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 1f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }


}
