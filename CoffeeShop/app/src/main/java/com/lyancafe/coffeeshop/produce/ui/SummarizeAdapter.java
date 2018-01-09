package com.lyancafe.coffeeshop.produce.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.DeliverPlatform;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.bean.Product;
import com.lyancafe.coffeeshop.bean.SummarizeGroup;
import com.lyancafe.coffeeshop.common.OrderHelper;
import com.lyancafe.coffeeshop.event.StartProduceBatchEvent;
import com.lyancafe.coffeeshop.logger.Logger;
import com.lyancafe.coffeeshop.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/12/20.
 */

public class SummarizeAdapter extends RecyclerView.Adapter<SummarizeAdapter.ViewHolder> {


    private static final String TAG = "SummarizeAdapter";



    private Context mContext;
    private List<SummarizeGroup> groupList;

    public SummarizeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.summarize_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SummarizeGroup group = groupList.get(position);
        holder.tvExpected.setText(group.getGroupName());
        if ("合计".equals(group.getGroupName())) {
            holder.btnProduce.setVisibility(View.GONE);
        }
        LogUtil.d(TAG, group.getDeliverPlatformMap().toString());
        createPlatformUI(holder.llPlatformContent,holder.tvPlatformTotal,group);
        createHedanUI(holder.llHedan, group.getBoxOrderMap());
        createIconUI(holder.llIcon, group.getIconMap());
        createBeiheUI(holder.llBeiheContent,holder.tvBeiheTotal,group);
        createCoffeeUI(holder.llCoffee, group.getCoffee());
        createDrinkUI(holder.llDrink, group.getDrink());

