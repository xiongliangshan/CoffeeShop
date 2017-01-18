package com.lyancafe.coffeeshop.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lyancafe.coffeeshop.R;

/**
 * Created by Administrator on 2015/10/26.
 */
public class FeedBackDialog extends Dialog implements View.OnClickListener{

    private static final String TAG ="FeedBackDialog";
    private Context context;
    private Button submitBtn;
    private Button cancelBtn;

    public FeedBackDialog(Context context) {
        super(context);
        this.context = context;
    }

    public FeedBackDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_feedback);
        this.setCanceledOnTouchOutside(false);
        submitBtn = (Button) findViewById(R.id.btn_submit);
        submitBtn.setOnClickListener(this);
        cancelBtn = (Button) findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                break;
            case R.id.btn_cancel:
                this.dismiss();
                break;
        }
    }
}
