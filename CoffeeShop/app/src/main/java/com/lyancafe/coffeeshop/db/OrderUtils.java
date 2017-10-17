package com.lyancafe.coffeeshop.db;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.ItemContentBeanDao;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.OrderBeanDao;
import com.lyancafe.coffeeshop.utils.LogUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/2/22.
 */
public class OrderUtils {

    private static final String TAG  = "OrderUtils";
    private ThreadPoolExecutor tpl;
    private static OrderUtils orderUtils;
    private OrderBeanDao mOrderDao;
    private ItemContentBeanDao mItemOrderDao;

    private OrderUtils() {
        mOrderDao = CSApplication.getInstance().getDaoSession().getOrderBeanDao();
        mItemOrderDao = CSApplication.getInstance().getDaoSession().getItemContentBeanDao();
        tpl = new ThreadPoolExecutor(1, 3, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
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



    //批量插入新的订单记录
    public  void insertOrderList(final List<OrderBean> list){
        if(list==null || list.size()==0){
            return;
        }
        tpl.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (list){
                    for(int i=0;i<list.size();i++){
                        List<ItemContentBean> items = list.get(i).getItems();
                        for(int j =0;j<items.size();j++){
                            items.get(j).setOrderId(list.get(i).getId());
                        }
                        mItemOrderDao.insertOrReplaceInTx(items);
                    }
                    mOrderDao.insertOrReplaceInTx(list);
                }
            }
        });

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

    //批量更新订单的生产状态
    public void updateBatchOrder(final List<Long> orderIds, final int produceStatus){
        tpl.execute(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<orderIds.size();i++){
                    updateOrder(orderIds.get(i),produceStatus);
                }
            }
        });
    }

    //更新被撤回订单
    public void updateRevokedOrder(long orderId){
        OrderBean orderBean = queryOrder(orderId);
        if(orderBean==null){
            return;
        }
        orderBean.setRevoked(true);
        mOrderDao.update(orderBean);
        LogUtil.i(TAG,"更新订单 "+orderId+" 标记为 被撤回 ");
    }


    //查询所有被撤回的订单
    public List<OrderBean> queryRevokedOrders(){
        QueryBuilder<OrderBean> qb = mOrderDao.queryBuilder();
        qb.where(OrderBeanDao.Properties.Revoked.eq(true));
        List<OrderBean> list = qb.list();
        return  list;
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
        QueryBuilder<OrderBean> qb = mOrderDao.queryBuilder();
        List<OrderBean> list = qb.list();
        LogUtil.d(TAG,"查询所有订单: "+list.size()+" 条记录");
        return  list;
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
        List<OrderBean> list = qb.list();
        return  list;
    }

    //查询待取货订单数量（待取货）
    public long queryFetchCount(){
        QueryBuilder<OrderBean> qb = mOrderDao.queryBuilder();
        qb.where(OrderBeanDao.Properties.Status.in(3020));
        long count = qb.count();
        return  count;
    }


}
