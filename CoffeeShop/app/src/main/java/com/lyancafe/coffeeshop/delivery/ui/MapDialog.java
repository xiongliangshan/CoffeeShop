package com.lyancafe.coffeeshop.delivery.ui;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.lyancafe.coffeeshop.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LAT = "lat";
    private static final String ARG_LNG = "lng";

    // TODO: Rename and change types of parameters
    private double mLat;
    private double mLng;

    private MapView mMapView;
    BaiduMap mBaiduMap;
    MapStatus ms;

    public MapDialog() {
        // Required empty public constructor
    }

    /**
     *
     * @param lat 纬度
     * @param lng 经度
     * @return
     */
    public static MapDialog newInstance(double lat, double lng) {
        MapDialog fragment = new MapDialog();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LNG, lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLat = getArguments().getDouble(ARG_LAT);
            mLng = getArguments().getDouble(ARG_LNG);
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MapDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_dialog, container, false);
        mMapView = (MapView) view.findViewById(R.id.map_view);
        LatLng latLng = new LatLng(mLat, mLng);
        if(latLng!=null){
            ms = new MapStatus.Builder().target(latLng).zoom(15).build();
            mBaiduMap = mMapView.getMap();
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
            addMark(latLng);
        }
        return view;
    }

    private void addMark(LatLng point) {
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.mark);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.1f;
            window.setAttributes(windowParams);
            window.setLayout((int) (dm.widthPixels * 0.6), (int)(dm.widthPixels * 0.4));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mBaiduMap!=null){
            mBaiduMap.clear();
        }
        if(mMapView!=null){
            mMapView.onDestroy();
        }
    }
}
