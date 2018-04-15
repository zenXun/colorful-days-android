package com.zhengxunw.colorfuldays.stats_module;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;
import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.commons.Constants;
import com.zhengxunw.colorfuldays.commons.GraphTab;
import com.zhengxunw.colorfuldays.commons.StatsUtils;
import com.zhengxunw.colorfuldays.commons.TaskSettingActivity;
import com.zhengxunw.colorfuldays.database.DatabaseConstants;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_START_DATE;
import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_ITEM;

public class TaskStatsActivity extends AppCompatActivity {

    @BindView(R.id.tabs_graphs) TabLayout graphTabs;
    @BindView(R.id.pager_graphs) ViewPager viewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.task_name_part) TextView taskNameTV;
    @BindView(R.id.task_hour_part) TextView taskHourTV;
    @BindView(R.id.task_start_date_tv) TextView taskStartDaysTV;
    @BindView(R.id.stats_total_days) TextView totalDaysTV;
    @BindView(R.id.stats_daily_average) TextView dailyAvgTV;
    @BindView(R.id.stats_task_note) ListView taskNotesLV;
    private DatabaseHelper db;
    private TaskItem taskItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_stats);
        ButterKnife.bind(this);
        Slidr.attach(this, Constants.slidrConfig);
        db = DatabaseHelper.getInstance(getApplicationContext());

        taskItem = getIntent().getParcelableExtra(INTENT_EXTRA_TASK_ITEM);
        StatsUtils.populateStatsRow(taskItem, findViewById(R.id.task_info_row));
        populateStatsPart();
        populateNotePart();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        graphTabs.addTab(graphTabs.newTab().setText("Daily"));
        graphTabs.addTab(graphTabs.newTab().setText("Weekly"));
        graphTabs.addTab(graphTabs.newTab().setText("Monthly"));

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

    private void populateStatsPart() {
        int id = taskItem.getId();

        Cursor cursor = db.queryUniqueTransactionsDate(id);
        int totalDays = cursor.getCount();
        cursor.close();

        float totalHours = 0f;
        Cursor totalHourCursor = db.queryTaskTotalHours(id);
        totalHourCursor.moveToFirst();
        int idx = totalHourCursor.getColumnIndex(DatabaseConstants.TRANSACTION_TABLE_TASK_HOUR);
        if (totalHourCursor.getCount() > 0 && idx >= 0) {
            totalHours = totalHourCursor.getFloat(idx);
        }
        totalHourCursor.close();

        String firstDate = db.getFirstTransactionDate(id);
        taskStartDaysTV.setText((firstDate == null ? "Haven't started yet." : "Started from:\n" + firstDate));
        totalDaysTV.setText("Days insisted:\n" + totalDays);
        dailyAvgTV.setText("Daily average is:\n" + (totalDays == 0 ? 0 : totalHours / totalDays));
    }

    private void populateNotePart() {
        ArrayList<String> notes = new ArrayList<>();
        Cursor cursor = db.queryTransactionsByTaskId(taskItem.getId(), true);
        StringBuilder sb = new StringBuilder();
        String currDate = null;
        while (cursor.moveToNext()) {
            String note = (String) DatabaseHelper.getFieldFromCursor(cursor, DatabaseConstants.TRANSACTION_TABLE_NOTE);
            if (note.isEmpty()) {
                continue;
            }
            String date = (String) DatabaseHelper.getFieldFromCursor(cursor, DatabaseConstants.TRANSACTION_TABLE_DATE);
            if (currDate == null) {
                currDate = date;
                sb.append(date + "\n" + note);
            } else if (currDate.equals(date)) {
                sb.append("\n" + note);
            } else {
                currDate = date;
                notes.add(sb.toString());
                sb = new StringBuilder();
                sb.append(date + "\n" + note);
            }
        }
        if (sb.length() > 0) {
            notes.add(sb.toString());
        }
        taskNotesLV.setAdapter(new taskNotesAdapter(getApplicationContext(), notes));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = db.getCursorTaskById(taskItem.getId());
        if (cursor.getCount() <= 0) {
            onBackPressed();
            return;
        }
        cursor.moveToFirst();
        taskItem = DatabaseHelper.getTaskItem(cursor);
        StatsUtils.populateStatsRow(taskItem, findViewById(R.id.task_info_row));
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
                case 1:
                    return GraphTab.newInstance(taskId, Constants.WEEKLY_GRAPH);
                case 2:
                    return GraphTab.newInstance(taskId, Constants.MONTHLY_GRAPH);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }

    class taskNotesAdapter extends ArrayAdapter<String> {

        private Context context;

        public taskNotesAdapter(Context context, ArrayList<String> notes) {
            super(context, 0, notes);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String note = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(this.context).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(note);
            return convertView;
        }
    }

}
