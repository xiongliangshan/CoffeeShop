package com.lyancafe.coffeeshop.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtil {


	public static void show(Context context, int sting_id) {
		show(context, context.getResources().getString(sting_id));
	}

	public static void show(Context context, String str) {
		if(context!=null){
			Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
			toast.show();
		}
	}


	/**
	 *弹出toast
	 *在非UI线程中调用
	 */
	public static void showToast(final Context context,final String toast)
	{
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}).start();
	}

	public static void showToast(final Context context,int resId){
		showToast(context,context.getString(resId));
	}

	/**
	 *弹出toast
	 *在非UI线程中调用
	 */
	public static void showToastOnThread(final Context context,final String toast){
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context,toast,Toast.LENGTH_SHORT).show();
			}
		});
	}

}
