package com.yzk.lightweightmvc.config;

import android.view.View;

public class ButterknifeConfigMode {

    private static ButterknifeDefaultSetting defaultSetting;

    public static void setDefaultSetting(ButterknifeDefaultSetting defaultSetting) {
        ButterknifeConfigMode.defaultSetting = defaultSetting;
    }

    public static void bindView(Object o, View fragmentView) {
        if (defaultSetting == null) {
            return;
        }
        defaultSetting.bindView(o, fragmentView);
    }

    public static void unbindView(Object o) {
        if (defaultSetting == null) {
            return;
        }
        defaultSetting.unbindView(o.hashCode());
    }

    public interface ButterknifeDefaultSetting {
        void bindView(Object o, View fragmentView);

        void unbindView(int hashCode);
    }
}

