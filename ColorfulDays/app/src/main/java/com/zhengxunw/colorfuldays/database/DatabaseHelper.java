package com.zhengxunw.colorfuldays.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

import com.zhengxunw.colorfuldays.TaskItem;
import com.zhengxunw.colorfuldays.commons.CustomizedColorUtils;
import com.zhengxunw.colorfuldays.commons.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zhengxunw.colorfuldays.database.DatabaseConstants.CALENDAR_TABLE_COLOR;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.CALENDAR_TABLE_DATE;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.CALENDAR_TABLE_NAME;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.DATABASE_NAME;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_COLOR;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_IS_IDLE;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_NAME;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_TASK_HOUR;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_TASK_NAME;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TRANSACTION_TABLE_DATE;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TRANSACTION_TABLE_NAME;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TRANSACTION_TABLE_TASK_HOUR;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TRANSACTION_TABLE_TASK_NAME;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.getCalendarTableCreationSQL;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.getColorOnTaskNameSQL;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.getDropTableSQL;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.getTaskTableCreationSQL;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.getTransactionTableCreationSQL;

/**
 * Created by zhengxunw on 3/15/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mInstance = null;

    private SQLiteDatabase db;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context);
        }
        return mInstance;
    }

    public static String getNameInTaskTable(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(TASK_TABLE_TASK_NAME));
    }

    public static int getColorInTaskTable(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(TASK_TABLE_COLOR));
    }

    public static float getHourInTaskColor(Cursor cursor) {
        return cursor.getFloat(cursor.getColumnIndex(TASK_TABLE_TASK_HOUR));
    }

    public static String getNameInTransTable(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(TRANSACTION_TABLE_TASK_NAME));
    }

    public static float getHourInTransTable(Cursor cursor) {
        return cursor.getFloat(cursor.getColumnIndex(TASK_TABLE_TASK_HOUR));
    }

    public static int getColorInColorTable(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(CALENDAR_TABLE_COLOR));
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(getTaskTableCreationSQL());
        sqLiteDatabase.execSQL(getTransactionTableCreationSQL());
        sqLiteDatabase.execSQL(getCalendarTableCreationSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(getDropTableSQL(TASK_TABLE_NAME));
        sqLiteDatabase.execSQL(getDropTableSQL(TRANSACTION_TABLE_NAME));
        sqLiteDatabase.execSQL(getDropTableSQL(CALENDAR_TABLE_NAME));
    }

    /**
     * task table
     */

    public Integer getTaskColor(String taskName) {
        Cursor taskCursor = db.rawQuery(getColorOnTaskNameSQL(taskName), null);
        taskCursor.moveToFirst();
        if (taskCursor.getCount() > 0) {
            return getColorInTaskTable(taskCursor);
        }
        taskCursor.close();
        return null;
    }

    public boolean insertData(TaskItem taskItem) {
        return db.insert(TASK_TABLE_NAME, null, taskItem.toContentValues()) != -1;
    }

    public boolean updateData(TaskItem taskItem) {
        String where = String.format("%s='%s'", TASK_TABLE_TASK_NAME, taskItem.getTaskName());
        return db.update(TASK_TABLE_NAME, taskItem.toContentValues(), where, null) != -1;
    }

    public boolean deleteTask(String taskName) {
        String whereInTask = String.format("%s='%s'", TASK_TABLE_TASK_NAME, taskName);
        String whereInTrans = String.format("%s='%s'", TRANSACTION_TABLE_TASK_NAME, taskName);
        return (db.delete(TASK_TABLE_NAME, whereInTask, null) != -1) &&
                (db.delete(TRANSACTION_TABLE_NAME, whereInTrans, null) != -1);
    }

    public boolean removeTaskByName(String taskName) {
        String where = String.format("%s='%s'", TASK_TABLE_TASK_NAME, taskName);
        return db.delete(TASK_TABLE_NAME, where, null) > 0;
    }

    public boolean removeTransactionsByName(String taskName) {
        String where = String.format("%s='%s'", TRANSACTION_TABLE_TASK_NAME, taskName);
        return db.delete(TRANSACTION_TABLE_NAME, where, null) > 0;
    }

    public void addTimeByName(String taskName, float timeAdded) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_TABLE_TASK_HOUR, getTime(taskName) + timeAdded);
        String where = String.format("%s='%s'", TASK_TABLE_TASK_NAME, taskName);
        db.update(TASK_TABLE_NAME, contentValues, where, null);
    }

    public float getTime(String taskName) {
        String queryTime = String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", TASK_TABLE_NAME, TASK_TABLE_TASK_NAME, taskName);
        Cursor cursor = db.rawQuery(queryTime, null);
        cursor.moveToFirst();
        float time = cursor.getFloat(cursor.getColumnIndex(TASK_TABLE_TASK_HOUR));
        cursor.close();
        return time;
    }

    private boolean updateByName(String taskName, String columnName, int newVal) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, newVal);
        String where = String.format("%s='%s'", TASK_TABLE_TASK_NAME, taskName);
        return db.update(TASK_TABLE_NAME, contentValues, where, null) != -1;
    }

    public boolean updateState(String taskName, int isIdle) {
        return updateByName(taskName, TASK_TABLE_IS_IDLE, isIdle);
    }

    public Cursor getTaskContentsByState(int taskType) {
        String sql;
        if (taskType == TaskItem.ALL) {
            sql = "SELECT rowid _id, * FROM " + TASK_TABLE_NAME;
        } else {
            sql = String.format("SELECT rowid _id, * FROM %s WHERE %s=%s", TASK_TABLE_NAME, TASK_TABLE_IS_IDLE, taskType);
        }
        return db.rawQuery(sql, null);
    }

    /**
     * transaction table
     * */
    public boolean appendTransaction(String date, String name, float hours) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_TABLE_DATE, date);
        contentValues.put(TRANSACTION_TABLE_TASK_NAME, name);
        contentValues.put(TRANSACTION_TABLE_TASK_HOUR, hours);
        return db.insert(TRANSACTION_TABLE_NAME, null, contentValues) != -1;
    }

    public Cursor queryTransactionByDate(String date) {
        String sql = String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE, date);
        return db.rawQuery(sql, null);
    }

    private Cursor getLastTransactionEntry() {
        String sql = String.format("SELECT rowid _id, * FROM %s ORDER BY %s DESC LIMIT 1;", TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE);
        return db.rawQuery(sql, null);
    }

    private Cursor getFirstTransactionEntry() {
        String sql = String.format("SELECT rowid _id, * FROM %s ORDER BY %s ASC LIMIT 1;", TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE);
        return db.rawQuery(sql, null);
    }

    /**
     * color table
     * */
    public boolean appendCalendarEntry(String date, int colorCode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALENDAR_TABLE_DATE, date);
        contentValues.put(CALENDAR_TABLE_COLOR, colorCode);
        return db.insert(CALENDAR_TABLE_NAME, null, contentValues) != -1;
    }

    public int getDayColor(String date) {
        String sql = String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", CALENDAR_TABLE_NAME, CALENDAR_TABLE_DATE, date);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return getColorInColorTable(cursor);
        }
        return Color.WHITE;
    }

    private Cursor getLastCalendarEntry() {
        String sql = String.format("SELECT rowid _id, * FROM %s ORDER BY %s DESC LIMIT 1;", CALENDAR_TABLE_NAME, CALENDAR_TABLE_DATE);
        return db.rawQuery(sql, null);
    }

    /**
     * calendar related
     * */

    public void populateColorTable() {
        Cursor lastTransCursor = getLastTransactionEntry();
        lastTransCursor.moveToFirst();
        // transaction doesn't exist, no need to populate color
        if (lastTransCursor.getCount() == 0) {
            return;
        }
        String lastTransDate = lastTransCursor.getString(lastTransCursor.getColumnIndex(TRANSACTION_TABLE_DATE));
        lastTransCursor.close();

        Cursor lastColorEntryCursor = getLastCalendarEntry();
        lastColorEntryCursor.moveToFirst();
        // transaction exists but color doesn't exist, populate color anyway
        // need to populate dates based on all transactions
        if (lastColorEntryCursor.getCount() == 0) {
            Cursor firstTransEntryCursor = getFirstTransactionEntry();
            firstTransEntryCursor.moveToFirst();
            String firstTransDate = firstTransEntryCursor.getString(firstTransEntryCursor.getColumnIndex(TRANSACTION_TABLE_DATE));
            populate(firstTransDate, lastTransDate);
            return;
        }

        String lastColorDate = lastColorEntryCursor.getString(lastColorEntryCursor.getColumnIndex(CALENDAR_TABLE_DATE));
        lastColorEntryCursor.close();

        // transaction exists and color exists, check whether color is out-dated
        // need to populate dates from the last color date to the last transaction date
        if (!lastTransDate.equals(lastColorDate)) {
            populate(lastColorDate, lastTransDate);
        }
    }

    private void populate(String startDateKey, String endDateKey) {
        for (String key : getKeysToBePopulated(startDateKey, endDateKey)) {
            appendCalendarEntry(key, generateColorOnDate(key));
        }
    }

    private List<String> getKeysToBePopulated(String startDateKey, String endDateKey) {
        List<String> ret = new ArrayList<>();
        Calendar endDate = TimeUtils.toCalendar(endDateKey);
        Calendar startDate = TimeUtils.toCalendar(startDateKey);
        startDate.add(Calendar.DAY_OF_MONTH, 1);
        while (startDate.before(endDate)) {
            ret.add(TimeUtils.toDateStr(startDate));
            startDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        return ret;
    }

    public int generateColorOnDate(String key) {
        Map<Integer, Float> colorToHour = new HashMap<>();
        try (Cursor dateCursor = queryTransactionByDate(key)) {
            dateCursor.moveToFirst();
            if (dateCursor.getCount() == 0) {
                return Color.WHITE;
            }
            do {
                String taskName = getNameInTransTable(dateCursor);
                float hour = getHourInTransTable(dateCursor);
                Integer curColor = getTaskColor(taskName);
                if (curColor != null && hour > 0) {
                    float passHour = colorToHour.getOrDefault(curColor, 0f);
                    colorToHour.put(curColor, passHour + hour);
                }
            } while (dateCursor.moveToNext());
        }
        return CustomizedColorUtils.mixColors(colorToHour);
    }

}
