package com.lyancafe.coffeeshop.main.presenter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.login.model.UserBean;
import com.lyancafe.coffeeshop.main.ui.HomeActivity;
import com.lyancafe.coffeeshop.service.UpdateService;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
* Created by Administrator on 2017/03/14
*/

public class MainPresenterImpl implements MainPresenter{

    private Context mContext;

    public MainPresenterImpl(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void checkUpdate(Activity activity,boolean isBackground) {
        if(isBackground){
            HttpHelper.getInstance().reqCheckUpdate(new JsonCallback<XlsResponse>() {
                @Override
                public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                    handleCheckUpdateResponse(xlsResponse,call,response);
                }
            });
        }else{
            if (!MyUtil.isOnline(mContext)) {
                ToastUtil.show(mContext, mContext.getResources().getString(R.string.check_internet));
            } else {
                HttpHelper.getInstance().reqCheckUpdate(new DialogCallback<XlsResponse>(activity) {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleCheckUpdateResponse(xlsResponse, call, response);
                    }
                });
            }
        }
    }

    @Override
    public void handleCheckUpdateResponse(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==0){
            final ApkInfoBean apkInfoBean = ApkInfoBean.parseJsonToBean(xlsResponse.data.toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(mContext.getResources().getString(R.string.confirm_download, apkInfoBean.getAppName()));
            builder.setTitle(mContext.getResources().getString(R.string.version_update));
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //启动Service下载apk文件
                    Intent intent = new Intent(mContext, UpdateService.class);
                    intent.putExtra("apk",apkInfoBean);
                    mContext.startService(intent);
                }
            });
            builder.setNegativeButton(mContext.getResources().getString(R.string.cacel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    @Override
    public void resetToken() {
        UserBean userBean = LoginHelper.getLoginBean(mContext);
        userBean.setToken("");
        LoginHelper.saveLoginBean(mContext, userBean);
    }

    @Override
    public void exitLogin() {
        HttpHelper.getInstance().reqLoginOut(new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleLoginOutResponse(xlsResponse,call,response);
            }
        });
    }

    @Override
    public void handleLoginOutResponse(XlsResponse xlsResponse, Call call, Response response) {
        Intent intent_update = new Intent(mContext, UpdateService.class);
        mContext.stopService(intent_update);
    }
}