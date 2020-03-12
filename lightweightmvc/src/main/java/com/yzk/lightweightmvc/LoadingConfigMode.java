package com.yzk.lightweightmvc;

import android.app.Activity;
import android.app.Dialog;
import android.util.ArrayMap;
import timber.log.Timber;

/***
 * 配置基类
 */
public class LoadingConfigMode {

    private final static ArrayMap<Integer, Dialog> dialogs = new ArrayMap<>();

    private static DefaultLoadingDialog defaultLoadingDialog;

    public static void hideLoding(Activity activity) {
        int haseCode = activity.hashCode();
        if (dialogs.containsKey(haseCode)) {
            dialogs.get(haseCode).dismiss();
        }
    }

    public void setDefaultLoadingDialog(DefaultLoadingDialog defaultLoadingDialog) {
        this.defaultLoadingDialog = defaultLoadingDialog;
    }

    public static <T extends BaseController> Dialog showLoading(String message, Activity activity) {
        int haseCode = activity.hashCode();
        if (dialogs.containsKey(haseCode)) {
            Dialog dialog = dialogs.get(haseCode);
            if (dialog.isShowing()) {
                return dialog;
            } else {
                dialog.show();
                return dialog;
            }
        } else {
            if (defaultLoadingDialog != null) {
                Timber.e("Please check the default loading popup initialization status");
                return null;
            }
            Dialog dialog = defaultLoadingDialog.createDialog(activity);
            defaultLoadingDialog.setMessage(dialog, message);
            dialog.show();
            dialogs.put(haseCode, dialog);
            return dialog;
        }
    }

    public static void releaseCreateDialog(Activity activity) {
        int hashCode = activity.hashCode();
        if (dialogs.containsKey(hashCode)) {
            dialogs.remove(hashCode);
        }
    }

    /**
     * 加载框
     */
    public interface DefaultLoadingDialog {

        Dialog createDialog(Activity activity);

        /**
         * 每次调用showDialog都会调用
         */
        void setMessage(Dialog dialog, String message);

    }
}
