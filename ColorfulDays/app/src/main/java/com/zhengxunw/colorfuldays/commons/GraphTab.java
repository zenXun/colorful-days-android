package com.zhengxunw.colorfuldays.commons;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.database.DatabaseConstants;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GraphTab extends Fragment {

    private final static String TASK_ID = "taskId";
    private final static String GRAPH_TYPE = "graphType";
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
        displayGraph(getArguments().getInt(TASK_ID), getArguments().getInt(GRAPH_TYPE));
        return view;
    }

    private void displayGraph(int taskId, int graphType) {
        List<BarEntry> entries = new ArrayList<>();
        String[] labels = new String[7];

        populateEntryAndLabel(taskId, entries, labels, graphType);
        setGraph(entries, labels, graphType);
    }

    private void populateEntryAndLabel(int taskId, List<BarEntry> entries, String[] labels, int graphType) {

        Calendar cal = Calendar.getInstance();


        int calUnit = getCalendarUnit(graphType);
        cal.add(calUnit, -6);
        for (int i = 0; i < 7; i++) {
            Cursor cursor = db.queryHourByGraphType(graphType, taskId, cal);
            cursor.moveToFirst();
            labels[i] = TimeUtils.getLabel(cal, graphType);
            if (cursor.getCount() > 0) {
                float time = (Float) DatabaseHelper.getFieldFromCursor(cursor, DatabaseConstants.TRANSACTION_TABLE_TASK_HOUR);
                entries.add(new BarEntry(i, time));
            } else {
                entries.add(new BarEntry(i, 0));
            }
            cal.add(calUnit, 1);
        }
    }

    private int getCalendarUnit(int graphType) {
        int ret = Calendar.DAY_OF_WEEK;
        switch (graphType) {
            case Constants.WEEKLY_GRAPH:
                ret = Calendar.WEEK_OF_MONTH;
                break;
            case Constants.MONTHLY_GRAPH:
                ret = Calendar.MONTH;
                break;
            default:
                break;
        }
        return ret;
    }

    private void setGraph(List<BarEntry> entries, String[] labels, int graphType) {
        BarDataSet barDataSet = new BarDataSet(entries, "time spent (in hour)");
        BarData barData = new BarData(barDataSet);
        barChart.getXAxis().setValueFormatter(new LabelFormatter(labels));
        barChart.setData(barData);
        Description description = new Description();
        if (graphType == Constants.DAILY_GRAPH) {
            description.setText("The most recent week's record.");
        }
        barChart.setDescription(description);
        barChart.setTouchEnabled(false);
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
