package com.lyancafe.coffeeshop.event;

import com.lyancafe.coffeeshop.bean.MaterialBean;

/**
 * Created by Administrator on 2016/4/22.
 */
public class MaterialSelectEvent {

    public int selected;
    public MaterialBean materialBean;

    public MaterialSelectEvent(int selected, MaterialBean materialBean) {
        this.selected = selected;
        this.materialBean = materialBean;
    }
}
