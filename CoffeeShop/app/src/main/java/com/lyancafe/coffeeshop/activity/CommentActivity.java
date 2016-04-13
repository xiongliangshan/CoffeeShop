package com.lyancafe.coffeeshop.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.CommentAdapter;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/13.
 */
public class CommentActivity extends BaseActivity {

    private Context mContext;
    private ProgressBar progressBar;
    private Button closeBtn;
    private TextView commentCountText;
    private RecyclerView commentsListView;
    private RecyclerView.LayoutManager layoutManager;
    private CommentAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_comment);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.x = -100;
        lp.y = 40;
        getWindow().setAttributes(lp);

        initView();
        new OrderFinishedQry(mContext, OrderHelper.PRODUCE_TIME, OrderHelper.ALL,false).doRequest();


    }

    private void initView(){
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        commentCountText = (TextView) findViewById(R.id.tv_comment_count);
        closeBtn = (Button) findViewById(R.id.btn_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        commentsListView = (RecyclerView) findViewById(R.id.rv_comments);
        layoutManager = new LinearLayoutManager(mContext);
        commentsListView.setLayoutManager(layoutManager);
        mAdapter = new CommentAdapter(mContext);
        commentsListView.setAdapter(mAdapter);
        commentsListView.addItemDecoration(new MyItemDecoration(this, RecyclerView.VERTICAL) );
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //已完成订单列表接口
    class OrderFinishedQry implements Qry {

        private Context context;
        private int orderBy;
        private int fillterInstant;
        private boolean isShowProgress;
        public OrderFinishedQry(Context context, int orderBy, int fillterInstant,boolean isShowProgress) {
            this.context = context;
            this.orderBy = orderBy;
            this.fillterInstant = fillterInstant;
            this.isShowProgress = isShowProgress;
        }

        @Override
        public void doRequest() {
            progressBar.setVisibility(View.VISIBLE);
            String token = LoginHelper.getToken(context);
            int shopId = LoginHelper.getShopId(context);
            String url = HttpUtils.BASE_URL+shopId+"/orders/today/finished?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("orderBy",orderBy);
            params.put("fillterInstant", fillterInstant);

            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this, isShowProgress);
        }

        @Override
        public void showResult(Jresp resp) {
            progressBar.setVisibility(View.GONE);
            if(resp==null){
                ToastUtil.showToast(context, R.string.unknown_error);
                return;
            }
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(context, resp);
            mAdapter.setData(orderBeans);
        }
    }

    class  MyItemDecoration extends RecyclerView.ItemDecoration{

        /*
     * RecyclerView的布局方向，默认先赋值
     * 为纵向布局
     * RecyclerView 布局可横向，也可纵向
     * 横向和纵向对应的分割想画法不一样
     * */
        private int mOrientation = LinearLayoutManager.VERTICAL ;

        /**
         * item之间分割线的size，默认为1
         */
        private int mItemSize = 1 ;

        /**
         * 绘制item分割线的画笔，和设置其属性
         * 来绘制个性分割线
         */
        private Paint mPaint ;

        /**
         * 构造方法传入布局方向，不可不传
         * @param context
         * @param orientation
         */
        public MyItemDecoration(Context context,int orientation) {
            this.mOrientation = orientation;
            if(orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL){
                throw new IllegalArgumentException("请传入正确的参数") ;
            }
            mItemSize = (int) TypedValue.applyDimension(mItemSize, TypedValue.COMPLEX_UNIT_DIP, context.getResources().getDisplayMetrics());
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG) ;
            mPaint.setColor(context.getResources().getColor(R.color.bg_line_separator));
         /*设置填充*/
            mPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if(mOrientation == LinearLayoutManager.VERTICAL){
                drawVertical(c,parent) ;
            }else {
                drawHorizontal(c,parent) ;
            }
        }

        /**
         * 绘制纵向 item 分割线
         * @param canvas
         * @param parent
         */
        private void drawVertical(Canvas canvas,RecyclerView parent){
            final int left = parent.getPaddingLeft() ;
            final int right = parent.getMeasuredWidth() - parent.getPaddingRight() ;
            final int childSize = parent.getChildCount() ;
            for(int i = 0 ; i < childSize ; i ++){
                final View child = parent.getChildAt( i ) ;
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int top = child.getBottom() + layoutParams.bottomMargin ;
                final int bottom = top + mItemSize ;
                canvas.drawRect(left,top,right,bottom,mPaint);
            }
        }

        /**
         * 绘制横向 item 分割线
         * @param canvas
         * @param parent
         */
        private void drawHorizontal(Canvas canvas,RecyclerView parent){
            final int top = parent.getPaddingTop() ;
            final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom() ;
            final int childSize = parent.getChildCount() ;
            for(int i = 0 ; i < childSize ; i ++){
                final View child = parent.getChildAt( i ) ;
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int left = child.getRight() + layoutParams.rightMargin ;
                final int right = left + mItemSize ;
                canvas.drawRect(left,top,right,bottom,mPaint);
            }
        }

        /**
         * 设置item分割线的size
         * @param outRect
         * @param view
         * @param parent
         * @param state
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if(mOrientation == LinearLayoutManager.VERTICAL){
                outRect.set(0,0,0,mItemSize);
            }else {
                outRect.set(0,0,mItemSize,0);
            }
        }
    }

}
