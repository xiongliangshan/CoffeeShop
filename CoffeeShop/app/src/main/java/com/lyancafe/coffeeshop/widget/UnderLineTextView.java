package com.lyancafe.coffeeshop.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/2/18.
 */

public class UnderLineTextView extends TextView {

    private static final String TAG = UnderLineTextView.class.getName();
    public UnderLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = getPaint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(3.0f);
        canvas.drawLine(getPaddingLeft(),getHeight()-getPaddingBottom(),getWidth()-getPaddingRight(),getHeight()-getPaddingBottom(),paint);
    }
}
