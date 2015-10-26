package com.lyancafe.coffeeshop.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.activity.HomeActivity;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.helper.ShopHelper;
import com.lyancafe.coffeeshop.utils.MyUtil;
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
public class SettingWindow extends PopupWindow implements View.OnClickListener{

    private static final String TAG = "SettingWindow";
    private Context context;
    private View contentView;
    private RadioGroup statusSwitch;
    private RadioButton freeRb;
    private RadioButton busyRb;
    private RadioButton stopRb;
    private TextView feedbackTxt;
    private TextView currentVerTxt;
    private Button loginOutBtn;
    private static String current_status = "G";

    private static final String GREEN = "G";
    private static final String YELLOW = "Y";
    private static final String RED = "R";



    public SettingWindow(Context context) {
        super(context);
        this.context = context;
        contentView = LayoutInflater.from(context).inflate(R.layout.window_popup_setting,null);
        this.setContentView(contentView);
        initView(contentView, context);
        this.setHeight(OrderHelper.dip2Px(210, context));
        this.setWidth(OrderHelper.dip2Px(220, context));
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setAnimationStyle(R.style.setting_popwin_anim_style);
        //同步本地文件的状态到内存
        current_status = ShopHelper.getShopStatus(context);
        setBackRadio();
    }


    private void initView(View contentView, final Context context){
        currentVerTxt = (TextView) contentView.findViewById(R.id.current_version);
        feedbackTxt = (TextView) contentView.findViewById(R.id.feedback);
        feedbackTxt.setOnClickListener(this);
        loginOutBtn = (Button) contentView.findViewById(R.id.login_out);
        loginOutBtn.setOnClickListener(this);
        currentVerTxt.setText("当前版本:"+MyUtil.getVersion(context));
        statusSwitch = (RadioGroup)contentView.findViewById(R.id.status_switch);
        freeRb = (RadioButton) contentView.findViewById(R.id.radio_free);
        freeRb.setChecked(true);
        busyRb = (RadioButton) contentView.findViewById(R.id.radio_busy);
        stopRb = (RadioButton) contentView.findViewById(R.id.radio_stop);
        freeRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SwitchShopStatusQry(context, GREEN).doRequest();
            }
        });
        busyRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SwitchShopStatusQry(context, YELLOW).doRequest();
            }
        });
        stopRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SwitchShopStatusQry(context, RED).doRequest();
            }
        });

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.feedback:
                //意见反馈
                FeedBackDialog fd = new FeedBackDialog(context,R.style.MyDialog);
                fd.show();
                break;
            case R.id.login_out:
                //退出登录
                ((HomeActivity)context).finish();
                LoginHelper.saveToken(context,"");
                break;
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
            setBackRadio();
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
                current_status = isBusy;
                ShopHelper.saveShopStatus(context,isBusy);
                setBackRadio();
            }else{
                ToastUtil.showToast(context, "切换状态失败:"+resp.message);
            }
        }
    }

    private void setBackRadio(){
        switch (current_status){
            case "G":
                freeRb.setChecked(true);
                break;
            case "Y":
                busyRb.setChecked(true);
                break;
            case "R":
                stopRb.setChecked(true);
                break;
        }
    }
}
