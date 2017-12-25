package com.lyancafe.coffeeshop.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.BaseEntity;
import com.lyancafe.coffeeshop.bean.Device;
import com.lyancafe.coffeeshop.bean.DeviceGroup;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.RetrofitHttp;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.printer.PrintFace;
import com.lyancafe.coffeeshop.printer.PrintSetting;
import com.lyancafe.coffeeshop.utils.LogUtil;
import com.lyancafe.coffeeshop.utils.MyUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/12/19.
 */

public class MonitorService extends Service {

    private static final String TAG = "MonitorService";

    //运行时监控时间间隔，单位：分钟
    private static final  long monitorInterval = 3;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG,TAG+"--onCreate");
        Logger.getLogger().log("MonitorService 启动");
        Observable.interval(0,30, TimeUnit.SECONDS)
                .take(3)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        LogUtil.d(TAG,"accept = "+aLong);
                        hartBeat(0);
                        if(aLong==2){
                            runMonitor();
                        }
                    }
                });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG,TAG+"--onStartCommand");
        Logger.getLogger().log("MonitorService--onStartCommand");
        return START_STICKY;
    }

    private void runMonitor(){
        LogUtil.d(TAG,"启动运行时监控");
        Observable.interval(monitorInterval,TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        LogUtil.d(TAG,"accept = "+aLong);
                        hartBeat(1);
                    }
                });
    }


    private void hartBeat(int type){
        DeviceGroup deviceGroup = getDevicesInfo(type);
        RetrofitHttp.getRetrofit().heartbeat(deviceGroup)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
               .subscribe(new Consumer<BaseEntity<JsonObject>>() {
                   @Override
                   public void accept(BaseEntity<JsonObject> jsonObjectBaseEntity) throws Exception {
                        LogUtil.d(TAG,"收到服务器的返回");
                   }
               }, new Consumer<Throwable>() {
                   @Override
                   public void accept(Throwable throwable) throws Exception {
                       LogUtil.d(TAG,"心跳结果异常");
                       LogUtil.d(TAG,throwable.getMessage());
                   }
               });
    }


    private DeviceGroup getDevicesInfo(int type){
        DeviceGroup deviceGroup = new DeviceGroup();
        deviceGroup.setDeviceId(JPushInterface.getRegistrationID(getApplicationContext()));
        deviceGroup.setType(type);
        UserBean user = LoginHelper.getUser(getApplicationContext());
        if(user!=null){
            deviceGroup.setShopId(user.getShopId());
        }
        List<Device> devices = new ArrayList<>();
        //pad信息
        Device devicePad = new Device();
        devicePad.setDeviceType(0);
        devicePad.setHardVersion(android.os.Build.MODEL);
        devicePad.setSyssoftVersion(Build.VERSION.RELEASE);
        devicePad.setAppsoftVersion(MyUtil.getVersion(getApplicationContext()));
        devices.add(devicePad);

        //打印机信息
        int bigPrinter = PrintSetting.getBigPrinter(getApplicationContext());
        int smallPrinter = PrintSetting.getSmallPrinter(getApplicationContext());

        //大标签
        Device deviceBig = new Device();
        deviceBig.setDeviceType(1);
        deviceBig.setHardVersion(bigPrinter==PrintSetting.FUJITSU?"富士通":"WinPos");
        int bigPrinterStatus = checkPrinterStatus(PrintFace.BIGLABELIP,9100,bigPrinter==PrintSetting.FUJITSU);
        deviceBig.setStatus(bigPrinterStatus);
        devices.add(deviceBig);


        //小标签
        Device deviceSmall = new Device();
        deviceSmall.setDeviceType(2);
        deviceSmall.setHardVersion(smallPrinter==PrintSetting.FUJITSU?"富士通":"WinPos");
        int smallPrinterStatus = checkPrinterStatus(PrintFace.SMALLLABELIP,9100,smallPrinter==PrintSetting.FUJITSU);
        deviceSmall.setStatus(smallPrinterStatus);
        devices.add(deviceSmall);


        deviceGroup.setGroup(devices);

        return deviceGroup;
    }


    private int checkPrinterStatus(String ip,int port,boolean isNew){
        int status = 100;
        if(isNew){
            byte[] bytes = new byte[]{0x1b,0x21,0x3f};
            byte[] result = new byte[1];
            Socket client = null;
            try {
                client = new Socket(ip, port);
                client.setSoTimeout(1000);
                OutputStream os = client.getOutputStream();
                os.write(bytes);

                InputStream is = client.getInputStream();
                is.read(result);
                is.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.e(TAG, "UnknownHostException:"+e.getMessage());
                status = 200;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "IOException" + e.getMessage());
                status = 200;
            }finally {

                try {if(client!=null){
                    client.close();
                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return transformResult(result);

        }else{
            int timeOut = 1000; //定义超时，表明该时间内连不上即认定为不可达，超时值不能太小。
            try {//ping功能
                boolean isSuccess = InetAddress.getByName(ip).isReachable(timeOut);
                if(isSuccess){
                    status = 100;
                }else {
                    status = 200;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
                status = 200;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
                status = 200;
            }

            return status;
        }

    }


    public int transformResult(byte[] src) {
        int result = 100;
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return result;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        String hexCode = stringBuilder.toString();
        if("00".equals(hexCode)){
            result = 100;
        }else if("01".equals(hexCode)){
            result = 101;
        }else if("02".equals(hexCode)){
            result = 102;
        }else if("04".equals(hexCode)){
            result = 104;
        }else if("08".equals(hexCode)){
            result = 108;
        }else if("10".equals(hexCode)){
            result = 110;
        }else {
            result = 100;
        }
        return result;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG,TAG+"--onDestroy");
        Logger.getLogger().log("MonitorService 销毁");
    }
}
