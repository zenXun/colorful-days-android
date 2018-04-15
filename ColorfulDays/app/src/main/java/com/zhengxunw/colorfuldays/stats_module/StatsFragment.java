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
import com.zhengxunw.colorfuldays.commons.StatsUtils;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_START_DATE;
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
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
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
        notifyAdapters();
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
            return LayoutInflater.from(context).inflate(R.layout.stats_task_row, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            final TaskItem taskItem = DatabaseHelper.getTaskItem(cursor);
            StatsUtils.populateStatsRow(taskItem, view);
            TextView startDateTV = view.findViewById(R.id.task_start_date_tv);
            TextView lastingDaysTV = view.findViewById(R.id.task_days_tv);
            int taskId = taskItem.getId();
            int insistedDays = 0;
            String firstDate = db.getFirstTransactionDate(taskId);
            if (firstDate != null) {
                startDateTV.setText("Started from: " + firstDate);
                insistedDays = db.getUniqueTransactionsDays(taskId);
                if (insistedDays > 0) {
                    lastingDaysTV.setText("Insisted for " + insistedDays + " days");
                }
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), TaskStatsActivity.class);
                    intent.putExtra(INTENT_EXTRA_TASK_ITEM, taskItem);
                    intent.putExtra(INTENT_EXTRA_START_DATE, firstDate);
                    startActivity(intent);
                }
            });
        }
    }
}
