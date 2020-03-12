package com.yzk.lightweightmvc.network;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cliped.douzhuan.R;
import com.cliped.douzhuan.app.App;
import com.cliped.douzhuan.base.BaseController;
import com.cliped.douzhuan.entity.UserBean;
import com.cliped.douzhuan.page.login.LoginActivity;
import com.cliped.douzhuan.page.main.discover.analysis.AccountAnalysisActivity;
import com.cliped.douzhuan.page.main.mine.activitydialog.DownlineNotifyWindowActivity;
import com.cliped.douzhuan.page.main.mine.attention.MyAttentionActivity;
import com.cliped.douzhuan.utils.AlertDialogUtils;
import com.cliped.douzhuan.utils.MessageUtils;
import com.cliped.douzhuan.utils.UserUtils;

import java.net.UnknownHostException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;
import timber.log.Timber;

public abstract class ApiCallback<M> implements Observer<ResponseInfo<M>> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onError(Throwable e) {
        onResultError(null, e);
        e.printStackTrace();
    }

    @Override
    public void onNext(ResponseInfo<M> mResponseInfo) {
//        mResponseInfo.code = -1; 模拟服务器异常
//        mResponseInfo.code = 12; //模拟服务器异常
        try {
            if (mResponseInfo.code == 0) {
                M data = mResponseInfo.data;
                onResult(data);
            } else {
                if (mResponseInfo.code == 2) {
                    Intent intent = new Intent(App.getAppContext(), DownlineNotifyWindowActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (mResponseInfo.data instanceof UserBean) {
                        UserBean data = (UserBean) mResponseInfo.data;
                        if (data != null && data.getUser() != null) {
                            String userId = data.getUser().getUserId();
                            intent.putExtra("userId", userId);
                        }
                    }
                    if (UserUtils.isLogin()) {
                        UserBean data = UserUtils.getUserBean();
                        if (data != null && data.getUser() != null) {
                            String userId = data.getUser().getUserId();
                            intent.putExtra("userId", userId);
                        }
                    }
                    App.getAppContext().startActivity(intent);
                    UserUtils.syncUserInfo(null);
                    onResultError(null, null);
                    return;
                } else if (10 == mResponseInfo.code) {
                    UserUtils.syncUserInfo(null);
                    List<BaseController> activities = App.getActivities();
                    BaseController baseController = activities.get(activities.size() - 1);
                    AlertDialog alertDialog = AlertDialogUtils.showDialogCommon(baseController,
                            "提示",
                            mResponseInfo.msg,
                            null,
                            "我已知晓",
                            "",
                            (dialog, which) -> dialog.dismiss(),
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.setOnDismissListener(dialog -> {
                        Timber.e("onDismiss");
                        junpLogin();
                    });
                    alertDialog.findViewById(R.id.tv_alert_desc).setVisibility(View.GONE);
                    alertDialog.findViewById(R.id.btn_alert_cancel).setVisibility(View.GONE);
                    TextView viewById = (TextView) alertDialog.findViewById(R.id.tv_alert_content);
                    viewById.setGravity(Gravity.CENTER);
                    Button viewById1 = (Button) alertDialog.findViewById(R.id.btn_alert_ok);
                    viewById1.getPaint().setFakeBoldText(true);
                    alertDialog.show();
                    onResultError(mResponseInfo, null);
                    return;
                } else if (mResponseInfo.code == 12) {
                    List<BaseController> activities = App.getActivities();
                    BaseController baseController = activities.get(activities.size() - 1);
                    if (baseController instanceof MyAttentionActivity || baseController instanceof AccountAnalysisActivity) {
                        onResultError(mResponseInfo, null);
                        return;
                    }
                    AlertDialogUtils.showDialogVipTimeout(baseController, "失效提示",
                            "抖音绑定授权失效，请重新绑定",
                            "取消",
                            "确定",
                            (dialogInterface, i) -> dialogInterface.dismiss(),
                            (dialog, which) -> {
                                dialog.dismiss();
                                baseController.startActivity(new Intent(baseController, MyAttentionActivity.class));
                            });
                    onResultError(mResponseInfo, null);
                    return;
                }
                onResultError(mResponseInfo, null);
            }
        } catch (Exception e) {
            onResultError(null, e);
            e.printStackTrace();
        }

    }

    private void junpLogin() {
        List<BaseController> activities = App.getActivities();
        for (BaseController temp : activities) {
            temp.finish();
        }
        Context appContext = App.getAppContext();
        Intent intent = new Intent(appContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(intent);
    }

    public abstract void onResult(M m);

    static boolean isShowServerError;

    public void onResultError(ResponseInfo m, Throwable e) {
        if (m != null) {
            if (m.code == -1) {
                showServerDialog();
            } else {
                MessageUtils.showErrorMessage(m.msg);
            }
        } else if (e != null) {
//            if (App.isDebug(App.getAppContext()))
//                MessageUtils.showErrorMessage(e.getMessage());
            if (e instanceof UnknownHostException || e instanceof HttpException || e instanceof java.net.ConnectException || e instanceof java.net.SocketTimeoutException) {
                MessageUtils.showErrorMessage("网络异常~");
            } else if (App.isDebug(App.getAppContext())) {
                MessageUtils.showErrorMessage(e.getMessage());
            }
        }
    }

    private synchronized void showServerDialog() {
        if (isShowServerError) {
            return;
        }
        List<BaseController> activities = App.getActivities();
        for (int i = activities.size() - 1; i >= 0; i--) {
            BaseController baseController = activities.get(i);
            if (!baseController.isFinishing()) {
                AlertDialog alertDialog = AlertDialogUtils.showDialogNetUnusual(baseController, "服务器异常，我们在努力改进， 请稍后再试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isShowServerError = false;
                    }
                });
                if (isShowServerError) {
                    return;
                }
                alertDialog.show();
                isShowServerError = true;
                return;
            }
        }
    }

    @Override
    public void onComplete() {

    }


}
