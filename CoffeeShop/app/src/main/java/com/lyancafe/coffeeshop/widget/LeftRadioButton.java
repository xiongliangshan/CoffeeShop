package com.lyancafe.coffeeshop.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.lyancafe.coffeeshop.R;

/**
 * Created by Administrator on 2015/9/18.
 */
public class LeftRadioButton extends RadioButton {

    public LeftRadioButton(Context context) {
        super(context);
    }

    public LeftRadioButton(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LeftRadioButton.this.setTextColor(context.getResources().getColor(R.color.radiobutton_text_press));
                    invalidate();
                } else {
                    LeftRadioButton.this.setTextColor(context.getResources().getColor(R.color.radiobutton_text_normal));
                    invalidate();
                }
            }
        });
    }
}
