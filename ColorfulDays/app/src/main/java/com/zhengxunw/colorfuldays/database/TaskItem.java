package com.zhengxunw.colorfuldays.database;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhengxunw on 3/16/18.
 */

public class TaskItem implements Parcelable {

    public static final int IDLE = 0;
    public static final int WORKING = 1;
    public static final int ALL = 2;


    private int id;
    private String taskName;
    private float taskHour;
    private int state;
    private int color;

    public TaskItem(int id, String taskName, float taskHour, int color, int state) {
        this.id = id;
        this.taskName = taskName;
        this.taskHour = taskHour;
        this.state = state;
        this.color = color;
    }

    @Override
    public int hashCode() {
        return taskName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return id == ((TaskItem) obj).getId();
    }

    public TaskItem() {

    }

    public String getTaskName() {
        return taskName;
    }

    public int getId() {
        return id;
    }

    public float getTaskHour() {
        return taskHour;
    }

    public int getState() {
        return state;
    }

    public boolean isIdle() {
        return state == TaskItem.IDLE;
    }

    public int getColor() {
        return color;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskHour(float taskHour) {
        this.taskHour = taskHour;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.TASK_TABLE_TASK_NAME, taskName);
        contentValues.put(DatabaseConstants.TASK_TABLE_TASK_HOUR, taskHour);
        contentValues.put(DatabaseConstants.TASK_TABLE_IS_IDLE, state);
        contentValues.put(DatabaseConstants.TASK_TABLE_COLOR, color);
        return contentValues;
    }

    protected TaskItem(Parcel in) {
        id = in.readInt();
        taskName = in.readString();
        taskHour = in.readFloat();
        state = in.readInt();
        color = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(taskName);
        dest.writeFloat(taskHour);
        dest.writeInt(state);
        dest.writeInt(color);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TaskItem> CREATOR = new Parcelable.Creator<TaskItem>() {
        @Override
        public TaskItem createFromParcel(Parcel in) {
            return new TaskItem(in);
        }

        @Override
        public TaskItem[] newArray(int size) {
            return new TaskItem[size];
        }
    };
}