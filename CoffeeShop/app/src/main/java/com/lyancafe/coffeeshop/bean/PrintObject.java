package com.lyancafe.coffeeshop.bean;

import com.lyancafe.coffeeshop.utils.LogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/3.
 */

public class PrintObject {
    private String start;
    private List<String> content;
    private String end;

    public PrintObject() {
        this.start = "N"+"\n"+
                    "q640"+"\n"+
                    "Q400,16"+"\n"+
                    "S3"+"\n"+
                    "D8"+"\n";

        this.end = "P1"+"\n";
        this.content = new ArrayList<>();
    }

    public PrintObject(PrintObject printObject){
        this.start = printObject.getStart();
        this.end = printObject.getEnd();
        this.content = printObject.getContent();
    }

    public static List<PrintObject> transformPrintObjects(Map<String,Integer> coffeeMap) {

        List<PrintObject> printObjects = new ArrayList<>();
        if(coffeeMap.size()==0){
            return printObjects;
        }
        Iterator<String> iterator = coffeeMap.keySet().iterator();
        int size = coffeeMap.size()%6==0?coffeeMap.size()/6:coffeeMap.size()/6+1;
        for(int i=0;i<size;i++){
            printObjects.add(new PrintObject());
        }
        int l = 30;
        int t = 30;
        int i = 0;
        while (iterator.hasNext()){
            String name = iterator.next();
            LogUtil.d("xls",name+" x "+coffeeMap.get(name));
            printObjects.get(i/6).getContent().add("A"+l+","+t+",0,230,1,1,N,\""+name+"    x "+coffeeMap.get(name)+"\""+"\n");
            t+=50;
            i++;
            if(i%6==0){
                l = 30;
                t = 30;
            }
        }
        return printObjects;
    }

    public String getPrintContent(){
        StringBuffer sb = new StringBuffer();
        sb.append(start);
        for(String c:content){
            sb.append(c);
        }
        sb.append(end);
        return sb.toString();
    }


    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }
}
