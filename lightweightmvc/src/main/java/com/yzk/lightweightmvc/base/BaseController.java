package com.yzk.lightweightmvc.base;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.RxLifecycle;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.RxLifecycleAndroid;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public abstract class BaseController<P extends BaseView> extends FragmentActivity implements LifecycleProvider<ActivityEvent> {

    // 绑定界面生命周期
    // bindToLifecycle();

    protected P view;

    private boolean isResume;

    protected abstract void initData();

    public P getMvcView() throws Exception {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            Type[] actualTypeArguments = p.getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                Class c = (Class) actualTypeArguments[0];
                return (P) c.newInstance();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

    @NonNull
    @CheckResult
    public final Observable<ActivityEvent> lifecycle() {
        return this.lifecycleSubject.hide();
    }

    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(this.lifecycleSubject, event);
    }

    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(this.lifecycleSubject);
    }

    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.lifecycleSubject.onNext(ActivityEvent.CREATE);
        try {
            view = getMvcView();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (view != null) {
            int i = view.setContentLayout();
            if (i != 0) {
                initView(i);
            } else {
                View view = this.view.setContentLayout(getLayoutInflater());
                if (view != null) {
                    initView(view);
                }
            }
        }
        initData();
    }

    protected boolean useTranslucent() {
        return true;
    }

    private void initView(View view) {
        setContentView(view);
        this.view.setView(this);
        this.view.onCreated();
    }

    private void initView(int i) {
        setContentView(i);
        view.setView(this);
        view.onCreated();
    }

    @CallSuper
    protected void onStart() {
        super.onStart();
        this.lifecycleSubject.onNext(ActivityEvent.START);
    }

    @CallSuper
    protected void onResume() {
        isResume = true;
        super.onResume();
        this.lifecycleSubject.onNext(ActivityEvent.RESUME);
        if (view != null) {
            view.onResume();
        }
    }

    @CallSuper
    protected void onPause() {
        isResume = false;
        this.lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
        if (view != null) {
            view.onPause();
        }
    }

    @CallSuper
    protected void onStop() {
        this.lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @CallSuper
    protected void onDestroy() {
        this.lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
        if (view != null) {
            view.onDestroy();
        }
    }


    public boolean isResume() {
        return isResume;
    }
}
