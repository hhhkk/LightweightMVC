package com.yzk.lightweightmvc;

import android.app.Activity;
import android.widget.Toast;
import com.tapadoo.alerter.Alerter;
import java.util.List;

/***
 * 本类方法 如果调用时机在调用 activity.finish()前一步,
 * 那么应当 先finish activity,
 * 再调用本类方法,这样做不会产生内存泄漏,也不会影响提示消息的正常弹出.
 */
public class MessageUtils {

    public static void showMessage(String title, String message, int bgColor) {
        List<Activity> activities = BaseApp.getActivities();
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity baseController = activities.get(i);
            if (!baseController.isFinishing()) {
                Alerter.create(baseController)
                        .setBackgroundColor(bgColor)
                        .setTitle(title)
                        .setText(message)
                        .show();
                return;
            }
        }

    }


    public static void showToastShort(String message) {
        List<Activity> activities = BaseApp.getActivities();
        for (int i = activities.size()-1; i >= 0; i--) {
            Activity baseController = activities.get(i);
            if (!baseController.isFinishing()) {
                Toast.makeText(baseController, message, Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public static void showToastLong(String message) {
        List<Activity> activities = BaseApp.getActivities();
        for (int i = activities.size()-1; i >= 0; i--) {
            Activity baseController = activities.get(i);
            if (!baseController.isFinishing()) {
                Toast.makeText(baseController, message, Toast.LENGTH_LONG).show();
                return;
            }
        }
    }


    public static void showMessage(String message) {
        showMessage("温馨提示", message, android.R.color.darker_gray);
    }

    public static void showErrorMessage(String message) {
        showMessage("温馨提示", message, android.R.color.holo_red_light);
    }

    public static void showWarningMessage(String message) {
        showMessage("温馨提示", message, android.R.color.holo_orange_light);
    }

}