        holder.btnProduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<OrderBean> orders = group.getOrders();
                EventBus.getDefault().post(new StartProduceBatchEvent(orders));
                Logger.getLogger().log("点击 汇总生产 ， 总数: "+orders.size()+"订单集合:"+OrderHelper.getOrderIds(orders));
            }
        });
    }


    @Override
    public int getItemCount() {
        return groupList == null ? 0 : groupList.size();
    }

    @OnClick({R.id.ll_hedan, R.id.ll_icon})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_hedan:
                break;
            case R.id.ll_icon:
                break;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_expected)
        TextView tvExpected;
        @BindView(R.id.ll_platform)
        LinearLayout llPlatform;
        @BindView(R.id.ll_hedan)
        LinearLayout llHedan;
        @BindView(R.id.ll_icon)
        LinearLayout llIcon;
        @BindView(R.id.ll_beihe)
        LinearLayout llBeihe;
        @BindView(R.id.ll_coffee)
        LinearLayout llCoffee;
        @BindView(R.id.ll_drink)
        LinearLayout llDrink;
        @BindView(R.id.btn_produce)
        TextView btnProduce;
        @BindView(R.id.ll_platform_content)
        LinearLayout llPlatformContent;
        @BindView(R.id.tv_platform_total)
        TextView tvPlatformTotal;
        @BindView(R.id.ll_beihe_content)
        LinearLayout llBeiheContent;
        @BindView(R.id.tv_beihe_total)
        TextView tvBeiheTotal;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public void setData(List<SummarizeGroup> groupList) {
        this.groupList = groupList;
        for (SummarizeGroup group : this.groupList) {
            LogUtil.d(TAG, group.toString());
        }
        notifyDataSetChanged();
    }

    /**
     * 展示平台订单数据
     *
     * @param container
     * @param group
     */
    private void createPlatformUI(LinearLayout container,TextView total, final SummarizeGroup group) {
        container.removeAllViews();
        Map<String, DeliverPlatform> dpMap = group.getDeliverPlatformMap();
        Iterator<String> iterator = dpMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            DeliverPlatform dp = dpMap.get(key);
            if (dp.getOrderCount() > 0) {
                TextView textView = new TextView(mContext);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextAppearance(R.style.SummarizeText);
                }else{
                    textView.setTextAppearance(mContext,R.style.SummarizeText);
                }
                textView.setText(dp.getName() + " : " + dp.getCupCount() + " 杯, " + dp.getOrderCount() + " 单");
                container.addView(textView);
            }
        }

        total.setText("合计: " + group.getCupsCount() + " 杯, " + group.getOrderCount() + " 单");

        container.invalidate();
    }

    /**
     * 盒单UI
     *
     * @param llHedan
     * @param boxOrderMap
     */
    private void createHedanUI(LinearLayout llHedan, Map<String, Integer> boxOrderMap) {
        llHedan.removeAllViews();
        Iterator<String> it = boxOrderMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String name = key.replace("hedan", "盒单");
            TextView textView = new TextView(mContext);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextAppearance(R.style.SummarizeText);
            }else{
                textView.setTextAppearance(mContext,R.style.SummarizeText);
            }
            textView.setText(name + " : " + boxOrderMap.get(key));
            llHedan.addView(textView);
        }
        llHedan.invalidate();
    }


    /**
     * 图标UI
     *
     * @param llIcon
     * @param iconMap
     */
    private void createIconUI(LinearLayout llIcon, Map<String, Integer> iconMap) {
        llIcon.removeAllViews();
        Iterator<String> it = iconMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            TextView tv = new TextView(mContext);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAppearance(R.style.SummarizeText);
            }else{
                tv.setTextAppearance(mContext,R.style.SummarizeText);
            }
            Drawable drawable = null;
            switch (key){
                case "ji":
                    drawable = ContextCompat.getDrawable(mContext,R.mipmap.flag_reminder);
                    break;
                case "yan":
                    drawable = ContextCompat.getDrawable(mContext,R.mipmap.eye);
                    break;
                case "bei":
                    drawable = ContextCompat.getDrawable(mContext,R.mipmap.flag_bei);
                    break;
                case "bu":
                    drawable = ContextCompat.getDrawable(mContext,R.mipmap.flag_replenish);
                    break;
            }
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            tv.setCompoundDrawables(drawable,null,null,null);

            tv.setText(" : " + iconMap.get(key));
            llIcon.addView(tv);
        }
        llIcon.invalidate();

    }

    /**
     * 杯盒信息
     *
     * @param llBeihe
     * @param group
     */
    private void createBeiheUI(LinearLayout llBeihe,TextView total, SummarizeGroup group) {
        llBeihe.removeAllViews();
        Map<String, Integer> cupBoxMap = group.getCupBoxMap();
        Iterator<String> it = cupBoxMap.keySet().iterator();

        while (it.hasNext()) {
            String key = it.next();
            String name = transformName(key);
            int value = cupBoxMap.get(key);
            if (value > 0) {
                TextView tv = new TextView(mContext);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv.setTextAppearance(R.style.SummarizeText);
                }else{
                    tv.setTextAppearance(mContext,R.style.SummarizeText);
                }
                tv.setText(name + " : " + value);
                llBeihe.addView(tv);
            }

        }

        total.setText("合计: " + group.getBoxCount() + " 盒");

        llBeihe.invalidate();
    }

    private String transformName(String key) {
        String name = null;
        switch (key) {
            case "1beihe":
                name = "单杯盒";
                break;
            case "2beihe":
                name = "双杯盒";
                break;
            case "4beihe":
                name = "四杯盒";
                break;
        }
        return name;
    }

    /**
     * 咖啡师出品
     *
     * @param llCoffee
     * @param coffee
     */
    private void createCoffeeUI(LinearLayout llCoffee, Map<String, Product> coffee) {
        llCoffee.removeAllViews();
        List<Map.Entry<String,Product>> list = new ArrayList<>(coffee.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Product>>() {
            @Override
            public int compare(Map.Entry<String, Product> o1, Map.Entry<String, Product> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        for(Map.Entry<String,Product> entry:list){
            Product product = entry.getValue();
            TextView tv = new TextView(mContext);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAppearance(R.style.SummarizeText);
            }else {
                tv.setTextAppearance(mContext,R.style.SummarizeText);
            }
            tv.setText(product.getName() + " * " + product.getCount());

            if (product.isCustom()) {
                Drawable drawable = ContextCompat.getDrawable(CSApplication.getInstance(),R.mipmap.flag_ding);
                drawable.setBounds(0,0, OrderHelper.dip2Px(12,mContext),OrderHelper.dip2Px(12,mContext));
                tv.setCompoundDrawablePadding(OrderHelper.dip2Px(4,mContext));
                tv.setCompoundDrawables(null, null,drawable,null);
            }

            llCoffee.addView(tv);
        }

        llCoffee.invalidate();
    }

    /**
     * 饮品师出品
     *
     * @param llDrink
     * @param drink
     */
    private void createDrinkUI(LinearLayout llDrink, Map<String, Product> drink) {
        llDrink.removeAllViews();
        List<Map.Entry<String,Product>> list = new ArrayList<>(drink.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Product>>() {
            @Override
            public int compare(Map.Entry<String, Product> o1, Map.Entry<String, Product> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        for(Map.Entry<String,Product> entry:list){
            Product product = entry.getValue();
            TextView tv = new TextView(mContext);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAppearance(R.style.SummarizeText);
            }else{
                tv.setTextAppearance(mContext,R.style.SummarizeText);
            }
            if (product.isCustom()) {
                tv.setText(product.getName() + " * " + product.getCount() + " 定");
            } else {
                tv.setText(product.getName() + " * " + product.getCount());
            }

            llDrink.addView(tv);
        }
        llDrink.invalidate();
    }


}
