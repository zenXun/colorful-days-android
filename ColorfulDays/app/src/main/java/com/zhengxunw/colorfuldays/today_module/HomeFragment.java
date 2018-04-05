package com.zhengxunw.colorfuldays.today_module;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.commons.Constants;
import com.zhengxunw.colorfuldays.commons.CustomizedColorUtils;
import com.zhengxunw.colorfuldays.commons.TimeUtils;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.database.TaskItem;

import java.util.Date;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private TextView mTextDate;
    private IdleTaskCursorAdapter idleListAdapter;
    private WorkingTaskCursorAdapter workingListAdapter;
    private HomeFragmentContext homeContext;
    private Context context;
    private DatabaseHelper db;

    private View.OnDragListener taskDropListener = new View.OnDragListener() {

        @Override
        public boolean onDrag(View view, DragEvent dragEvent){
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    DragContext dragContext = (DragContext) dragEvent.getLocalState();
                    String srcListType = dragContext.srcView.getTag().toString();
                    String destListType = view.getTag().toString();
                    if (srcListType.equals(Constants.IDLE_TASK_TAG)
                            && destListType.equals(Constants.WORKING_TASK_TAG)) {
                        DatabaseHelper.getInstance(getContext())
                                .updateTaskState(dragContext.taskItem.getId(), TaskItem.WORKING);
                        notifyAdapters();
                    }
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        db = DatabaseHelper.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        homeContext = new HomeFragmentContext(getActivity(), getContext());
        ListView idleTaskList = view.findViewById(R.id.idle_task_list);
        ListView workingTaskList = view.findViewById(R.id.working_task_list);
        mTextDate = view.findViewById(R.id.today_date);

        workingTaskList.setTag(Constants.WORKING_TASK_TAG);
        workingTaskList.setOnDragListener(taskDropListener);
        workingListAdapter = new WorkingTaskCursorAdapter(context, db.getTaskByState(TaskItem.WORKING));
        workingTaskList.setAdapter(workingListAdapter);

        idleTaskList.setTag(Constants.IDLE_TASK_TAG);
        idleTaskList.setOnDragListener(taskDropListener);
        idleListAdapter = new IdleTaskCursorAdapter(context, db.getTaskByState(TaskItem.IDLE));
        idleTaskList.setAdapter(idleListAdapter);

        displayCurrentDate();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        homeContext.loadTaskStartTime();
        Log.d(TAG, "onStart: HomeFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyAdapters();
        displayCurrentDate();
        Log.d(TAG, "onResume: HomeFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        homeContext.stopRunningTasks();
        Log.d(TAG, "onPause: HomeFragment");
    }

    @Override
    public void onStop() {
        super.onStop();
        homeContext.serializeTaskStartTime();
        Log.d(TAG, "onStop: HomeFragment");
    }

    private void notifyAdapters() {
        idleListAdapter.changeCursor(db.getTaskByState(TaskItem.IDLE));
        idleListAdapter.notifyDataSetChanged();

        workingListAdapter.changeCursor(db.getTaskByState(TaskItem.WORKING));
        workingListAdapter.notifyDataSetChanged();
    }

    private void displayCurrentDate() {
        Date currentTime = TimeUtils.getTodayDate();
        mTextDate.setText(TimeUtils.DATE_FORMAT_HOME.format(currentTime));
    }

    public class IdleTaskCursorAdapter extends android.widget.CursorAdapter {

        IdleTaskCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, final Cursor cursor, final ViewGroup viewGroup) {
            View view = LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            view.setTag(Constants.IDLE_TASK_TAG);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.d(TAG, "bindView: idle task on HomeFragment");
            TaskItem taskItem = DatabaseHelper.getTaskItemInTaskTable(cursor);
            // listeners setup
            view.setOnClickListener(new idleTaskOnClickListener(taskItem));
            view.setOnLongClickListener(new idleTaskOnLongClickListener(new DragContext(view, taskItem)));
            view.setOnDragListener(taskDropListener);

            // text population and color setting
            int bgColor = DatabaseHelper.getColorInTaskTable(cursor);
            view.setBackgroundColor(bgColor);
            TextView task = view.findViewById(android.R.id.text1);
            task.setText(DatabaseHelper.getNameInTaskTable(cursor));
            task.setTextColor(CustomizedColorUtils.getTextColor(bgColor));
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
            view.setTag(Constants.WORKING_TASK_TAG);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.d(TAG, "bindView: working task on HomeFragment");
            TaskItem taskItem = DatabaseHelper.getTaskItemInTaskTable(cursor);
            // time counting thread setup
            int taskId = taskItem.getId();
            homeContext.putTaskTextView(taskId, (TextView) view.findViewById(android.R.id.text2));
            Runnable runnable;
            if (homeContext.isTaskRunning(taskId)) {
                runnable = homeContext.getRunningTask(taskId);
            } else {
                runnable = new displayTimerOnView(taskId, homeContext);
                homeContext.putRunningTask(taskId, runnable);
            }
            homeContext.startRunningTask(runnable);
            if (!homeContext.didTaskStart(taskId)) {
                homeContext.putTaskStartTime(taskId, System.currentTimeMillis());
            }

            view.setOnClickListener(new workingTaskOnClickListener(taskItem));

            // color setting
            int bgColor = taskItem.getColor();
            view.setBackgroundColor(bgColor);
            TextView taskView = view.findViewById(android.R.id.text1);
            TextView timeView = view.findViewById(android.R.id.text2);
            taskView.setText(taskItem.getTaskName());
            int txtColor = CustomizedColorUtils.getTextColor(bgColor);
            taskView.setTextColor(txtColor);
            timeView.setTextColor(txtColor);
        }
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
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.HoloDialog,
                    new taskOnTimeSetListener(taskItem), 0, 0, true);
            timePickerDialog.setTitle(taskItem.getTaskName());
            timePickerDialog.show();
        }
    }

    private void switchTaskStateToIdle(int taskId) {
        db.updateTaskState(taskId, TaskItem.IDLE);
        homeContext.stopRunningTask(taskId);
        homeContext.clearTaskResource(taskId);
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
            int taskId = taskItem.getId();
            if (!taskItem.isIdle()) {
                switchTaskStateToIdle(taskId);
            }
            if (timeAdded > 0) {
                db.addTaskTime(taskId, timeAdded);
                db.appendTransaction(TimeUtils.getCurrentDateKey(), taskId, timeAdded);
            }
            Toast.makeText(context, taskItem.getTaskName() + " " + timeAdded, Toast.LENGTH_SHORT).show();
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
            float timeAdded = TimeUtils.millisToHour(System.currentTimeMillis() - homeContext.getTaskStartTime(taskItem.getId()));
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.HoloDialog,
                    new taskOnTimeSetListener(taskItem), TimeUtils.getHour(timeAdded), TimeUtils.getMinute(timeAdded), true);
            timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if (!taskItem.isIdle()) {
                        switchTaskStateToIdle(taskItem.getId());
                    }
                    Toast.makeText(context, taskItem.getTaskName() + " is canceled", Toast.LENGTH_SHORT).show();
                }
            });
            timePickerDialog.setTitle(taskItem.getTaskName());
            timePickerDialog.show();
        }
    }

    class displayTimerOnView implements Runnable {

        int taskId;
        private HomeFragmentContext homeFragmentContext;

        displayTimerOnView(int taskId, HomeFragmentContext homeFragmentContext) {
            this.taskId = taskId;
            this.homeFragmentContext = homeFragmentContext;
        }

        public void run() {
            long millis = System.currentTimeMillis() - homeFragmentContext.getTaskStartTime(taskId);
            homeFragmentContext.getTaskTextView(taskId).setText(TimeUtils.getCountingTime(millis));
            homeFragmentContext.startRunningTask(this);
        }
    }

}
