package com.zhengxunw.colorfuldays.stats_module;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;
import com.zhengxunw.colorfuldays.MainActivity;
import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.commons.Constants;
import com.zhengxunw.colorfuldays.commons.GraphTab;
import com.zhengxunw.colorfuldays.commons.StatsUtils;
import com.zhengxunw.colorfuldays.commons.TaskSettingActivity;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_ITEM;

public class TaskStatsActivity extends AppCompatActivity {

    @BindView(R.id.tabs_graphs) TabLayout graphTabs;
    @BindView(R.id.pager_graphs) ViewPager viewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.task_name_part) TextView taskNameTV;
    @BindView(R.id.task_hour_part) TextView taskHourTV;
    private TaskItem taskItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_stats);
        ButterKnife.bind(this);
        Slidr.attach(this, Constants.slidrConfig);

        taskItem = getIntent().getParcelableExtra(INTENT_EXTRA_TASK_ITEM);
        StatsUtils.populateStatsRow(DatabaseHelper.getInstance(getApplicationContext()), taskItem, findViewById(R.id.task_info_row));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        graphTabs.addTab(graphTabs.newTab().setText("Daily"));
//        graphTabs.addTab(graphTabs.newTab().setText("weekly"));
//        graphTabs.addTab(graphTabs.newTab().setText("monthly"));

        viewPager.setAdapter(new GraphPagerAdapter(getSupportFragmentManager(), graphTabs.getTabCount()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                graphTabs.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        graphTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = DatabaseHelper.getInstance(getApplicationContext()).getTaskById(taskItem.getId());
        if (cursor.getCount() == 0) {
            onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_setting, menu);
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
            case R.id.task_setting:
                Intent intent = new Intent(TaskStatsActivity.this, TaskSettingActivity.class);
                intent.putExtra(INTENT_EXTRA_TASK_ITEM, taskItem);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GraphPagerAdapter extends FragmentStatePagerAdapter {

        int tabCount;

        private GraphPagerAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            //Initializing tab count
            this.tabCount= tabCount;
        }

        @Override
        public Fragment getItem(int position) {
            int taskId = taskItem.getId();
            switch (position) {
                case 0:
                    return GraphTab.newInstance(taskId, Constants.DAILY_GRAPH);
//                case 1:
//                    return GraphTab.newInstance(taskId, Constants.WEEKLY_GRAPH);
//                case 2:
//                    return GraphTab.newInstance(taskId, Constants.MONTHLY_GRAPH);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }
}
