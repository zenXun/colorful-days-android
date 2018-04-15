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
    public static final String TASK_TABLE_GOAL = "task_goal";
    public static final String TASK_TABLE_GOAL_TYPE = "task_goal_type";

    public static final String TRANSACTION_TABLE_NAME = "transaction_table";
    public static final String TRANSACTION_TABLE_ID = "transaction_id";
    public static final String TRANSACTION_TABLE_DATE = "transaction_task_date";
    public static final String TRANSACTION_TABLE_TASK_HOUR = "transaction_task_hour";
    public static final String TRANSACTION_TABLE_NOTE = "transaction_note";

    public static final String CALENDAR_TABLE_NAME = "calendar_table";
    public static final String CALENDAR_TABLE_DATE = "calendar_date";
    public static final String CALENDAR_TABLE_COLOR = "color_code";


    static String getTaskTableCreationSQL() {
        String template = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s FLOAT, %s INTEGER, %s INTEGER, %s FLOAT)";
        return String.format(template, TASK_TABLE_NAME, TASK_TABLE_TASK_ID, TASK_TABLE_TASK_NAME, TASK_TABLE_TASK_HOUR, TASK_TABLE_IS_IDLE, TASK_TABLE_COLOR, TASK_TABLE_GOAL);
    }

    static String getTransactionTableCreationSQL() {
        String template = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s FLOAT)";
        return String.format(template, TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_ID, TASK_TABLE_TASK_ID, TRANSACTION_TABLE_DATE, TRANSACTION_TABLE_TASK_HOUR);
    }

    static String getCalendarTableCreationSQL() {
        String template = "CREATE TABLE %s (%s TEXT PRIMARY KEY, %s INTEGER)";
        return String.format(template, CALENDAR_TABLE_NAME, CALENDAR_TABLE_DATE, CALENDAR_TABLE_COLOR);
    }

    static String getLastCalendarEntrySQL() {
        return String.format("SELECT rowid _id, * FROM %s ORDER BY %s DESC LIMIT 1;", CALENDAR_TABLE_NAME, CALENDAR_TABLE_DATE);
    }

    static String getFirstTransactionSQL(int taskID) {
        return String.format("SELECT rowid _id, * FROM %s WHERE %s='%s' ORDER BY %s ASC LIMIT 1;", TRANSACTION_TABLE_NAME, TASK_TABLE_TASK_ID, taskID, TRANSACTION_TABLE_DATE);
    }

    static String getLastTransactionSQL() {
        return String.format("SELECT rowid _id, * FROM %s ORDER BY %s DESC LIMIT 1;", TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE);
    }

    static String getTransactionsGroupByTaskOnDateSQL(String date) {
        return String.format("SELECT t.%s AS %s, SUM(t.%s) AS %s, tt.%s AS %s, tt.%s AS %s, tt.%s AS %s, tt.%s AS %s, tt.%s AS %s ",
                TASK_TABLE_TASK_ID, TASK_TABLE_TASK_ID, TRANSACTION_TABLE_TASK_HOUR, TASK_TABLE_TASK_HOUR, TASK_TABLE_TASK_NAME, TASK_TABLE_TASK_NAME, TASK_TABLE_COLOR, TASK_TABLE_COLOR, TASK_TABLE_IS_IDLE, TASK_TABLE_IS_IDLE, TASK_TABLE_GOAL, TASK_TABLE_GOAL, TASK_TABLE_GOAL_TYPE, TASK_TABLE_GOAL_TYPE)
                + String.format("FROM %s t ", TRANSACTION_TABLE_NAME)
                + String.format("INNER JOIN (SELECT * FROM %s) tt ", TASK_TABLE_NAME)
                + String.format("ON t.%s = tt.%s ", TASK_TABLE_TASK_ID, TASK_TABLE_TASK_ID)
                + String.format("WHERE t.%s='%s' GROUP BY t.%s ", TRANSACTION_TABLE_DATE, date, TASK_TABLE_TASK_ID);
    }

    static String getTransactionTableJoinTaskTableOnDate(String date) {
        return String.format("SELECT * FROM %s AS t " +
                        "INNER JOIN (SELECT * FROM %s) tt ON t.%s = tt.%s " +
                        "WHERE t.%s ='%s'",
                TRANSACTION_TABLE_NAME,
                TASK_TABLE_NAME, TASK_TABLE_TASK_ID, TASK_TABLE_TASK_ID,
                TRANSACTION_TABLE_DATE, date);
    }

    static String getDropTableSQL(String tableName) {
        return String.format("DROP IF TABLE EXISTS %s", tableName);
    }

    static String getTasksQueryByStateSQL(int taskType) {
        if (taskType == TaskItem.ALL) {
            return getRecordsByFieldsSQL(TASK_TABLE_NAME);
        }
        return getRecordsByFieldsSQL(TASK_TABLE_NAME, new Pair(TASK_TABLE_IS_IDLE, Integer.toString(taskType)));
    }

    static String getRecordsByFieldsSQL(String tableName, Pair... pairs) {
        StringBuilder builder = new StringBuilder(String.format("SELECT rowid _id, * FROM %s", tableName));
        return populatePairsIntoQuery(builder, pairs).toString();
    }

    private static StringBuilder populatePairsIntoQuery(StringBuilder builder, Pair[] pairs) {
        if (pairs.length == 0) {
            return builder;
        }
        builder.append(String.format(" WHERE %s='%s'", pairs[0].getFieldName(), pairs[0].getVal()));
        for (int i = 1; i < pairs.length; i++) {
            builder.append(String.format(" AND %s='%s'", pairs[i].getFieldName(), pairs[i].getVal()));
        }
        return builder;
    }

    static String getUniqueRecordsByFieldsSQL(String tableName, String recordName, Pair... pairs) {
        StringBuilder builder = new StringBuilder(String.format("SELECT DISTINCT(%s) FROM %s", recordName, tableName));
        return populatePairsIntoQuery(builder, pairs).toString();
    }

    static String getHoursByGraphTypeSQL(String date, int taskId) {
        return String.format("SELECT rowid _id, SUM(%s) AS %s FROM %s WHERE %s='%s' AND %s='%s' GROUP BY %s",
                TRANSACTION_TABLE_TASK_HOUR, TRANSACTION_TABLE_TASK_HOUR, TRANSACTION_TABLE_NAME, TRANSACTION_TABLE_DATE, date, TASK_TABLE_TASK_ID, taskId, TASK_TABLE_TASK_ID);
    }

    private static String getMonthFormat(int month) {
        return String.valueOf(month);
    }

}
