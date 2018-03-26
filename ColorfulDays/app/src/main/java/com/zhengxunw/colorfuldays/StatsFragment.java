package com.zhengxunw.colorfuldays;

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

import com.zhengxunw.colorfuldays.commons.CustomizedColorUtils;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;

import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_COLOR_KEY;
import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_HOUR_KEY;
import static com.zhengxunw.colorfuldays.commons.Constants.INTENT_EXTRA_TASK_NAME_KEY;

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
        allTaskListAdapter = new AllTaskCursorAdapter(getContext(), db.getTaskContentsByState(TaskItem.ALL));
        allTaskList.setAdapter(allTaskListAdapter);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
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
            TextView task = view.findViewById(android.R.id.text1);

            final int color = DatabaseHelper.getColorInTaskTable(cursor);
            final String taskName = DatabaseHelper.getNameInTaskTable(cursor);
            final float hour = DatabaseHelper.getHourInTaskColor(cursor);
            task.setText(taskName + " " + String.format("%.02f", hour) + " hours");
            if (!CustomizedColorUtils.isLightColor(color)) {
                task.setTextColor(Color.WHITE);
            }
            view.setBackgroundColor(color);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), TaskDetailActivity.class);
                    intent.putExtra(INTENT_EXTRA_TASK_NAME_KEY, taskName);
                    intent.putExtra(INTENT_EXTRA_TASK_HOUR_KEY, hour);
                    intent.putExtra(INTENT_EXTRA_TASK_COLOR_KEY, color);
                    startActivity(intent);
                }
            });
        }
    }
}
