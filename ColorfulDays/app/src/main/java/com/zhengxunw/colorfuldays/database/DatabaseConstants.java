package com.zhengxunw.colorfuldays.database;

/**
 * Created by zhengxunw on 3/25/18.
 */

public class DatabaseConstants {

    public static final String DATABASE_NAME = "mytask.db";
    public static final String TASK_TABLE_NAME = "mytask_table";
    public static final String TASK_TABLE_TASK_ID = "task_id";
    public static final String TASK_TABLE_TASK_NAME = "task_name";
    public static final String TASK_TABLE_TASK_HOUR = "task_total_hour";
    public static final String TASK_TABLE_IS_IDLE = "task_state";
    public static final String TASK_TABLE_COLOR = "task_color";

    public static final String TRANSACTION_TABLE_NAME = "transaction_table";
    public static final String TRANSACTION_TABLE_ID = "transaction_id";
    public static final String TRANSACTION_TABLE_DATE = "transaction_task_date";
    public static final String TRANSACTION_TABLE_TASK_NAME = "transaction_task_name";
    public static final String TRANSACTION_TABLE_TASK_HOUR = "transaction_task_hour";

    public static final String CALENDAR_TABLE_NAME = "calendar_table";
    public static final String CALENDAR_TABLE_DATE = "calendar_date";
    public static final String CALENDAR_TABLE_COLOR = "color_code";


    public static String getTaskTableCreationSQL() {
        String template = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s FLOAT, %s INTEGER, %s INTEGER)";
        return String.format(template, TASK_TABLE_NAME, TASK_TABLE_TASK_ID, TASK_TABLE_TASK_NAME, TASK_TABLE_TASK_HOUR, TASK_TABLE_IS_IDLE, TASK_TABLE_COLOR);
    }

    public static String getTransactionTableCreationSQL() {
        String template = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s FLOAT)";
        return String.format(template, TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_ID, TASK_TABLE_TASK_ID, TRANSACTION_TABLE_DATE, TRANSACTION_TABLE_TASK_HOUR);
    }

    public static String getCalendarTableCreationSQL() {
        String template = "CREATE TABLE %s (%s TEXT PRIMARY KEY, %s INTEGER)";
        return String.format(template, CALENDAR_TABLE_NAME, CALENDAR_TABLE_DATE, CALENDAR_TABLE_COLOR);
    }

    public static String getColorOfTaskIdSQL(int taskId) {
        return String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", TASK_TABLE_NAME, TASK_TABLE_TASK_ID, taskId);
    }

    public static String getColorOfDate(String date) {
        return String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", CALENDAR_TABLE_NAME, CALENDAR_TABLE_DATE, date);
    }

    public static String getLastCalendarEntrySQL() {
        return String.format("SELECT rowid _id, * FROM %s ORDER BY %s DESC LIMIT 1;", CALENDAR_TABLE_NAME, CALENDAR_TABLE_DATE);
    }

    public static String getFirstTransactionSQL() {
        return String.format("SELECT rowid _id, * FROM %s ORDER BY %s ASC LIMIT 1;", TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE);
    }

    public static String getLastTransactionSQL() {
        return String.format("SELECT rowid _id, * FROM %s ORDER BY %s DESC LIMIT 1;", TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE);
    }

    public static String getTaskByIdSQL(int taskId) {
        return String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", TASK_TABLE_NAME, TASK_TABLE_TASK_ID, taskId);
    }

    public static String getDropTableSQL(String tableName) {
        return String.format("DROP IF TABLE EXISTS %s", tableName);
    }

    public static String getTasksQueryByStateSQL(int taskType) {
        String sql;
        if (taskType == TaskItem.ALL) {
            sql = "SELECT rowid _id, * FROM " + TASK_TABLE_NAME;
        } else {
            sql = String.format("SELECT rowid _id, * FROM %s WHERE %s=%s", TASK_TABLE_NAME, TASK_TABLE_IS_IDLE, taskType);
        }
        return sql;
    }

    public static String getTransactionByDateSQL(String date) {
        return String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE, date);
    }
}
