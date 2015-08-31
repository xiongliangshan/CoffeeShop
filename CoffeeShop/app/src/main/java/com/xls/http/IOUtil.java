package com.xls.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2015/8/31.
 */
public class IOUtil {

    private static final int BUFFER_SIZE = 1024;

    public static String read(InputStream is) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        char[] cbuf = new char[BUFFER_SIZE];
        int count = -1;
        while ((count = rd.read(cbuf)) >= 0) {
            sb.append(cbuf, 0, count);
        }
        rd.close();
        return sb.toString();
    }

    public static String read(GZIPInputStream zipins) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            int length = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((length = zipins.read(buffer, 0, BUFFER_SIZE)) != -1) {
                baos.write(buffer, 0, length);
            }
            String str = baos.toString("utf-8");
            baos.close();
            zipins.close();
            Log.i("liang", "str=" + str);
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (zipins != null) {
                try {
                    zipins.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;

    }
}
