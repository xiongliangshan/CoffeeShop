package com.lyancafe.coffeeshop.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.utils.LogUtil;

/**
 * Created by Administrator on 2017/12/27.
 */

public class ProgressPercent extends View {

    private static final String TAG = "ProgressPercent";

    private String mBeforeStartText;
    private String mStartText;
    private String mEndText;
    private String mOverText;
    private int mDesTextColor;
    private float mDesTextSize;
    private int mPassColor;
    private float mRoundRadius;
    private Paint textPaint;
    private Paint rectPaint;


    private int passMinute;
    private int unPassMinute;
    private int overMinute;

    private RectF mProgressRect = null;
    private RectF mPassRect = null;
    private RectF mUnpassRect = null;

    /**
     * -1:当前时间小于起始时间
     * 0:当前时间在起止范围之内
     * 1:当前时间大于截止时间
     */
    private int status;


    public ProgressPercent(Context context) {
        super(context);
    }

    public ProgressPercent(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ProgressPercent);
        mStartText = a.getString(R.styleable.ProgressPercent_startText);
        mEndText = a.getString(R.styleable.ProgressPercent_endText);
        mDesTextColor = a.getColor(R.styleable.ProgressPercent_desTextColor, Color.BLACK);
        mDesTextSize = a.getDimension(R.styleable.ProgressPercent_desTextSize,16f);
        mPassColor = a.getColor(R.styleable.ProgressPercent_passColor,Color.GREEN);
        mRoundRadius = a.getDimension(R.styleable.ProgressPercent_roundRadius,20f);
        LogUtil.d(TAG,"mRoundRadius = "+mRoundRadius);
        a.recycle();
        initDraw();
    }

    public ProgressPercent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initDraw(){
        mBeforeStartText = "未开始";
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(mDesTextColor);
        textPaint.setTextSize(mDesTextSize);
        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        status = -1;




    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(caculateWidth(widthMeasureSpec),caculateHeight(heightMeasureSpec));
    }

    /**
     * 更新进度
     * @param startTime
     * @param endTime
     */
    public void updateProgress(long startTime,long endTime){
        long nowTime = System.currentTimeMillis();
        if(startTime==0||nowTime<=startTime){
            this.status = -1;
        }else if(nowTime>startTime && nowTime<=endTime){
            this.status = 0;
            passMinute = (int) ((nowTime-startTime)/(60*1000));
            unPassMinute = (int) ((endTime-nowTime)/(60*1000));
        }else {
            this.status = 1;
            overMinute = (int) (Math.abs(endTime-nowTime)/(60*1000));
            mOverText = "超时 "+overMinute+" 分钟";
        }

        invalidate();

        LogUtil.d(TAG,"acceptTime = "+OrderHelper.formatOrderDate(startTime)+"|status = "+status+"|pass ="+passMinute+"|unpass="+unPassMinute+"|over ="+overMinute);
    }



    private int caculateWidth(int widthMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if(widthMode==MeasureSpec.EXACTLY){
            return widthSize;
        }else if(widthMode==MeasureSpec.AT_MOST){
            return Math.min(OrderHelper.dip2Px(200,getContext()),widthSize);
        }else {
            return OrderHelper.dip2Px(200,getContext());
        }

    }

    private int caculateHeight(int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(heightMode==MeasureSpec.EXACTLY){
            return heightSize;
        }else if(heightMode==MeasureSpec.AT_MOST){
            return Math.min(OrderHelper.dip2Px(26,getContext()),heightSize);
        }else {
            return OrderHelper.dip2Px(26,getContext());
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int height = getHeight() - paddingTop - paddingBottom;

        float startTextWidth = textPaint.measureText(mStartText);
        float endTextWidth = textPaint.measureText(mEndText);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        float baseLineY = paddingTop + height/2 - top/2 - bottom/2;//基线中间点的y轴计算公式

        textPaint.setColor(Color.BLACK);
        canvas.drawText(mStartText,getPaddingLeft()+2,baseLineY,textPaint);
        canvas.drawText(mEndText,getWidth()-getPaddingRight()-endTextWidth-2,baseLineY,textPaint);
        if(mProgressRect==null){
            mProgressRect = new RectF(paddingLeft+startTextWidth+4,paddingTop+1,getWidth()-paddingRight-endTextWidth-4,height-1);
        }else {
            mProgressRect.set(paddingLeft+startTextWidth+4,paddingTop+1,getWidth()-paddingRight-endTextWidth-4,height-1);
        }

        if(status==-1){
            //未开始
            rectPaint.setStyle(Paint.Style.STROKE);
            rectPaint.setAntiAlias(true);
            rectPaint.setStrokeWidth(1f);
            rectPaint.setColor(Color.BLACK);
            canvas.drawRoundRect(mProgressRect,mRoundRadius,mRoundRadius,rectPaint);
            textPaint.setColor(Color.BLACK);
            float beforeStartWidth = textPaint.measureText(mBeforeStartText);
            canvas.drawText(mBeforeStartText,mProgressRect.left+(mProgressRect.width()-beforeStartWidth)/2.0f,baseLineY,textPaint);
        }else if(status==0){
            //进行中
            if(mPassRect==null){
                mPassRect = new RectF(mProgressRect.left+1,mProgressRect.top+1,mProgressRect.left+mProgressRect.width()*passMinute/(passMinute+unPassMinute)+1,mProgressRect.bottom-1);
            }else{
                mPassRect.set(mProgressRect.left+1,mProgressRect.top+1,mProgressRect.left+mProgressRect.width()*passMinute/(passMinute+unPassMinute)+1,mProgressRect.bottom-1);
            }
//            RectF rectPass = new RectF(progressRect.left+1,progressRect.top+1,progressRect.left+progressRect.width()*passMinute/(passMinute+unPassMinute)+1,progressRect.bottom-1);
//            RectF rectUnPass = new RectF(rectPass.right+1,paddingTop+1,progressRect.right-1,height-1);
            if(mUnpassRect==null){
                mUnpassRect = new RectF(mPassRect.right+1,paddingTop+1,mProgressRect.right-1,height-1);
            }else {
                mUnpassRect.set(mPassRect.right+1,paddingTop+1,mProgressRect.right-1,height-1);
            }

            rectPaint.setStyle(Paint.Style.STROKE);
            rectPaint.setStrokeJoin(Paint.Join.ROUND);
            rectPaint.setStrokeCap(Paint.Cap.ROUND);
            rectPaint.setDither(true);
            rectPaint.setStrokeWidth(1f);
            rectPaint.setColor(Color.BLACK);
            canvas.drawRoundRect(mProgressRect,mRoundRadius,mRoundRadius,rectPaint);

            rectPaint.setStyle(Paint.Style.FILL);
            rectPaint.setColor(mPassColor);
            canvas.drawRoundRect(mPassRect,mRoundRadius,mRoundRadius,rectPaint);

            float passTextWidth = textPaint.measureText(String.valueOf(passMinute));
            textPaint.setColor(Color.BLACK);
            canvas.drawText(String.valueOf(passMinute),mPassRect.left+(mPassRect.width()-passTextWidth)/2.0f,baseLineY,textPaint);


            float unPassTextWidth = textPaint.measureText(String.valueOf(unPassMinute));
            canvas.drawText(String.valueOf(unPassMinute),mUnpassRect.left-1+(mUnpassRect.width()-unPassTextWidth)/2.0f,baseLineY,textPaint);
        }else{
            //已超时
            rectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            rectPaint.setColor(Color.RED);
            canvas.drawRoundRect(mProgressRect,mRoundRadius,mRoundRadius,rectPaint);
            textPaint.setColor(Color.WHITE);
            float overTextWidth = textPaint.measureText(mOverText);
            canvas.drawText(mOverText,mProgressRect.left+(mProgressRect.width()-overTextWidth)/2.0f,baseLineY,textPaint);
        }
    }
}
