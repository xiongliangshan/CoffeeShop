package com.xls.http;

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
                return jsonUrl;
            }catch (JSONException e){

            }

           /* StringBuilder sb = new StringBuilder();
            for(String key:params.keySet()){
                sb.append(key);
                sb.append("=");
                sb.append(params.get(key)+"&");
            }
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();*/
        }
        return "";
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


    private static String prepareParam(Map<String,Object> paramMap){
        StringBuffer sb = new StringBuffer();
        if(paramMap.isEmpty()){
            return "" ;
        }else{
            for(String key: paramMap.keySet()){
                String value = (String)paramMap.get(key);
                if(sb.length()<1){
                    sb.append(key).append("=").append(value);
                }else{
                    sb.append("&").append(key).append("=").append(value);
                }
            }
            return sb.toString();
        }
    }
}
