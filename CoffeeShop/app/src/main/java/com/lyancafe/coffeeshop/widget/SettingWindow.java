package com.lyancafe.coffeeshop.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.HomeActivity;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.bean.LoginBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.DialogCallback;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.service.UpdateService;
import com.lyancafe.coffeeshop.utils.MyUtil;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.HttpUtils;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2015/10/22.
 */
public class SettingWindow extends PopupWindow implements View.OnClickListener{

    private static final String TAG = "SettingWindow";
    private Activity mContext;
    private View contentView;
    private TextView feedbackTxt;
    private RelativeLayout versionUpdateLayout;
    private TextView currentVerTxt;
    private Button loginOutBtn;


    public SettingWindow(Activity context) {
        super(context);
        this.mContext = context;
        contentView = LayoutInflater.from(context).inflate(R.layout.window_popup_setting,null);
        this.setContentView(contentView);
        initView(contentView, context);
        this.setHeight(OrderHelper.dip2Px(210, context));
        this.setWidth(OrderHelper.dip2Px(220, context));
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setAnimationStyle(R.style.setting_popwin_anim_style);

    }


    private void initView(View contentView, final Context context){
        versionUpdateLayout = (RelativeLayout) contentView.findViewById(R.id.rl_version_update);
        versionUpdateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HttpUtils.isOnline(context)) {
                    ToastUtil.show(context, context.getResources().getString(R.string.check_internet));
                } else {
                    SettingWindow.this.dismiss();
                    HttpHelper.getInstance().reqCheckUpdate(new DialogCallback<XlsResponse>(mContext) {
                        @Override
                        public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                            handleCheckUpdateResponse(xlsResponse, call, response);
                        }
                    });
                }
            }
        });
        currentVerTxt = (TextView) contentView.findViewById(R.id.current_version);
        feedbackTxt = (TextView) contentView.findViewById(R.id.feedback);
        feedbackTxt.setOnClickListener(this);
        loginOutBtn = (Button) contentView.findViewById(R.id.login_out);
        loginOutBtn.setOnClickListener(this);
        currentVerTxt.setText("当前版本:" + MyUtil.getVersion(context));

    }


    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showSettingWindow(View parent) {
        if (!this.isShowing()) {
            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            this.showAtLocation(parent, Gravity.NO_GRAVITY, location[0]+parent.getWidth(), location[1]-this.getHeight());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.feedback:
                //意见反馈
                FeedBackDialog fd = new FeedBackDialog(mContext,R.style.MyDialog);
                fd.show();
                break;
            case R.id.login_out:
                //退出登录
                HttpHelper.getInstance().reqLoginOut(new JsonCallback<XlsResponse>() {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        handleLoginOutResponse(xlsResponse,call,response);
                    }
                });
                LoginBean loginBean = LoginHelper.getLoginBean(mContext);
                loginBean.setToken("");
                LoginHelper.saveLoginBean(mContext,loginBean);
                OrderHelper.batchList.clear();
                ((HomeActivity)mContext).finish();
                ((HomeActivity)mContext).overridePendingTransition(R.anim.scale_center_in, R.anim.scale_center_out);
                break;
        }
    }

    /**
     * 处理退出登录
     * @param xlsResponse
     * @param call
     * @param response
     */
    private void handleLoginOutResponse(XlsResponse xlsResponse, Call call, Response response) {
        Intent intent_update = new Intent(mContext, UpdateService.class);
        mContext.stopService(intent_update);
    }


    /**
     * 处理检测更新接口
     * @param xlsResponse
     * @param call
     * @param response
     */
    private void handleCheckUpdateResponse(XlsResponse xlsResponse,Call call,Response response){
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
        }else{
            ToastUtil.show(mContext, xlsResponse.message);
        }
    }

}
