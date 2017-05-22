package com.lyancafe.coffeeshop.produce.presenter;

/**
 * Created by Administrator on 2017/3/15.
 */

public interface AssignOrderPresenter {

    //小哥列表请求
    void loadDeliversForAssign();

    //指派订单请求
    void doAssignOrder(long orderId,long courierId);


}
