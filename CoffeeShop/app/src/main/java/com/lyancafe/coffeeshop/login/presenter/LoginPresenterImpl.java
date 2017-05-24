package com.lyancafe.coffeeshop.login.presenter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.login.model.LoginModel;
import com.lyancafe.coffeeshop.login.model.LoginModelImpl;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.login.view.LoginView;
import com.lyancafe.coffeeshop.main.ui.HomeActivity;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.RsaEncryptor;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
* Created by Administrator on 2017/03/13
*/

public class LoginPresenterImpl implements LoginPresenter{

    private static final String TAG = "login";
    private Context mContext;
    private LoginModel mLoginModel;
    private LoginView mLoginView;

    public LoginPresenterImpl(Context mContext, LoginView mLoginView) {
        this.mContext = mContext;
        this.mLoginView = mLoginView;
        mLoginModel = new LoginModelImpl();
    }


    @Override
    public void login() {
        String loginName = mLoginView.getUserName();
        String password = mLoginView.getPassword();
        if(TextUtils.isEmpty(loginName)){
            ToastUtil.showToast(mContext.getApplicationContext(), R.string.username_empty_prompt);
            return ;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtil.showToast(mContext.getApplicationContext(),R.string.password_empty_prompt);
            return ;
        }

        try {
            RsaEncryptor rsa = new RsaEncryptor(CSApplication.getInstance(), "public.key");
            password = rsa.encrypt(password);
        } catch (Exception e) {
            Log.e("login", e.getMessage());
        }
        mLoginModel.login(loginName, password, new Observer<BaseEntity<UserBean>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mLoginView.showLoadingDlg();
            }

            @Override
            public void onNext(@NonNull BaseEntity<UserBean> userBeanBaseEntity) {
                if(userBeanBaseEntity.getStatus()==LoginHelper.LOGIN_SUCCESS){
                    UserBean userBean = userBeanBaseEntity.getData();

                    LoginHelper.saveUser(mContext, userBean);
                    //如果是当天第一次登陆，就清空本地缓存的订单打印记录
                    if(mLoginModel.isCurrentDayFirstLogin(mContext)){
                        OrderHelper.clearPrintedSet(mContext);
                    }

                    uploadDeviceInfo(userBean.getShopId(),userBean.getUserId(),userBean.getToken());

                    mLoginView.stepToMain();

                }else{
                    ToastUtil.showToast(mContext.getApplicationContext(),userBeanBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG,"登录失败 :"+e.getMessage());
                mLoginView.dismissLoadingDlg();
                ToastUtil.showToast(mContext.getApplicationContext(),e.getMessage());
            }

            @Override
            public void onComplete() {
                mLoginView.dismissLoadingDlg();
                Log.d(TAG,"登录成功");
            }
        });
    }

    @Override
    public void checkLoginStatus() {
        //如果已经登录过了，并且没有点退出，可以直接进入主界面
        if(!TextUtils.isEmpty(LoginHelper.getUser(mContext).getToken())){
            Intent intent = new Intent(mContext, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            if(mContext instanceof Activity){
                ((Activity) mContext).finish();
            }
        }
    }


    /**
     * 上传设备信息
     * @param token
     */
    @Override
    public void uploadDeviceInfo(int shopId,int userId,String token) {

        final String deviceId = "";
        final String mType = android.os.Build.MODEL; // 手机型号
        final int appCode = MyUtil.getVersionCode(CSApplication.getInstance());
        final String redId = JPushInterface.getRegistrationID(CSApplication.getInstance());
        if(TextUtils.isEmpty(redId)){
            LogUtil.w("jpush","Jpush  getRegistrationID 获取regId失败");
            return;
        }
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("deviceId",deviceId);
        params.put("mType",mType);
        params.put("appCode",appCode);
        params.put("regId", redId);
        params.put("token",token);

        mLoginModel.uploadDeviceInfo(shopId,userId,params,new Observer<BaseEntity>(){
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull BaseEntity baseEntity) {
                if(baseEntity.getStatus()==0){
                    Log.i(TAG,"上传设备信息成功:"+mType+"|"+appCode+"|"+redId);
                }else{
                    Log.w(TAG,"上传失败:"+baseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }
}