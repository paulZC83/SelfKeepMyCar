package cn.sh.changxing.selfkeepmycar.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import cn.sh.changxing.selfkeepmycar.R;


public class ConfirmCommDialog extends Dialog {
	private Button mCancel, mConfirm;
	private TextView mTitle;
	private View mDivider;

	public ConfirmCommDialog(Context context) {
		super(context, R.style.Translucent_NoTitle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_confirm_comm);
		initView();
	}

	private void initView() {
		mCancel = (Button) findViewById(R.id.dialog_confirm_cancel);
		mConfirm = (Button) findViewById(R.id.dialog_confirm_confirm);
		mTitle = ((TextView) findViewById(R.id.dialog_confirm_title));
		mDivider = ((View) findViewById(R.id.dialog_divider));
	}

	public void setOneButton() {
		mDivider.setVisibility(View.GONE);
		mCancel.setVisibility(View.GONE);
	}

	public void setContentText(String str) {
		mTitle.setText(str);
	}

	public void setConfirmText(String str) {
		mConfirm.setText(str);
	}

	public void setCancelText(String str) {
		mCancel.setText(str);
	}

	public void setOnCancelClickListener(
			android.view.View.OnClickListener listener) {
		mCancel.setOnClickListener(listener);
	}

	public void setOnConfirmClickListener(
			android.view.View.OnClickListener listener) {
		mConfirm.setOnClickListener(listener);
	}

}
