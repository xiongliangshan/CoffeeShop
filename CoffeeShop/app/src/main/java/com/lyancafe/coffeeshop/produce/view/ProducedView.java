package com.lyancafe.coffeeshop.produce.view;

import com.lyancafe.coffeeshop.base.BaseView;
import com.lyancafe.coffeeshop.bean.OrderBean;

/**
* Created by Administrator on 2017/03/15
*/

public interface ProducedView extends BaseView<OrderBean>{

    //从列表中删除某个item
    void removeItemFromList(int id);

}