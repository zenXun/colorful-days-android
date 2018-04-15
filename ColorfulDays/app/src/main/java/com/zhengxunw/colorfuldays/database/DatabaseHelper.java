package com.zhengxunw.colorfuldays.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zhengxunw.colorfuldays.commons.TimeUtils;

import java.util.Calendar;

import static com.zhengxunw.colorfuldays.database.DatabaseConstants.DATABASE_NAME;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_COLOR;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_GOAL;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_GOAL_TYPE;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_IS_IDLE;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_NAME;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_TASK_HOUR;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_TASK_ID;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TASK_TABLE_TASK_NAME;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TRANSACTION_TABLE_DATE;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TRANSACTION_TABLE_NAME;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.TRANSACTION_TABLE_TASK_HOUR;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.getCalendarTableCreationSQL;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.getRecordsByFieldsSQL;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.getTaskTableCreationSQL;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.getTasksQueryByStateSQL;
import static com.zhengxunw.colorfuldays.database.DatabaseConstants.getTransactionTableCreationSQL;

/**
 * Created by zhengxunw on 3/15/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    private static DatabaseHelper mInstance = null;

    private SQLiteDatabase db;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context);
        }
        return mInstance;
    }

    public static TaskItem getTaskItem(Cursor cursor) {
        return new TaskItem((Integer) getFieldFromCursor(cursor, TASK_TABLE_TASK_ID),
                (String) getFieldFromCursor(cursor, TASK_TABLE_TASK_NAME),
                (Float) getFieldFromCursor(cursor, TASK_TABLE_TASK_HOUR),
                (Integer) getFieldFromCursor(cursor, TASK_TABLE_COLOR),
                (Integer) getFieldFromCursor(cursor, TASK_TABLE_IS_IDLE),
                (Integer) getFieldFromCursor(cursor, TASK_TABLE_GOAL),
                (Integer) getFieldFromCursor(cursor, TASK_TABLE_GOAL_TYPE));
    }

    public static Object getFieldFromCursor(Cursor cursor, String fieldName) {
        int idx = cursor.getColumnIndexOrThrow(fieldName);
        switch (cursor.getType(idx)) {
            case Cursor.FIELD_TYPE_INTEGER:
                return cursor.getInt(idx);
            case Cursor.FIELD_TYPE_FLOAT:
                return cursor.getFloat(idx);
            case Cursor.FIELD_TYPE_STRING:
        }
        return cursor.getString(idx);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(getTaskTableCreationSQL());
        sqLiteDatabase.execSQL(getTransactionTableCreationSQL());
        sqLiteDatabase.execSQL(getCalendarTableCreationSQL());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If you need to add a new column
        if (newVersion > oldVersion && newVersion == 2) {
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s INTEGER DEFAULT 0", TASK_TABLE_NAME, TASK_TABLE_GOAL));
            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s INTEGER DEFAULT 0", TASK_TABLE_NAME, TASK_TABLE_GOAL_TYPE));
        }
    }

    public boolean addNewTask(TaskItem task) {
        return db.insert(TASK_TABLE_NAME, null, task.toContentValues()) != -1;
    }

    public boolean updateTask(TaskItem task) {
        String where = String.format("%s='%s'", TASK_TABLE_TASK_ID, task.getId());
        return db.update(TASK_TABLE_NAME, task.toContentValues(), where, null) != -1;
    }

    public boolean deleteTask(int taskId) {
        String where = String.format("%s='%s'", TASK_TABLE_TASK_ID, taskId);
        return (db.delete(TASK_TABLE_NAME, where, null) != -1)
                && (db.delete(TRANSACTION_TABLE_NAME, where, null) != -1);
    }

    public boolean addTaskTime(int taskId, float timeAdded) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_TABLE_TASK_HOUR, getTaskTimeByTaskId(taskId) + timeAdded);
        String where = String.format("%s='%s'", TASK_TABLE_TASK_ID, taskId);
        return db.update(TASK_TABLE_NAME, contentValues, where, null) != -1;
    }

    private float getTaskTimeByTaskId(int taskId) {
        Cursor cursor = db.rawQuery(DatabaseConstants.getRecordsByFieldsSQL(
                TASK_TABLE_NAME, new Pair(TASK_TABLE_TASK_ID, Integer.toString(taskId))
        ), null);
        cursor.moveToFirst();
        float time = (Float) getFieldFromCursor(cursor, TASK_TABLE_TASK_HOUR);
        cursor.close();
        return time;
    }

    public boolean updateTaskAttribute(int taskId, String columnName, int newVal) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, newVal);
        String where = String.format("%s='%s'", TASK_TABLE_TASK_ID, taskId);
        return db.update(TASK_TABLE_NAME, contentValues, where, null) != -1;
    }

    public Cursor getTaskByState(int taskType) {
        return db.rawQuery(getTasksQueryByStateSQL(taskType), null);
    }

    public String getFirstTransactionDate(int taskId) {
        Cursor cursor = db.rawQuery(DatabaseConstants.getFirstTransactionSQL(taskId), null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        }
        String ret = (String) getFieldFromCursor(cursor, TRANSACTION_TABLE_DATE);
        cursor.close();
        return ret;
    }

    public int getUniqueTransactionsDays(int taskId) {
        Cursor uniqueDatesCursor = queryUniqueTransactionsDate(taskId);
        uniqueDatesCursor.moveToFirst();
        int days = uniqueDatesCursor.getCount();
        uniqueDatesCursor.close();
        return days;
    }

    public Cursor getTaskById(int taskId) {
        return db.rawQuery(getRecordsByFieldsSQL(
                TASK_TABLE_NAME, new Pair(TASK_TABLE_TASK_ID, Integer.toString(taskId))
        ), null);
    }

    /**
     * transaction table
     * */
    public boolean appendTransaction(String date, int taskId, float hours) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_TABLE_DATE, date);
        contentValues.put(TASK_TABLE_TASK_ID, taskId);
        contentValues.put(TRANSACTION_TABLE_TASK_HOUR, hours);
        return db.insert(TRANSACTION_TABLE_NAME, null, contentValues) != -1;
    }

    public Cursor queryTransactionGroupByTaskByDate(String date) {
        return db.rawQuery(DatabaseConstants.getTransactionsGroupByTaskOnDateSQL(date), null);
    }

    public Cursor queryTransactionJoinTaskByDate(String date) {
        return db.rawQuery(DatabaseConstants.getTransactionTableJoinTaskTableOnDate(date), null);
    }

    public Cursor queryUniqueTransactionsDate(int id) {
        return db.rawQuery(DatabaseConstants.getUniqueRecordsByFieldsSQL(
                TRANSACTION_TABLE_NAME,
                TRANSACTION_TABLE_DATE,
                new Pair(TASK_TABLE_TASK_ID, Integer.toString(id))), null);
    }

    public Cursor queryTransactionByDateAndTask(String date, int id) {
        String sql = DatabaseConstants.getRecordsByFieldsSQL(TRANSACTION_TABLE_NAME, new Pair(TRANSACTION_TABLE_DATE, date), new Pair(TASK_TABLE_TASK_ID, Integer.toString(id)));
        return db.rawQuery(sql, null);
    }

    public Cursor queryHourByDate(int id, Calendar cal) {
        return queryHourByDate(TimeUtils.getDateKey(cal.getTime()), id);
    }

    private Cursor queryHourByDate(String date, int id) {
        return db.rawQuery(DatabaseConstants.getHoursByGraphTypeSQL(date, id), null);
    }

}
