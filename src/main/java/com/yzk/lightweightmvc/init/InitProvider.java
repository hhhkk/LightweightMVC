package com.yzk.lightweightmvc.init;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yzk.lightweightmvc.base.SuperApp;

public class InitProvider extends ContentProvider {

    /**
     * 内容提供者长持有,降低异常情况下内存不足导致 SuperApp 被回收的概率
     */
    private SuperApp superApp;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        Context applicationContext = context.getApplicationContext();
        superApp = SuperApp.onCreate((Application) applicationContext);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
