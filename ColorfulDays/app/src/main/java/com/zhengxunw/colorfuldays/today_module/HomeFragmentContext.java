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
    private Map<Integer, Long> taskToTime = new HashMap<>();
    private Map<Integer, TextView> taskToView = new HashMap<>();
    private Map<Integer, Runnable> taskToRunnable = new HashMap<>();

    void serializeTaskStartTime() {
        Set<String> ret = new HashSet<>();
        for (Map.Entry<Integer, Long> entry : taskToTime.entrySet()) {
            int key = entry.getKey();
            ret.add(key + Constants.LOCAL_STORAGE_TASK_TO_STARTTIME_SEPARATOR + String.valueOf(entry.getValue()));
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
        taskToTime.remove(taskId);
    }

    boolean hasBindedTextView(int taskId) {
        return taskToView.containsKey(taskId);
    }

    void updateBindedTextView(int taskId, TextView tv) {
        taskToView.put(taskId, tv);
    }

    void stopRunningTask() {
        for (Map.Entry<Integer, Runnable> entry : taskToRunnable.entrySet()) {
            int key = entry.getKey();
            handler.removeCallbacks(entry.getValue());
        }
        taskToRunnable.clear();
        taskToView.clear();
    }

    void stopRunningTask(int taskId) {
        handler.removeCallbacks(taskToRunnable.get(taskId));
        taskToRunnable.remove(taskId);
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
        return taskToRunnable.containsKey(taskId);
    }

    boolean didTaskStart(int taskId) {
        return taskToTime.containsKey(taskId);
    }

    void putTaskStartTime(int taskId, long startTime) {
        taskToTime.put(taskId, startTime);
    }

    void clearTaskToView() {
        taskToTime.clear();
    }

    void clearRunningThreads() {
        taskToRunnable.clear();
    }

}
