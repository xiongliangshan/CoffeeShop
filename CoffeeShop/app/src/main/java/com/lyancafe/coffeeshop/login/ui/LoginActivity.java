package com.lyancafe.coffeeshop.login.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseActivity;
import com.lyancafe.coffeeshop.login.presenter.LoginPresenter;
import com.lyancafe.coffeeshop.login.presenter.LoginPresenterImpl;
import com.lyancafe.coffeeshop.login.view.LoginView;
import com.lyancafe.coffeeshop.main.ui.HomeActivity;
import com.lyancafe.coffeeshop.utils.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/9/1.
 */
public class LoginActivity extends BaseActivity implements LoginView{

    private static final String TAG = "LoginActivity";
    private ProgressDialog dialog;

    @BindView(R.id.username)
    EditText userNameEdit;

    @BindView(R.id.password)
    EditText passwordEdit;

    private LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginPresenter = new LoginPresenterImpl(this,this);
        mLoginPresenter.checkLoginStatus();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }


    @Override
    public String getUserName() {
        return userNameEdit.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return passwordEdit.getText().toString().trim();
    }

    @OnClick(R.id.login_btn)
     void login(){
        mLoginPresenter.login();
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
    public void stepToMain() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.scale_center_in, R.anim.scale_center_out);

    }

    @Override
    public void showLoadingDlg() {
        if(dialog==null){
            dialog = new ProgressDialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("请求网络中...");
        }
        if(!dialog.isShowing()){
            dialog.show();
        }
    }

    @Override
    public void dismissLoadingDlg() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
