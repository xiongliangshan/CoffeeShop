package com.lyancafe.coffeeshop.shop.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.ExceptionalOrder;
import com.lyancafe.coffeeshop.bean.UserBean;
import com.lyancafe.coffeeshop.common.LoginHelper;
import com.lyancafe.coffeeshop.http.CustomObserver;
import com.lyancafe.coffeeshop.shop.model.ExceptionalModel;
import com.lyancafe.coffeeshop.shop.model.ExceptionalModelImpl;
import com.lyancafe.coffeeshop.shop.view.ExceptionalView;

import java.util.List;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ExceptionalPresenterImpl implements ExceptionalPresenter {

    private ExceptionalModel mExceptionalModel;
    private ExceptionalView mExceptionalView;
    private Context mContext;

    public ExceptionalPresenterImpl(Context context,ExceptionalView mExceptionalView) {
        this.mContext = context;
        this.mExceptionalView = mExceptionalView;
        mExceptionalModel = new ExceptionalModelImpl();
    }


    @Override
    public void loadExceptionalOrders() {
        UserBean user = LoginHelper.getUser(CSApplication.getInstance());
        mExceptionalModel.loadExceptionalOrdes(user.getShopId(), user.getToken(), new CustomObserver<List<ExceptionalOrder>>(mContext) {
            @Override
            protected void onHandleSuccess(List<ExceptionalOrder> exceptionalOrders) {
                mExceptionalView.bindDataToView(exceptionalOrders);
            }
        });
    }


    @Override
    public void doRePush(final long orderId, int deliverTeam) {
        UserBean user = LoginHelper.getUser(CSApplication.getInstance());
        mExceptionalModel.doRePush(user.getShopId(), orderId, deliverTeam, user.getToken(), new CustomObserver<JsonObject>(mContext,true) {
            @Override
            protected void onHandleSuccess(JsonObject jsonObject) {
                mExceptionalView.showToast("操作成功");
                mExceptionalView.removeItemFromList(orderId);
            }
        });
    }
}
