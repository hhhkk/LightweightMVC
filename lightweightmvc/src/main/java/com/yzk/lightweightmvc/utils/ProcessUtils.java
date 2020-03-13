package com.yzk.lightweightmvc.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

import timber.log.Timber;

public class ProcessUtils {

    public static boolean isMainProcess(Context application) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcesses) {
            Timber.e("appProcess " + appProcess.processName);
            if (appProcess.pid == pid) {
                return application.getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }
}
