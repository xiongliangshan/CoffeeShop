package com.lyancafe.coffeeshop.produce.presenter;

import com.lyancafe.coffeeshop.bean.XlsResponse;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/3/15.
 */

public interface AssignOrderPresenter {

    //小哥列表请求
    void loadDelivers();

    //指派订单请求
    void assignOrder(long orderId,long deliverId);

}
