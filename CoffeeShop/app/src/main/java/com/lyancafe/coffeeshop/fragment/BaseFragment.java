package com.lyancafe.coffeeshop.fragment;


import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {


    protected boolean isVisible;
    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            isVisible = true;
            onVisible();
        }else{
            isVisible = false;
            onInVisible();
        }
    }

    protected void onVisible(){

    }

    protected void onInVisible(){

    }
}