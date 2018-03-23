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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String IDLE_TASK_TAG = "idle task";
    private static final String WORKING_TASK_TAG = "working task";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView mTextDate;
    private static final DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
    private IdleTaskCursorAdapter idleListAdapter;
    private WorkingTaskCursorAdapter workingListAdapter;
    private DatabaseHelper db;
    private static final String START_TIME_MAPPING_KEY = "startTime";
    private static final String TASK_START_TIME_SEPARTOR = "_";

    Handler handler = new Handler();
    Map<String, Long> taskToTime = new HashMap<>();
    Map<String, TextView> taskToView = new HashMap<>();
    Map<String, Runnable> taskToRunnable = new HashMap<>();

    private View.OnDragListener taskDragListener = new View.OnDragListener() {
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
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ListView idleTaskList = view.findViewById(R.id.idle_task_list);
        ListView workingTaskList = view.findViewById(R.id.working_task_list);
        mTextDate = view.findViewById(R.id.today_date);

        db = DatabaseHelper.getInstance(getContext());

        workingTaskList.setTag(WORKING_TASK_TAG);
        workingTaskList.setOnDragListener(taskDragListener);
        workingListAdapter = new WorkingTaskCursorAdapter(getContext(), db.getTaskContentsByState(TaskItem.WORKING));
        workingTaskList.setAdapter(workingListAdapter);

        idleTaskList.setTag(IDLE_TASK_TAG);
        idleTaskList.setOnDragListener(taskDragListener);
        idleListAdapter = new IdleTaskCursorAdapter(getContext(), db.getTaskContentsByState(TaskItem.IDLE));
        idleTaskList.setAdapter(idleListAdapter);

        displayCurrentDate();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        notifyAdapters();
        displayCurrentDate();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        loadTaskStartTime(sharedPref.getStringSet(START_TIME_MAPPING_KEY, null));
        context.getSharedPreferences(START_TIME_MAPPING_KEY, 0).edit().clear().apply();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(START_TIME_MAPPING_KEY, serializeTaskStartTime());
        editor.apply();
    }

    private Set<String> serializeTaskStartTime() {
        Set<String> ret = new HashSet<>();
        for (Map.Entry<String, Long> entry : taskToTime.entrySet()) {
            ret.add(entry.getKey() + "_" + String.valueOf(entry.getValue()));
        }
        return ret;
    }

    private void loadTaskStartTime(Set<String> serializedTaskTime) {
        if (serializedTaskTime != null) {
            for (String elem : serializedTaskTime) {
                String[] parts = elem.split(TASK_START_TIME_SEPARTOR);
                String taskName = parts[0];
                String startTime = parts[1];
                taskToTime.put(taskName, Long.valueOf(startTime));
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class IdleTaskCursorAdapter extends android.widget.CursorAdapter {

        public IdleTaskCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, final Cursor cursor, final ViewGroup viewGroup) {
            View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            final String taskName = cursor.getString(DatabaseHelper.NAME_INDEX);
            view.setTag(IDLE_TASK_TAG);
            final ListView srcListView = (ListView) viewGroup;
            final DragContext dragContext = new DragContext(view, (CursorAdapter) srcListView.getAdapter());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar c = Calendar.getInstance();
                    new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                            float timeAdded = hourOfDay + (float)minute / 60;
                            db.addTime(taskName, timeAdded);
                            notifyAdapters();
                        }
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
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
            view.setBackgroundColor(cursor.getInt(DatabaseHelper.COLOR_INDEX));
            TextView task = view.findViewById(android.R.id.text1);
            task.setText(cursor.getString(DatabaseHelper.NAME_INDEX));
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
            View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, viewGroup, false);
            view.setBackgroundColor(Color.CYAN);
            final String taskName = cursor.getString(DatabaseHelper.NAME_INDEX);
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
                    db.addTime(taskName, timeAdded);
                    Toast.makeText(getContext(), String.valueOf(timeAdded), Toast.LENGTH_SHORT).show();
                    handler.removeCallbacks(taskToRunnable.get(taskName));

                    taskToView.remove(taskName);
                    taskToRunnable.remove(taskName);
                    taskToTime.remove(taskName);
                    db.updateState(((TextView)view.findViewById(android.R.id.text1)).getText().toString(), TaskItem.IDLE);
                    notifyAdapters();
                }
            });
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            view.setBackgroundColor(cursor.getInt(DatabaseHelper.COLOR_INDEX));
            TextView taskView = view.findViewById(android.R.id.text1);
            String taskName = cursor.getString(DatabaseHelper.NAME_INDEX);
            taskView.setText(taskName);
            handler.post(taskToRunnable.get(taskName));
        }
    }

    public class DragContext {

        private View srcView;
        private CursorAdapter cursorAdapter;

        DragContext(View view, CursorAdapter cursorAdapter) {
            this.srcView = view;
            this.cursorAdapter = cursorAdapter;
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
