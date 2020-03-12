package com.yzk.lightweightmvc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static android.content.Context.INPUT_METHOD_SERVICE;

/***
 * 继承此类的所有子类必须是公开的类.
 * @param <T>
 */
public abstract class BaseView<T extends BaseController> {

    protected T mController;

    private View rootView;

    private Unbinder bind;

    public void setView(T activity) {
        mController = activity;
        rootView = activity.findViewById(android.R.id.content);
        bind = ButterKnife.bind(this, rootView);
    }

    public abstract int setContentLayout();

    public int setLeftButtonIcon() {

        return 0;
    }

    public void showLoading(String s) {

    }

    public void hideLoding() {

    }

    public void showLoading() {
        showLoading("加载中,请稍后");
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
        if (bind != null) {
            bind.unbind();
        }
        hideLoding();
    }

    public BaseView() {

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
}
