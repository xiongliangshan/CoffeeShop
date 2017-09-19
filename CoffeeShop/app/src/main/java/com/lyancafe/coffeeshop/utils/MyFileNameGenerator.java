package com.lyancafe.coffeeshop.utils;

import android.net.Uri;

import com.danikula.videocache.ProxyCacheUtils;
import com.danikula.videocache.file.FileNameGenerator;

/**
 * Created by Administrator on 2017/9/19.
 */

public class MyFileNameGenerator implements FileNameGenerator {

    @Override
    public String generate(String url) {
        String name = ProxyCacheUtils.computeMD5(url);
        return name;
    }


}
