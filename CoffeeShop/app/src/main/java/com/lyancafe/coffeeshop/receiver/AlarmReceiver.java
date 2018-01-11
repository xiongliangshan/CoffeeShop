package com.lyancafe.coffeeshop.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.utils.LogUtil;

import java.io.File;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2018/1/9.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d(TAG,"onReceive:"+intent.toString()+" | 进程:"+getProcessName(context, Process.myPid()));
        Logger.getLogger().log("收到闹钟事件:"+intent.getAction()+" (我要被上传了，拜拜 pad)");
        checkUploadFile();
    }

    private void checkUploadFile() {
        LogUtil.d(TAG,"checkUploadFile");
        File logDir = new File(CSApplication.LOG_DIR);
        if(!logDir.exists()){
            LogUtil.w(TAG,"日志目录不存在!");
            return;
        }
        File[] files = logDir.listFiles();
        if(files.length==0){
            return;
        }
        for(File file:files){
            uploadFile(file);
        }
    }

    private void uploadFile(final File file){
        /*String serverFileName = generateServerFileName(file);
        if(TextUtils.isEmpty(serverFileName)){
            return;
        }*/
        UserBean userBean = LoginHelper.getUser(CSApplication.getInstance());
        int shopId = userBean.getShopId();
        RequestBody shopIdStr = RequestBody.create(MediaType.parse("text/plain"),String.valueOf(shopId));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("logFile",file.getName(),requestBody);
        LogUtil.d(TAG,"开始上传文件:"+file.getName());
        RetrofitHttp.getRetrofit().uploadFile(shopIdStr,part)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<BaseEntity<JsonObject>>() {
                    @Override
                    public void accept(BaseEntity<JsonObject> jsonObjectBaseEntity) throws Exception {
                        if (jsonObjectBaseEntity.getStatus() == 0) {
                            LogUtil.d(TAG, "上传文件成功，删除本地文件 " + file.getName());
                            file.delete();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e(TAG,"上传文件失败"+throwable.getMessage());
                    }
                });
    }


    private String generateServerFileName(File file){
        if(file==null || !file.exists()){
            return null;
        }

        UserBean userBean = LoginHelper.getUser(CSApplication.getInstance());
        int shopId = userBean.getShopId();
        if(shopId!=0){
            return "app."+shopId+"."+file.getName();
        }else {
            return "app.xls."+file.getName();
        }
    }




    private String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}
