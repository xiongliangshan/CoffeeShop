package com.lyancafe.coffeeshop.setting.presenter;

import android.content.Context;
import android.content.Intent;

import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.http.RxHelper;
import com.lyancafe.coffeeshop.service.DownLoadService;
import com.lyancafe.coffeeshop.service.TaskService;
import com.lyancafe.coffeeshop.setting.view.SettingView;
import com.lyancafe.coffeeshop.utils.UpdateUtil;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
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

    private void resetToken() {
        UserBean userBean = LoginHelper.getUser(mContext);
        userBean.setToken("");
        LoginHelper.saveUser(mContext, userBean);
    }

    @Override
    public void checkUpdate(final boolean isShowProgress) {
        UpdateUtil.init().checkUpdate(new Observer<BaseEntity<ApkInfoBean>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                if(isShowProgress){
                    mSettingView.showLoading();
                }
            }

            @Override
            public void onNext(@NonNull BaseEntity<ApkInfoBean> apkInfoBeanBaseEntity) {
                if(apkInfoBeanBaseEntity.getStatus()==0){
                    ApkInfoBean apk = apkInfoBeanBaseEntity.getData();
                    mSettingView.showUpdateConfirmDlg(apk);
                }else{
                    if(isShowProgress){
                        mSettingView.showToast(apkInfoBeanBaseEntity.getMessage());
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                if(isShowProgress){
                    mSettingView.dismissLoading();
                }
                mSettingView.showToast(e.getMessage());
            }

            @Override
            public void onComplete() {
                if(isShowProgress){
                    mSettingView.dismissLoading();
                }
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
                            resetToken();
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