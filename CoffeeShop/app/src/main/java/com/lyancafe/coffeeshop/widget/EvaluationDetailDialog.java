package com.lyancafe.coffeeshop.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/2/27.
 */

public class EvaluationDetailDialog extends DialogFragment {


    @BindView(R.id.tv_evalutaion_content)
    TextView tvEvalutaionContent;

    private int orderId;
    private String content;

    private Unbinder unbinder;

    public static EvaluationDetailDialog newInstance(int orderId,String content) {

        Bundle args = new Bundle();
        args.putInt("orderId",orderId);
        args.putString("content",content);
        EvaluationDetailDialog fragment = new EvaluationDetailDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            orderId = bundle.getInt("orderId");
            content = bundle.getString("content");
        }
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_evaluation_detail, container);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        tvEvalutaionContent.setText(content);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.4), (int)(dm.heightPixels * 0.4));
            dialog.setTitle(String.valueOf(orderId));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
