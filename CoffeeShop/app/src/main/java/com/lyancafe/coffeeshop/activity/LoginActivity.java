package com.lyancafe.coffeeshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.igexin.sdk.PushManager;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.utils.RsaEncryptor;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;
import com.xls.http.md5.MD5;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

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
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                LoginHelper.saveUserInfo(mContext,userId,shopId,shopName,token);
                Intent intent = new Intent(mContext, HomeActivity.class);
                mContext.startActivity(intent);
                overridePendingTransition(R.anim.scale_center_in, R.anim.scale_center_out);
                LoginActivity.this.finish();
            }else if(resp.status==LoginHelper.LOGIN_FAIL){
                ToastUtil.showToast(mContext,resp.message);
            }
        }
    }
}
