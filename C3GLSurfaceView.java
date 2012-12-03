package com.TQFramework;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.TQFramework.C3SurfaceView;


class TextInputWraper implements TextWatcher, OnEditorActionListener {
	
	private static final Boolean debug = false;
	private static boolean gInputEditIsMultiLine =true;
	private static int gInputMaxLen = 0;
	   
	private void LogD(String msg) {
		if (debug) Log.d("TextInputFilter", msg);
	}
	
	private C3GLSurfaceView mMainView;
	private String mText;
	private String mOriginText;
	
	public static void SetInputEditEnableMultiLine(boolean bIsMultiline)
	{
		gInputEditIsMultiLine = bIsMultiline;	
	}
	public static void SetInputEditMaxLen(int nMaxLen)
	{
		gInputMaxLen = nMaxLen;
	}
	
	private Boolean isFullScreenEdit() {
		InputMethodManager imm = (InputMethodManager)mMainView.getTextField().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.isFullscreenMode();
	}

	public TextInputWraper(C3GLSurfaceView view) {
		mMainView = view;
	}
	
	public void setOriginText(String text) {
		mOriginText = text;
	}
	
	public void afterTextChanged(Editable s) {
				
		if(gInputMaxLen >0 && s.toString().length()>gInputMaxLen)
		{
			String strTip =String.format("超出最大字符限制:%d",gInputMaxLen);
			Toast.makeText(mMainView.MainContext,strTip,Toast.LENGTH_SHORT).show();
			//Toast.makeText(this,strTip,Toast.LENGTH_SHORT).show();
            s.delete(mText.length(),s.toString().length());
		}
		if (isFullScreenEdit()) {
			return;
		}
		LogD("afterTextChanged: " + s);
		mMainView.insertText(s.toString());
		return;
		/*int nModified = s.length() - mText.length();
		if (nModified > 0) {
			final String insertText = s.subSequence(mText.length(), s.length()).toString();
			mMainView.insertText(insertText);
			LogD("insertText(" + insertText + ")");
		}
		else {
			for (; nModified < 0; ++nModified) {
				mMainView.deleteBackward();
				LogD("deleteBackward");
			}
		}*/
		//mText = s.toString();
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		LogD("beforeTextChanged(" + s + ")start: " + start + ",count: " + count + ",after: " + after);
		mText = s.toString();
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			
		if (mMainView.getTextField() == v && isFullScreenEdit()) {
			// user press the action button, delete all old text and insert new text
			for (int i = mOriginText.length(); i > 0; --i) {
				mMainView.deleteBackward();
				LogD("deleteBackward");
			}
			String text = v.getText().toString();
				
			/*
			 * If user input nothing, translate "\n" to engine.
			 */
			//if (text.compareTo("") == 0){
			//	text = "\n";
			//}
			
			//if ('\n' != text.charAt(text.length() - 1)) {
			//	text += '\n';
			//}
			
			final String insertText = text;
			mMainView.insertText(insertText);
			LogD("insertText(" + insertText + ")");
			
			//如果是单行输入enter则完成输入
			if(!gInputEditIsMultiLine &&  actionId == 0){  		  
				InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  				  
				if(imm.isActive()){  			  
				imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0 );  	  
				}    
				return true;  
			}
		}
		return false;
	}
}

public class C3GLSurfaceView extends C3SurfaceView {
    static private C3GLSurfaceView mainView;
	private final static int HANDLER_OPEN_IME_KEYBOARD = 2;
	private final static int HANDLER_CLOSE_IME_KEYBOARD = 3;
	private static Handler handler;
	private static TextInputWraper textInputWraper;
	private TqEditText mTextField;
	private String sResourcePath;
	private C3Renderer mRenderer;
	
	public static Context MainContext;
	
