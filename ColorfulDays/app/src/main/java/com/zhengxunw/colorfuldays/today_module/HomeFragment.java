package com.zhengxunw.colorfuldays.today_module;

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

import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.database.TaskItem;
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
    private Map<Integer, Long> taskToTime = new HashMap<>();
    private Map<Integer, TextView> taskToView = new HashMap<>();
    private Map<Integer, Runnable> taskToRunnable = new HashMap<>();

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
        workingListAdapter = new WorkingTaskCursorAdapter(getContext(), DatabaseHelper.getInstance(getContext()).getTaskByState(TaskItem.WORKING));
        workingTaskList.setAdapter(workingListAdapter);

        idleTaskList.setTag(Constants.IDLE_TASK_TAG);
        idleTaskList.setOnDragListener(taskDropListener);
        idleListAdapter = new IdleTaskCursorAdapter(getContext(), DatabaseHelper.getInstance(getContext()).getTaskByState(TaskItem.IDLE));
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
        idleListAdapter.changeCursor(DatabaseHelper.getInstance(getContext()).getTaskByState(TaskItem.IDLE));
        idleListAdapter.notifyDataSetChanged();
        workingListAdapter.changeCursor(DatabaseHelper.getInstance(getContext()).getTaskByState(TaskItem.WORKING));
        workingListAdapter.notifyDataSetChanged();
    }

    private void displayCurrentDate() {
        Date currentTime = TimeUtils.getTodayDate();
        mTextDate.setText(TimeUtils.DATE_FORMAT_HOME.format(currentTime));
    }

    private void serializeTaskStartTime() {
        Set<String> ret = new HashSet<>();
        for (Map.Entry<Integer, Long> entry : taskToTime.entrySet()) {
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
                String taskId = parts[0];
                String startTime = parts[1];
                taskToTime.put(Integer.valueOf(taskId), Long.valueOf(startTime));
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
            TaskItem taskItem = DatabaseHelper.getTaskItemInTaskTable(cursor);
            view.setTag(Constants.IDLE_TASK_TAG);
            final DragContext dragContext = new DragContext(view, taskItem);
            view.setOnClickListener(new idleTaskOnClickListener(taskItem));
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
        TaskItem taskItem = dragContext.taskItem;
        String srcListType = dragContext.srcView.getTag().toString();
        String destListType = destView.getTag().toString();
        if (srcListType.equals(Constants.IDLE_TASK_TAG)) {
            if (destListType.equals(Constants.WORKING_TASK_TAG)) {
                DatabaseHelper.getInstance(getContext()).updateTaskState(taskItem.getId(), TaskItem.WORKING);
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
            TaskItem taskItem = DatabaseHelper.getTaskItemInTaskTable(cursor);
            int taskId = taskItem.getId();
            taskToView.put(taskId, (TextView) view.findViewById(android.R.id.text2));
            if (!taskToRunnable.containsKey(taskId)) {
                Runnable runnable = new displayTimerOnView(taskId);
                taskToRunnable.put(taskId, runnable);
                if (!taskToTime.containsKey(taskId)) {
                    taskToTime.put(taskId, System.currentTimeMillis());
                }
            }
            view.setOnClickListener(new workingTaskOnClickListener(taskItem));
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TaskItem taskItem = DatabaseHelper.getTaskItemInTaskTable(cursor);
            String taskName = taskItem.getTaskName();
            int color = taskItem.getColor();
            view.setBackgroundColor(color);
            TextView taskView = view.findViewById(android.R.id.text1);
            TextView timeView = view.findViewById(android.R.id.text2);
            taskView.setText(taskName);
            if (!CustomizedColorUtils.isLightColor(color)) {
                taskView.setTextColor(Color.WHITE);
                timeView.setTextColor(Color.WHITE);
            }
            handler.post(taskToRunnable.get(taskItem.getId()));
        }
    }

    private void clearTaskResource(int taskId) {
        taskToView.remove(taskId);
        taskToRunnable.remove(taskId);
        taskToTime.remove(taskId);
    }

    public class DragContext {

        private View srcView;
        private TaskItem taskItem;

        DragContext(View view, TaskItem taskItem) {
            this.srcView = view;
            this.taskItem = taskItem;
        }
    }

    class idleTaskOnClickListener implements View.OnClickListener {

        TaskItem taskItem;

        idleTaskOnClickListener(TaskItem taskItem) {
            this.taskItem = taskItem;
        }

        @Override
        public void onClick(View view) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    new taskOnTimeSetListener(taskItem), 0, 0, true);
            timePickerDialog.setTitle(taskItem.getTaskName());
            timePickerDialog.show();
        }
    }

    private void switchTaskStateToIdle(int taskId) {
        DatabaseHelper.getInstance(getContext()).updateTaskState(taskId, TaskItem.IDLE);
        handler.removeCallbacks(taskToRunnable.get(taskId));
        clearTaskResource(taskId);
        notifyAdapters();
    }


    class taskOnTimeSetListener implements TimePickerDialog.OnTimeSetListener {

        TaskItem taskItem;

        taskOnTimeSetListener(TaskItem taskItem) {
            this.taskItem = taskItem;
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            float timeAdded = hourOfDay + (float)minute / 60;
            int id = taskItem.getId();
            String name = taskItem.getTaskName();
            DatabaseHelper.getInstance(getContext()).addTaskTime(id, timeAdded);
            DatabaseHelper.getInstance(getContext()).appendTransaction(TimeUtils.getCurrentDateKey(), id, timeAdded);
            if (!taskItem.isIdle()) {
                switchTaskStateToIdle(id);
                Toast.makeText(getContext(), name + " " + timeAdded, Toast.LENGTH_SHORT).show();
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

        TaskItem taskItem;

        workingTaskOnClickListener(TaskItem taskItem) {
            this.taskItem = taskItem;
        }
        @Override
        public void onClick(View view) {
            float timeAdded = TimeUtils.millisToHour(System.currentTimeMillis() - taskToTime.get(taskItem.getId()));
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    new taskOnTimeSetListener(taskItem), TimeUtils.getHour(timeAdded), TimeUtils.getMinute(timeAdded), true);
            timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    switchTaskStateToIdle(taskItem.getId());
                    Toast.makeText(getContext(), taskItem.getTaskName() + " is canceled", Toast.LENGTH_SHORT).show();
                }
            });
            timePickerDialog.setTitle(taskItem.getTaskName());
            timePickerDialog.show();
        }
    }

    class displayTimerOnView implements Runnable {
        int taskId;
        displayTimerOnView(int taskId) {
            this.taskId = taskId;
        }
        public void run() {
            long millis = System.currentTimeMillis() - taskToTime.get(taskId);
            taskToView.get(taskId).setText(TimeUtils.getCountingTime(millis));
            handler.post(this);
        }
    }

}
