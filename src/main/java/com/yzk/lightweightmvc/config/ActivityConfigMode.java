package com.yzk.lightweightmvc.config;


import android.app.Activity;
import android.view.View;

/**
 * 配置项目通用Activity 标题栏返回按钮
 */
public class ActivityConfigMode {

    private static DefaultToolBarSetting defaultToolBarSetting;

    public static void setDefaultToolBarSetting(DefaultToolBarSetting defaultToolBarSetting) {
        ActivityConfigMode.defaultToolBarSetting = defaultToolBarSetting;
    }

    public static void configToolbar(View fragmentView,Activity activity) {
        if (fragmentView != null && defaultToolBarSetting != null) {
            defaultToolBarSetting.configToolbar(fragmentView,activity);
        }
    }

    public interface DefaultToolBarSetting {
        void configToolbar(View view, Activity activity);
    }




}
