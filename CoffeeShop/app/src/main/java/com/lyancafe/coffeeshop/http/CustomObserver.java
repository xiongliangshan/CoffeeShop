package com.lyancafe.coffeeshop.http;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.login.ui.LoginActivity;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.LoadingDialog;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/6/9.
 */

public abstract class CustomObserver<T> implements Observer<BaseEntity<T>> {

    private static final String TAG = "CustomObserver";
    private Context mContext;
    private boolean isShowProgress;
    private LoadingDialog mLoadinngDlg;

    public CustomObserver(Context context) {
        this.mContext = context;
        this.isShowProgress = false;
        initDialogProgress();
    }

    public CustomObserver(Context mContext, boolean isShowProgress) {
        this.mContext = mContext;
        this.isShowProgress = isShowProgress;
        initDialogProgress();
    }

    private void initDialogProgress(){
        LogUtil.d(TAG,"initDialogProgress : showProgress = "+isShowProgress);
        if(isShowProgress){
            if(mLoadinngDlg==null){
                mLoadinngDlg = new LoadingDialog(mContext);
            }
        }
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        LogUtil.d(TAG,"onSubscribe  ");
        if(isShowProgress){
            Activity activity = mLoadinngDlg.getOwnerActivity();
            if(activity!=null && !activity.isFinishing()){
                mLoadinngDlg.show();
            }

        }
    }

    @Override
    public void onNext(@NonNull BaseEntity<T> tBaseEntity) {
        LogUtil.d(TAG,"onNext  , status = "+tBaseEntity.getStatus());
        if(tBaseEntity.getStatus()==0){
            onHandleSuccess(tBaseEntity.getData());
        }else if(tBaseEntity.getStatus()==103){
            ToastUtil.show(mContext,tBaseEntity.getMessage());
            UserBean userBean = LoginHelper.getUser(mContext);
            userBean.setToken("");
            LoginHelper.saveUser(mContext, userBean);
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            if(mContext!=null && mContext instanceof Activity){
                ((Activity) mContext).finish();
            }
        }else{
            onHandleFailed(tBaseEntity.getMessage());
        }

    }


    @Override
    public void onError(@NonNull Throwable e) {
        LogUtil.e(TAG,"onError :"+e.getMessage());
        if(isShowProgress){
            if(mLoadinngDlg!=null && mLoadinngDlg.isShowing()){
                Activity activity = mLoadinngDlg.getOwnerActivity();
                if(activity!=null && !activity.isFinishing()){
                    mLoadinngDlg.dismiss();
                }
            }
        }
        ToastUtil.show(CSApplication.getInstance(),e.getMessage());
    }

    @Override
    public void onComplete() {
        LogUtil.d(TAG,"onComplete");
        if(isShowProgress){
            if(mLoadinngDlg!=null && mLoadinngDlg.isShowing()){
                Activity activity = mLoadinngDlg.getOwnerActivity();
                if(activity!=null && !activity.isFinishing()){
                    mLoadinngDlg.dismiss();
                }
            }
        }
    }

    protected abstract void onHandleSuccess(T t);

    protected void onHandleFailed(String msg) {
        LogUtil.d(TAG,"onHandleFailed : msg  = "+msg);
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }


}
