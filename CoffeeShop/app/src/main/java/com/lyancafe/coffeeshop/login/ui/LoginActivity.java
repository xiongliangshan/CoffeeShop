package com.lyancafe.coffeeshop.login.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.BaseActivity;
import com.lyancafe.coffeeshop.login.presenter.LoginPresenter;
import com.lyancafe.coffeeshop.login.presenter.LoginPresenterImpl;
import com.lyancafe.coffeeshop.login.view.LoginView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/9/1.
 */
public class LoginActivity extends BaseActivity implements LoginView{

    private static final String TAG = "LoginActivity";

    @BindView(R.id.username)
    EditText userNameEdit;

    @BindView(R.id.password)
    EditText passwordEdit;

    private Context mContext;
    private LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mLoginPresenter = new LoginPresenterImpl(getApplication(),this);
        mLoginPresenter.checkLoginStatus();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }


    @Override
    public String getUserName() {
        return userNameEdit.getText().toString();
    }

    @Override
    public String getPassword() {
        return passwordEdit.getText().toString();
    }

    @OnClick(R.id.login_btn)
     void login(){
       mLoginPresenter.login(this);
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


}
