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
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.RsaEncryptor;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;
import com.xls.http.md5.MD5;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

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
        if(!TextUtils.isEmpty(LoginHelper.getToken(mContext))){
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
                    new LoginQry(mContext,userName,password).doRequest();
                }

            }
        });
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

    class LoginQry implements Qry{

        private Context context;
        private String userName;
        private String password;

        public LoginQry(Context context,String userName, String password) {
            this.userName = userName;
            this.password = password;
            this.context = context;
        }

        @Override
        public void doRequest() {
            String url = HttpUtils.BASE_URL+"token";
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("loginName",userName);
            MD5 md5 = new MD5();
            RsaEncryptor rsa = null;
            String enc_pwd = "";
            try {
                rsa = new RsaEncryptor(context, "public.key");
                enc_pwd = rsa.encrypt(password);
            } catch (Exception e) {
                Log.d(TAG,e.getMessage());
            }
            params.put("password", enc_pwd);

            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this,true);
        }

        @Override
           public void showResult(Jresp resp) {
            Log.d(TAG,"loginQry:resp = "+resp);
            if(resp==null){
                ToastUtil.showToast(mContext,R.string.unknown_error);
                return;
            }
            if(resp.status==LoginHelper.LOGIN_SUCCESS){
                int shopId = resp.data.optInt("shopId");
                int userId = resp.data.optInt("userId");
                String shopName = resp.data.optString("shopName");
                String token = resp.data.optString("token");
                boolean isSFMode = resp.data.optBoolean("isSFMode");
                LoginHelper.saveUserInfo(mContext,userId,shopId,shopName,isSFMode,token);
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
                new UpLoadDeviceInfoQry(context, CSApplication.REG_ID).doRequest();

                LoginActivity.this.finish();
            }else if(resp.status==LoginHelper.LOGIN_FAIL){
                ToastUtil.showToast(mContext,resp.message);
            }
        }
    }

    //上报设备信息接口
    class UpLoadDeviceInfoQry implements Qry{

        private Context context;
        private String regId;

        public UpLoadDeviceInfoQry(Context context, String regId) {
            this.context = context;
            this.regId = regId;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            int userId = LoginHelper.getUserId(context);
            String deviceId = "";
            String mType = android.os.Build.MODEL; // 手机型号
            int appCode = MyUtil.getVersionCode(context);
            if(TextUtils.isEmpty(regId)){
                JPushInterface.init(CSApplication.getInstance());
                return;
            }
            if(userId==0){
                return;
            }
            String url = HttpUtils.BASE_URL+shopId+"/barista/"+userId+"/device?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("deviceId",deviceId);
            params.put("mType",mType);
            params.put("appCode",appCode);
            params.put("regId", regId);
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this, false);
            Log.d(TAG, "upload device info:" + deviceId + "|" + mType + "|" + appCode + "|" + regId);
        }

        @Override
        public void showResult(Jresp resp) {
            if(resp==null){
                Log.e(TAG,"UpLoadDeviceInfoQry :resp = "+resp);
                return;
            }
            Log.d(TAG,"UpLoadDeviceInfoQry :resp = "+resp);
            if(resp.status==0){
                Log.d(TAG,"上传RegId成功");
            }else{
                Log.e(TAG,resp.message);
            }
        }
    }
}
