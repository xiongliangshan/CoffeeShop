package com.lyancafe.coffeeshop.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.XlsResponse;
import com.lyancafe.coffeeshop.callback.JsonCallback;
import com.lyancafe.coffeeshop.helper.HttpHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/2/18.
 */

public class ReportIssueDialog extends DialogFragment implements View.OnClickListener {


    public static final String TAG = ReportIssueDialog.class.getName();
    private long mOrderId;
    private Button changeShopBtn;
    private Button changeAddressBtn;
    private Button changeDateBtn;
    private Button changeTimeBtn;
    private EditText contentEdit;
    private Button commitBtn;


    public static ReportIssueDialog newInstance(long orderId) {

        Bundle args = new Bundle();
        args.putLong("orderId",orderId);
        ReportIssueDialog fragment = new ReportIssueDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        Bundle bundle = getArguments();
        if(bundle!=null){
            mOrderId = bundle.getLong("orderId");
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.dialog_report_issue,container);
        initView(view);
        return view;
    }

    private void initView(View view) {
        changeShopBtn = (Button) view.findViewById(R.id.btn_change_shop);
        changeAddressBtn = (Button) view.findViewById(R.id.btn_change_address);
        changeDateBtn = (Button) view.findViewById(R.id.btn_change_date);
        changeTimeBtn = (Button) view.findViewById(R.id.btn_change_time);
        contentEdit = (EditText) view.findViewById(R.id.et_issue_content);
        commitBtn = (Button) view.findViewById(R.id.btn_commit_issue);

        changeShopBtn.setOnClickListener(this);
        changeAddressBtn.setOnClickListener(this);
        changeDateBtn.setOnClickListener(this);
        changeTimeBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.45), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_change_shop:
                if(!contentEdit.getText().toString().contains(getText(R.string.change_shop))){
                    contentEdit.getText().append(getText(R.string.change_shop)).append(", ");
                    contentEdit.setSelection(contentEdit.getText().length());
                }

                break;
            case R.id.btn_change_address:
                if(!contentEdit.getText().toString().contains(getText(R.string.change_address))){
                    contentEdit.getText().append(getText(R.string.change_address)).append(", ");
                    contentEdit.setSelection(contentEdit.getText().length());
                }
                break;
            case R.id.btn_change_date:
                if(!contentEdit.getText().toString().contains(getText(R.string.change_date))){
                    contentEdit.getText().append(getText(R.string.change_date)).append(", ");
                    contentEdit.setSelection(contentEdit.getText().length());
                }

                break;
            case R.id.btn_change_time:
                if(!contentEdit.getText().toString().contains(getText(R.string.change_time))){
                    contentEdit.getText().append(getText(R.string.change_time)).append(", ");
                    contentEdit.setSelection(contentEdit.getText().length());
                }
                break;
            case R.id.btn_commit_issue:
                String content = contentEdit.getText().toString();
                if(TextUtils.isEmpty(content)){
                    ToastUtil.show(getContext(),"请输入问题说明");
                    return;
                }
                HttpHelper.getInstance().reqReportIssueOrder(mOrderId, 0, 0, contentEdit.getText().toString(), new JsonCallback<XlsResponse>() {
                    @Override
                    public void onSuccess(XlsResponse xlsResponse, Call call, Response response) {
                        if(xlsResponse.status==0){
                            dismiss();
                        }else{
                            ToastUtil.show(getContext(),xlsResponse.message);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach");
    }
}
