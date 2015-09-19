package com.lyancafe.coffeeshop.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.lyancafe.coffeeshop.R;

/**
 * Created by Administrator on 2015/9/18.
 */
public class ListRadioButton extends RadioButton {


    public ListRadioButton(Context context) {
        super(context);
    }

    public ListRadioButton(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              if (isChecked) {
                    ListRadioButton.this.setTextColor(context.getResources().getColor(R.color.radiobutton_text_press));
                    invalidate();
                } else {
                    ListRadioButton.this.setTextColor(context.getResources().getColor(R.color.radiobutton_text_normal));
                    invalidate();
                }
            }
        });
    }



}
