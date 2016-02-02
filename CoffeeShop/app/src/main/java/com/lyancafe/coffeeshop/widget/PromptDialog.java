package com.lyancafe.coffeeshop.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.MyUtil;

/**
 * Created by Administrator on 2016/2/1.
 */
public class PromptDialog extends Dialog {

    private Context mContext;
    private String content;
    private Mode mode = Mode.SINGLE;
    private OnClickOKListener mListener;
    private TextView contentText;
    private Button okBtn;
    private Button cacelBtn;

    public PromptDialog(Context context) {
        super(context);
    }

    public PromptDialog(Context context, int theme) {
        super(context, theme);
    }

    public PromptDialog(Context context, int theme, OnClickOKListener OKListener) {
        super(context,theme);
        mContext = context;
        this.mListener = OKListener;

    }

    public enum Mode{
        BOTH,SINGLE
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_prompt);
        contentText = (TextView) findViewById(R.id.tv_dialog_content);
        okBtn = (Button) findViewById(R.id.btn_dialog_ok);
        cacelBtn = (Button) findViewById(R.id.btn_dialog_cacel);

        contentText.setText(content);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onClickOK();
                }
            }
        });
        cacelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if(mode==Mode.SINGLE){
            cacelBtn.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(OrderHelper.dip2Px(200,mContext), ViewGroup.LayoutParams.WRAP_CONTENT);
            okBtn.setLayoutParams(layoutParams);
        }else {
            cacelBtn.setVisibility(View.VISIBLE);
        }

        setCanceledOnTouchOutside(false);
    }



    public interface OnClickOKListener{
        public void onClickOK();
    }

}


