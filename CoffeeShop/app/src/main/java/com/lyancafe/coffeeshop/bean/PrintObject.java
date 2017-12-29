package com.lyancafe.coffeeshop.bean;

import com.lyancafe.coffeeshop.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public static List<PrintObject> transformPrintObjects(Map<String,Product> coffeeMap, Map<String,Map<String,Integer>> recipeFittingsMap) {

        List<PrintObject> printObjects = new ArrayList<>();
        if(coffeeMap.size()==0){
            return printObjects;
        }

        List<Map.Entry<String,Product>> list = new ArrayList<>(coffeeMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Product>>() {
            @Override
            public int compare(Map.Entry<String, Product> o1, Map.Entry<String, Product> o2) {

                return o1.getValue().compareTo(o2.getValue());

            }
        });

        Iterator<Map.Entry<String,Product>> iterator = list.iterator();
        int coffeeSize = coffeeMap.size();
        int fittingSize = getMapSize(recipeFittingsMap);
        int totalSize = coffeeSize+fittingSize;
        int size = totalSize%6==0?totalSize/6:totalSize/6+1;
        for(int i=0;i<size;i++){
            printObjects.add(new PrintObject());
        }
        int l = 30;
        int t = 30;
        int i = 0;
        while (iterator.hasNext()){
            Map.Entry<String,Product> entry = iterator.next();
            String name = entry.getKey();
            int value = entry.getValue().getCount();
            LogUtil.d("xls",name+" x "+value);
            printObjects.get(i/6).getContent().add("A"+l+","+t+",0,230,2,2,N,\""+name+" x "+value+"\""+"\n");
            t+=60;
            i++;
            if(i%6==0){
                l = 30;
                t = 30;
            }

            Map<String ,Integer> fittingsMap = recipeFittingsMap.get(name);
            if(fittingsMap!=null){
                Iterator<String> iterator1 = fittingsMap.keySet().iterator();
                while (iterator1.hasNext()){

                    String fitting = iterator1.next();
                    if("少冰".equals(fitting)){
                        printObjects.get(i/6).getContent().add("A"+(l+150)+","+t+",0,230,1,1,N,\""+fitting+" x "+fittingsMap.get(fitting)+"\""+"\n");
                        t+=60;
                        i++;
                        if(i%6==0){
                            l = 30;
                            t = 30;
                        }
                    }

                }
            }


        }

       /* //构造头和尾
        PrintObject start = new PrintObject();
        start.getContent().add("A0,200,0,230,2,2,N,\"----------生产汇总------------ \""+"\n");
        PrintObject end = new PrintObject();
        end.getContent().add("A0,200,0,230,2,2,N,\"----------生产汇总------------ \""+"\n");
        printObjects.add(0,start);
        printObjects.add(end);*/
        return printObjects;
    }

    public static List<PrintObject> transformPrintObjects(List<Product> list,Map<String,Map<String,Integer>> recipeFittingsMap ){
        List<PrintObject> printObjects = new ArrayList<>();
        if(list.size()==0){
            return printObjects;
        }

        int coffeeSize = list.size();
        int fittingSize = getMapSize(recipeFittingsMap);
        int totalSize = coffeeSize+fittingSize;
        int size = totalSize%6==0?totalSize/6:totalSize/6+1;
        for(int i=0;i<size;i++){
            printObjects.add(new PrintObject());
        }
        int l = 30;
        int t = 30;
        int i = 0;
        for(Product product:list){
            String name = product.getName();
            int count = product.getCount();
            printObjects.get(i/6).getContent().add("A"+l+","+t+",0,230,2,2,N,\""+name+" x "+count+"\""+"\n");
            t+=60;
            i++;
            if(i%6==0){
                l = 30;
                t = 30;
            }

            Map<String ,Integer> fittingsMap = recipeFittingsMap.get(name);
            if(fittingsMap!=null){
                Iterator<String> iterator1 = fittingsMap.keySet().iterator();
                while (iterator1.hasNext()){

                    String fitting = iterator1.next();
                    if("少冰".equals(fitting)){
                        printObjects.get(i/6).getContent().add("A"+(l+150)+","+t+",0,230,1,1,N,\""+fitting+" x "+fittingsMap.get(fitting)+"\""+"\n");
                        t+=60;
                        i++;
                        if(i%6==0){
                            l = 30;
                            t = 30;
                        }
                    }

                }
            }
        }

        return printObjects;
    }

    public static int getMapSize(Map<String,Map<String,Integer>> map){
        int sum = 0;
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            sum+=map.get(key).size();
        }
        return sum;
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
