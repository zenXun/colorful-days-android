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
    public static final String TASK_TABLE_NAME = "mytask_table";
    public static final String TASK_TABLE_ID = "TASK_TABLE_ID";
    public static final String TASK_NAME = "TASK_NAME";
    public static final String TASK_HOUR = "TASK_HOUR";
    public static final String IS_IDLE = "IS_IDLE";
    public static final String COLOR = "COLOR";
    public static final int TASK_TABLE_NAME_INDEX = 2;
    public static final int TASK_TABLE_HOUR_INDEX = 3;
    public static final int TASK_TABLE_STATE_INDEX = 4;
    public static final int TASK_TABLE_COLOR_INDEX = 5;

    public static final String TRANSACTION_TABLE_NAME = "transaction_table";
    public static final String TRANSACTION_TABLE_ID = "ID";
    public static final String TRANSACTION_TABLE_DATE = "TASK_DATE";
    public static final String TRANSACTION_TABLE_TASK_NAME = "TASK_NAME";
    public static final String TRANSACTION_TABLE_TASK_HOUR = "TASK_HOUR";
    public static final int TRANSACTION_TABLE_DATE_INDEX = 2;
    public static final int TRANSACTION_TABLE_NAME_INDEX = 3;
    public static final int TRANSACTION_TABLE_HOUR_INDEX = 4;

    public static final String COLOR_TABLE_NAME = "color_table";
    public static final String COLOR_TABLE_DATE = "color_date";
    public static final String COLOR_TABLE_CODE = "color_code";
    public static final int COLOR_TABLE_CODE_INDEX = 2;

    private SQLiteDatabase db;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String taskTableCreationSQLTemplate = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s FLOAT, %s INTEGER, %s INTEGER)";
        String taskTableCreationSQL = String.format(taskTableCreationSQLTemplate, TASK_TABLE_NAME, TASK_TABLE_ID, TASK_NAME, TASK_HOUR, IS_IDLE, COLOR);
        sqLiteDatabase.execSQL(taskTableCreationSQL);

        String transactionTableCreationSQLTemplate = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s FLOAT)";
        String transactionTableCreationSQL = String.format(transactionTableCreationSQLTemplate, TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_ID, TRANSACTION_TABLE_DATE, TRANSACTION_TABLE_TASK_NAME, TRANSACTION_TABLE_TASK_HOUR);
        sqLiteDatabase.execSQL(transactionTableCreationSQL);

        String colorTableCreationSQLTemplate = "CREATE TABLE %s (%s TEXT PRIMARY KEY, %s INTEGER)";
        String colorTableCreationSQL = String.format(colorTableCreationSQLTemplate, COLOR_TABLE_NAME, COLOR_TABLE_DATE, COLOR_TABLE_CODE);
        sqLiteDatabase.execSQL(colorTableCreationSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String dropTableSQLTemplate = "DROP IF TABLE EXISTS %s";
        sqLiteDatabase.execSQL(String.format(dropTableSQLTemplate, TASK_TABLE_NAME));
        sqLiteDatabase.execSQL(String.format(dropTableSQLTemplate, TRANSACTION_TABLE_NAME));
        sqLiteDatabase.execSQL(String.format(dropTableSQLTemplate, COLOR_TABLE_NAME));
    }

    /**
     * task table
     */

    public boolean insertData(TaskItem taskItem) {
        return db.insert(TASK_TABLE_NAME, null, taskItem.toContentValues()) != -1;
    }

    public boolean updateData(TaskItem taskItem) {
        String where = String.format("%s='%s'", TASK_NAME, taskItem.getTaskName());
        return db.update(TASK_TABLE_NAME, taskItem.toContentValues(), where, null) != -1;
    }

    public boolean removeTaskByName(String taskName) {
        String where = String.format("%s='%s'", TASK_NAME, taskName);
        return db.delete(TASK_TABLE_NAME, where, null) > 0;
    }

    public void addTimeByName(String taskName, float timeAdded) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_HOUR, getTime(taskName) + timeAdded);
        String where = String.format("%s='%s'", TASK_NAME, taskName);
        db.update(TASK_TABLE_NAME, contentValues, where, null);


    }

    public float getTime(String taskName) {
        String queryTime = String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", TASK_TABLE_NAME, TASK_NAME, taskName);
        Cursor cursor = db.rawQuery(queryTime, null);
        cursor.moveToFirst();
        float time = cursor.getFloat(TASK_TABLE_HOUR_INDEX);
        cursor.close();
        return time;
    }

    private boolean updateByName(String taskName, String columnName, int newVal) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, newVal);
        String where = String.format("%s='%s'", TASK_NAME, taskName);
        return db.update(TASK_TABLE_NAME, contentValues, where, null) != -1;
    }

    public boolean updateState(String taskName, int isIdle) {
        return updateByName(taskName, IS_IDLE, isIdle);
    }

    public Cursor getTaskContentsByState(int taskType) {
        String sql;
        if (taskType == TaskItem.ALL) {
            sql = "SELECT rowid _id, * FROM " + TASK_TABLE_NAME;
        } else {
            sql = String.format("SELECT rowid _id, * FROM %s WHERE %s=%s", TASK_TABLE_NAME, IS_IDLE, taskType);
        }
        return db.rawQuery(sql, null);
    }

    /**
     * transaction table
     * */
    public boolean appendTransaction(String date, String name, float hours) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_TABLE_DATE, date);
        contentValues.put(TRANSACTION_TABLE_NAME, name);
        contentValues.put(TRANSACTION_TABLE_TASK_HOUR, hours);
        return db.insert(TRANSACTION_TABLE_NAME, null, contentValues) != -1;
    }

    public Cursor queryTransactionByDate(String date) {
        String sql = String.format("SELECT * FROM %s WHERE %s='%s'", TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE, date);
        return db.rawQuery(sql, null);
    }

    public Cursor queryTransactionByDateAndName(String date, String name) {
        String sql = String.format("SELECT * FROM %s WHERE %s='%s' AND %s='%s'", TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE, date, TRANSACTION_TABLE_TASK_NAME, name);
        return db.rawQuery(sql, null);
    }

    /**
     * color table
     * */
    public boolean appendColor(String date, int colorCode) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLOR_TABLE_DATE, date);
        contentValues.put(COLOR_TABLE_CODE, colorCode);
        return db.insert(COLOR_TABLE_NAME, null, contentValues) != -1;
    }

    public Cursor queryColorByDate(String date) {
        String sql = String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", COLOR_TABLE_NAME, COLOR_TABLE_DATE, date);
        return db.rawQuery(sql, null);
    }
}
