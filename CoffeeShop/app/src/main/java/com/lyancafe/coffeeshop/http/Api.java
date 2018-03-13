package com.lyancafe.coffeeshop.http;

import com.lyancafe.coffeeshop.BuildConfig;

/**
 * Created by Administrator on 2017/3/30.
 */

public class Api {


//    public static final String BASE_URL = "https://api.lyancafe.com/shop/";
//    public static final String BASE_URL = "https://apiqa.lyancafe.cn/shop/";
//    public static  String BASE_URL = "http://192.168.0.61:8080/shop/";

//      public static  String BASE_URL = "http://192.168.3.45:12034/shop/";

      public static  String BASE_URL = BuildConfig.BASE_URL;

      public static void changeBaseUrl(){
            BASE_URL = BuildConfig.Y_BASE_URL;
            RetrofitHttp.reset();
      }


      public static void initBaseUrl(){
            BASE_URL = BuildConfig.BASE_URL;
            RetrofitHttp.reset();
      }


}
