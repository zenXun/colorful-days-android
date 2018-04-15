package com.zhengxunw.colorfuldays.commons;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.database.DatabaseConstants;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GraphTab extends Fragment {

    private final static String TASK_ID = "taskId";
    private final static String GRAPH_TYPE = "graphType";
    private final static String TASK_GOAL = "taskGoal";
    private final static String GOAL_TYPE = "taskType";
    private TaskItem taskItem;
    private int taskId;
    private int graphType;

    private Unbinder unbinder;
    private DatabaseHelper db;
    @BindView(R.id.chart) BarChart barChart;

    public static GraphTab newInstance(int taskId, int graphType) {

        Bundle args = new Bundle();
        GraphTab fragment = new GraphTab();
        args.putInt(TASK_ID, taskId);
        args.putInt(GRAPH_TYPE, graphType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats_graph, container, false);
        unbinder = ButterKnife.bind(this, view);
        db = DatabaseHelper.getInstance(getContext());
        Bundle args = getArguments();
        taskId = args.getInt(TASK_ID);
        graphType = args.getInt(GRAPH_TYPE);
        updateTaskItem();
        displayGraph(taskId, graphType, taskItem.getGoal(), taskItem.getGoalType());
        return view;
    }

    private void updateTaskItem() {
        Cursor cursor = db.getTaskById(taskId);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            taskItem = DatabaseHelper.getTaskItem(cursor);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTaskItem();
        displayGraph(taskItem.getId(), graphType, taskItem.getGoal(), taskItem.getGoalType());
    }

    private void displayGraph(int taskId, int graphType, int goal, int goalType) {
        List<BarEntry> entries = new ArrayList<>();
        String[] labels = new String[7];
        populateEntryAndLabel(taskId, entries, labels, graphType);
        setGraph(entries, labels, getGoalHour(goal, goalType)[graphType], graphType);
    }

    private int[] getGoalHour(int goal, int goalType) {
        int[] goals = new int[3];
        if (goalType == Constants.DAILY_GOAL) {
            goals[Constants.DAILY_GOAL] = goal;
        } else {
            goals[Constants.DAILY_GOAL] = 0;
        }
        if (goalType == Constants.WEEKLY_GOAL) {
            goals[Constants.WEEKLY_GOAL] = goal;
        } else {
            goals[Constants.WEEKLY_GOAL] = goals[Constants.DAILY_GOAL] * 7;
        }
        if (goalType == Constants.MONTHLY_GOAL) {
            goals[Constants.MONTHLY_GOAL] = goal;
        } else {
            goals[Constants.MONTHLY_GOAL] = Math.max(
                    goals[Constants.WEEKLY_GOAL] * 4, goals[Constants.DAILY_GOAL * 30]
            );
        }
        return goals;
    }

    private void populateEntryAndLabel(int taskId, List<BarEntry> entries, String[] labels, int graphType) {

        Calendar cal = Calendar.getInstance();

        int calUnit = getCalendarUnit(graphType);
        cal.add(calUnit, -6);
        for (int i = 0; i < 7; i++) {
            labels[i] = TimeUtils.getLabel(cal, graphType);
            entries.add(new BarEntry(i, getHourByGraphType(graphType, cal)));
        }
    }

    private float getHourByGraphType(int graphType, Calendar cal) {
        float ret = 0f;
        int currUnit = Calendar.DAY_OF_WEEK;
        if (graphType == Constants.WEEKLY_GRAPH) {
            currUnit = Calendar.WEEK_OF_YEAR;
        } else if (graphType == Constants.MONTHLY_GRAPH) {
            currUnit = Calendar.MONTH;
        }
        int curr = cal.get(currUnit);
        do {
            ret += getDayHour(cal);
            cal.add(Calendar.DAY_OF_WEEK, 1);
        } while (cal.get(currUnit) == curr);
        return ret;
    }

    private float getDayHour(Calendar cal) {
        Cursor cursor = db.queryHourByDate(taskId, cal);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return (Float) DatabaseHelper.getFieldFromCursor(cursor, DatabaseConstants.TRANSACTION_TABLE_TASK_HOUR);
        }
        return 0f;
    }

    private int getCalendarUnit(int graphType) {
        int ret = Calendar.DAY_OF_WEEK;
        switch (graphType) {
            case Constants.WEEKLY_GRAPH:
                return Calendar.WEEK_OF_MONTH;
            case Constants.MONTHLY_GRAPH:
                return Calendar.MONTH;
        }
        return ret;
    }

    private void setGraph(List<BarEntry> entries, String[] labels, int goal, int graphType) {
        BarDataSet barDataSet = new BarDataSet(entries, "time spent (in hour)");
        BarData barData = new BarData(barDataSet);
        barChart.getXAxis().setValueFormatter(new LabelFormatter(labels));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.setData(barData);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.setTouchEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);
        if (graphType == Constants.WEEKLY_GRAPH) {
            barChart.getXAxis().setLabelRotationAngle(-20);
        }
        barChart.getDescription().setEnabled(false);
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        float entryMax = entries.stream().max(Comparator.comparing(BarEntry::getY)).get().getY();
        leftAxis.setAxisMaximum(Math.max(entryMax, goal));
        leftAxis.removeAllLimitLines();
        leftAxis.setStartAtZero(true);
        if (goal > 0) {
            LimitLine ll = new LimitLine(goal);
            ll.setLineColor(Color.RED);
            ll.setLineWidth(4f);
            leftAxis.addLimitLine(ll);
        }
    }

    private class LabelFormatter implements IAxisValueFormatter {
        private final String[] mLabels;

        public LabelFormatter(String[] labels) {
            mLabels = labels;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mLabels[(int) value];
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
