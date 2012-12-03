package com.TQFramework;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.TQFramework.C3SurfaceView;


// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class TQFrameworkActivity extends Activity {
	protected boolean bInit = false;
	static protected boolean s_bExtract = false;
	public  static boolean IsCHeckUpdate =false;
	protected C3GLSurfaceView mGLView;
	public static final String STR_RES_RES_INTEGRITY = "res_intergrity";
  //  private static C3MusicEngine backgroundMusicPlayer;
   // private static C3Sound soundPlayer;
	private static C3AudioEngine audioEngine;

	private String strappexitgametitle="";
	private String strappexitgame="";
	private String strappexitok="";
	private String strappexitcancel="";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(mGLView);
	}

	public C3GLSurfaceView getGLView() {
		return mGLView;
	}
	
	//检测新版本
	public boolean CheckNewVersion(String sAssetsRes,String sDestResPath,String sUrl) {
		
		if(IsCHeckUpdate)
			return false;	
		IsCHeckUpdate =true;
		TQUpdateActivity.MainContext = this;
		int ncurVersion = 0;
		int nVersion=0;
		SharedPreferences sharedPreferences = this.getSharedPreferences(
				STR_RES_RES_INTEGRITY, Context.MODE_PRIVATE);
		try {
			if (sharedPreferences.contains(STR_RES_RES_INTEGRITY)) {
	
					nVersion = sharedPreferences.getInt(STR_RES_RES_INTEGRITY, 0);
			}else
			{
				IsCHeckUpdate =true;
				return false;
			}
		} catch (Exception ex) {
			IsCHeckUpdate =true;
			return false;
		}
		
		boolean bIsAssets = false;
		bIsAssets = IsAssetsContainRes(sAssetsRes);
		if (bIsAssets)
		{	
			String strSourceVer = sAssetsRes + "/" + "TqUpdate.ver";
			try {
				ncurVersion = OpenAssetVerFile(strSourceVer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (ncurVersion > 0 && (sUrl != null && sUrl.length() > 0))
			{
				TQUpdateActivity.Maincls = this.getClass();
				Intent intentUpdate = new Intent();
				intentUpdate.putExtra("srcUrl", sUrl);
				intentUpdate.putExtra("srcResPath", sDestResPath);
				intentUpdate.putExtra("nCurVer", ncurVersion);
				intentUpdate.setClass(this,TQUpdateActivity.class);
				startActivityForResult(intentUpdate, 0);	//启动更新界面
				IsCHeckUpdate = true;
				finish();
				return true;
			}		
		}
		return false;
	}
	
	
	public boolean ExtractRes(String sAssetsRes, String sDestResPath) {
		if (s_bExtract)
		{
			return false;//不用再抽取了
		}
		s_bExtract = true;
		// 棄1?7测是否是第一次启动游戏，如果是就拷贝资源到指定目录sDestResPath
		SharedPreferences sharedPreferences = this.getSharedPreferences(
				STR_RES_RES_INTEGRITY, Context.MODE_PRIVATE);
		CopyResFilesActivity.MainContext = this;
		boolean bIsAssets = false;
		int nVersion = 0;
		int ncurVersion = 0;

		bIsAssets = IsAssetsContainRes(sAssetsRes);
		if (bIsAssets) {
			try {
				String strSourceVer = sAssetsRes + "/" + "TqUpdate.ver";
				ncurVersion = OpenAssetVerFile(strSourceVer);
			} catch (Exception ex) {
			}
		}
		if (sharedPreferences.contains(STR_RES_RES_INTEGRITY)) {
			try {
				nVersion = sharedPreferences.getInt(STR_RES_RES_INTEGRITY, 0);
			} catch (Exception ex) {
			}
		}

		if ((nVersion == 0 || (nVersion != 0 && nVersion < ncurVersion))
				&& bIsAssets) {
			Intent intentCopyRes = new Intent();
			intentCopyRes.putExtra("srcResPath", sAssetsRes);
			intentCopyRes.putExtra("destResPath", sDestResPath);
			intentCopyRes.putExtra("nCurVer", ncurVersion);
			CopyResFilesActivity.Maincls = this.getClass();
			// intentCopyRes.putExtra("Person", frameworkActivity);
			intentCopyRes.setClass(this,
					CopyResFilesActivity.class);
			startActivityForResult(intentCopyRes, 0);
			finish();
			return true;
		}

		return false;

	}
	static boolean s_bInit = false;
	public boolean EnterGame( String sResPath) {
		if (s_bInit) {
			//return false;
		}
		s_bInit=true;					

		DynamicActivityId.InitActivityID(this);
		
		strappexitgametitle=getString(DynamicActivityId.strid.str_appexitgametitle).toString();
		strappexitgame=getString(DynamicActivityId.strid.str_appexitgame).toString();
		strappexitok=getString(DynamicActivityId.strid.str_appexitok).toString();
		strappexitcancel=getString(DynamicActivityId.strid.str_appexitcancel).toString();
		
		PreInitApp();
		TQUpdateActivity.MainContext = this;
		String sLibFolderPath="";
		if (TQUpdateActivity.IsWholePackage())//如果/data/data/包名/lib/目录下如果不存在tqcopyso.xml，则为更新包
		{
			sLibFolderPath="/data/data/"+getPackageName()+"/lib";	
		}else
		{
			sLibFolderPath=sResPath+"/libso";	
		}
		SetAndroidLibFolderPath(sLibFolderPath);
		
		setOSInfo(); //设置系统相关信息
        
		ViewGroup.LayoutParams framelayout_params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                           ViewGroup.LayoutParams.FILL_PARENT);
            FrameLayout framelayout = new FrameLayout(this);
            framelayout.setLayoutParams(framelayout_params);

            // TqEditText layout
            ViewGroup.LayoutParams edittext_layout_params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                           ViewGroup.LayoutParams.WRAP_CONTENT);
            TqEditText edtInput= new TqEditText(this);
            edtInput.setLayoutParams(edittext_layout_params);

            // ...add to FrameLayout
            framelayout.addView(edtInput);

            // LuaGLSurfaceView
	        mGLView = new C3GLSurfaceView(this);
            framelayout.addView(mGLView);
	        mGLView.initView(sResPath);
	        mGLView.setTqRenderer(new C3Renderer());
            mGLView.setTextField(edtInput);
         	NDLoginAdapter.sharedInstance().SetGLSurfaceView(mGLView);

            // Set framelayout as the content view
			setContentView(framelayout);
       // DisplayMetrics dm = new DisplayMetrics();
       // getWindowManager().getDefaultDisplay().getMetrics(dm);

        audioEngine=new C3AudioEngine(this); 
        audioEngine.SetResPath(sResPath);
        audioEngine.init();
		/*try {
			System.loadLibrary(sLibName);
		} catch (Exception es) {
			bRet = false;
			return false;
		}*/

		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		//		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	//	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

/*
		final LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		layout.setLayoutParams(params);
		setContentView(layout);
		// mGLView = new C3GLSurfaceView(this);
		// mGLView.initView();
		LayoutParams paramsView = new LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		mGLView.setLayoutParams(paramsView);
		edtInput = new EditText(this);
		LayoutParams parEdit = new LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		edtInput.setLayoutParams(parEdit);

		mGLView.setTextField(edtInput);
		// C3GLSurfaceView.mainView=mGLView;
		layout.addView(edtInput);
		layout.addView(mGLView);
		*/
		return true;
	}

	 @Override
	 protected void onPause() {
	     super.onPause();
	     mGLView.onPause();
	 }

	 @Override
	 protected void onResume() {
	     super.onResume();
	     mGLView.onResume();
	 }
	 
	private AssetManager mAssetManager = null;

	public int OpenAssetVerFile(String srcFile) throws IOException {
		int ncurVersion = 0;
		mAssetManager = this.getAssets();
		InputStream input = mAssetManager.open(srcFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);
		// 缓冲数组
		byte[] buf = new byte[1024];
		int len;
		len = inBuff.read(buf);
		buf[(int) len] = 0;
		String strCurVersion = new String(buf);
		inBuff.close();
		input.close();
		strCurVersion = strCurVersion.trim();
		String[] strArray = strCurVersion.split("\r\n");
		if (strArray.length >= 2) {
			String sSection = strArray[0].trim();
			String strLine = strArray[1].trim();

			if (sSection.equalsIgnoreCase("[public]")) {
				strCurVersion = strLine.substring(strLine.indexOf("=") + 1)
						.trim();
				ncurVersion = Integer.parseInt(strCurVersion.trim());
				return ncurVersion;
			}
		}
		return ncurVersion;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this).setTitle(strappexitgametitle)
					.setMessage(strappexitgame)
					.setPositiveButton(strappexitok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
									android.os.Process.killProcess(android.os.Process.myPid());
								}
							})
					.setNegativeButton(strappexitcancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).show();
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private boolean IsAssetsContainRes(String strSrcResPath) {
		String[] strResFiles = null;
		try {
			strResFiles = this.getAssets().list(strSrcResPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		if (strResFiles == null || strResFiles.length == 0) {
			return false;
		}
		return true;
	}

	static public void terminateProcess() {
		//C3AudioEngine.stopBackgroundMusic();
		// onPause();
		// TQFrameworkActivity.this.finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	//手机操作系统版本
	//手机IMSI
	//手机IMEI
	//手机型号
	//手机系统
	//手机固件
	//客户端Ip地址
	//游戏版本
	private void setOSInfo(){
		TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String strImei = telephonyManager.getDeviceId();
		String strImsi = telephonyManager.getSubscriberId();
		
		if(null == strImei)
		{
			strImei ="";
		}
		if(null == strImsi)
		{
			strImsi ="";
		}
		
		PackageInfo packageInfo = null;
		try {
			packageInfo = this.getPackageManager().getPackageInfo(
					this.getPackageName(), PackageManager.GET_CONFIGURATIONS);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int isWifiConnected = -1;
		ConnectivityManager networkManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] networkInfos = networkManager.getAllNetworkInfo();  
		for(NetworkInfo networkInfo : networkInfos){
			if(networkInfo.getState() == NetworkInfo.State.CONNECTED){
				int nNetType = networkInfo.getType();
				if(nNetType == ConnectivityManager.TYPE_MOBILE){
					isWifiConnected = 0;
					break;
				}
				else if(nNetType == ConnectivityManager.TYPE_WIFI){
					isWifiConnected = 1;
					break;
				}
			}
		}
		String strClientIP = "";
		if(isWifiConnected == 1){
			WifiManager wifiManager = (WifiManager)this.getSystemService(WIFI_SERVICE);
			if (wifiManager.isWifiEnabled()) {		
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				int ipAddress = wifiInfo.getIpAddress();
				strClientIP = (ipAddress & 0xFF ) + "." + ((ipAddress >> 8 ) & 0xFF) + "." +
								((ipAddress >> 16 ) & 0xFF) + "." + (ipAddress >> 24 & 0xFF);
			}
		}
		else if(isWifiConnected == 0){
			try {
				for (Enumeration<NetworkInterface> networkEnumeration = NetworkInterface.getNetworkInterfaces(); 
						networkEnumeration.hasMoreElements();) {
					NetworkInterface networkInterface = networkEnumeration.nextElement();
					for (Enumeration<InetAddress> inetAddrEnumeration = networkInterface.getInetAddresses();
							inetAddrEnumeration.hasMoreElements();) {
						InetAddress inetAddress = inetAddrEnumeration.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							strClientIP = inetAddress.getHostAddress()
;
							break;
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		SetOSInformation(android.os.Build.VERSION.RELEASE, strImsi, strImei,android.os.Build.MODEL, "Android", android.os.Build.BOARD,strClientIP, packageInfo.versionName);
	}
	
	public native static int PreInitApp();
	public native static int GetDeviceOrientation();
	public native static void SetAndroidLibFolderPath(String strLibFolderPath);
	public native static void SetOSInformation(String OSVersion,String OSImsi,String OSImei,String OSModel,String OSName,String OSFirmware,String ClientIp,String ClientVersion);

}

// //////////////////////////////////////////////////////////
