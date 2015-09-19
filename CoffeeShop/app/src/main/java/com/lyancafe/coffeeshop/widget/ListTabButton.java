package com.lyancafe.coffeeshop.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;

/**
 * Created by Administrator on 2015/9/19.
 */
public class ListTabButton extends LinearLayout {

    private TextView contentTxt;
    private TextView textView;
    private String content;

    public ListTabButton(Context context) {
        super(context);
    }

    public ListTabButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LTB);
        content = a.getString(R.styleable.LTB_ltb_text);
        a.recycle();
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        contentTxt = new TextView(context,attrs);
        contentTxt.setTextColor(Color.BLACK);
        contentTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        contentTxt.setText(content);

        textView = new TextView(context,attrs);
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setBackgroundResource(R.drawable.number_text_background_normal);
        textView.setPadding(dip2Px(5), dip2Px(1), dip2Px(5), dip2Px(1));
        textView.setText("99");
        addView(contentTxt);
        addView(textView);


    }

    /*
     * converts dip to px
     */
    private int dip2Px(float dip) {
        return (int) (dip * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    public void setCount(int number){
        textView.setText(number+"");
    }

    public void setClickBg(boolean click){
        if(click){
            contentTxt.setTextColor(Color.GRAY);
            textView.setBackgroundResource(R.drawable.number_text_background_pressed);
        }else{
            contentTxt.setTextColor(Color.BLACK);
            textView.setBackgroundResource(R.drawable.number_text_background_normal);
        }
    }
}
