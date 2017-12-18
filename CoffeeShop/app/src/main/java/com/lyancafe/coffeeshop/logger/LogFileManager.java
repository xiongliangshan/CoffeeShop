package com.lyancafe.coffeeshop.logger;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.utils.LogUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/12/18.
 */

public class LogFileManager {

    private static final String TAG ="logger";

    private File logDir;
    private File currentFile;

    /**
     * 决定每个日志文件记录的log时间段长度
     */
    private static final long interval = 2*60*1000;

    public LogFileManager() {
        if(logDir==null){
            logDir = new File(getLogDir());
            logDir.mkdirs();
        }
    }

    public String getLogDir(){
        return CSApplication.LOG_DIR;
    }

    /**
     * 判断一个日志目录是否存在
     * @return
     */
    private boolean isLogDirExists(){
        File logDir = new File(CSApplication.LOG_DIR);
        if(logDir!=null && logDir.exists()){
            return true;
        }
        return false;
    }

    /**
     * 创建日志目录
     * @return
     */
    private void createLogDir() {
        LogUtil.d(TAG,"创建日志目录 ");
        logDir = new File(getLogDir());
        logDir.mkdirs();
    }

    /**
     * 创建日志文件
     * @param fileName
     * @return
     */
    public File createLogFile(String fileName){
        LogUtil.d(TAG,"创建日志文件 "+fileName);
        if(logDir==null){
            createLogDir();
        }
        File file = new File(logDir,fileName);
        if(!file.exists()){
            try {
                file.createNewFile();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }


    /**
     * 删除一个日志文件
     * @param fileName
     */
    public void deleteLogFile(String fileName){
        File file = new File(getLogDir(),fileName);
        if(file!=null && file.exists()){
            file.delete();
        }
    }

    public File[] getAllLogs(){
        if(logDir!=null && logDir.exists()){
            return logDir.listFiles();
        }else {
            return new File[]{};
        }
    }


    /**
     * 获取当前需要写入的目标日志文件
     * @return
     */
    private File getCurrentLogFile(){
        File[] allFiles = getAllLogs();
        LogUtil.d(TAG,"当前日志文件总数:"+allFiles.length);
        if(allFiles.length==0){
            LogUtil.d(TAG,"当前没有日志文件");
            String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(System.currentTimeMillis());
            currentFile = createLogFile(fileName+".log");

        }else{
            if(currentFile!=null && currentFile.exists()){
                List<File> files = Arrays.asList(allFiles);
                Collections.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if(o1.isDirectory() && o2.isFile()){
                            return -1;
                        }else if(o1.isFile() && o2.isDirectory()){
                            return 1;
                        }

                        return o1.getName().compareTo(o2.getName());
                    }
                });
                File lastFile = files.get(files.size()-1);
                LogUtil.d(TAG,"lastFile  = "+lastFile.getName());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                try {
                    String lastFileName = lastFile.getName();
                    Date date = simpleDateFormat.parse(lastFileName.substring(0,lastFileName.indexOf(".")));
                    long nowTime = System.currentTimeMillis();
                    if(nowTime-date.getTime()>=interval){
                        String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(nowTime);
                        currentFile = createLogFile(fileName+".log");
                        LogUtil.d(TAG,"大于时间间隔，重新创建文件");
                    }else{
                        currentFile = lastFile;
                        LogUtil.d(TAG,"在间隔时间内，就用最后一个文件");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG,e.getMessage());
                    String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(System.currentTimeMillis());
                    currentFile = createLogFile(fileName+".log");
                }

            }else{
                String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(System.currentTimeMillis());
                currentFile = createLogFile(fileName+".log");
            }
        }

        LogUtil.d(TAG,"获取当前log文件名:"+currentFile.getName());

        return currentFile;
    }



    public synchronized void writeLog(String log){
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(getCurrentLogFile(),true);
            fileWriter.write(log);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(fileWriter!=null){
                    fileWriter.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }
}
