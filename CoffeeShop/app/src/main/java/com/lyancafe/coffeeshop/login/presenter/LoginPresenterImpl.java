package com.lyancafe.coffeeshop.login.presenter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.main.ui.HomeActivity;
import com.lyancafe.coffeeshop.login.model.UserBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.login.model.LoginModel;
import com.lyancafe.coffeeshop.login.model.LoginModelImpl;
import com.lyancafe.coffeeshop.login.view.LoginView;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/13
*/

public class LoginPresenterImpl implements LoginPresenter,LoginModelImpl.OnHandleLoginListener,LoginModelImpl.OnHandleUpLoadDeviceInfoListener{

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
    public void login(Activity activity) {
        String userName = mLoginView.getUserName();
        String password = mLoginView.getPassword();
        if(TextUtils.isEmpty(userName)){
            ToastUtil.showToast(mContext, R.string.username_empty_prompt);
            return ;
        }
        if(TextUtils.isEmpty(password)){
            ToastUtil.showToast(mContext,R.string.password_empty_prompt);
            return ;
        }
        mLoginModel.login(activity,userName,password,this);
    }

    @Override
    public void checkLoginStatus() {
        //如果已经登录过了，并且没有点退出，可以直接进入主界面
        if(!TextUtils.isEmpty(LoginHelper.getLoginBean(mContext).getToken())){
            Intent intent = new Intent(mContext, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            if(mContext instanceof Activity){
                ((Activity) mContext).finish();
            }
        }
    }

    @Override
    public void handleLoginResponse(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status== LoginHelper.LOGIN_SUCCESS){
            UserBean login = UserBean.parseJsonLoginBean(mContext,xlsResponse);
            LoginHelper.saveLoginBean(mContext, login);
            //如果是当天第一次登陆，就清空本地缓存的订单打印记录
            if(mLoginModel.isCurrentDayFirstLogin(mContext)){
                OrderHelper.clearPrintedSet(mContext);
            }
            Intent intent = new Intent(mContext, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            if(mLoginView instanceof Activity){
                ((Activity)mLoginView).overridePendingTransition(R.anim.scale_center_in, R.anim.scale_center_out);
            }

            if(TextUtils.isEmpty(CSApplication.REG_ID)){
                CSApplication.REG_ID = JPushInterface.getRegistrationID(CSApplication.getInstance());
            }

            mLoginModel.uploadDeviceInfo(CSApplication.REG_ID,this);

            if(mLoginView instanceof Activity){
                ((Activity)mLoginView).finish();
            }
        }else if(xlsResponse.status==LoginHelper.LOGIN_FAIL){
            ToastUtil.showToast(mContext,xlsResponse.message);
        }
    }

    @Override
    public void handleUploadDeviceInfoResponse(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==0){
            Log.d(TAG,"上传RegId成功");
        }else{
            Log.e(TAG,xlsResponse.message);
        }
    }

    @Override
    public void onLoginSuccess(XlsResponse xlsResponse, Call call, Response response) {
        handleLoginResponse(xlsResponse,call,response);
    }

    @Override
    public void onLoginFailure(Call call, Response response, Exception e) {

    }


    @Override
    public void onUpLoadDeviceInfoSuccess(XlsResponse xlsResponse, Call call, Response response) {
        handleUploadDeviceInfoResponse(xlsResponse,call,response);
    }

    @Override
    public void onUpLoadDeviceInfoFailure(Call call, Response response, Exception e) {

    }
}