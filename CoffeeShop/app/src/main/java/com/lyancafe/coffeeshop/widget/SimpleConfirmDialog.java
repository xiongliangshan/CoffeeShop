package com.lyancafe.coffeeshop.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lyancafe.coffeeshop.R;


public class SimpleConfirmDialog extends Dialog {

	private Context context;
	private String content;
	private TextView confirm_dialog_content;
	private Button confirm_yes;

	public SimpleConfirmDialog(Context context) {
		super(context);
		this.context = context;
	}

	public SimpleConfirmDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}


	public void setContent(int content) {
		this.content = context.getResources().getString(content);
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_confirm_dialog);

		confirm_dialog_content = (TextView) findViewById(R.id.confirm_dialog_content);
		confirm_dialog_content.setText(content);

		confirm_yes = (Button) findViewById(R.id.confirm_yes);
		confirm_yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		setCanceledOnTouchOutside(false);
	}


}
