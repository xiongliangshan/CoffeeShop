package com.lyancafe.coffeeshop.db;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.ItemContentBeanDao;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.OrderBeanDao;
import com.lyancafe.coffeeshop.constant.OrderAction;
import com.lyancafe.coffeeshop.constant.OrderStatus;
import com.lyancafe.coffeeshop.utils.LogUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2016/2/22.
 */
public class OrderUtils {

    private static final String TAG  = "OrderUtils";
    private static OrderUtils orderUtils;
    private OrderBeanDao mOrderDao;
    private ItemContentBeanDao mItemOrderDao;

    private OrderUtils() {
        mOrderDao = CSApplication.getInstance().getDaoSession().getOrderBeanDao();
        mItemOrderDao = CSApplication.getInstance().getDaoSession().getItemContentBeanDao();
    }

    public static OrderUtils with(){
        if(orderUtils==null){
            orderUtils = new OrderUtils();
        }
        return orderUtils;
    }

    //插入一条订单记录到order表
    public void insertOrder(OrderBean orderBean){
        for(int i =0;i<orderBean.getItems().size();i++){
            orderBean.getItems().get(i).setOrderId(orderBean.getId());
        }
        mItemOrderDao.insertInTx(orderBean.getItems());
        mOrderDao.insertOrReplace(orderBean);
    }

    public void insertOrderForProducing(OrderBean orderBean){
        List<ItemContentBean> items = orderBean.getItems();
        long orderId = orderBean.getId();
        for (int j = 0; j < items.size(); j++) {
            items.get(j).setOrderId(orderId);
        }
        mItemOrderDao.insertOrReplaceInTx(items);
        mOrderDao.insertOrReplaceInTx(orderBean);
    }



