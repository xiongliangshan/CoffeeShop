package com.lyancafe.coffeeshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.LoginBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2015/9/1.
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private EditText userNameEdit;
    private EditText passwordEdit;
    private ImageButton loginBtn;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //如果已经登录过了，并且没有点退出，可以直接进入主界面
        if(!TextUtils.isEmpty(LoginHelper.getLoginBean(mContext).getToken())){
            Intent intent = new Intent(mContext, HomeActivity.class);
            mContext.startActivity(intent);
            LoginActivity.this.finish();
        }
        setContentView(R.layout.activity_login);
        userNameEdit = (EditText) findViewById(R.id.username);
        passwordEdit = (EditText) findViewById(R.id.password);
        loginBtn = (ImageButton) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if(LoginHelper.verifyLoginParams(mContext,userName,password)){
                    HttpHelper.getInstance().reqLogin(userName, password, new DialogCallback<XlsResponse>(LoginActivity.this) {
                        @Override
                        public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                            handleLoginResponse(xlsResponse,call,response);
                        }
                    });
                }

            }
        });
    }

    //处理登录返回的结果
    private void handleLoginResponse(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==LoginHelper.LOGIN_SUCCESS){
            LoginBean login = LoginBean.parseJsonLoginBean(mContext,xlsResponse);
            LoginHelper.saveLoginBean(mContext, login);
            //如果是当天第一次登陆，就清空本地缓存的订单打印记录
            if(LoginHelper.isCurrentDayFirstLogin(mContext)){
                OrderHelper.clearPrintedSet(mContext);
            }
            Intent intent = new Intent(mContext, HomeActivity.class);
            mContext.startActivity(intent);
            overridePendingTransition(R.anim.scale_center_in, R.anim.scale_center_out);

            if(TextUtils.isEmpty(CSApplication.REG_ID)){
                CSApplication.REG_ID = JPushInterface.getRegistrationID(CSApplication.getInstance());
            }
            HttpHelper.getInstance().reqUploadDeviceInfo(CSApplication.REG_ID, new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleUploadDeviceInfoResponse(xlsResponse,call,response);
                }
            });

            LoginActivity.this.finish();
        }else if(xlsResponse.status==LoginHelper.LOGIN_FAIL){
            ToastUtil.showToast(mContext,xlsResponse.message);
        }
    }

    //处理上传设备信息返回的结果
    private void handleUploadDeviceInfoResponse(XlsResponse xlsResponse,Call call, Response response) {
        if(xlsResponse.status==0){
            Log.d(TAG,"上传RegId成功");
        }else{
            Log.e(TAG,xlsResponse.message);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
