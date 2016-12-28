package com.lyancafe.coffeeshop.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ApkInfoBean;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UpdateService extends IntentService {

    private static final String TAG = "UpdateService";
    private static final int DOWNLOADNOTIFICATIONID = 333;
    private static final int TIMEOUT = 60 * 1000;// 超时时间
    private boolean isDownloading = false;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private File mAPKDir;
    private File  mFile;

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service  onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "download apk file ,isdownloading = " + isDownloading);
        ApkInfoBean apkInfoBean = (ApkInfoBean) intent.getSerializableExtra("apk");
        if(!isDownloading){
            if (isNewestAPKexist(apkInfoBean)) {
                installApk(UpdateService.this, mFile);
            } else {
                if (createFile(mFile)) {
                    if (downloadUpdateFile(apkInfoBean.getUrl(), mFile)) {
                        installApk(UpdateService.this, mFile);
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }


    }

    /**
     * 初始化通知栏消息
     */
    private void initNotification(){
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentText(getResources().getString(R.string.downloading));
    }

    /***
     * 发送通知栏下载进度消息
     */
    public void sendNotification(int pro) {
        Log.d(TAG, "send notification");
        mBuilder.setProgress(100, pro, false);
        mNotificationManager.notify(DOWNLOADNOTIFICATIONID, mBuilder.build());

    }

    /**
     * 启动安装apk  Activity
     */
    private void installApk(Context context,File apkfile){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    /**
     * 创建文件
     */
    public boolean createFile(File file){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            mAPKDir =  new File(CSApplication.APK_DIR);
            if(!mAPKDir.exists()){
                mAPKDir.mkdirs();
                Log.d(TAG,"apkdir = "+mAPKDir.getAbsolutePath());

            }
            Log.d(TAG,"dir create");
            if(!file.exists()){
                try {
                    file.createNewFile();
                    Log.d(TAG,"create file sucess");
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "create file failed:"+e.getMessage());
                    return false;
                }
            }else{
                Log.d(TAG,"file has exists");
                return true;
            }
        }

        return false;
    }

    /**
     * 删除文件
     */
    private void deleteFile(File file){
        if(file!=null && file.exists()){
            file.delete();
            Log.d(TAG,"delete file");
        }
    }

    /**
     * 判断该最新apk文件是否已经下载完成，在本地存在
     * newVersionCode 从服务器端获取的最新版本号
     */
    private boolean isNewestAPKexist(ApkInfoBean apkInfoBean){
        mFile = new File(CSApplication.APK_DIR+File.separator+"coffeeshop_"+apkInfoBean.getAppNo()+".apk");
        if(mFile!=null && mFile.exists()){
            Log.d(TAG,"newestapk file exist!");
            //F6C95DAA257080223CC4211F05744AF8
            if(getMD5Code(mFile).equalsIgnoreCase(apkInfoBean.getAppMd5())){
                Log.d(TAG,"MD5 一致");
                return true;
            }else{
                return false;
            }
        }else{
            Log.d(TAG,"newestapk file not exist");
            return false;
        }
    }

    /***
     * 下载文件
     */
    public boolean downloadUpdateFile(String down_url, File file){
        Log.d(TAG,"start download, url="+down_url);
        ToastUtil.showToast(CSApplication.getInstance(), getResources().getString(R.string.start_download));
        long beginPosition = 0L;//下载的起始位置
        isDownloading = true;
        int down_step = 3;// 提示step
        long totalSize;// 文件总大小
        int updateCount = 0;// 已经上传的文件大小
        InputStream inputStream;
        RandomAccessFile output = null;
        try{
            output = new RandomAccessFile(file,"rw");
            long length = file.length();

            URL url = new URL(down_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(TIMEOUT);
            httpURLConnection.setReadTimeout(TIMEOUT);
            // 获取下载文件的size
            totalSize =getFileTotalSize(down_url,file);
            if (totalSize<=0) {
                throw new Exception("get file length fail!");
            }
            if(length<totalSize){
                beginPosition = length;
            }
            Log.d(TAG,"totalSize = "+totalSize);
            httpURLConnection.setAllowUserInteraction(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Range", "bytes=" + beginPosition + "-");
            inputStream = httpURLConnection.getInputStream();
            output.seek(beginPosition);
            byte buffer[] = new byte[1024];
            int readsize = 0;
            while ((readsize = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, readsize);
                beginPosition+=readsize;

                if (updateCount == 0 || (beginPosition * 100 / totalSize - down_step) >= updateCount){
                    updateCount += down_step;
                    sendNotification(updateCount);
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            inputStream.close();
            output.close();
        }catch (Exception e){
//            deleteFile(file);
            Log.d(TAG, "download file failed");
            isDownloading = false;
            ToastUtil.showToast(CSApplication.getInstance(), getResources().getString(R.string.download_fail));
            mNotificationManager.cancel(DOWNLOADNOTIFICATIONID);
            return false;
        }


        isDownloading = false;
        mNotificationManager.cancel(DOWNLOADNOTIFICATIONID);
        Log.d(TAG,"download file sucess");
        return true;
    }

    /**
     * 获取文件字节长度
     */
    private long getFileTotalSize(String down_url, File file){
        long totalSize = 0L;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(down_url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(TIMEOUT);
            httpURLConnection.setReadTimeout(TIMEOUT);
            totalSize = httpURLConnection.getContentLength();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(httpURLConnection!=null){
               httpURLConnection.disconnect();
            }
        }
        return totalSize;
    }
    /**
     * 获取文件的MD5
     */
    private String getMD5Code(File apkFile){
        String md5 = "";
        if(apkFile!=null && apkFile.exists()){
            try {
                byte buffer[] = new byte[1024];
                int len;
                MessageDigest digest = MessageDigest.getInstance("MD5");
                FileInputStream in = new FileInputStream(apkFile);
                while ((len = in.read(buffer, 0, 1024)) != -1) {
                    digest.update(buffer, 0, len);
                }
                in.close();
                BigInteger bigInt = new BigInteger(1, digest.digest());
                md5 = bigInt.toString(16);
                if(md5.length()==31){
                    md5 = "0"+md5;
                }
                Log.d(TAG,"MD5="+md5);

            }catch (NoSuchAlgorithmException e){
                Log.e(TAG,e.getMessage());
            }catch (FileNotFoundException e){
                Log.e(TAG,e.getMessage());
            }catch (IOException e){
                Log.e(TAG,e.getMessage());
            }

            return md5;
        }else{
            Log.d(TAG,"file not exits!!!");
            return "";
        }

    }

}
