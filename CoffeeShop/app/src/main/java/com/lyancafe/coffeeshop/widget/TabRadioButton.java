package com.lyancafe.coffeeshop.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.lyancafe.coffeeshop.R;


/**
 * Created by Administrator on 2015/8/7.
 */
public class TabRadioButton extends RadioButton {

    public TabRadioButton(Context context) {
        super(context);
    }

    public TabRadioButton(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TabRadioButton.this.setTextColor(context.getResources().getColor(R.color.radiobutton_text_press));
                    invalidate();
                } else {
                    TabRadioButton.this.setTextColor(context.getResources().getColor(R.color.radiobutton_text_normal));
                    invalidate();
                }
            }
        });
    }
}
