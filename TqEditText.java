package com.TQFramework;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class TqEditText extends EditText {
	
	private C3GLSurfaceView mView;

	public TqEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public TqEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public TqEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setMainView(C3GLSurfaceView glSurfaceView) {
		mView = glSurfaceView;
	}
	
	/*
	 * Let GlSurfaceView get focus if back key is input
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mView.requestFocus();
		}
		
		return true;
	}
}
