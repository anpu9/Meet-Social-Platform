package com.tianyuyang.framework.helper;

import android.app.Activity;
import android.app.ActivityManager;

import java.util.HashSet;

/**
 * FileName: ActivityHelper
 * Founder: tianyuyang
 * Profile: 统计Activity
 */
public class ActivityHelper {

    private static ActivityHelper instance = new ActivityHelper();

    private static HashSet<Activity> hashSet = new HashSet<>();

    private ActivityHelper() {

    }

    public static ActivityHelper getInstance() {
        return instance;
    }

    /**
     * 填充
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        try {
            hashSet.add(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出
     */
    public void exit() {
        try {
            for (Activity activity : hashSet) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
