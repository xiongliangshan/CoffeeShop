package com.lyancafe.coffeeshop.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2017/1/22.
 * 设置RecyclerView item之间的间距
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

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
