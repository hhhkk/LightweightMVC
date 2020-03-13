package com.yzk.lightweightmvc.base;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.yzk.lightweightmvc.config.ButterknifeConfigMode;
import com.yzk.lightweightmvc.config.LoadingConfigMode;
import com.yzk.lightweightmvc.utils.MessageUtils;
import com.yzk.lightweightmvc.utils.QMUIStatusBarHelper;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static android.content.Context.INPUT_METHOD_SERVICE;

/***
 * 继承此类的所有子类必须是公开的类.
 * @param <T>
 */
public abstract class BaseView<T extends BaseController> {

    protected T mController;

    private View rootView;


    /***
     * 重写此方法决定是不是执行默认toolbar设置
     * @return true 执行,false 不执行,默认return true
     */
    public boolean isConfigToolbar() {
        return true;
    }

    public void setView(T activity) {
        mController = activity;
        activity.setRequestedOrientation(setRequestedOrientation());
        if (useTranslucent()) {
            QMUIStatusBarHelper.translucent(activity); //沉浸式状态栏
        }
        rootView = activity.findViewById(android.R.id.content);
        ButterknifeConfigMode.bindView(this, rootView);
    }

    /**
     * 是否启用状态栏沉浸,默认开启
     */
    private boolean useTranslucent() {

        return true;
    }

    /**
     * 默认强制竖屏,如需改变屏幕方向,请重写此方法,并返回对应的屏幕方向
     *
     * @return
     */
    public int setRequestedOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }


    public abstract int setContentLayout();

    public void showLoading(String message) {
        LoadingConfigMode.showLoading(message, mController);
    }

    public void showLoading() {
        LoadingConfigMode.showLoading("加载中,请稍候~", mController);
    }

    Disposable closeTimer;

    public void showLoadingWithTimeOut(String message, int second) {
        Dialog dialog = LoadingConfigMode.showLoading(message, mController);
        Observable.timer(second, TimeUnit.SECONDS)
                .compose(mController.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        closeTimer = d;
                    }

                    @Override
                    public void onNext(Long value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (dialog.isShowing()) {
                            hideLoding();
                        }
                    }
                });
        dialog.setOnDismissListener((dialogInterface) -> {
            if (closeTimer != null) {
                closeTimer.dispose();
                closeTimer = null;
            }
            dialog.setOnDismissListener(null);
        });
    }

    public void hideLoding() {
        LoadingConfigMode.hideLoding(mController);
    }

    public View setContentLayout(LayoutInflater layoutInflater) {

        return null;
    }

    public void showMessage(String message) {
        MessageUtils.showMessage(message);
    }

    public void showErrorMessage(String message) {
        MessageUtils.showErrorMessage(message);
    }

    public void showWarningMessage(String message) {
        MessageUtils.showWarningMessage(message);
    }

    public void showToastMessage(String message) {
        MessageUtils.showToastShort(message);
    }

    public void onCreated() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onDestroy() {
        ButterknifeConfigMode.unbindView(this);
        LoadingConfigMode.releaseCreateDialog(mController);
    }

    public boolean isResume() {
        if (mController == null) {
            Timber.e("isResume Please check the control layer initialization status");
            return false;
        }
        return mController.isResume();
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) mController.getSystemService(INPUT_METHOD_SERVICE);
        View v = mController.getWindow().peekDecorView();
        if (null != v && null != imm) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseView<?> baseView = (BaseView<?>) o;
        return Objects.equals(mController, baseView.mController) &&
                Objects.equals(rootView, baseView.rootView);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mController, rootView);
    }
}
