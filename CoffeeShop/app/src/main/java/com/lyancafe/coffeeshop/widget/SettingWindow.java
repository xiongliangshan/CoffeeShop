package com.lyancafe.coffeeshop.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/22.
 */
public class SettingWindow extends PopupWindow {

    private static final String TAG = "SettingWindow";
    private Context context;
    private View contentView;
    private RadioGroup statusSwitch;
    private static final String current_status = "G";

    private static final String GREEN = "G";
    private static final String YELLOW = "Y";



    public SettingWindow(Context context) {
        super(context);
        this.context = context;
        contentView = LayoutInflater.from(context).inflate(R.layout.window_popup_setting,null);
        this.setContentView(contentView);
        initView(contentView, context);
        this.setHeight(OrderHelper.dip2Px(200, context));
        this.setWidth(OrderHelper.dip2Px(200, context));
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setAnimationStyle(android.R.style.Animation_Dialog);
    }

    private void initView(View contentView, final Context context){
        statusSwitch = (RadioGroup)contentView.findViewById(R.id.status_switch);
       /* statusSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (current_status){
                    case "G":
                        new SwitchShopStatusQry(context, YELLOW).doRequest();
                        break;
                    case "Y":

                        break;
                }

            }
        });*/
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


    // http://api.lyancafe.com/shop/v3/{shopId}/status
    class SwitchShopStatusQry implements Qry{

        private Context context;
        private String isBusy;

        public SwitchShopStatusQry(Context context, String isBusy) {
            this.context = context;
            this.isBusy = isBusy;
        }

        @Override
        public void doRequest() {
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/status?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("light", isBusy);
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this);
        }

        @Override
        public void showResult(Jresp resp) {
            Log.d(TAG, "SwitchShopStatusQry:resp =" + resp);
            if(resp == null){
                ToastUtil.showToast(context, R.string.unknown_error);
                return;
            }
            if(resp.status==0){
                ToastUtil.showToast(context, "切换状态成功");
               /* if(YELLOW.equals(isBusy)){
                    shopStatusTxt.setText("繁忙");
                }else{
                    shopStatusTxt.setText("空闲");
                }*/
            }
        }
    }
}
