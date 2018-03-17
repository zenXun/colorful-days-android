package com.zhengxunw.colorfuldays;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Point;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


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
    private IdelTaskCursorAdapter idleListAdapter;
    private DatabaseHelper db;

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
        db = DatabaseHelper.getmInstance(getContext());
        workingTaskList.setTag(WORKING_TASK_TAG);
        workingTaskList.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        DragContext dragContext = (DragContext) dragEvent.getLocalState();
                        dropOperation(dragContext, view);
                        break;
                }
                return true;
            }
        });
        idleTaskList.setTag(IDLE_TASK_TAG);
        idleTaskList.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DROP:
                        DragContext dragContext = (DragContext) dragEvent.getLocalState();
                        dropOperation(dragContext, view);
                        break;
                }
                return true;
            }
        });
        idleListAdapter = new IdelTaskCursorAdapter(getContext(), db.getTaskContentsByState(TaskItem.IDLE));
        idleTaskList.setAdapter(idleListAdapter);

        displayCurrentDate();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        idleListAdapter.changeCursor(db.getTaskContentsByState(TaskItem.IDLE));
        idleListAdapter.notifyDataSetChanged();
        displayCurrentDate();
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public class IdelTaskCursorAdapter extends android.widget.CursorAdapter {

        public IdelTaskCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, final Cursor cursor, final ViewGroup viewGroup) {
            View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            view.setTag(IDLE_TASK_TAG);
            final ListView srcListView = (ListView) viewGroup;
            final DragContext dragContext = new DragContext(view, (CursorAdapter) srcListView.getAdapter());
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
            TextView task = view.findViewById(android.R.id.text1);
            task.setText(cursor.getString(DatabaseHelper.NAME_INDEX));
        }
    }

    private void dropOperation(DragContext dragContext, View destView) {
        String taskName = ((TextView)dragContext.srcView).getText().toString();
        String srcListType = dragContext.srcView.getTag().toString();
        String destListType = destView.getTag().toString();
        Toast.makeText(getContext(), srcListType + " " + destListType, Toast.LENGTH_SHORT).show();
//                            String taskName = dragContext.srcView.toString();
//                            String listType = dragContext.srcView.getTag().toString();
//                            if (listType.equals(IDLE_TASK_TAG)) {
//                                db.updateState(taskName, TaskItem.IDLE);
//                            } else {
//                                db.updateState(taskName, TaskItem.WORKING);
//                            }
//                            dragContext.cursorAdapter.changeCursor(db.getTaskContentsByState(TaskItem.IDLE));
//                            dragContext.cursorAdapter.notifyDataSetChanged();
//                            destAdapter.changeCursor(db.getTaskContentsByState(TaskItem.WORKING));
//                            destAdapter.notifyDataSetChanged();
    }

    public class WorkingTaskCursorAdapter extends android.widget.CursorAdapter {

        WorkingTaskCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

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
}
