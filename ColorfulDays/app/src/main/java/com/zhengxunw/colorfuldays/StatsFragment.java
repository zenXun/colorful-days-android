package com.zhengxunw.colorfuldays;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.zhengxunw.colorfuldays.database.DatabaseConstants;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;

public class StatsFragment extends Fragment {

    public static final String TASK_NAME_KEY = "taskName";
    public static final String TASK_HOUR_KEY = "taskHour";
    public static final String TASK_COLOR_KEY = "taskColor";

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
        allTaskListAdapter = new AllTaskCursorAdapter(getContext(), db.getTaskContentsByState(TaskItem.ALL));
        allTaskList.setAdapter(allTaskListAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        allTaskListAdapter.changeCursor(db.getTaskContentsByState(TaskItem.ALL));
        allTaskListAdapter.notifyDataSetChanged();
    }

    public class AllTaskCursorAdapter extends android.widget.CursorAdapter {

        AllTaskCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(android.R.layout.select_dialog_item, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            view.setBackgroundColor(cursor.getInt(DatabaseConstants.TASK_TABLE_COLOR_INDEX));
            TextView task = view.findViewById(android.R.id.text1);
            final String taskName = cursor.getString(DatabaseConstants.TASK_TABLE_NAME_INDEX);
            final float hour = cursor.getFloat(DatabaseConstants.TASK_TABLE_HOUR_INDEX);
            final int color = cursor.getInt(DatabaseConstants.TASK_TABLE_COLOR_INDEX);
            task.setText(taskName + " " + String.format("%.02f", hour) + " hours");
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), TaskDetailActivity.class);
                    intent.putExtra(TASK_NAME_KEY, taskName);
                    intent.putExtra(TASK_HOUR_KEY, hour);
                    intent.putExtra(TASK_COLOR_KEY, color);
                    startActivity(intent);
                }
            });
        }
    }
}
