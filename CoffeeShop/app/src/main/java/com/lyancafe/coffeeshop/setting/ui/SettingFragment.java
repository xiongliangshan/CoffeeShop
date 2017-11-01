package com.lyancafe.coffeeshop.setting.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.main.ui.HomeActivity;
import com.lyancafe.coffeeshop.service.DownLoadService;
import com.lyancafe.coffeeshop.setting.presenter.SettingPresenter;
import com.lyancafe.coffeeshop.setting.presenter.SettingPresenterImpl;
import com.lyancafe.coffeeshop.setting.view.SettingView;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment implements SettingView {


    @BindView(R.id.tv_current_version)
    TextView tvCurrentVersion;
    @BindView(R.id.btn_check_update)
    Button btnCheckUpdate;
    @BindView(R.id.btn_login_out)
    Button btnLoginOut;
    Unbinder unbinder;

    private LoadingDialog mLoadingDlg;
    private SettingPresenter mSettingPresenter;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingPresenter = new SettingPresenterImpl(getContext(),this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    private void initViews(){
        tvCurrentVersion.setText(MyUtil.getVersion(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_check_update, R.id.btn_login_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_check_update:
                //系统更新
                mSettingPresenter.checkUpdate(true);
                break;
            case R.id.btn_login_out:
                //退出登录
                mSettingPresenter.exitLogin();
                OrderHelper.batchList.clear();
                if(getContext() instanceof HomeActivity){
                    ((HomeActivity) getContext()).finish();
                    ((HomeActivity) getContext()).overridePendingTransition(R.anim.scale_center_in, R.anim.scale_center_out);
                }
                break;
        }
    }


    @Override
    public void showUpdateConfirmDlg(final ApkInfoBean apk) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.confirm_download, apk.getAppName()));
        builder.setTitle(getString(R.string.version_update));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //启动Service下载apk文件
                Intent intent = new Intent(getContext(), DownLoadService.class);
                intent.putExtra("apk", apk);
                getContext().startService(intent);
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

    @Override
    public void showToast(String message) {
        ToastUtil.show(getContext(),message);
    }

    @Override
    public void showLoading() {
        if (mLoadingDlg == null) {
            mLoadingDlg = new LoadingDialog(getContext());
        }
        if (!mLoadingDlg.isShowing()) {
            mLoadingDlg.show();
        }
    }

    @Override
    public void dismissLoading() {
        if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
            Activity activity = mLoadingDlg.getOwnerActivity();
            if(activity!=null && !activity.isFinishing()){
                mLoadingDlg.dismiss();
            }
        }
    }
}
