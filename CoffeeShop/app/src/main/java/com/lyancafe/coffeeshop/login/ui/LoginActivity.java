package com.lyancafe.coffeeshop.login.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.BuildConfig;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseActivity;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.http.Api;
import com.lyancafe.coffeeshop.login.presenter.LoginPresenter;
import com.lyancafe.coffeeshop.login.presenter.LoginPresenterImpl;
import com.lyancafe.coffeeshop.login.view.LoginView;
import com.lyancafe.coffeeshop.main.ui.HomeActivity;
import com.lyancafe.coffeeshop.service.DownLoadService;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.PreferencesUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2015/9/1.
 */
public class LoginActivity extends BaseActivity implements LoginView {

    private static final String TAG = "LoginActivity";
    @BindView(R.id.tv_current_ip)
    TextView tvCurrentIp;
    @BindView(R.id.btn_modify_ip)
    Button btnModifyIp;
    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.rl_ip_container)
    RelativeLayout rlIpContainer;
    @BindView(R.id.rb_other)
    RadioButton rbOther;
    @BindView(R.id.rb_qa)
    RadioButton rbQa;
    @BindView(R.id.rb_ol)
    RadioButton rbOl;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    private ProgressDialog dialog;

    @BindView(R.id.username)
    EditText userNameEdit;

    @BindView(R.id.password)
    EditText passwordEdit;

    private LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginPresenter = new LoginPresenterImpl(this, this);
        mLoginPresenter.checkLoginStatus();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        String lastAccount = PreferencesUtil.getLastLoginAccount(this.getApplicationContext());
        userNameEdit.setText(lastAccount);
        initServerIp();

    }

    private void initServerIp() {
        if (BuildConfig.DEBUG) {
            rlIpContainer.setVisibility(View.VISIBLE);
            String lastIp = mLoginPresenter.getDebugIP();
            mLoginPresenter.updateUrl(lastIp);
            tvCurrentIp.setText(Api.BASE_URL);
        } else {
            rlIpContainer.setVisibility(View.GONE);
        }

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
    void login() {
        mLoginPresenter.login();
        mLoginPresenter.loadProductCapacity();
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
        LoginActivity.this.finish();

    }

    @Override
    public void showLoadingDlg() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("请求网络中...");
        }
        if (!dialog.isShowing()) {
            Activity activity = dialog.getOwnerActivity();
            if (activity != null && !activity.isFinishing()) {
                dialog.show();
            }
        }
    }

    @Override
    public void dismissLoadingDlg() {
        if (dialog != null && dialog.isShowing()) {
            Activity activity = dialog.getOwnerActivity();
            if (activity != null && !activity.isFinishing()) {
                dialog.dismiss();
            }

        }
    }


    @Override
    public void showUpdateConfirmDlg(final ApkInfoBean apk) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_download, apk.getAppName()));
        builder.setTitle(getString(R.string.version_update));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //启动Service下载apk文件
                Intent intent = new Intent(LoginActivity.this, DownLoadService.class);
                intent.putExtra("apk", apk);
                startService(intent);
            }
        });
        builder.setNegativeButton(getString(R.string.cacel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @OnClick(R.id.btn_modify_ip)
    public void onViewClicked() {
        String btnName = btnModifyIp.getText().toString();
        if (("修改").equals(btnName)) {
            animateShowEdit();
        } else if (("提交").equals(btnName)) {
            String ip = etIp.getText().toString();
            if (TextUtils.isEmpty(ip)) {
                ToastUtil.show(getApplicationContext(), "请输入新的服务器地址");
                return;
            }
            mLoginPresenter.saveDebugIp(ip);

            animateHideEdit();

        }
    }


    private void animateShowEdit() {
        if (etIp == null || etIp.getVisibility() == View.VISIBLE) {
            return;
        }

        ObjectAnimator animatorEdit = ObjectAnimator.ofFloat(etIp, "alpha", 0, 1);
        ObjectAnimator animatorRb = ObjectAnimator.ofFloat(radioGroup,"alpha",0,1);
        float deltaX = btnModifyIp.getMeasuredWidth() + etIp.getMeasuredWidth() + OrderHelper.dip2Px(36, getApplicationContext());
        ObjectAnimator animatorBtn = ObjectAnimator.ofFloat(btnModifyIp, "translationX", 0, deltaX);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorEdit, animatorBtn,animatorRb);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(600);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                etIp.setVisibility(View.VISIBLE);
                radioGroup.setVisibility(View.VISIBLE);
                radioGroup.check(R.id.rb_other);
                etIp.setText("");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                btnModifyIp.setText("提交");
                etIp.requestFocus();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    private void animateHideEdit() {

        LogUtil.d(TAG, "translationX = " + btnModifyIp.getTranslationX());
        ObjectAnimator animatorEdit = ObjectAnimator.ofFloat(etIp, "alpha", 1, 0);
        float deltaX = btnModifyIp.getMeasuredWidth() + etIp.getMeasuredWidth() + OrderHelper.dip2Px(36, getApplicationContext());
        ObjectAnimator animatorBtn = ObjectAnimator.ofFloat(btnModifyIp, "translationX", deltaX, 0);
        ObjectAnimator animatorRb = ObjectAnimator.ofFloat(radioGroup,"alpha",1,0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorEdit, animatorBtn,animatorRb);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(600);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                btnModifyIp.setText("修改");
                etIp.setVisibility(View.INVISIBLE);
                radioGroup.setVisibility(View.INVISIBLE);
                tvCurrentIp.setText(Api.BASE_URL);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();

    }


    @OnClick({R.id.rb_other, R.id.rb_qa, R.id.rb_ol})
    public void onRbClicked(View view) {
        switch (view.getId()) {
            case R.id.rb_other:
                etIp.setText("");
                break;
            case R.id.rb_qa:
                etIp.setText("apiqa.lyancafe.cn");
                etIp.setSelection(etIp.getText().toString().length());
                break;
            case R.id.rb_ol:
                etIp.setText("api.lyancafe.com");
                etIp.setSelection(etIp.getText().toString().length());
                break;
        }
    }
}
