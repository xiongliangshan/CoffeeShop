package com.lyancafe.coffeeshop.shop.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.base.BaseFragment;
import com.lyancafe.coffeeshop.bean.ShopInfo;
import com.lyancafe.coffeeshop.printer.PrintFace;
import com.lyancafe.coffeeshop.printer.PrintSetting;
import com.lyancafe.coffeeshop.shop.presenter.ManagerPresenter;
import com.lyancafe.coffeeshop.shop.presenter.ManagerPresenterImpl;
import com.lyancafe.coffeeshop.shop.view.ManagerView;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.lyancafe.coffeeshop.widget.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ManagerFragment extends BaseFragment implements ManagerView {

    @BindView(R.id.tv_shopName)
    TextView tvShopName;
    @BindView(R.id.tv_shop_address)
    TextView tvShopAddress;
    @BindView(R.id.tv_constant_shopPhone)
    TextView tvConstantShopPhone;
    @BindView(R.id.tv_shopPhone)
    TextView tvShopPhone;
    @BindView(R.id.iv_edit)
    ImageView ivEdit;
    @BindView(R.id.et_phone)
    TextInputEditText etPhone;
    @BindView(R.id.iv_save)
    ImageView ivSave;
    @BindView(R.id.ll_modify_container)
    LinearLayout llModifyContainer;
    Unbinder unbinder;
    @BindView(R.id.tv_select_printer)
    TextView tvSelectPrinter;
    @BindView(R.id.tv_small_printer)
    TextView tvSmallPrinter;
    @BindView(R.id.rb_winpos_small)
    RadioButton rbWinposSmall;
    @BindView(R.id.rb_fujitsu_small)
    RadioButton rbFujitsuSmall;
    @BindView(R.id.radio_group_small)
    RadioGroup radioGroupSmall;
    @BindView(R.id.tv_big_printer)
    TextView tvBigPrinter;
    @BindView(R.id.rb_winpos_big)
    RadioButton rbWinposBig;
    @BindView(R.id.rb_fujitsu_big)
    RadioButton rbFujitsuBig;
    @BindView(R.id.radio_group_big)
    RadioGroup radioGroupBig;
    private LoadingDialog loadingDialog;

    private ManagerPresenter mManagerPresenter;

    public ManagerFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManagerPresenter = new ManagerPresenterImpl(this, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager, container, false);
        unbinder = ButterKnife.bind(this, view);
        initRadioButton();
        return view;
    }


    private void initRadioButton() {
        int bigPrinter = PrintSetting.getBigPrinter(getContext());
        int smallPrinter = PrintSetting.getSmallPrinter(getContext());
        if(bigPrinter==PrintSetting.FUJITSU){
            radioGroupBig.check(R.id.rb_fujitsu_big);
        }else {
            radioGroupBig.check(R.id.rb_winpos_big);
        }
        if(smallPrinter == PrintSetting.FUJITSU){
            radioGroupSmall.check(R.id.rb_fujitsu_small);
        }else{
            radioGroupSmall.check(R.id.rb_winpos_small);
        }
    }

    @Override
    public void showToast(String message) {
        ToastUtil.show(getContext(), message);
    }

    @Override
    public void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getContext());
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    @Override
    public void dismissLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.hide();
        }
    }

    @Override
    public void showEdit() {
        if (llModifyContainer != null && llModifyContainer.getVisibility() != View.VISIBLE) {
            llModifyContainer.setVisibility(View.VISIBLE);
            etPhone.setText("");
            etPhone.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }


    }

    @Override
    public void hideEdit() {
        if (llModifyContainer != null && llModifyContainer.getVisibility() == View.VISIBLE) {
            llModifyContainer.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etPhone.getWindowToken(), 0);
        }
    }

    @Override
    public void setTelephone(String phone) {
        if (tvShopPhone != null) {
            tvShopPhone.setText(phone);
        }
    }

    @Override
    public void bindShopInfoDataToView(ShopInfo shopInfo) {
        if (shopInfo == null) {
            return;
        }
        tvShopName.setText(shopInfo.getShopName());
        tvShopAddress.setText(shopInfo.getShopAddress());
        tvShopPhone.setText(shopInfo.getShopTelephone());
    }

    @Override
    public void onVisible() {
        super.onVisible();
        if (!isResumed()) {
            return;
        }

        mManagerPresenter.loadShopInfo();
    }


    @Override
    public void onInVisible() {
        super.onInVisible();
        hideEdit();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        dismissLoading();
    }

    @OnClick({R.id.iv_edit, R.id.iv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_edit:
                showEdit();
                break;
            case R.id.iv_save:
                String newPhone = etPhone.getText().toString().trim();
                if (TextUtils.isEmpty(newPhone)) {
                    ToastUtil.show(getContext(), "请输入新的电话号码");
                    return;
                }
                mManagerPresenter.modifyShopTelephone(newPhone);
                break;
        }
    }

    @OnClick({R.id.rb_winpos_small, R.id.rb_fujitsu_small, R.id.rb_winpos_big, R.id.rb_fujitsu_big})
    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.rb_winpos_small:
                PrintSetting.saveSmallPrinter(getContext(),PrintSetting.WINPOS);
                break;
            case R.id.rb_fujitsu_small:
                PrintSetting.saveSmallPrinter(getContext(),PrintSetting.FUJITSU);
                break;
            case R.id.rb_winpos_big:
                PrintSetting.saveBigPrinter(getContext(),PrintSetting.WINPOS);
                break;
            case R.id.rb_fujitsu_big:
                PrintSetting.saveBigPrinter(getContext(),PrintSetting.FUJITSU);
                break;
        }
        PrintFace.reset();
    }
}
