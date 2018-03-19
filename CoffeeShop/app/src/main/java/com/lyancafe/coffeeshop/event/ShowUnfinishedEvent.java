package com.lyancafe.coffeeshop.event;

/**
 * @author yangjz 2018/3/18.
 */

public class ShowUnfinishedEvent {

    public String title;
    public boolean isShow;

    public ShowUnfinishedEvent(String title, boolean isShow){
        this.title = title;
        this.isShow = isShow;
    }
}
