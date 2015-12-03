package com.lyancafe.coffeeshop;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;

import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by Administrator on 2015/12/3.
 */
public class ShopLocation  {

    private static final String TAG ="ShopLocation";
    private Context mContext;
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    private LocationClientOption.LocationMode currentMode = LocationClientOption.LocationMode.Hight_Accuracy;
    private String currentCoor="bd09ll";
    private int spanTime = 0; //请求定位间隔时间,单位ms
    public ShopLocation(Context context) {
        mContext = context;
        initLocation();
    }


    private void initLocation(){
        mLocationClient = new LocationClient(mContext.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(currentMode);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType(currentCoor);//可选，默认gcj02，设置返回的定位结果坐标系，
        option.setScanSpan(spanTime);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
    }

    public void startLocation(){
        mLocationClient.start();
    }

    /**
     * 实现实时位置回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location

            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType()==BDLocation.TypeNetWorkLocation
                    || location.getLocType()== BDLocation.TypeOffLineLocation ){// 定位成功
                CoffeeShopApplication.latLng = new LatLng(location.getLatitude(),location.getLongitude());
                Log.d(TAG,"lat = "+CoffeeShopApplication.latLng.latitude+" , lng = "+CoffeeShopApplication.latLng.longitude+",type="+location.getLocType());

            }  else{
                //定位失败
                startLocation();
            }
        }


    }


}
