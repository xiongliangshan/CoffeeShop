package com.lyancafe.coffeeshop.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;


public class ConfirmDialog extends Dialog {

	private Context context;
	private String content;
	private TextView confirm_dialog_content;
	private Button confirm_cancel, confirm_yes;
	private int cancal_txt, yes_txt;
	private OnClickYesListener mListener;

	public ConfirmDialog(Context context) {
		super(context);
		this.context = context;
	}

	public ConfirmDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public ConfirmDialog(Context context, int theme,
						 OnClickYesListener mListener) {
		super(context, theme);
		this.context = context;
		this.mListener = mListener;
	}

	public void setContent(int content) {
		this.content = context.getResources().getString(content);
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	

	public void setBtnTxt(int cancal_txt, int yes_txt) {
		this.cancal_txt = cancal_txt;
		this.yes_txt = yes_txt;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_dialog);

		confirm_dialog_content = (TextView) findViewById(R.id.confirm_dialog_content);
		confirm_dialog_content.setText(content);

		confirm_cancel = (Button) findViewById(R.id.confirm_cancel);
		confirm_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		confirm_yes = (Button) findViewById(R.id.confirm_yes);
		confirm_yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onClickYes();
				}
				dismiss();
			}
		});
		if (cancal_txt != 0) {
			confirm_cancel.setText(cancal_txt);
		}
		if (yes_txt != 0) {
			confirm_yes.setText(yes_txt);
		}
		setCanceledOnTouchOutside(false);
	}

	public interface OnClickYesListener {
		public void onClickYes();
	}

}
