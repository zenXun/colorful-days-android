package com.zhengxunw.colorfuldays.database;

/**
 * Created by zhengxunw on 3/25/18.
 */

public class DatabaseConstants {

    public static final String DATABASE_NAME = "mytask.db";
    static final String TASK_TABLE_NAME = "mytask_table";
    static final String TASK_TABLE_TASK_ID = "task_id";
    static final String TASK_TABLE_TASK_NAME = "task_name";
    static final String TASK_TABLE_TASK_HOUR = "task_total_hour";
    static final String TASK_TABLE_IS_IDLE = "task_state";
    static final String TASK_TABLE_COLOR = "task_color";

    static final String TRANSACTION_TABLE_NAME = "transaction_table";
    static final String TRANSACTION_TABLE_ID = "transaction_id";
    static final String TRANSACTION_TABLE_DATE = "transaction_task_date";
    static final String TRANSACTION_TABLE_TASK_HOUR = "transaction_task_hour";

    static final String CALENDAR_TABLE_NAME = "calendar_table";
    static final String CALENDAR_TABLE_DATE = "calendar_date";
    static final String CALENDAR_TABLE_COLOR = "color_code";


    static String getTaskTableCreationSQL() {
        String template = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s FLOAT, %s INTEGER, %s INTEGER)";
        return String.format(template, TASK_TABLE_NAME, TASK_TABLE_TASK_ID, TASK_TABLE_TASK_NAME, TASK_TABLE_TASK_HOUR, TASK_TABLE_IS_IDLE, TASK_TABLE_COLOR);
    }

    static String getTransactionTableCreationSQL() {
        String template = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s FLOAT)";
        return String.format(template, TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_ID, TASK_TABLE_TASK_ID, TRANSACTION_TABLE_DATE, TRANSACTION_TABLE_TASK_HOUR);
    }

    static String getCalendarTableCreationSQL() {
        String template = "CREATE TABLE %s (%s TEXT PRIMARY KEY, %s INTEGER)";
        return String.format(template, CALENDAR_TABLE_NAME, CALENDAR_TABLE_DATE, CALENDAR_TABLE_COLOR);
    }

    static String getColorOfTaskIdSQL(int taskId) {
        return String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", TASK_TABLE_NAME, TASK_TABLE_TASK_ID, taskId);
    }

    static String getColorOfDate(String date) {
        return String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", CALENDAR_TABLE_NAME, CALENDAR_TABLE_DATE, date);
    }

    static String getLastCalendarEntrySQL() {
        return String.format("SELECT rowid _id, * FROM %s ORDER BY %s DESC LIMIT 1;", CALENDAR_TABLE_NAME, CALENDAR_TABLE_DATE);
    }

    static String getFirstTransactionSQL(int taskID) {
        return String.format("SELECT * FROM %s WHERE %s='%s' ORDER BY %s ASC LIMIT 1;", TRANSACTION_TABLE_NAME, TASK_TABLE_TASK_ID, taskID, TRANSACTION_TABLE_DATE);
    }

    static String getLastTransactionSQL() {
        return String.format("SELECT rowid _id, * FROM %s ORDER BY %s DESC LIMIT 1;", TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE);
    }

    static String getTransactionsGroupByTaskOnDateSQL(String date) {
        return String.format("SELECT t.%s AS %s, SUM(t.%s) AS %s, tt.%s AS %s, tt.%s AS %s, tt.%s AS %s ",
                TASK_TABLE_TASK_ID, TASK_TABLE_TASK_ID, TRANSACTION_TABLE_TASK_HOUR, TRANSACTION_TABLE_TASK_HOUR, TASK_TABLE_TASK_NAME, TASK_TABLE_TASK_NAME, TASK_TABLE_COLOR, TASK_TABLE_COLOR, TASK_TABLE_IS_IDLE, TASK_TABLE_IS_IDLE)
                + String.format("FROM %s t ", TRANSACTION_TABLE_NAME)
                + String.format("INNER JOIN (SELECT * FROM %s) tt ", TASK_TABLE_NAME)
                + String.format("ON t.%s = tt.%s ", TASK_TABLE_TASK_ID, TASK_TABLE_TASK_ID)
                + String.format("WHERE t.%s='%s' GROUP BY t.%s ", TRANSACTION_TABLE_DATE, date, TASK_TABLE_TASK_ID);
    }

    static String getTaskByIdSQL(int taskId) {
        return String.format("SELECT rowid _id, * FROM %s WHERE %s='%s'", TASK_TABLE_NAME, TASK_TABLE_TASK_ID, taskId);
    }

    static String getDropTableSQL(String tableName) {
        return String.format("DROP IF TABLE EXISTS %s", tableName);
    }

    static String getTasksQueryByStateSQL(int taskType) {
        String sql;
        if (taskType == TaskItem.ALL) {
            sql = "SELECT rowid _id, * FROM " + TASK_TABLE_NAME;
        } else {
            sql = String.format("SELECT rowid _id, * FROM %s WHERE %s=%s", TASK_TABLE_NAME, TASK_TABLE_IS_IDLE, taskType);
        }
        return sql;
    }

    static String getTransactionByDateSQL(String date) {
        return String.format("SELECT * FROM %s WHERE %s='%s'", TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE, date);
    }

    static String getTransactionsByDateAndTaskSQL(String date, int taskId) {
        return String.format("SELECT * FROM %s WHERE %s='%s' AND %s='%s'", TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE, date, TASK_TABLE_TASK_ID, taskId);
    }

    static String getHoursByDateAndTaskSQL(String date, int taskId) {
        return String.format("SELECT SUM(%s) AS %s FROM %s WHERE %s='%s' AND %s='%s' GROUP BY %s",
                TRANSACTION_TABLE_TASK_HOUR, TRANSACTION_TABLE_TASK_HOUR, TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE, date, TASK_TABLE_TASK_ID, taskId, TASK_TABLE_TASK_ID);
    }

}
