package com.lyancafe.coffeeshop.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lyancafe.coffeeshop.CSApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/9/19.
 */

public class BitmapCache {

    private static final String TAG ="BitmapCache";

    private static BitmapCache bitmapCache;

    private BitmapCache() {
    }

    public static BitmapCache getInst(){
        if(bitmapCache==null){
            bitmapCache = new BitmapCache();
        }
        return bitmapCache;
    }

    public String addBitmapToCache(String key,Bitmap bitmap){
        File file = new File(CSApplication.CACHE_DIR+File.separator+key);

        if(!file.exists()){
            file.getParentFile().mkdirs();
            LogUtil.d(TAG,"mkdirs");
            try {
                file.createNewFile();
                LogUtil.d(TAG,"createNewFile success");
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                fos.flush();
                fos.close();
                LogUtil.d(TAG,"addBitmapToCache success");
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.e(TAG,"发生异常:"+e.getMessage());
            }
        }
        return file.getAbsolutePath();

    }

    public Bitmap getBitmapFromCache(String key){
        File directory = new File(CSApplication.CACHE_DIR);
        if(directory.exists() && directory.isDirectory()){
            File[] files = directory.listFiles();
            for(File file:files){
                if(key.equals(file.getName())){
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    LogUtil.d(TAG,"getBitmapFromCache success");
                    return bitmap;
                }
            }
        }
        LogUtil.e(TAG,"getBitmapFromCache failed");
        return null;
    }

}
