package com.lyancafe.coffeeshop.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;

/**
 * Created by Administrator on 2016/7/5.
 */
public class InfoDetailDialog extends Dialog {

    private static final String TAG = "InfoDetailDialog";
    private TextView infoDetailText;

    public InfoDetailDialog(Context context) {
        super(context, R.style.MyDialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate:");
        getWindow().setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
        setContentView(R.layout.dialog_info_detail);
        infoDetailText = (TextView) findViewById(R.id.tv_info_detail);
    }

    public void show(String content){
        Log.d(TAG, "show:" + content);
        if(TextUtils.isEmpty(content)||"\n".equals(content)){
            return;
        }
        this.show();
        if(infoDetailText!=null){
            infoDetailText.setText(content);
        }
    }
}
