package com.lyancafe.coffeeshop.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.helper.LoginHelper;
import com.xls.http.HttpAsyncTask;
import com.xls.http.HttpEntity;
import com.xls.http.HttpUtils;
import com.xls.http.Jresp;
import com.xls.http.Qry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/5.
 */
public class InfoDetailDialog extends Dialog {

    private static final String TAG = "InfoDetailDialog";
    private TextView infoDetailText;
    private ContentLoadingProgressBar progressBar;
    private static InfoDetailDialog mDialog = null;
    private Context mContext;

    public InfoDetailDialog(Context context) {
        super(context);
    }

    public InfoDetailDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public InfoDetailDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static InfoDetailDialog getInstance(Context context){
        if(mDialog==null){
            mDialog = new InfoDetailDialog(context,R.style.MyDialog);
        }

        return mDialog;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate:");
        getWindow().setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        setContentView(R.layout.dialog_info_detail);
        infoDetailText = (TextView) findViewById(R.id.tv_info_detail);
        progressBar = (ContentLoadingProgressBar) findViewById(R.id.load_comments_progress);
    }

    public void show(String content){
        Log.d(TAG, "show:" + content);
        if(TextUtils.isEmpty(content)||"\n".equals(content)){
            return;
        }
        mDialog.show();
        if(infoDetailText!=null){
            infoDetailText.setText(content);
        }
    }

    public void show(long orderId){
        mDialog.show();
        new RequestOrderCommentsQry(mContext,orderId).doRequest();
    }


    class RequestOrderCommentsQry implements Qry{

        private Context context;
        private long orderId;

        public RequestOrderCommentsQry(Context context, long orderId) {
            this.context = context;
            this.orderId = orderId;
        }

        @Override
        public void doRequest() {
            if(progressBar!=null){
                progressBar.setVisibility(View.VISIBLE);
            }
            String token = LoginHelper.getToken(context);
            String url = HttpUtils.BASE_URL+"/order/"+orderId+"/checkComments?token="+token;
            Map<String,Object> params = new HashMap<String,Object>();
            HttpAsyncTask.request(new HttpEntity(HttpEntity.POST, url, params), context, this, false);
        }

        @Override
        public void showResult(Jresp resp) {
            if(progressBar!=null){
                progressBar.setVisibility(View.GONE);
            }
            if(resp==null){
                Log.e(TAG,"RequestOrderCommentsQry:resp = "+resp);
            }
            Log.d(TAG,"RequestOrderCommentsQry:resp = "+resp);
        }
    }
}
