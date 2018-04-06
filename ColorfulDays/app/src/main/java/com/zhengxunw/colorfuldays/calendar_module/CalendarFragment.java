package com.zhengxunw.colorfuldays.calendar_module;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.Updatable;

import butterknife.BindView;


public class CalendarFragment extends Fragment{

    @BindView(R.id.calendar_view) private CustomizedCalendarView cv;

    public CalendarFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_color, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        cv.updateCalendar();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
