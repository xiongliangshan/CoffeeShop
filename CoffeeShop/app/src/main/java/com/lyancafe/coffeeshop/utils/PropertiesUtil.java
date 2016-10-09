package com.lyancafe.coffeeshop.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.service.UpdateService;
import com.xls.http.HttpUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * Created by Administrator on 2015/7/31.
 */
public class PropertiesUtil {

    private static final String TAG = "PropertiesUtil";
    public static final String APK_DIR = Environment.getExternalStorageDirectory() +File.separator+"lyancoffee"+File.separator+"apk";
    private static final int TIMEOUT = 60 * 1000;// 超时时间
    private static PropertiesUtil mProUtil;

    private PropertiesUtil() {

    }

    public static PropertiesUtil getInstance(){
        if(mProUtil==null){
            return new PropertiesUtil();
        }else{
            return mProUtil;
        }
    }

    /***
     * 下载文件
     */
    public boolean downloadFile(String down_url, File file){
        Log.d(TAG,"start download profile");
        InputStream inputStream;
        OutputStream outputStream;
        try{
            URL url = new URL(down_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(TIMEOUT);
            httpURLConnection.setReadTimeout(TIMEOUT);
            if (httpURLConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            inputStream = httpURLConnection.getInputStream();
            outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉
            byte buffer[] = new byte[1024];
            int readsize = 0;
            while ((readsize = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readsize);
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            inputStream.close();
            outputStream.close();
        }catch (Exception e){
            Log.d(TAG, "exception download profile failed"+e.getMessage());
            return false;
        }
        Log.d(TAG,"download file sucess");
        return true;
    }
    /**
     * 创建文件
     */
    public boolean createFile(File file){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            File apkDir =  new File(PropertiesUtil.APK_DIR);
            if(!apkDir.exists()){
                apkDir.mkdirs();
                Log.d(TAG,"apkdir = "+apkDir.getAbsolutePath());

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
     * 获取版本名
     * @return 当前应用的版本名
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            Log.d("xiong","version = "+version);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取版本号
     */
    public static int getAppVersionCode(Context context){
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
