package com.lyancafe.coffeeshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lyancafe.coffeeshop.R;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
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
        setContentView(R.layout.activity_login);
        userNameEdit = (EditText) findViewById(R.id.username);
        passwordEdit = (EditText) findViewById(R.id.password);
        loginBtn = (ImageButton) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                new LoginQry(mContext,userName,password).doRequest();
                /*Intent intent = new Intent(mContext, HomeActivity.class);
                mContext.startActivity(intent);*/
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
            String url = "http://192.168.1.99:8080/shop/v3/token";
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("loginName",userName);
            MD5 md5 = new MD5();
            try {
                md5.Update(password, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.d(TAG,"");
            }
            params.put("password", md5.asHex());

            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST,url,params),context,this);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG,"loginQry:resp = "+resp);
            if(resp==null){
                return;
            }
            Log.d(TAG,"message  ="+resp.message+",data = "+resp.data+",status  ="+resp.status);
        }
    }
}