	public C3GLSurfaceView(Context context) {
		super(context);
		setEGLContextClientVersion(2);
		setEGLConfigChooser(5, 6, 5, 0, 16,GetUseStencilBuffer());
		MainContext =context;
	//	mRenderer = new C3Renderer();
	}
	
	
	public void initView(String sResPath){
		sResourcePath = sResPath;
        setFocusableInTouchMode(true);

		textInputWraper = new TextInputWraper(this);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HANDLER_OPEN_IME_KEYBOARD:
					if (null != mTextField && mTextField.requestFocus()) {
						mTextField.removeTextChangedListener(textInputWraper);
						mTextField.setText("");
						String text = (String) msg.obj;
						mTextField.append(text);
						
						mTextField.setHint("<请输入>");
						textInputWraper.setOriginText(text);
						mTextField.addTextChangedListener(textInputWraper);
					
						// Context c=getContext();
						InputMethodManager imm = (InputMethodManager) mainView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.showSoftInput(mTextField, 0);
						// imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
						// 0);

						Log.d("GLSurfaceView", "showSoftInput");

						// ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0,
						// InputMethodManager.HIDE_NOT_ALWAYS);

					}
					break;

				case HANDLER_CLOSE_IME_KEYBOARD:
					if (null != mTextField) {
						mTextField.removeTextChangedListener(textInputWraper);
						InputMethodManager imm = (InputMethodManager)mainView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								mTextField.getWindowToken(), 0);
						Log.d("GLSurfaceView", "HideSoftInput");
					}
					break;
				}
			}
		};
		
		mainView=this;
	}

	// / beginning of 杞敭鐩樺姛鑳芥帴鍙??
	public TextView getTextField() {
		return mTextField;
	}

    public void setTqRenderer(C3Renderer renderer){
    	mRenderer = renderer;
    	setRenderer(mRenderer);
    	mRenderer.SetResPath(sResourcePath);
    }
    
	public void setTextField(TqEditText view) {
		mTextField = view;
		if (null != mTextField && null != textInputWraper) {
		//	LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mTextField
		//			.getLayoutParams();
		//	linearParams.height = 0;
		//	mTextField.setLayoutParams(linearParams);
		//	mTextField.setOnEditorActionListener(textInputWraper);
			mTextField.setOnEditorActionListener(textInputWraper);
    		mTextField.setMainView(this);
			this.requestFocus();
		}
	}

	public void insertText(final String text) {
		queueEvent(new Runnable() {
			// @Override
			public void run() {
				mRenderer.SetEditTextResult(text);
			}
		});
	}

	public void deleteBackward() {
		queueEvent(new Runnable() {
			// @Override
			public void run() {
				// android涓嬬殑妯睆骞曚笉瀛樺湪鍗虫椂杈撳叆鍗虫椂鏄剧ず鐨勬儏鍐碉紝鎵??浠ユ棤闇??妯℃嫙閫??鏍奸敭锛岀洿鎺ユ竻闄ら噸璁炬枃鏈嵆鍙??
			}
		});
	}

	public static void openIMEKeyboard(String initEditText) {
		Message msg = new Message();
		msg.what = HANDLER_OPEN_IME_KEYBOARD;
		// msg.obj = mainView.getContentText();
		if (initEditText == null) {
			initEditText = "";
		}
		msg.obj = initEditText;
		handler.sendMessage(msg);
	}

	private String getContentText() {
		return "";
	}

	public static void closeIMEKeyboard() {
		Message msg = new Message();
		msg.what = HANDLER_CLOSE_IME_KEYBOARD;
		handler.sendMessage(msg);
	}

	// / end of 杞敭鐩樺姛鑳芥帴鍙??
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// / beginning of android UI娑堟伅澶勭悊妯″潡锛屽垎鍙戣嚦C++鍘熺敓浠ｇ爜鍐呭鐞??
	static int MAX_TOUCH_POINTER = 5;

	class TouchEventRunnable implements Runnable {
		int[] iTouchInfo;

		public TouchEventRunnable(int[] i_TouchInfo) {
			iTouchInfo = i_TouchInfo;
		}

		public void run() {
			TouchEvent(iTouchInfo);

		}
	}

	public boolean onTouchEvent(final MotionEvent event) {

		// 瀵瑰簲C++鐨刄I娑堟伅缁撴瀯
		// typedef struct
		// {
		// int x, y;
		// }CMyPos;
		// typedef struct
		// {
		// CMyPos OldPos;
		// CMyPos CurPos;
		// } TOUCHPOS_INFO;
		//
		// typedef struct
		// {
		// int tapCount; //瑙︽懜鐐规暟锛屽鐐硅Е鎽告敮鎸??
		// int state; //UI鎿嶄綔绫诲瀷
		// vector<TOUCHPOS_INFO> vecPos;
		// } TOUCH_EVENT_INFO;

		int nPoints = event.getPointerCount();
		nPoints = Math.min(nPoints, MAX_TOUCH_POINTER);
		int nPos = 0;

		int[] mTouchInfo = new int[nPoints * 2 + 2];

		mTouchInfo[nPos++] = nPoints;
		mTouchInfo[nPos++] = event.getAction();
		for (int i = 0; i < nPoints; i++) {
			mTouchInfo[nPos++] = (int) event.getX(i);
			mTouchInfo[nPos++] = (int) event.getY(i);
		}

		// 蹇呴』灏嗚Е鍙慤I娑堟伅瀵笴++鐨勮皟鐢ㄥ姞鍏ュ埌OpenGL
		// C++娓叉煋瀛愮嚎绋嬩腑杩愯锛屽惁鍒欎細鍑虹幇澶氱嚎绋婤UG瀵艰嚧娓叉煋寮傚父
		queueEvent(new TouchEventRunnable(mTouchInfo));
		// TouchEvent(mTouchInfo);

		Log.i("TouchInfo--%s", String.format(
				"Time:%d, count:%d, action:%d, X:%d, Y:%d",
				SystemClock.uptimeMillis(), mTouchInfo[0], mTouchInfo[1],
				mTouchInfo[2], mTouchInfo[3]));
		return true;
	}

	public void onPause() {
		int[] mTouchInfo = new int[8];
		mTouchInfo[0] = 0;
		mTouchInfo[1] = 1;
		mTouchInfo[2] = 0;
		TouchEvent(mTouchInfo);

		super.onPause();
	}

	public void onResume() {
		int[] mTouchInfo = new int[8];
		mTouchInfo[0] = 0;
		mTouchInfo[1] = 1;
		mTouchInfo[2] = 1;
		TouchEvent(mTouchInfo);

		super.onResume();
	}

	public native static void TouchEvent(int[] arrEventInfo);
	public native static int GetUseStencilBuffer();
	// / end of android UI娑堟伅澶勭悊妯″潡锛屽垎鍙戣嚦C++鍘熺敓浠ｇ爜鍐呭鐞??
}
