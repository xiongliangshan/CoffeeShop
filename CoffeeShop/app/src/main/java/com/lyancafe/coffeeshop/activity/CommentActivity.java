package com.lyancafe.coffeeshop.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.adapter.CommentAdapter;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.helper.OrderHelper;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/4/13.
 */
public class CommentActivity extends BaseActivity {

    private static final String TAG = "CommentActivity";
    private Context mContext;
    private ProgressBar progressBar;
    private Button closeBtn;
    private TextView commentCountText;
    private RecyclerView commentsListView;
    private RecyclerView.LayoutManager layoutManager;
    private CommentAdapter mAdapter;
    private int mCommentType = -1;


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
        int type = getIntent().getIntExtra("coment_type",4);
        initView();
        updateTitle(type, -1);
        mCommentType = type;
        HttpHelper.getInstance().reqCommentList(type, new JsonCallback<XlsResponse>() {
            @Override
            public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                handleCommentListResponse(xlsResponse,call,response);
            }
        });


    }

    /**
     * 处理评论列表返回结果
     * @param xlsResponse
     * @param call
     * @param response
     */
    private void handleCommentListResponse(XlsResponse xlsResponse, Call call, Response response) {
        if(xlsResponse.status==0){
            List<OrderBean> orderBeans = OrderBean.parseJsonOrders(this, xlsResponse);
            updateTitle(mCommentType,orderBeans.size());
            mAdapter.setData(orderBeans);
        }
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
        commentsListView.addItemDecoration(new MyItemDecoration(this, RecyclerView.VERTICAL));
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

    //更新标题栏
    private void updateTitle(int commentType,int count){
        String countStr = count<0?"":(count+"");
        if(OrderHelper.GOOD_COMMENT==commentType){
            //好评
            commentCountText.setText("今日好评 "+countStr);
        }else if(OrderHelper.BAD_COMMENT==commentType){
            //差评
            commentCountText.setText("今日差评 "+countStr);
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
