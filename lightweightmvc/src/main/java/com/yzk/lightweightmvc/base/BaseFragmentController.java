package com.yzk.lightweightmvc.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.RxLifecycle;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.android.RxLifecycleAndroid;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public abstract class BaseFragmentController<P extends BaseFragmentView> extends Fragment implements LifecycleProvider<FragmentEvent> {

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    protected P view;

    /**
     * 摒弃此类写法,因为有了更好或者说,更合理的解决方案
     * @param activity
     */
//    protected Context mContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        mContext = activity;
        this.lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    public P getMvcView() throws java.lang.InstantiationException, IllegalAccessException {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType p = (ParameterizedType) type;
        Class c = (Class) p.getActualTypeArguments()[0];
        return (P) c.newInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (this.view!=null){
            return view.onCreateView(inflater,container,savedInstanceState);
        }else{
            try {
                this.view = getMvcView();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (this.view!=null){
                return view.onCreateView(inflater,container,savedInstanceState);
            }else{
                return null;
            }
        }
    }

    protected abstract void initData();

    @NonNull
    @CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return this.lifecycleSubject.hide();
    }

    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(this.lifecycleSubject, event);
    }

    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(this.lifecycleSubject);
    }


    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @CallSuper
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @CallSuper
    public void onStart() {
        super.onStart();
        this.lifecycleSubject.onNext(FragmentEvent.START);
        if (null != view) {
            view.onStart();
        }
    }

    @CallSuper
    public void onResume() {
        super.onResume();
        this.lifecycleSubject.onNext(FragmentEvent.RESUME);
        if (null != view) {
            view.onResume();
        }
    }

    @CallSuper
    public void onPause() {
        this.lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
        if (null != view) {
            view.onPause();
        }
    }

    @CallSuper
    public void onStop() {
        this.lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
        if (null != view) {
            view.onStop();
        }
    }

    @CallSuper
    public void onDestroyView() {
        this.lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @CallSuper
    public void onDetach() {
        this.lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
        if (null != view) {
            view.destroyView();
        }
    }

    @Override
    public void onDestroy() {
        this.lifecycleSubject.onNext(FragmentEvent.DESTROY);
        super.onDestroy();
    }

}
