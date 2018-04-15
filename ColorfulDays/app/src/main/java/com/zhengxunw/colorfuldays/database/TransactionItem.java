package com.zhengxunw.colorfuldays.database;

import android.content.ContentValues;

public class TransactionItem {

    private int taskID;
    private String transactionDate;
    private float transactionHour;
    private String transactionNote;

    public TransactionItem(int taskID, String transactionDate, float transactionHour, String transactionNote) {
        this.taskID = taskID;
        this.transactionDate = transactionDate;
        this.transactionHour = transactionHour;
        this.transactionNote = transactionNote;
    }


    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.TASK_TABLE_TASK_ID, taskID);
        contentValues.put(DatabaseConstants.TRANSACTION_TABLE_DATE, transactionDate);
        contentValues.put(DatabaseConstants.TRANSACTION_TABLE_TASK_HOUR, transactionHour);
        contentValues.put(DatabaseConstants.TRANSACTION_TABLE_NOTE, transactionNote);
        return contentValues;
    }

}
