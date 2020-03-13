package com.yzk.lightweightmvc.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;


public class SuperApp implements Application.ActivityLifecycleCallbacks {

    private static Application application;

    private static SuperApp superApp = new SuperApp();

    public static Context getAppContext() {
        return application;
    }

    public static boolean isDebug(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static boolean isDebug() {
        return isDebug(application);
    }

    public static SuperApp onCreate(Application application) {
        SuperApp.application = application;
        initLog();
        application.registerActivityLifecycleCallbacks(superApp);
        RxJavaPlugins.setErrorHandler(throwable -> throwable.printStackTrace());
        return superApp;
    }

    public static void KillMe() {
        List<Activity> activities = getActivities();
        for (Activity temp : activities) {
            temp.finish();
        }
        System.exit(0);
    }


    private static void initLog() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected void log(int priority, String tag, String message, Throwable t) {
                switch (priority) {
                    case Log.DEBUG:
                        if (t != null) {
                            Log.d(tag, message);
                        } else {
                            Log.d(tag, message, t);
                        }
                        break;
                    case Log.ERROR:
                        if (t != null) {
                            Log.e(tag, message);
                        } else {
                            Log.e(tag, message, t);
                        }
                        break;
                    case Log.INFO:
                        if (t != null) {
                            Log.i(tag, message);
                        } else {
                            Log.i(tag, message, t);
                        }
                        break;
                    case Log.WARN:
                        if (t != null) {
                            Log.w(tag, message);
                        } else {
                            Log.w(tag, message, t);
                        }
                        break;

                }
            }
        });
    }

    private static List<Activity> activities = new ArrayList<>();

    public static List<Activity> getActivities() {
        return activities;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Timber.e("onActivityCreated" + activity.getLocalClassName());
        activities.add(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Timber.e("onActivityStarted" + activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Timber.e("onActivityResumed" + activity.getLocalClassName());
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Timber.e("onActivityPaused" + activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Timber.e("onActivityStopped" + activity.getLocalClassName());
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Timber.e("onActivitySaveInstanceState" + activity.getLocalClassName());
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Timber.e("onActivityDestroyed" + activity.getLocalClassName());
        activities.remove(activity);
    }
}
