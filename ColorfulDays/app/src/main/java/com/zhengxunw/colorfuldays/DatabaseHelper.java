package com.zhengxunw.colorfuldays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhengxunw on 3/15/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper mInstance = null;

    public static final String DATABASE_NAME = "mytask.db";
    public static final String TABLE_NAME = "mytask_data";
    public static final String COL1 = "ID";
    public static final String COL2 = "TASK_NAME";
    public static final String COL3 = "TASK_HOUR";
    public static final String COL4 = "IS_IDLE";
    public static final int NAME_INDEX = 2;
    public static final int HOUR_INDEX = 3;
    public static final int STATE_INDEX = 4;

    private SQLiteDatabase db;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    public static DatabaseHelper getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String creationTemplate = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s FLOAT, %s INTEGER)";
        String creationSQL = String.format(creationTemplate, TABLE_NAME, COL1, COL2, COL3, COL4);
        sqLiteDatabase.execSQL(creationSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String dropSQL = "DROP IF TABLE EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(dropSQL);
    }

    public boolean addData(TaskItem taskItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, taskItem.getTaskName());
        contentValues.put(COL3, taskItem.getTaskHour());
        contentValues.put(COL4, taskItem.isIdle());
        return db.insert(TABLE_NAME, null, contentValues) != -1;
    }

    public boolean addTime(String taskName, float timeAdded) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL3, getTime(taskName) + timeAdded);
        return db.update(TABLE_NAME, contentValues, COL2 + "='" + taskName + "'", null) != -1;
    }

    public float getTime(String taskName) {
        String queryTime = String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", TABLE_NAME, COL2, taskName);
        Cursor cursor = db.rawQuery(queryTime, null);
        cursor.moveToFirst();
        float time = cursor.getFloat(HOUR_INDEX);
        cursor.close();
        return time;
    }

    public boolean updateState(String taskName, int isIdle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL4, isIdle);
        return db.update(TABLE_NAME, contentValues, COL2 + "='" + taskName + "'", null) != -1;
    }

    public Cursor getTaskContentsByState(int taskType) {
        String sql;
        if (taskType == TaskItem.ALL) {
            sql = "SELECT rowid _id, * FROM " + TABLE_NAME;
        } else {
            sql = String.format("SELECT rowid _id, * FROM %s WHERE %s=%s", TABLE_NAME, COL4, taskType);
        }
        return db.rawQuery(sql, null);
    }

}
