package com.lyancafe.coffeeshop.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.Window;

import java.util.List;

/**
 * Created by Administrator on 2017/5/19.
 */

public class LoadingDialog extends ProgressDialog {


    public LoadingDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setMessage("请求网络中...");
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) {

    }
}
