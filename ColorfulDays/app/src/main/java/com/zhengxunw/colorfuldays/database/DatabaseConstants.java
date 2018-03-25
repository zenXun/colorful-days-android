package com.zhengxunw.colorfuldays.database;

/**
 * Created by zhengxunw on 3/25/18.
 */

public class DatabaseConstants {

    public static final String DATABASE_NAME = "mytask.db";
    public static final String TASK_TABLE_NAME = "mytask_table";
    public static final String TASK_TABLE_ID = "TASK_TABLE_ID";
    public static final String TASK_TABLE_TASK_NAME = "TASK_TABLE_TASK_NAME";
    public static final String TASK_TABLE_TASK_HOUR = "TASK_TABLE_TASK_HOUR";
    public static final String TASK_TABLE_IS_IDLE = "TASK_TABLE_IS_IDLE";
    public static final String TASK_TABLE_COLOR = "TASK_TABLE_COLOR";
    public static final int TASK_TABLE_NAME_INDEX = 2;
    public static final int TASK_TABLE_HOUR_INDEX = 3;
    public static final int TASK_TABLE_STATE_INDEX = 4;
    public static final int TASK_TABLE_COLOR_INDEX = 5;

    public static final String TRANSACTION_TABLE_NAME = "transaction_table";
    public static final String TRANSACTION_TABLE_ID = "ID";
    public static final String TRANSACTION_TABLE_DATE = "TASK_DATE";
    public static final String TRANSACTION_TABLE_TASK_NAME = "TASK_TABLE_TASK_NAME";
    public static final String TRANSACTION_TABLE_TASK_HOUR = "TASK_TABLE_TASK_HOUR";
    public static final int TRANSACTION_TABLE_DATE_INDEX = 2;
    public static final int TRANSACTION_TABLE_NAME_INDEX = 3;
    public static final int TRANSACTION_TABLE_HOUR_INDEX = 4;

    public static final String CALENDAR_TABLE_NAME = "calendar_table";
    public static final String CALENDAR_TABLE_DATE = "calendar_date";
    public static final String CALENDAR_TABLE_COLOR = "color_code";
    public static final int CALENDAR_TABLE_DATE_INDEX = 1;
    public static final int CALENDAR_TABLE_COLOR_INDEX = 2;


    public static String getTaskTableCreationSQL() {
        String template = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s FLOAT, %s INTEGER, %s INTEGER)";
        return String.format(template, TASK_TABLE_NAME, TASK_TABLE_ID, TASK_TABLE_TASK_NAME, TASK_TABLE_TASK_HOUR, TASK_TABLE_IS_IDLE, TASK_TABLE_COLOR);
    }

    public static String getTransactionTableCreationSQL() {
        String template = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s FLOAT)";
        return String.format(template, TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_ID, TRANSACTION_TABLE_DATE, TRANSACTION_TABLE_TASK_NAME, TRANSACTION_TABLE_TASK_HOUR);
    }

    public static String getCalendarTableCreationSQL() {
        String template = "CREATE TABLE %s (%s TEXT PRIMARY KEY, %s INTEGER)";
        return String.format(template, CALENDAR_TABLE_NAME, CALENDAR_TABLE_DATE, CALENDAR_TABLE_COLOR);
    }

    public static String getColorOnTaskNameSQL(String taskName) {
        return String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", TASK_TABLE_NAME, TASK_TABLE_TASK_NAME, taskName);
    }

    public static String getDropTableSQL(String tableName) {
        return String.format("DROP IF TABLE EXISTS %s", tableName);
    }
}
