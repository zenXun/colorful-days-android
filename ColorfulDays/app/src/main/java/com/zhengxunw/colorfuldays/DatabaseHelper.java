package com.zhengxunw.colorfuldays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

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
    public static final int NAME_INDEX = 2;
    public static final int HOUR_INDEX = 3;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static DatabaseHelper getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String creationTemplate = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s FLOAT)";
        String creationSQL = String.format(creationTemplate, TABLE_NAME, COL1, COL2, COL3);
        sqLiteDatabase.execSQL(creationSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String dropSQL = "DROP IF TABLE EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(dropSQL);
    }

    public boolean addData(String taskName, float taskInitialHour) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, taskName);
        contentValues.put(COL3, taskInitialHour);
        return db.insert(TABLE_NAME, null, contentValues) != -1;
    }

    public Cursor getTaskContents() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME, null);
    }

}
