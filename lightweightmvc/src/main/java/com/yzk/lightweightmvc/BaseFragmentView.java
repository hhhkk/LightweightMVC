package com.yzk.lightweightmvc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragmentView<T extends BaseFragmentController> {

    protected T mController;

    private Unbinder bind;

    protected Context mContext;

    public void setFragmentView(T controller, Context context, View view) {
        bind = ButterKnife.bind(this, view);
        mContext = context;
        mController = controller;
        initView();
    }

    protected abstract View setContentLayout(LayoutInflater inflater, @Nullable ViewGroup container);

    protected abstract void initView();

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void destroyView() {
        bind.unbind();
        mContext = null;
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

    public static void showToastShort(String message) {
        MessageUtils.showToastShort(message);
    }

    public static void showToastLong(String message) {
        MessageUtils.showToastLong(message);
    }

    public BaseFragmentView() {

    }

}
