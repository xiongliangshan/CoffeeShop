package com.lyancafe.coffeeshop.login.presenter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.common.ProductHelper;
import com.lyancafe.coffeeshop.db.OrderUtils;
import com.lyancafe.coffeeshop.http.Api;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.login.model.LoginModel;
import com.lyancafe.coffeeshop.login.model.LoginModelImpl;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.login.view.LoginView;
import com.lyancafe.coffeeshop.main.ui.HomeActivity;
import com.lyancafe.coffeeshop.printer.PrintSetting;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.PreferencesUtil;
import com.lyancafe.coffeeshop.utils.RsaEncryptor;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.utils.UpdateUtil;

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
        final String loginName = mLoginView.getUserName();
        String password = mLoginView.getPassword();
        if(TextUtils.isEmpty(loginName)){
            ToastUtil.show(mContext.getApplicationContext(), R.string.username_empty_prompt);
            return ;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtil.show(mContext.getApplicationContext(),R.string.password_empty_prompt);
            return ;
        }

        try {
            RsaEncryptor rsa = new RsaEncryptor(CSApplication.getInstance(), "public.key");
            password = rsa.encrypt(password);
        } catch (Exception e) {
            Log.e("login", e.getMessage());
        }
        final String redId = JPushInterface.getRegistrationID(CSApplication.getInstance());
        final String mType = android.os.Build.MODEL; // 手机型号
        final String appVer = MyUtil.getVersion(CSApplication.getInstance());
        Map<String,Object> params = new HashMap<>();
        params.put("loginName",loginName);
        params.put("password",password);
        params.put("regId",redId);
        params.put("mType",mType);
        params.put("appVer",appVer);
        mLoginModel.login(params,new Observer<BaseEntity<UserBean>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mLoginView.showLoadingDlg();
                LogUtil.d(LogUtil.TAG_LOGIN,"onSubscribe thread  ="+Thread.currentThread().getName());
            }

            @Override
            public void onNext(@NonNull BaseEntity<UserBean> userBeanBaseEntity) {
                LogUtil.d(LogUtil.TAG_LOGIN,"onNext thread  ="+Thread.currentThread().getName());
                if (userBeanBaseEntity.getStatus() == LoginHelper.LOGIN_SUCCESS) {
                    UserBean userBean = userBeanBaseEntity.getData();
                    Logger.getLogger().log(loginName + " 登陆成功," + userBean.toString() + " , 设备信息: " + redId + " | " + mType + " | " + appVer);
                    LoginHelper.saveUser(mContext.getApplicationContext(), userBean);
                    PrintSetting.savePrintSecond(mContext.getApplicationContext(), userBean.isPrintSecond());
                    PrintSetting.savePrintTime(mContext.getApplicationContext(), userBean.isPrintTime());
                    //如果是当天第一次登陆，就清空本地缓存的订单打印记录
                    if (mLoginModel.isCurrentDayFirstLogin(mContext)) {
                        Logger.getLogger().clearAllLogs();
                        OrderHelper.clearPrintedSet(mContext);
                        OrderUtils.with().clearTable();
                        Logger.getLogger().log(loginName + " 这是今天第一次登陆");
                    }
                    mLoginView.stepToMain();
                } else {
                    ToastUtil.show(mContext.getApplicationContext(), userBeanBaseEntity.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG,"登录失败 :"+e.getMessage());
                Logger.getLogger().error("登录失败:"+e.getMessage());
                mLoginView.dismissLoadingDlg();
                ToastUtil.show(mContext.getApplicationContext(),e.getMessage());
            }

            @Override
            public void onComplete() {
                mLoginView.dismissLoadingDlg();
                Log.d(TAG,"登录成功");
                PreferencesUtil.putLastLoginAccount(mContext,loginName);
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
        }else{
            //检测版本更新
            UpdateUtil.init().checkUpdate(new Observer<BaseEntity<ApkInfoBean>>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(@NonNull BaseEntity<ApkInfoBean> apkInfoBeanBaseEntity) {
                    if(apkInfoBeanBaseEntity.getStatus()==0){
                        ApkInfoBean apk = apkInfoBeanBaseEntity.getData();
                        mLoginView.showUpdateConfirmDlg(apk);
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


    @Override
    public void saveDebugIp(String ip) {
        SharedPreferences sp = mContext.getSharedPreferences("login",Context.MODE_PRIVATE);
        sp.edit().putString("ip",ip).apply();
        updateUrl(ip);
    }

    @Override
    public String getDebugIP() {
        SharedPreferences sp = mContext.getSharedPreferences("login",Context.MODE_PRIVATE);
        return sp.getString("ip","");
    }

    @Override
    public void updateUrl(String ip) {
        if(TextUtils.isEmpty(ip)){
            return;
        }
        if(ip.contains("cn")||ip.contains("com")){
            Api.BASE_URL = "https://" + ip + "/shop/";
        }else{
            Api.BASE_URL = "http://" + ip + "/shop/";
        }
        RetrofitHttp.reset();
    }

    @Override
    public void loadProductCapacity() {
        mLoginModel.loadProductCapacity(new Observer<BaseEntity<JsonObject>>() {
            @Override
            public void onSubscribe(Disposable disposable) {
            }

            @Override
            public void onNext(BaseEntity<JsonObject> jsonObjectBaseEntity) {
                ProductHelper.saveProduct(mContext, jsonObjectBaseEntity.getData());
            }


            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }

        });

    }
}