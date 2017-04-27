package com.lyancafe.coffeeshop.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.lyancafe.coffeeshop.CSApplication;

public class ToastUtil {


	public static void show(Context context, int sting_id) {
		show(context, context.getResources().getString(sting_id));
	}

	public static void show(Context context, String str) {
		Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
		//toast.setGravity(Gravity.BOTTOM, 0, 300);
		toast.show();
	}


	/**
	 *异步安全弹出toast
	 */
	public static void showToast(final Context context,final String toast)
	{
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(CSApplication.getInstance(), toast, Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}).start();
	}

	public static void showToast(final Context context,int resId){
		showToast(CSApplication.getInstance(),CSApplication.getInstance().getString(resId));
	}

}
