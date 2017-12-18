package com.lyancafe.coffeeshop.logger;

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
}
