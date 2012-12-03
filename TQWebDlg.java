package com.TQFramework;


import android.R.style;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

public class TQWebDlg extends Dialog 
{
	private WebView wvBrowser = null;
	private ImageButton btnExit = null;
	private ImageButton btnBack = null;
	private TextView txtTitle = null;
	
	public TQWebDlg(Context context) 
	{
		//super(context);
		this(context, DynamicActivityId.style.tq_webdialog);
		// TODO Auto-generated constructor stub		
	}

	public TQWebDlg(Context context, int theme) 
	{
		super(context, theme);
		// TODO Auto-generated constructor stub
		setOwnerActivity((Activity)context);
	}	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(DynamicActivityId.layout.tq_webdlg);
		
		DisplayMetrics dm = new DisplayMetrics();
    	this.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
    	
    	LayoutParams layoutParams = new LayoutParams();
    	layoutParams.x = 0;
    	layoutParams.y = 0;
    	layoutParams.width = dm.widthPixels;
    	layoutParams.height = dm.heightPixels;
    	getWindow().setAttributes(layoutParams);
    	
    	wvBrowser = (WebView)findViewById(DynamicActivityId.id.tq_wv_browser);
    	WebSettings settings = wvBrowser.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); 
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        
        wvBrowser.setWebViewClient(new WebViewClient()
        {
        	public boolean shouldOverrideUrlLoading(WebView view, String url)
        	{
        		view.loadUrl(url);
        		return true;
        	}
        });
    	
        btnExit = (ImageButton)findViewById(DynamicActivityId.id.tq_btn_exit);        
        btnExit.setOnClickListener(new View.OnClickListener() 
        {
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				release();
			}
		});
          
        btnBack = (ImageButton)findViewById(DynamicActivityId.id.tq_btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() 
        {
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				if(wvBrowser.canGoBack())
				{
					wvBrowser.goBack();
				}
			}
		}); 
        
        txtTitle = (TextView)findViewById(DynamicActivityId.id.tq_txt_Title);
	}
	
	public void loadURL(String strUrl, String strTitle)
	{
		wvBrowser.loadUrl(strUrl);
		txtTitle.setText(strTitle);
	}	
	
	@Override
	protected void onStart() 
	{
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			release();
		}
		return true;
	}
	
	private void release()
	{
		this.dismiss();
	}
}
