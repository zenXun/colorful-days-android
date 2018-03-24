package com.zhengxunw.colorfuldays;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {

    private static final String IDLE_TASK_TAG = "idle task";
    private static final String WORKING_TASK_TAG = "working task";
    private static final String START_TIME_MAPPING_KEY = "startTime";
    private static final String TASK_START_TIME_SEPARATOR = "_";
    private static final DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
    public static final DateFormat dateKeyFormat = new SimpleDateFormat("dd-MMM-yyyy");

    private TextView mTextDate;
    private IdleTaskCursorAdapter idleListAdapter;
    private WorkingTaskCursorAdapter workingListAdapter;
    private DatabaseHelper db;

    private Handler handler = new Handler();
    private Map<String, Long> taskToTime = new HashMap<>();
    private Map<String, TextView> taskToView = new HashMap<>();
    private Map<String, Runnable> taskToRunnable = new HashMap<>();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView idleTaskList = view.findViewById(R.id.idle_task_list);
        ListView workingTaskList = view.findViewById(R.id.working_task_list);
        mTextDate = view.findViewById(R.id.today_date);
        db = DatabaseHelper.getInstance(getContext());

        View.OnDragListener taskDropListener = new View.OnDragListener() {
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

        workingTaskList.setTag(WORKING_TASK_TAG);
        workingTaskList.setOnDragListener(taskDropListener);
        workingListAdapter = new WorkingTaskCursorAdapter(getContext(), db.getTaskContentsByState(TaskItem.WORKING));
        workingTaskList.setAdapter(workingListAdapter);

        idleTaskList.setTag(IDLE_TASK_TAG);
        idleTaskList.setOnDragListener(taskDropListener);
        idleListAdapter = new IdleTaskCursorAdapter(getContext(), db.getTaskContentsByState(TaskItem.IDLE));
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
        idleListAdapter.changeCursor(db.getTaskContentsByState(TaskItem.IDLE));
        idleListAdapter.notifyDataSetChanged();
        workingListAdapter.changeCursor(db.getTaskContentsByState(TaskItem.WORKING));
        workingListAdapter.notifyDataSetChanged();
    }

    private void displayCurrentDate() {
        Date currentTime = Calendar.getInstance().getTime();
        mTextDate.setText(dateFormat.format(currentTime));
    }

    private void serializeTaskStartTime() {
        Set<String> ret = new HashSet<>();
        for (Map.Entry<String, Long> entry : taskToTime.entrySet()) {
            ret.add(entry.getKey() + "_" + String.valueOf(entry.getValue()));
        }
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(START_TIME_MAPPING_KEY, ret);
        editor.apply();
    }

    private void loadTaskStartTime() {
        Set<String> serializedTaskTime = getActivity().getPreferences(Context.MODE_PRIVATE)
                .getStringSet(START_TIME_MAPPING_KEY, null);
        if (serializedTaskTime != null) {
            for (String elem : serializedTaskTime) {
                String[] parts = elem.split(TASK_START_TIME_SEPARATOR);
                String taskName = parts[0];
                String startTime = parts[1];
                taskToTime.put(taskName, Long.valueOf(startTime));
            }
        }
        getContext().getSharedPreferences(START_TIME_MAPPING_KEY, 0).edit().clear().apply();
    }

    public static String getCurrentDate() {
        return dateKeyFormat.format(Calendar.getInstance().getTime());
    }

    public class IdleTaskCursorAdapter extends android.widget.CursorAdapter {

        IdleTaskCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, final Cursor cursor, final ViewGroup viewGroup) {
            View view = LayoutInflater.from(context)
                    .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            final String taskName = cursor.getString(DatabaseHelper.TASK_TABLE_NAME_INDEX);
            view.setTag(IDLE_TASK_TAG);
            final DragContext dragContext = new DragContext(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new TimePickerDialog(getContext(),
                            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                            new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            float timeAdded = hourOfDay + (float)minute / 60;
                            db.addTimeByName(taskName, timeAdded);
                            db.appendTransaction(getCurrentDate(), taskName, timeAdded);
                            notifyAdapters();
                        }
                    }, 0, 0, true).show();
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
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
            });
            view.setOnDragListener(new View.OnDragListener() {
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
            });
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            view.setBackgroundColor(cursor.getInt(DatabaseHelper.TASK_TABLE_COLOR_INDEX));
            TextView task = view.findViewById(android.R.id.text1);
            task.setText(cursor.getString(DatabaseHelper.TASK_TABLE_NAME_INDEX));
        }
    }

    private void dropOperation(DragContext dragContext, View destView) {
        String taskName = ((TextView)dragContext.srcView).getText().toString();
        String srcListType = dragContext.srcView.getTag().toString();
        String destListType = destView.getTag().toString();
        if (srcListType.equals(IDLE_TASK_TAG)) {
            if (destListType.equals(WORKING_TASK_TAG)) {
                db.updateState(taskName, TaskItem.WORKING);
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
            view.setBackgroundColor(Color.CYAN);
            final String taskName = cursor.getString(DatabaseHelper.TASK_TABLE_NAME_INDEX);
            taskToView.put(taskName, (TextView) view.findViewById(android.R.id.text2));
            if (!taskToRunnable.containsKey(taskName)) {
                Runnable runnable = new displayTimerOnView(taskName);
                taskToRunnable.put(taskName, runnable);
                if (!taskToTime.containsKey(taskName)) {
                    taskToTime.put(taskName, System.currentTimeMillis());
                }
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    float timeAdded = (float)(System.currentTimeMillis() - taskToTime.get(taskName)) / 3600000;
                    int hour = (int) timeAdded;
                    int minute = (int) ((timeAdded - hour) * 60);
                    new TimePickerDialog(getContext(),
                            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                            new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            float timeAdded = hourOfDay + (float)minute / 60;
                            db.addTimeByName(taskName, timeAdded);
                            db.appendTransaction(getCurrentDate(), taskName, timeAdded);
                            Toast.makeText(getContext(), String.valueOf(timeAdded), Toast.LENGTH_SHORT).show();
                            handler.removeCallbacks(taskToRunnable.get(taskName));
                            taskToView.remove(taskName);
                            taskToRunnable.remove(taskName);
                            taskToTime.remove(taskName);
                            db.updateState(taskName, TaskItem.IDLE);
                            notifyAdapters();
                        }
                    }, hour, minute, true).show();
                }
            });
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            view.setBackgroundColor(cursor.getInt(DatabaseHelper.TASK_TABLE_COLOR_INDEX));
            TextView taskView = view.findViewById(android.R.id.text1);
            String taskName = cursor.getString(DatabaseHelper.TASK_TABLE_NAME_INDEX);
            taskView.setText(taskName);
            handler.post(taskToRunnable.get(taskName));
        }
    }

    public class DragContext {

        private View srcView;

        DragContext(View view) {
            this.srcView = view;
        }
    }

    class displayTimerOnView implements Runnable {
        String taskName;
        displayTimerOnView(String taskName) {
            this.taskName = taskName;
        }
        public void run() {
            long millis = System.currentTimeMillis() - taskToTime.get(taskName);
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            taskToView.get(taskName).setText(String.format("%d:%02d", minutes, seconds));
            handler.post(this);
        }
    }

}
