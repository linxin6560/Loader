package com.levylin.lib.net;

import com.levylin.lib.net.listener.OnLoadListener;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * 请求订阅者
 * Created by LinXin on 2016/4/11 19:47.
 */
class ResponseSubscriber<T> extends ResourceSubscriber<T> {

    private OnLoadListener<T> listener;

    ResponseSubscriber(OnLoadListener<T> listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        listener.onStart();
    }

    @Override
    public void onComplete() {
        listener = null;//释放内存
    }

    @Override
    public void onError(Throwable e) {
        listener.onError(e);
    }

    @Override
    public void onNext(T response) {
        listener.onSuccess(response);
    }
}
