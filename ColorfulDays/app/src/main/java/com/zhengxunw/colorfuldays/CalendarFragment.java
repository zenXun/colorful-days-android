package com.zhengxunw.colorfuldays;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhengxunw.colorfuldays.commons.CustomizedColorUtils;
import com.zhengxunw.colorfuldays.commons.TimeUtils;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;


public class CalendarFragment extends Fragment {

    public CalendarFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_color, container, false);

        ListView todayTasks = view.findViewById(R.id.today_tasks);
        todayTasks.setAdapter(new todayTasksAdapter(getContext(), DatabaseHelper.getInstance(getContext()).queryTransactionByDate(TimeUtils.getCurrentDateKey())));
        CustomizedCalendarView cv = view.findViewById(R.id.calendar_view);
        cv.updateCalendar();
        return view;
    }

    class todayTasksAdapter extends CursorAdapter {

        todayTasksAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(R.layout.stats_item, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView taskPartTV = view.findViewById(R.id.task_name_part);
            TextView hourPartTV = view.findViewById(R.id.task_hour_part);

            final String taskName = DatabaseHelper.getNameInTransTable(cursor);
            final int color = DatabaseHelper.getInstance(getContext()).getTaskColor(taskName);
            final float hour = DatabaseHelper.getHourInTransTable(cursor);
            String hourPart = String.format("%.02f", hour) + " hours";
            taskPartTV.setText(taskName);
            hourPartTV.setText(hourPart);
            if (!CustomizedColorUtils.isLightColor(color)) {
                taskPartTV.setTextColor(Color.WHITE);
                hourPartTV.setTextColor(Color.WHITE);
            }
            view.setBackgroundColor(color);
        }
    }
}
