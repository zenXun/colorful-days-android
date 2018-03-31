package com.zhengxunw.colorfuldays.today_module;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.SparseArray;
import android.widget.TextView;

import com.zhengxunw.colorfuldays.commons.Constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by wukey on 3/30/18.
 */

class HomeFragmentContext {

    private FragmentActivity fragmentActivity;
    private Context context;

    HomeFragmentContext(FragmentActivity fragmentActivity, Context context) {
        this.fragmentActivity = fragmentActivity;
        this.context = context;
    }

    private Handler handler = new Handler();
    private SparseArray<Long> taskToTime = new SparseArray<>();
    private SparseArray<TextView> taskToView = new SparseArray<>();
    private SparseArray<Runnable> taskToRunnable = new SparseArray<>();

    void serializeTaskStartTime() {
        Set<String> ret = new HashSet<>();
        for (int idx = 0; idx < taskToTime.size(); idx++) {
            int key = taskToTime.keyAt(idx);
            ret.add(key + Constants.LOCAL_STORAGE_TASK_TO_STARTTIME_SEPARATOR + String.valueOf(taskToTime.get(key)));
        }
        SharedPreferences sharedPref = fragmentActivity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(Constants.LOCAL_STORAGE_TASK_TO_STARTTIME_KEY, ret);
        editor.apply();
    }

    void loadTaskStartTime() {
        Set<String> serializedTaskTime = fragmentActivity.getPreferences(Context.MODE_PRIVATE)
                .getStringSet(Constants.LOCAL_STORAGE_TASK_TO_STARTTIME_KEY, null);
        if (serializedTaskTime != null) {
            for (String elem : serializedTaskTime) {
                String[] parts = elem.split(Constants.LOCAL_STORAGE_TASK_TO_STARTTIME_SEPARATOR);
                String taskId = parts[0];
                String startTime = parts[1];
                taskToTime.put(Integer.valueOf(taskId), Long.valueOf(startTime));
            }
        }
        context.getSharedPreferences(Constants.LOCAL_STORAGE_TASK_TO_STARTTIME_KEY, 0)
                .edit()
                .clear()
                .apply();
    }
    void clearTaskResource(int taskId) {
        taskToView.remove(taskId);
        taskToRunnable.remove(taskId);
        taskToTime.remove(taskId);
    }

    void stopRunningTask(int taskId) {
        handler.removeCallbacks(taskToRunnable.get(taskId));
    }

    void startRunningTask(Runnable task) {
        handler.post(task);
    }

    Runnable getRunningTask(int taskId) {
        return taskToRunnable.get(taskId);
    }

    long getTaskStartTime(int taskId) {
        return taskToTime.get(taskId);
    }

    TextView getTaskTextView(int taskId) {
        return taskToView.get(taskId);
    }

    void putTaskTextView(int taskId, TextView textView) {
        taskToView.put(taskId, textView);
    }

    void putRunningTask(int taskId, Runnable task) {
        taskToRunnable.put(taskId, task);
    }

    boolean isTaskRunning(int taskId) {
        return taskToRunnable.get(taskId) != null;
    }

    boolean didTaskStart(int taskId) {
        return taskToTime.get(taskId) != null;
    }

    void putTaskStartTime(int taskId, long startTime) {
        taskToTime.put(taskId, startTime);
    }


}
