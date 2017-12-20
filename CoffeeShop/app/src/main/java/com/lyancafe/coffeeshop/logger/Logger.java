package com.lyancafe.coffeeshop.logger;

import com.lyancafe.coffeeshop.utils.LogUtil;

/**
 * Created by Administrator on 2017/12/18.
 */

public class Logger {

    private LogFileManager flm;

    private Logger() {}

    public static Logger getLogger(){
        return Holder.logger;
    }

    static class Holder{
        private final static Logger logger = new Logger();
    }


    public void log(String log){
        if(flm==null){
            flm = new LogFileManager();
        }
        flm.writeLog(log+"\n");
    }


    public void clearAllLogs(){
        LogUtil.d("logger","logger clearAllLogs");
        if(flm==null){
            flm = new LogFileManager();
        }
        flm.clearAllLogs();
    }
}
