package com.lyancafe.coffeeshop.logger;

import com.lyancafe.coffeeshop.CSApplication;
import com.lyancafe.coffeeshop.utils.LogUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/12/18.
 */

public class LogFileManager {

    private static final String TAG ="logger";

    private File logDir;

    /**
     * 决定每个日志文件最大容量,byte
     */
    private static final long maxLength = 10*1024*1024;

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
        return logDir != null && logDir.exists();
    }

    /**
     * 创建日志目录
     * @return
     */
    private void createLogDir() {
        logDir = new File(getLogDir());
        if(!logDir.exists()){
            logDir.mkdirs();
            LogUtil.d(TAG,"创建日志目录 ");
        }

    }

    /**
     * 创建日志文件
     * @param fileName
     * @return
     */
    public File createLogFile(String fileName){
        createLogDir();
        LogUtil.d(TAG,"创建日志文件 "+fileName);
        File file = new File(getLogDir()+fileName);
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
        if(file.exists()){
            file.delete();
        }
    }


    /**
     * 清空日志目录
     */
    public synchronized void clearAllLogs(){
        LogUtil.d(TAG,"clearAllLogs");
        File file = new File(getLogDir());
        if(file.exists()){
           File[] files = file.listFiles();
           if(files.length==0){
               file.delete();
               LogUtil.d(TAG,"删除log目录");
           }else{
               for(int i=0;i<files.length;i++){
                   files[i].delete();
                   LogUtil.d(TAG,"删除文件 "+i);
               }
           }
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
        File currentFile = null;
        File[] allFiles = getAllLogs();
        LogUtil.d(TAG,"当前日志文件总数:"+allFiles.length);
        if(allFiles.length==0){
            LogUtil.d(TAG,"当前没有日志文件");
            currentFile = createLogFile(generateFileName(System.currentTimeMillis()));

        }else{
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
            long fileLength = lastFile.length();
            LogUtil.d(TAG,"lastFile  = "+lastFile.getName()+" | length = "+fileLength);
            if(fileLength>=maxLength){
                currentFile = createLogFile(generateFileName(System.currentTimeMillis()));
                LogUtil.d(TAG,"文件大于10M，重新创建文件");
            }else {
                currentFile = lastFile;
                LogUtil.d(TAG,"文件大小不足10M，就用最后一个文件");
            }
        }

        LogUtil.d(TAG,"获取当前log文件名:"+currentFile.getName());

        return currentFile;
    }

    /**
     * 创建日志文件名
     * @return
     */
    private String generateFileName(long time){
        return  new SimpleDateFormat("MM-dd-HH-mm").format(time)+".log";
    }



    public synchronized void writeLog(String log){
        String wholeLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())+"\t"+log;
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(getCurrentLogFile(),true);
            fileWriter.write(wholeLog);
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
