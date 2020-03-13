package com.yzk.lightweightmvc.config;

import android.app.Activity;
import android.content.Context;

import com.tapadoo.alerter.Alerter;


public class MessageConfigMode {

    private static DefauletMessageSetting defauletMessage;

    public static void setDefauletMessageSetting(DefauletMessageSetting defauletMessage) {
        MessageConfigMode.defauletMessage = defauletMessage;
    }

    public static Alerter createMessageDialog(Activity baseController, String title, String message, int bgColor) {
        if (defauletMessage != null) {
            return defauletMessage.createMessageDialog(baseController, title, message, bgColor);
        }
        return null;
    }

    public static int setTipsMessageColor(Context appContext) {
        if (defauletMessage != null) {
            return defauletMessage.setTipsMessageColor(appContext);
        }
        return 0;
    }

    public static int setErrorMessageColor(Context appContext) {
        if (defauletMessage != null) {
            return defauletMessage.setErrorMessageColor(appContext);
        }
        return 0;
    }

    public static int setWarningMessageColor(Context appContext) {
        if (defauletMessage != null) {
            return defauletMessage.setWarningMessageColor(appContext);
        }
        return 0;
    }

    public interface DefauletMessageSetting {
        /***
         * 构建通用提示消息参数
         * @param baseController
         * @param title
         * @param message
         * @param bgColor
         * @return Alerter Alerter创建方法 详见 com.tapadoo.android:alerter 库的使用.
         */
        Alerter createMessageDialog(Activity baseController, String title, String message, int bgColor);

        int setErrorMessageColor(Context context);

        int setWarningMessageColor(Context context);

        int setTipsMessageColor(Context context);

    }

}
