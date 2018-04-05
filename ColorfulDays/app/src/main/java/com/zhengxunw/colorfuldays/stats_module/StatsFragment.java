package com.zhengxunw.colorfuldays.stats_module;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.commons.CustomizedColorUtils;
import com.zhengxunw.colorfuldays.commons.TaskDetailActivity;
import com.zhengxunw.colorfuldays.commons.TimeUtils;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_ITEM;

public class StatsFragment extends Fragment {

    private AllTaskCursorAdapter allTaskListAdapter;
    private DatabaseHelper db;
    private Context context;

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            notifyAdapters();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        db = DatabaseHelper.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.stats_fragment, container, false);
        ListView allTaskList = view.findViewById(R.id.all_task_list);
        allTaskListAdapter = new AllTaskCursorAdapter(context, db.getTaskByState(TaskItem.ALL));
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
        notifyAdapters();
    }

    private void notifyAdapters() {
        allTaskListAdapter.changeCursor(db.getTaskByState(TaskItem.ALL));
        allTaskListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
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
            TextView stripTV = view.findViewById(R.id.task_color_strip);
            TextView taskTV = view.findViewById(R.id.task_name_part);
            TextView hourTV = view.findViewById(R.id.task_hour_part);

            final TaskItem taskItem = DatabaseHelper.getTaskItemInTaskTable(cursor);
            int bgColor = taskItem.getColor();
            int txtColor = CustomizedColorUtils.getTextColor(bgColor);
            String firstDate = db.getFirstTransactionDate(taskItem.getId());

            if (firstDate != null) {
                Cursor uniqueDatesCursor = db.queryUniqueTransactionsDate(taskItem.getId());
                uniqueDatesCursor.moveToFirst();
                int days = uniqueDatesCursor.getCount();
                uniqueDatesCursor.close();
                TextView startDateTV = view.findViewById(R.id.task_start_date_tv);
                TextView lastingDaysTV = view.findViewById(R.id.task_days_tv);
                startDateTV.setText("Started from: " + firstDate);
                if (days > 0) {
                    lastingDaysTV.setText("Insisted for " + days + " days");
                }
            }
            taskTV.setText(taskItem.getTaskName());
            hourTV.setText(TimeUtils.getDisplayHour(taskItem.getTaskHour()));
            hourTV.setTextColor(txtColor);
            hourTV.setBackgroundColor(bgColor);
            stripTV.setBackgroundColor(bgColor);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), TaskDetailActivity.class);
                    intent.putExtra(INTENT_EXTRA_TASK_ITEM, taskItem);
                    startActivity(intent);
                }
            });
        }
    }
}
