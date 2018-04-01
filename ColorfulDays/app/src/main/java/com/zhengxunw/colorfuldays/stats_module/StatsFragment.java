package com.zhengxunw.colorfuldays.stats_module;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.commons.CustomizedColorUtils;
import com.zhengxunw.colorfuldays.commons.TimeUtils;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_ITEM;

public class StatsFragment extends Fragment {

    private AllTaskCursorAdapter allTaskListAdapter;
    private DatabaseHelper db;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance() {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        ListView allTaskList = view.findViewById(R.id.all_task_list);
        db = DatabaseHelper.getInstance(getContext());
        allTaskListAdapter = new AllTaskCursorAdapter(getContext(), db.getTaskByState(TaskItem.ALL));
        allTaskList.setAdapter(allTaskListAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        allTaskListAdapter.changeCursor(db.getTaskByState(TaskItem.ALL));
        allTaskListAdapter.notifyDataSetChanged();
    }

    public class AllTaskCursorAdapter extends android.widget.CursorAdapter {

        AllTaskCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(R.layout.stats_item, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView taskStripTV = view.findViewById(R.id.task_color_strip);
            TextView taskPartTV = view.findViewById(R.id.task_name_part);
            TextView hourPartTV = view.findViewById(R.id.task_hour_part);

            final TaskItem taskItem = DatabaseHelper.getTaskItemInTaskTable(cursor);
            int bgColor = taskItem.getColor();
            int txtColor = CustomizedColorUtils.getTextColor(bgColor);
            String firstDate = DatabaseHelper.getInstance(getContext()).getFirstTransactionDate(taskItem.getId());
            String hourPart = TimeUtils.getDisplayHour(taskItem.getTaskHour());

            if (firstDate != null) {
                TextView datePartTV = view.findViewById(R.id.task_start_date_tv);
                datePartTV.setText("Start from: " + firstDate);
            }
            taskPartTV.setText(taskItem.getTaskName());
            hourPartTV.setText(hourPart);
            hourPartTV.setTextColor(txtColor);
            taskStripTV.setBackgroundColor(bgColor);
            hourPartTV.setBackgroundColor(bgColor);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), TaskDetailActivity.class);
                    intent.putExtra(INTENT_EXTRA_TASK_ITEM, new TaskItem(taskItem.getId(),
                            taskItem.getTaskName(), taskItem.getTaskHour(), taskItem.getColor(), taskItem.getState()));
                    startActivity(intent);
                }
            });
        }
    }
}
