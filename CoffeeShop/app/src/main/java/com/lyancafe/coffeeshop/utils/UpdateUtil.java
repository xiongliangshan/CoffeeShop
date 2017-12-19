package com.lyancafe.coffeeshop.utils;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;
import com.lyancafe.coffeeshop.logger.Logger;

import io.reactivex.Observer;

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
        Logger.getLogger().log("检测App版本更新，当前使用版本 : "+curVersion);
        RetrofitHttp.getRetrofit().checkUpdate(curVersion)
                .compose(RxHelper.<BaseEntity<ApkInfoBean>>io_main())
                .subscribe(observer);
    }
}
