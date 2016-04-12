package com.lyancafe.coffeeshop.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by Administrator on 2016/4/12.
 */
public class DeliverImageDialog extends Dialog {

    private static DeliverImageDialog mDialog;
    private String name;
    private String phone;
    private String imgUrl;

    private ImageView crossImage;
    private ImageView iconImage;
    private TextView deliverNameText;
    private TextView deliverPhoneText;

    private DisplayImageOptions mDisplayImageOptions;

    private DeliverImageDialog(Context context, int theme) {
        super(context, theme);
    }

    /*public DeliverImageDialog(Context context, int theme, String name, String phone, String url) {
        super(context, theme);
        this.name = name;
        this.phone = phone;
        this.url = url;
    }*/

    public static DeliverImageDialog getInstance(Context context){
        if(mDialog==null){
            mDialog = new DeliverImageDialog(context, R.style.PromptDialog);
        }
        return mDialog;
    }

    public DeliverImageDialog setContent(String name,String phone,String imgUrl){
        this.name = name;
        this.phone = phone;
        this.imgUrl = imgUrl;
        return mDialog;
    }

    private void initView(){
        crossImage = (ImageView) findViewById(R.id.iv_cross);
        crossImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDialog!=null){
                    mDialog.dismiss();
                }
            }
        });
        iconImage = (ImageView) findViewById(R.id.iv_deliver_icon);
        deliverNameText = (TextView) findViewById(R.id.tv_deliver_name);
        deliverPhoneText = (TextView) findViewById(R.id.tv_deliver_phone);

        deliverNameText.setText(name);
        deliverPhoneText.setText(phone);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_deliver_img);
        mDisplayImageOptions = new DisplayImageOptions.Builder()
                /*.showImageOnLoading(R.mipmap.img_loading)
                .showImageOnFail(R.mipmap.img_loading)
                .showImageForEmptyUri(R.mipmap.img_empty)*/
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        initView();
    }


}
