package com.lyancafe.coffeeshop.setting.presenter;

import android.content.Context;
import android.content.Intent;

import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;
import com.lyancafe.coffeeshop.service.DownLoadService;
import com.lyancafe.coffeeshop.service.TaskService;
import com.lyancafe.coffeeshop.setting.view.SettingView;
import com.lyancafe.coffeeshop.utils.UpdateUtil;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2017/8/16.
 */

public class SettingPresenterImpl implements SettingPresenter {

    private Context mContext;
    private SettingView mSettingView;

    public SettingPresenterImpl(Context mContext, SettingView mSettingView) {
        this.mContext = mContext;
        this.mSettingView = mSettingView;
    }

    @Override
    public void resetToken() {
        UserBean userBean = LoginHelper.getUser(mContext);
        userBean.setToken("");
        LoginHelper.saveUser(mContext, userBean);
    }

    @Override
    public void checkUpdate(final boolean isShowProgress) {
        UpdateUtil.init().checkUpdate(new CustomObserver<ApkInfoBean>(mContext,true) {

            @Override
            protected void onHandleSuccess(ApkInfoBean apkInfoBean) {
                mSettingView.showUpdateConfirmDlg(apkInfoBean);
            }

        });
    }

    @Override
    public void exitLogin() {
        UserBean user = LoginHelper.getUser(mContext.getApplicationContext());
        RetrofitHttp.getRetrofit().exitLogin(user.getToken())
                .compose(RxHelper.<BaseEntity>io_main())
                .subscribe(new Consumer<BaseEntity>() {
                    @Override
                    public void accept(@NonNull BaseEntity baseEntity) throws Exception {
                        if (baseEntity.getStatus() == 0) {
                            Intent intent_update = new Intent(mContext, DownLoadService.class);
                            mContext.stopService(intent_update);
                            Intent intent_task = new Intent(mContext,TaskService.class);
                            mContext.stopService(intent_task);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                });

    }
}
