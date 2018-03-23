package com.zhengxunw.colorfuldays;

/**
 * Created by zhengxunw on 3/16/18.
 */

public class TaskItem {

    public static final int IDLE = 0;
    public static final int WORKING = 1;
    public static final int ALL = 2;


    private String taskName;
    private float taskHour;
    private int isIdle;
    private int color;

    TaskItem(String taskName, float taskHour, int color) {
        this.taskName = taskName;
        this.taskHour = taskHour;
        this.isIdle = IDLE;
        this.color = color;
    }

    public String getTaskName() {
        return taskName;
    }

    public float getTaskHour() {
        return taskHour;
    }

    public int isIdle() {
        return isIdle;
    }

    public int getColor() {
        return color;
    }
}
