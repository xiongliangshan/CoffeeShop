package com.xls.http;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/31.
 */
public class Response {
    public static final String TAG = "Response";

    /**
     * 生成完整的Url
     * @param params
     * @param ctx
     * @return
     */
    public static String createPostURLParams(Map<String, Object> params){
        if(params!=null){
            try{
                JSONObject data =  new JSONObject();
                for(String key:params.keySet()){
                    data.put(key,params.get(key));
                }
                String jsonUrl = data.toString();
                return "json = "+jsonUrl;
            }catch (JSONException e){

            }


        }
        return null;
    }

    public static String createGetURLParams(String url,Map<String, Object> params){
        if (params == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(url+"?");
        for (String key : params.keySet()) {
            Object val = params.get(key);
            if(val==null){
                val = "";
            }else{
                try {
                    val = URLEncoder.encode(val.toString(),"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                }
            }
            sb.append(key + "=" + val + "&");

        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
