package com.zhengxunw.colorfuldays;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.zhengxunw.colorfuldays.commons.CustomizedColorUtils;
import com.zhengxunw.colorfuldays.commons.Constants;
import com.zhengxunw.colorfuldays.commons.TimeUtils;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {

    private TextView mTextDate;
    private IdleTaskCursorAdapter idleListAdapter;
    private WorkingTaskCursorAdapter workingListAdapter;

    private Handler handler = new Handler();
    private Map<String, Long> taskToTime = new HashMap<>();
    private Map<String, TextView> taskToView = new HashMap<>();
    private Map<String, Runnable> taskToRunnable = new HashMap<>();

    private View.OnDragListener idleTaskDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //Toast.makeText(getContext(), "Drag", Toast.LENGTH_SHORT).show();
                    break;
                case DragEvent.ACTION_DROP:
                    dropOperation((DragContext) dragEvent.getLocalState(), view);
            }
            return true;
        }
    };

    private View.OnDragListener taskDropListener = new View.OnDragListener() {

        @Override
        public boolean onDrag(View view, DragEvent dragEvent){
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    DragContext dragContext = (DragContext) dragEvent.getLocalState();
                    dropOperation(dragContext, view);
                    break;
            }
            return true;
        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView idleTaskList = view.findViewById(R.id.idle_task_list);
        ListView workingTaskList = view.findViewById(R.id.working_task_list);
        mTextDate = view.findViewById(R.id.today_date);

        workingTaskList.setTag(Constants.WORKING_TASK_TAG);
        workingTaskList.setOnDragListener(taskDropListener);
        workingListAdapter = new WorkingTaskCursorAdapter(getContext(), DatabaseHelper.getInstance(getContext()).getTaskContentsByState(TaskItem.WORKING));
        workingTaskList.setAdapter(workingListAdapter);

        idleTaskList.setTag(Constants.IDLE_TASK_TAG);
        idleTaskList.setOnDragListener(taskDropListener);
        idleListAdapter = new IdleTaskCursorAdapter(getContext(), DatabaseHelper.getInstance(getContext()).getTaskContentsByState(TaskItem.IDLE));
        idleTaskList.setAdapter(idleListAdapter);

        displayCurrentDate();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadTaskStartTime();
        notifyAdapters();
        displayCurrentDate();
    }

    @Override
    public void onStop() {
        super.onStop();
        serializeTaskStartTime();
    }

    private void notifyAdapters() {
        idleListAdapter.changeCursor(DatabaseHelper.getInstance(getContext()).getTaskContentsByState(TaskItem.IDLE));
        idleListAdapter.notifyDataSetChanged();
        workingListAdapter.changeCursor(DatabaseHelper.getInstance(getContext()).getTaskContentsByState(TaskItem.WORKING));
        workingListAdapter.notifyDataSetChanged();
    }

    private void displayCurrentDate() {
        Date currentTime = TimeUtils.getTodayDate();
        mTextDate.setText(TimeUtils.DATE_FORMAT_HOME.format(currentTime));
    }

    private void serializeTaskStartTime() {
        Set<String> ret = new HashSet<>();
        for (Map.Entry<String, Long> entry : taskToTime.entrySet()) {
            ret.add(entry.getKey() +
                    Constants.LOCAL_STORAGE_TASK_TO_STARTTIME_SEPARATOR +
                    String.valueOf(entry.getValue()));
        }
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(Constants.LOCAL_STORAGE_TASK_TO_STARTTIME_KEY, ret);
        editor.apply();
    }

    private void loadTaskStartTime() {
        Set<String> serializedTaskTime = getActivity().getPreferences(Context.MODE_PRIVATE)
                .getStringSet(Constants.LOCAL_STORAGE_TASK_TO_STARTTIME_KEY, null);
        if (serializedTaskTime != null) {
            for (String elem : serializedTaskTime) {
                String[] parts = elem.split(Constants.LOCAL_STORAGE_TASK_TO_STARTTIME_SEPARATOR);
                String taskName = parts[0];
                String startTime = parts[1];
                taskToTime.put(taskName, Long.valueOf(startTime));
            }
        }
        getContext()
                .getSharedPreferences(Constants.LOCAL_STORAGE_TASK_TO_STARTTIME_KEY, 0)
                .edit().clear().apply();
    }

    public class IdleTaskCursorAdapter extends android.widget.CursorAdapter {

        IdleTaskCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, final Cursor cursor, final ViewGroup viewGroup) {
            View view = LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            final String taskName = DatabaseHelper.getNameInTaskTable(cursor);
            view.setTag(Constants.IDLE_TASK_TAG);
            final DragContext dragContext = new DragContext(view);
            view.setOnClickListener(new idleTaskOnClickListener(taskName));
            view.setOnLongClickListener(new idleTaskOnLongClickListener(dragContext));
            view.setOnDragListener(idleTaskDragListener);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            int bgColor = DatabaseHelper.getColorInTaskTable(cursor);
            view.setBackgroundColor(bgColor);
            TextView task = view.findViewById(android.R.id.text1);
            task.setText(DatabaseHelper.getNameInTaskTable(cursor));
            if (!CustomizedColorUtils.isLightColor(bgColor)) {
                task.setTextColor(Color.WHITE);
            }
        }
    }

    private void dropOperation(DragContext dragContext, View destView) {
        String taskName = ((TextView)dragContext.srcView).getText().toString();
        String srcListType = dragContext.srcView.getTag().toString();
        String destListType = destView.getTag().toString();
        if (srcListType.equals(Constants.IDLE_TASK_TAG)) {
            if (destListType.equals(Constants.WORKING_TASK_TAG)) {
                DatabaseHelper.getInstance(getContext()).updateState(taskName, TaskItem.WORKING);
                notifyAdapters();
            }
        }
    }

    public class WorkingTaskCursorAdapter extends android.widget.CursorAdapter {

        WorkingTaskCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view = LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_list_item_2, viewGroup, false);
            final String taskName = DatabaseHelper.getNameInTaskTable(cursor);
            taskToView.put(taskName, (TextView) view.findViewById(android.R.id.text2));
            if (!taskToRunnable.containsKey(taskName)) {
                Runnable runnable = new displayTimerOnView(taskName);
                taskToRunnable.put(taskName, runnable);
                if (!taskToTime.containsKey(taskName)) {
                    taskToTime.put(taskName, System.currentTimeMillis());
                }
            }
            view.setOnClickListener(new workingTaskOnClickListener(taskName));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            view.setBackgroundColor(DatabaseHelper.getColorInTaskTable(cursor));
            TextView taskView = view.findViewById(android.R.id.text1);
            TextView timeView = view.findViewById(android.R.id.text2);
            String taskName = DatabaseHelper.getNameInTaskTable(cursor);
            int color = DatabaseHelper.getColorInTaskTable(cursor);
            taskView.setText(taskName);
            if (!CustomizedColorUtils.isLightColor(color)) {
                taskView.setTextColor(Color.WHITE);
                timeView.setTextColor(Color.WHITE);
            }
            handler.post(taskToRunnable.get(taskName));
        }
    }

    private void clearTaskResource(String taskName) {
        taskToView.remove(taskName);
        taskToRunnable.remove(taskName);
        taskToTime.remove(taskName);
    }

    public class DragContext {

        private View srcView;

        DragContext(View view) {
            this.srcView = view;
        }
    }

    class idleTaskOnClickListener implements View.OnClickListener {

        String taskName;

        idleTaskOnClickListener(String taskName) {
            this.taskName = taskName;
        }

        @Override
        public void onClick(View view) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    new taskOnTimeSetListener(taskName, true), 0, 0, true);
            timePickerDialog.setTitle(taskName);
            timePickerDialog.show();
        }
    }

    private void switchTaskStateToIdle(String taskName) {
        DatabaseHelper.getInstance(getContext()).updateState(taskName, TaskItem.IDLE);
        handler.removeCallbacks(taskToRunnable.get(taskName));
        clearTaskResource(taskName);
        notifyAdapters();
    }


    class taskOnTimeSetListener implements TimePickerDialog.OnTimeSetListener {

        String taskName;
        boolean isIdle;

        taskOnTimeSetListener(String taskName, boolean isIdle) {
            this.taskName = taskName;
            this.isIdle = isIdle;
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            float timeAdded = hourOfDay + (float)minute / 60;
            DatabaseHelper.getInstance(getContext()).addTimeByName(taskName, timeAdded);
            DatabaseHelper.getInstance(getContext()).appendTransaction(TimeUtils.getCurrentDateKey(), taskName, timeAdded);
            if (!isIdle) {
                switchTaskStateToIdle(taskName);
                Toast.makeText(getContext(), taskName + " " + timeAdded, Toast.LENGTH_SHORT).show();
            }
            notifyAdapters();
        }
    }

    class idleTaskOnLongClickListener implements View.OnLongClickListener {

        DragContext dragContext;

        idleTaskOnLongClickListener(DragContext dragContext) {
            this.dragContext = dragContext;
        }

        @Override
        public boolean onLongClick(View view) {
            view.startDragAndDrop(null, new View.DragShadowBuilder(view) {
                @Override
                public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
                    super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
                }

                @Override
                public void onDrawShadow(Canvas canvas) {
                    super.onDrawShadow(canvas);
                }
            }, dragContext, 0);
            return true;
        }
    }

    class workingTaskOnClickListener implements View.OnClickListener {

        String taskName;

        workingTaskOnClickListener(String taskName) {
            this.taskName = taskName;
        }
        @Override
        public void onClick(View view) {
            float timeAdded = TimeUtils.millisToHour(taskToTime.get(taskName));
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    new taskOnTimeSetListener(taskName, false), TimeUtils.getHour(timeAdded), TimeUtils.getMinute(timeAdded), true);
            timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    switchTaskStateToIdle(taskName);
                    Toast.makeText(getContext(), taskName + " is canceled", Toast.LENGTH_SHORT).show();
                }
            });
            timePickerDialog.setTitle(taskName);
            timePickerDialog.show();
        }
    }

    class displayTimerOnView implements Runnable {
        String taskName;
        displayTimerOnView(String taskName) {
            this.taskName = taskName;
        }
        public void run() {
            long millis = System.currentTimeMillis() - taskToTime.get(taskName);
            taskToView.get(taskName).setText(TimeUtils.getCountingTime(TimeUtils.getMinute(millis), TimeUtils.getSecond(millis)));
            handler.post(this);
        }
    }

}
