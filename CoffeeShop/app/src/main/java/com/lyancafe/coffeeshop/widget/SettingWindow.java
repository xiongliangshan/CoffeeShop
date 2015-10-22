package com.lyancafe.coffeeshop.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.helper.OrderHelper;

/**
 * Created by Administrator on 2015/10/22.
 */
public class SettingWindow extends PopupWindow {

    private static final String TAG = "SettingWindow";
    private Context context;
    private View contentView;

    public SettingWindow(Context context) {
        super(context);
        this.context = context;
        contentView = LayoutInflater.from(context).inflate(R.layout.window_popup_setting,null);
        this.setContentView(contentView);
        initView(contentView,context);
        this.setHeight(OrderHelper.dip2Px(200, context));
        this.setWidth(OrderHelper.dip2Px(150, context));
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setAnimationStyle(android.R.style.Animation_Dialog);
    }

    private void initView(View contentView,Context context){

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

}
