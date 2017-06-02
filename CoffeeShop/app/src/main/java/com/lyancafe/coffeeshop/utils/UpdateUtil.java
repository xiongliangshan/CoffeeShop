package com.lyancafe.coffeeshop.utils;

import android.content.Context;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;
import com.lyancafe.coffeeshop.widget.LoadingDialog;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/27.
 */

public class UpdateUtil {

    private static UpdateUtil singleton;

    public static UpdateUtil init() {
        if (singleton == null) {
            synchronized (UpdateUtil.class) {
                singleton = new UpdateUtil();
            }
        }
        return singleton;
    }

    /**
     * 检测版本更新
     */
    public void checkUpdate(Observer<BaseEntity<ApkInfoBean>> observer) {
        int curVersion = MyUtil.getVersionCode(CSApplication.getInstance());
        RetrofitHttp.getRetrofit().checkUpdate(curVersion)
                .compose(RxHelper.<BaseEntity<ApkInfoBean>>io_main())
                .subscribe(observer);
    }
}
