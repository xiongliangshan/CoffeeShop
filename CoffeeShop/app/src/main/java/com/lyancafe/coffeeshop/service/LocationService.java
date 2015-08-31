package com.lyancafe.coffeeshop.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

public class LocationService extends Service implements AMapLocationListener {

    private static final String TAG = "LocationService";

    private LocationManagerProxy mLocationManagerProxy;

    public LocationService() {
        Log.d(TAG,"LocationService construct");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "LocationService onCreate");
        init();
    }

    /**
     ?* 初始化定位
     */
    private void init() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法?????
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 2 * 1000, 15, this);
        mLocationManagerProxy.setGpsEnable(true);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d(TAG,"code = "+aMapLocation.getAMapException().getErrorCode()+"\n onLocationChanged aMapLocation = "+aMapLocation.toString());
        if(aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0){
            //获取位置信息
            Double geoLat = aMapLocation.getLatitude();
            Double geoLng = aMapLocation.getLongitude();
            Log.d(TAG,"geoLat = "+geoLat + ",geolng = "+geoLng);
        }
    }

    //此方法已经被废弃
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged:provider = " + provider + ",status = " + status + ",extras = " + extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG,"onProviderEnabled : provider = "+provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG,"onProviderDisabled : provider = "+provider);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destroy();
        }
        mLocationManagerProxy = null;
    }
}