    //批量插入新的订单记录
    public  void insertOrderList(final CopyOnWriteArrayList<OrderBean> list){
        if(list==null || list.size()==0){
            return;
        }

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++) {
            List<ItemContentBean> items = list.get(i).getItems();
            long orderId = list.get(i).getId();
            for (int j = 0; j < items.size(); j++) {
                items.get(j).setOrderId(orderId);
            }
            mItemOrderDao.insertOrReplaceInTx(items);
        }
        mOrderDao.insertOrReplaceInTx(list);
        long endTime = System.currentTimeMillis();
        LogUtil.d(TAG,"插入: "+list.size()+" 条记录,所用时间: "+(endTime-startTime)+" ms");


    }

    //通过Id获取某个订单
    public OrderBean getOrderById(long orderId){
        return  queryOrder(orderId);
    }


    //更新某个订单的生产状态
    public  void updateOrder(long orderId,int produceStatus){
        OrderBean orderBean = queryOrder(orderId);
        if(orderBean==null){
            return;
        }
        orderBean.setProduceStatus(produceStatus);
        mOrderDao.update(orderBean);
        LogUtil.i(TAG,"更新订单 "+orderId+" 生产状态到 "+produceStatus);
    }

    public void updateOrderToProducing(OrderBean orderBeanOld, int produceStatus){
        OrderBean orderBean = getOrderById(orderBeanOld.getId());
        if (orderBean == null) {
            System.out.println("11111"+orderBeanOld);
            insertOrderForProducing(orderBeanOld);
            return;
        }
        orderBean.setProduceStatus(produceStatus);
        mOrderDao.update(orderBean);
        LogUtil.i(TAG,"更新订单 "+orderBeanOld.getId()+" 生产状态到 "+produceStatus);
    }

    //批量更新订单的生产状态
    public void updateBatchOrder(final List<Long> orderIds, final int produceStatus) {

        for (int i = 0; i < orderIds.size(); i++) {
            updateOrder(orderIds.get(i), produceStatus);
        }

    }

    //批量更新订单的配送状态
    public void updateStatusBatch(final List<Long> orderIds,final int status){
        for(int i=0;i<orderIds.size();i++){
            updateStatus(orderIds.get(i),status);
        }
    }

    //更新被撤回订单
    public void updateRevokedOrder(long orderId){
        OrderBean orderBean = queryOrder(orderId);
        if(orderBean==null){
            return;
        }
        orderBean.setRevoked(true);
        orderBean.setProduceStatus(OrderStatus.CANCELLED);
        mOrderDao.update(orderBean);
        LogUtil.i(TAG,"更新订单 "+orderId+" 标记为 被撤回 ");
    }

    public void updateUnFindOrder(long orderId){
        OrderBean orderBean = queryOrder(orderId);
        if(orderBean==null){
            return;
        }
        orderBean.setProduceStatus(OrderStatus.CANCELLED);
        mOrderDao.update(orderBean);
        LogUtil.i(TAG,"更新订单 "+orderId+" 标记为 被撤回 ");
    }

    //查询所有被撤回的订单
    public List<OrderBean> queryRevokedOrders(){
        QueryBuilder<OrderBean> qb = mOrderDao.queryBuilder();
        qb.where(OrderBeanDao.Properties.Revoked.eq(true));
        return qb.list();
    }


    //更新订单的配送状态
    public void updateStatus(long orderId,int status){
        OrderBean orderBean = queryOrder(orderId);
        if(orderBean==null){
            return;
        }
        orderBean.setStatus(status);
        mOrderDao.update(orderBean);
        LogUtil.i(TAG,"更新订单 "+orderId+"状态为 "+status);
    }

    //更新问题订单的状态
    public void updateIssueOrder(OrderBean orderBean, boolean isIssueOrder){
        if(orderBean==null){
            return;
        }
        orderBean.setIssueOrder(isIssueOrder);
        mOrderDao.update(orderBean);
    }

    //更新问题订单的状态
    public void updateIssueOrder(int orderId,boolean isIssueOrder){
        OrderBean orderBean = queryOrder(orderId);
        if(orderBean==null){
            return;
        }
        orderBean.setIssueOrder(isIssueOrder);
        mOrderDao.update(orderBean);
    }

    //清空表
    public void clearTable(){
        LogUtil.d(TAG,"清空表数据");
        mOrderDao.deleteAll();
    }


    //删除一条订单记录
    public void deleteOrder(long orderId){
        mOrderDao.deleteByKey(orderId);
    }

    public void deleteOrder(OrderBean orderBean){
        mOrderDao.delete(orderBean);
    }


    //查询单个订单
    public OrderBean queryOrder(long orderId){
        return mOrderDao.load(orderId);
    }

    //查询所有订单
    public List<OrderBean> queryAllOrders(){
        long startTime = System.currentTimeMillis();
        QueryBuilder<OrderBean> qb = mOrderDao.queryBuilder();
        List<OrderBean> list = qb.list();
        long endTime = System.currentTimeMillis();
        LogUtil.d(TAG,"查询所有订单: "+list.size()+" 条记录,所用时间: "+(endTime-startTime)+" ms");
        return  list;
    }

    //按订单生产状态查询订单集合(今日订单)
    public List<OrderBean> queryByProduceStatus(int produceStatus){
        QueryBuilder<OrderBean> qb = mOrderDao.queryBuilder();
        qb.where(OrderBeanDao.Properties.ProduceStatus.eq(produceStatus),OrderBeanDao.Properties.Status.lt(6000)
        ,qb.and(OrderBeanDao.Properties.ExpectedTime.lt(getTomorrowStartTime()),OrderBeanDao.Properties.ExpectedTime.gt(getTodayStartTime())));
        qb.orderAsc(OrderBeanDao.Properties.ExpectedTime);
        return qb.list();
    }


    //获取今日零点的时间戳
    private long getTodayStartTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return calendar.getTimeInMillis();
    }

    //获取明天零点的时间戳
    private long getTomorrowStartTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return calendar.getTimeInMillis();
    }



    //查询已经配送完成的订单
    public List<OrderBean> queryFinishedOrders(){
        QueryBuilder<OrderBean> qb = mOrderDao.queryBuilder();
        qb.where(OrderBeanDao.Properties.Status.ge(6000));
        return qb.list();
    }


    //查询待取货订单
    public List<OrderBean> queryToFetchOrders(){
        QueryBuilder<OrderBean> qb = mOrderDao.queryBuilder();
        qb.where(OrderBeanDao.Properties.Status.in(3020));
        return qb.list();
    }

    //查询待取货订单数量（待取货）
    public long queryFetchCount(){
        QueryBuilder<OrderBean> qb = mOrderDao.queryBuilder();
        qb.where(OrderBeanDao.Properties.Status.in(3020));
        return qb.count();
    }


}
