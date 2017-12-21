package com.lyancafe.coffeeshop.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/1/22.
 * 设置RecyclerView item之间的间距
 */

public class VSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public VSpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position

        outRect.left = 0;
        outRect.right = 0;
        outRect.top = space;



    }
}
