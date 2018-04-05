package com.zhengxunw.colorfuldays.calendar_module;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhengxunw.colorfuldays.R;
import com.zhengxunw.colorfuldays.commons.CustomizedColorUtils;
import com.zhengxunw.colorfuldays.database.DatabaseHelper;
import com.zhengxunw.colorfuldays.commons.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by a7med on 28/06/2015.
 */
public class CustomizedCalendarView extends LinearLayout {

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    public static final String DAILY_TASK_INTENT_EXTRA_KEY = "date";

    // current displayed month
    private Calendar currentDate = TimeUtils.getCurrentCalendar();
    private Context context;
    private DatabaseHelper db;

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;

    // seasons' rainbow
    int[] rainbow = new int[] {
            R.color.summer,
            R.color.fall,
            R.color.winter,
            R.color.spring
    };

    // month-season association (northern hemisphere, sorry australia :)
    int[] monthSeason = new int[] {2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};

    public CustomizedCalendarView(Context context) {
        super(context);
    }

    public CustomizedCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CustomizedCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        db = DatabaseHelper.getInstance(context);
        inflater.inflate(R.layout.calendar_grid, this);

        assignUiElements();
        assignClickHandlers();

        updateCalendar();
    }

    private void assignUiElements() {
        // layout is inflated, assign local variables to components
        header = (LinearLayout)findViewById(R.id.calendar_header);
        btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView)findViewById(R.id.calendar_next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);
    }

    private void assignClickHandlers() {
        // add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, 1);
                updateCalendar();
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

        // pressing a day
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Date date = (Date) adapterView.getItemAtPosition(position);
                if (isCellOutsideCurrentMonth(date)) {
                    return;
                }
                Cursor cursor = DatabaseHelper.getInstance(getContext()).queryTransactionByDate(TimeUtils.getDateKey(date));
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    Intent intent = new Intent(getContext(), DailyTaskHistoryActivity.class);
                    intent.putExtra(DAILY_TASK_INTENT_EXTRA_KEY, TimeUtils.DATE_FORMAT_AS_KEY.format(date));
                    getContext().startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Blank day.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar() {

        // update grid
        grid.setAdapter(new CalendarAdapter(context, getCalendarCells()));

        // update title
        txtDate.setText(TimeUtils.DATE_FORMAT_CALENDAR_TITLE.format(currentDate.getTime()));

        // set header color according to current season
        int season = monthSeason[currentDate.get(Calendar.MONTH)];
        int color = rainbow[season];

        header.setBackgroundColor(getResources().getColor(color));
    }

    private ArrayList<Date> getCalendarCells() {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return cells;
    }

    private int generateDayColor(Date date) {
        return DatabaseHelper.getInstance(getContext()).generateColorOnDate(TimeUtils.getDateKey(date));
    }

    private class CalendarAdapter extends ArrayAdapter<Date> {

        // for view inflation
        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Date> days) {
            super(context, R.layout.calendar_day, days);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            // inflate item if it does not exist yet
            if (view == null) {
                view = inflater.inflate(R.layout.calendar_day, parent, false);
            }

            // clear styling
            ((TextView)view).setTypeface(null, Typeface.NORMAL);
            ((TextView)view).setTextColor(Color.BLACK);

            // day in question
            Date date = getItem(position);
            if (isCellOutsideCurrentMonth(date)) {
                // if this day is outside current month, grey it out
                ((TextView)view).setTextColor(getResources().getColor(R.color.greyed_out));
            } else {
                // if it is today, set it to blue/bold
                if (isCellToday(date)) {
                    ((TextView) view).setTypeface(null, Typeface.BOLD);
                }
                int bgColor = generateDayColor(date);
                ((TextView) view).setTextColor(CustomizedColorUtils.getTextColor(bgColor));
                view.setBackgroundColor(bgColor);
            }

            ((TextView)view).setText(String.valueOf(date.getDate()));

            return view;
        }
    }

    private boolean isCellToday(Date cellDate) {
        int day = cellDate.getDate();
        int month = cellDate.getMonth();
        int year = cellDate.getYear();
        Date todayDate = Calendar.getInstance().getTime();
        return (day == todayDate.getDate() && month == todayDate.getMonth() && year == todayDate.getYear());
    }

    private boolean isCellOutsideCurrentMonth(Date cellDate) {
        int month = cellDate.getMonth();
        int year = cellDate.getYear();

        // today
        Date todayInThisMonth = currentDate.getTime();
        return (month != todayInThisMonth.getMonth()) || (year != todayInThisMonth.getYear());
    }
}