package com.lyancafe.coffeeshop.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CoffeeShopApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.SFGroupBean;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.constant.TabList;
import com.lyancafe.coffeeshop.event.FinishProduceEvent;
import com.lyancafe.coffeeshop.event.PrintOrderEvent;
import com.lyancafe.coffeeshop.event.StartProduceEvent;
import com.lyancafe.coffeeshop.fragment.OrdersFragment;
import com.lyancafe.coffeeshop.helper.OrderHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/5.
 */
public class OrderListViewAdapter extends RecyclerView.Adapter<OrderListViewAdapter.ViewHolder>{


    private static final String TAG  ="OrderListViewAdapter";
    private List<SFGroupBean> groupList = new ArrayList<>();
    private Context mContext;
    public int selected = -1;
    private SpaceItemDecoration mItemDecoration;

    public OrderListViewAdapter(Context mContext) {
        this.mContext = mContext;
        mItemDecoration = new SpaceItemDecoration(4,OrderHelper.dip2Px(12, mContext),false);
    }

    @Override
    public OrderListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_sf_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderListViewAdapter.ViewHolder holder, int position) {
        SFGroupBean sfGroupBean = groupList.get(position);
        holder.batchPromptText.setText(position + "条");
        holder.horizontalListView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        holder.horizontalListView.setHasFixedSize(true);
        holder.horizontalListView.setItemAnimator(new DefaultItemAnimator());
    //    holder.horizontalListView.addItemDecoration(mItemDecoration);
        SFItemListAdapter adapter = new SFItemListAdapter(mContext,sfGroupBean.getItemGroup());
        holder.horizontalListView.setAdapter(adapter);
    }



    @Override
    public int getItemCount() {
        return groupList.size();
    }



    public void setData(List<SFGroupBean> sfGroupList){
        this.groupList = sfGroupList;
        notifyDataSetChanged();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView batchPromptText;
        public Button batchHandlerBtn;
        public RecyclerView horizontalListView;

        public ViewHolder(View itemView) {
            super(itemView);
            batchPromptText = (TextView) itemView.findViewById(R.id.tv_sf_prompt);
            batchHandlerBtn = (Button) itemView.findViewById(R.id.btn_sf_handler);
            horizontalListView = (RecyclerView) itemView.findViewById(R.id.sf_horizontal_list);
        }
    }

    /*public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if(parent.getChildAdapterPosition(view) != 0)
                outRect.right = space;
        }
    }*/

    //设置RecyclerView item之间的间距
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

        private int spanCount;
        private int space;
        private boolean includeEdge;


        public SpaceItemDecoration(int spanCount, int space, boolean includeEdge) {
            this.spanCount = spanCount;
            this.space = space;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = space - column * space / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * space / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = space;
                }
                outRect.bottom = space; // item bottom
            } else {
                outRect.left = column * space / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = space - (column + 1) * space / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = space; // item top
                }
            }

        }
    }
}