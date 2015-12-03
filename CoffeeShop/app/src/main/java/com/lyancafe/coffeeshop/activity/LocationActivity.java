package com.lyancafe.coffeeshop.activity;

import android.app.Activity;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.lyancafe.coffeeshop.CoffeeShopApplication;
import com.lyancafe.coffeeshop.R;

/**
 * Created by Administrator on 2015/12/3.
 */
public class LocationActivity extends Activity {

    private MapView mapView;
    private BaiduMap mBaiduMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(CoffeeShopApplication.latLng, 16.0f);
        mBaiduMap.setMapStatus(msu);
        if(CoffeeShopApplication.latLng!=null){
            OverlayOptions textOption = new TextOptions()
                    .bgColor(0xAAFFFF00)
                    .fontSize(20)
                    .fontColor(0xFFFF00FF)
                    .text("我的门店")
                    .rotate(0)
                    .position(CoffeeShopApplication.latLng);
            mBaiduMap.addOverlay(textOption);
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.mipmap.ic_launcher);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(CoffeeShopApplication.latLng)
                    .zIndex(9)
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
        }


    //    lat = 31.167075  lng = 121.404038 古美路
    //手机定位    latitude : 31.161716 lontitude : 121.397646

    }

    private void addOverlayMark(double lat,double lng){
        //定义Maker坐标点
        LatLng point = new LatLng(lat, lng);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_location_mark_default);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .zIndex(9)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        addOverlayMark(31.177050,121.414238);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
