package com.xls.http;

import android.content.Context;
import android.util.Log;

import com.lyancafe.coffeeshop.utils.ToastUtil;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2015/8/31.
 */
public class ConnectionParams {

    private static final String TAG = "ConnectionParams";
    public static String exceptionInfo = "";
    private static void setTimeout(HttpURLConnection conn) {
        int x = 10 * 1000;// 超时时间
        conn.setConnectTimeout(x);
        conn.setReadTimeout(x);
    }

    public static Jresp doRequest(HttpEntity httpEntity,Context context) {
        exceptionInfo = "";
        try {
            String txt = "";
            switch (httpEntity.getMethod()){
                case HttpEntity.POST:
                    txt = requestByPost(httpEntity.getUrl(), Response.createPostURLParams(httpEntity.getParams()));
                    break;
                case HttpEntity.GET:
                    txt = requestByGet(Response.createGetURLParams(httpEntity.getUrl(), httpEntity.getParams()));
                    break;
            }

            if (txt == null || txt.length() == 0) {
                Log.e(TAG, "post:text 为空");
                return null;
            }

            JSONObject jsonObject = new JSONObject(txt);
            JSONObject data = jsonObject.optJSONObject("data");
            int response = jsonObject.optInt("status");
            String message = jsonObject.optString("message");
            Jresp jresp = new Jresp(response,message,data);
            return jresp;
        }  catch (Exception e) {
            return null;
        }
    }


    private static String requestByPost(String targetURL, String urlParameters) {
        Log.d(TAG,"post: url = "+targetURL+"\n params = "+urlParameters);
        HttpURLConnection conn = null;
        try {
            URL url = new URL(targetURL);
            conn = (HttpURLConnection) url.openConnection();
            setTimeout(conn);
            conn.setRequestMethod("POST");
            conn.setRequestProperty(
                    "Accept",
                    "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream wr = conn.getOutputStream();
            wr.write(urlParameters.getBytes("utf-8"));
            wr.flush();
            wr.close();
            int code = conn.getResponseCode();
            if (code >= 400) {
                Log.e(TAG, "http_code=" + code);
                exceptionInfo = "服务器出错"+code;
                return null;
            }
            Log.i(TAG, "code=" + code);
            InputStream is = conn.getInputStream();
            String encoding = conn.getContentEncoding();
            Log.d(TAG,"encoding = "+encoding);
            String txt = null;
            if("gzip".equals(encoding)){
                txt = IOUtil.read(new GZIPInputStream(is));
                Log.d(TAG, "post: gzip = true");
            }else{
                txt = IOUtil.read(is);
                Log.d(TAG, "post: gzip = false");
            }
            is.close();
            Log.d(TAG, "post: result = " + txt);
            return txt;
        } catch (SocketTimeoutException e){
            exceptionInfo = "连接服务器超时";
            Log.e(TAG, "post: SocketTimeoutException:"+e.getMessage());
        }  catch (ConnectException e){
            exceptionInfo = "连接服务器失败";
            Log.e(TAG, "post: ConnectException:"+e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "post: Exception:"+e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }


    private static String requestByGet(String targetURL) {
        Log.d(TAG,"get: url = "+targetURL);
        HttpURLConnection conn = null;
        try {
            URL url = new URL(targetURL);
            conn = (HttpURLConnection) url.openConnection();
            setTimeout(conn);
            conn.setRequestMethod("GET");
            conn.setRequestProperty(
                    "Accept",
                    "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Accept-Encoding", "gzip");
            int code = conn.getResponseCode();
            Log.i(TAG, "code=" + code);
            if (code >= 400) {
                Log.e(TAG, "http_code=" + code);
                exceptionInfo = "服务器出错"+code;
                return null;
            }

            InputStream is = conn.getInputStream();
            String encoding = conn.getHeaderField("Content-Encoding");
            boolean gzipped = encoding!=null && encoding.toLowerCase().contains("gzip");
            String txt = null;
            if(gzipped){
                txt = IOUtil.read(new GZIPInputStream(is));
            }else{
                txt = IOUtil.read(is);
            }
            is.close();
            Log.d(TAG, "get: result = " + txt);
            return txt;
        } catch (SocketTimeoutException e){
            exceptionInfo = "连接服务器超时";
            Log.e(TAG, "get: SocketTimeoutException:"+e.getMessage());
        } catch (ConnectException e){
            exceptionInfo = "连接服务器失败";
            Log.e(TAG, "get: ConnectException:"+e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "get: Exception"+e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }


}
