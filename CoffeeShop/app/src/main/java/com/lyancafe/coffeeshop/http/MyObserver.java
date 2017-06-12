package com.lyancafe.coffeeshop.http;

import android.content.Intent;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.login.ui.LoginActivity;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/6/9.
 */

public abstract class  MyObserver<T> implements Observer<BaseEntity<T>> {

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull BaseEntity<T> tBaseEntity) {
        if(tBaseEntity.getStatus()==103){
            ToastUtil.show(CSApplication.getInstance(),"token 过期,请重新登录");
            Intent intent = new Intent(CSApplication.getInstance(), LoginActivity.class);
            CSApplication.getInstance().startActivity(intent);
            return;
        }
    }


    @Override
    public void onError(@NonNull Throwable e) {
        ToastUtil.show(CSApplication.getInstance(),e.getMessage());
    }

    @Override
    public void onComplete() {

    }
}
