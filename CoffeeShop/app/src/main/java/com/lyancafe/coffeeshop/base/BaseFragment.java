package com.lyancafe.coffeeshop.base;


import android.support.v4.app.Fragment;

import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.LoadingDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment implements BaseView{


    private LoadingDialog mLoadingDlg;
    protected boolean isVisible;
    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            isVisible = true;
            onVisible();
        }else{
            isVisible = false;
            onInVisible();
        }
    }

    public void onVisible(){

    }

    public void onInVisible(){

    }

    @Override
    public void showToast(String promptStr) {
        ToastUtil.show(getContext(),promptStr);
    }

    @Override
    public void showLoading() {
        if (mLoadingDlg == null) {
            mLoadingDlg = new LoadingDialog(getContext());
        }
        if (!mLoadingDlg.isShowing()) {
            mLoadingDlg.show();
        }
    }

    @Override
    public void dismissLoading() {
        if (mLoadingDlg != null && mLoadingDlg.isShowing()) {
            mLoadingDlg.dismiss();
        }
    }
}
