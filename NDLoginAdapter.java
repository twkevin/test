package com.TQFramework;

import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.nd.commplatform.NdCallbackListener;
import com.nd.commplatform.NdCommplatform;
import com.nd.commplatform.NdErrorCode;
import com.nd.commplatform.NdMiscCallbackListener;
import com.nd.commplatform.NdMiscCallbackListener.OnLoginProcessListener;
import com.nd.commplatform.entry.NdAppInfo;
import com.nd.commplatform.entry.NdBuyInfo;
import com.TQFramework.TQWebDlg;

public class NDLoginAdapter  {
	private NDLoginAdapter(){};
	private static NDLoginAdapter instance = new NDLoginAdapter();
	protected C3GLSurfaceView mGLView;

	private Activity mainActivity = null; 
	private static Context ctx = null; 
	
	public Context GetContext(){return ctx;};
	
	public void Use91Sdk(Activity context){ctx = context;mainActivity=context;};
	
	public static NDLoginAdapter sharedInstance(){return instance;};
	
	public void SetGLSurfaceView( C3GLSurfaceView view){
		mGLView=view;
	}
	
	public boolean SetAppInfo(int nAppID, String sAppKey)
	{
		if (mainActivity == null) {
			return false;
		}
		final int nAppIDTemp = nAppID;
		final String sAppKeyTemp = sAppKey;
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				NdAppInfo appInfo = new NdAppInfo();
				appInfo.setAppId(nAppIDTemp);
				appInfo.setAppKey(sAppKeyTemp);
				appInfo.setCtx(mainActivity); 
				NdCommplatform.getInstance().initial(0, appInfo);
			}
		});
		return true;
	};
	
	public boolean Login(){
		if (mainActivity == null) {
			return false;
		}
		NdCommplatform.getInstance().ndSetScreenOrientation(NdCommplatform.SCREEN_ORIENTATION_LANDSCAPE);
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				NdCommplatform.getInstance().ndLogin(mainActivity, new OnLoginProcessListener() {
					public void finishLoginProcess(int arg0) {
						switch (arg0) {
						case NdErrorCode.ND_COM_PLATFORM_SUCCESS://登录成功
							NDLoginEvent2JniCall(0,1 , 0);
							break;
							
						case NdErrorCode.ND_COM_PLATFORM_ERROR_LOGIN_FAIL: //登录失败
							NDLoginEvent2JniCall(0, 0, 0);
							break;
							
						case NdErrorCode.ND_COM_PLATFORM_ERROR_CANCEL://取消登录 
							NDLoginEvent2JniCall(1, arg0, 0);
							break;
							

						default://登录失败 
							break;
						}
						
					}
				});
			
			}
		});
		return true;
	}

	public boolean EnterAccountManage(){
		if (mainActivity == null) {
			return false;
		}
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				NdCommplatform.getInstance().ndEnterAccountManage(mainActivity, new OnLoginProcessListener() {
					public void finishLoginProcess(int arg0) {
						switch (arg0) {
						case NdErrorCode.ND_COM_PLATFORM_SUCCESS://登录成功
							NDLoginEvent2JniCall(0,1 , 0);
							break;
							
						case NdErrorCode.ND_COM_PLATFORM_ERROR_LOGIN_FAIL: //登录失败
							NDLoginEvent2JniCall(0, 0, 0);
							break;
							
						case NdErrorCode.ND_COM_PLATFORM_ERROR_CANCEL://取消登录 
							NDLoginEvent2JniCall(1, arg0, 0);
							break;
							

						default://登录失败 
							break;
						}
						
					}
				});
			
			}
		});
		return true;
	}


	public boolean Logout(){
		if (mainActivity == null) {
			return false;
		}
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				NdCommplatform.getInstance().ndLogout(NdCommplatform.LOGOUT_TO_RESET_AUTO_LOGIN_CONFIG, mainActivity);
			}
		});
		return true;
	}
	public boolean isLogined(){
		if (mainActivity == null) {
			return false;
		}
		boolean bResult=false;
		bResult = NdCommplatform.getInstance().isLogined();
		return bResult;
	}
	
	public boolean isAutoLogin(){
		if (mainActivity == null) {
			return false;
		}
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				NdCommplatform.getInstance().isAutoLogin(mainActivity);
			}
		});
		return true;
	}
	
	public boolean Pay(int nCount, String pszGoodID, String sServerID, String sGoodName, float fPrice){
		if (mainActivity==null) {
			return false;
		}
		final NdBuyInfo buyInfo = new NdBuyInfo(); 
		buyInfo.setSerial(UUID.randomUUID().toString());//订单号唯一(不能为空) 
		buyInfo.setProductId(pszGoodID);//产品ID 
		buyInfo.setProductName(sGoodName);//产品名称 
		buyInfo.setProductPrice(fPrice);//产品价格 (不能小于0.01个91豆) 
		buyInfo.setProductOrginalPrice(fPrice);//产品原价 
		buyInfo.setCount(nCount);//购买数量(商品数量最大10000，最新1) 
		buyInfo.setPayDescription(sServerID);//描述 


		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				int aError = NdCommplatform.getInstance().ndUniPayAsyn(buyInfo, mainActivity ,new NdMiscCallbackListener.OnPayProcessListener() {  
				  public void finishPayProcess(int code) { 
				    switch(code){ 
				    case NdErrorCode.ND_COM_PLATFORM_SUCCESS: 
				      Toast.makeText(ctx, "购买成功", Toast.LENGTH_SHORT).show();  
				      break; 
				    case NdErrorCode.ND_COM_PLATFORM_ERROR_PAY_FAILURE: 
				      Toast.makeText(ctx, "购买失败", Toast.LENGTH_SHORT).show();  
				      break; 
				    case NdErrorCode.ND_COM_PLATFORM_ERROR_PAY_CANCEL: 
				      Toast.makeText(ctx, "取消购买", Toast.LENGTH_SHORT).show(); 
				      break; 
				    default: 
				      Toast.makeText(ctx, "购买失败", Toast.LENGTH_SHORT).show();  
				    }
				    NDLoginEvent2JniCall(3, code, 0);
				  } 
				}); 
				if(aError != 0){ 
				  Toast.makeText(ctx, "您输入参数有错，无法提交购买请求", Toast.LENGTH_SHORT).show(); 
				}
			
			}
		});

		return true;
	}
	

	public void EnterAppBBS(){
		if (mainActivity == null) {
			return ;
		}
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				NdCommplatform.getInstance().ndEnterAppBBS(mainActivity, 0);
			}
		});
		return ;
	}

	public void UserFeedback(){
		if (mainActivity == null) {
			return ;
		}
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				NdCommplatform.getInstance().ndUserFeedback(mainActivity);
			}
		});
		return ;
	}
	public void EnterPlatform(){
		if (mainActivity == null) {
			return ;
		}
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				NdCommplatform.getInstance().ndEnterPlatform(0,mainActivity);
			}
		});
		return ;
	}
	public boolean UpdateVer()
	{
		if (mainActivity == null) {
			return false;
		}
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
	
				NdCallbackListener<Integer> callback = new NdCallbackListener<Integer>() {

					@SuppressWarnings("deprecation")
					@Override
					public void callback(int responseCode, Integer t) {
						if (responseCode == NdErrorCode.ND_COM_PLATFORM_SUCCESS) {
							switch (t) {
							case NdCommplatform.UPDATESTATUS_NONE://无新版本
								break;
							case NdCommplatform.UPDATESTATUS_UNMOUNTED_SDCARD://未安装SD卡
								break;
							case NdCommplatform.UPDATESTATUS_CANCEL_UPDATE://用户取消普通更新
								break;
							case NdCommplatform.UPDATESTATUS_CHECK_FAILURE://新版本检测失败break;
							case NdCommplatform.UPDATESTATUS_FORCES_LOADING://强制更新正在下载
								break;
							case NdCommplatform.UPDATESTATUS_RECOMMEND_LOADING://推荐更新正在下载
								break;
							default://检查更新失败
							}
						}else{
							Toast.makeText(ctx, "网络异常或者服务端出错,检查更新失败", Toast.LENGTH_LONG).show();
						}
					}
				};
				NdCommplatform.getInstance().ndAppVersionUpdate(mainActivity, callback);
			}
		});
		return true;
	};
	private int mnEventID;
	private int mwParam;
	private int mlParam;
	
	public int NDLoginEvent2JniCall(int nEventID, int wParam, int lParam)
	{
		mnEventID = nEventID;
		mwParam = wParam;
		mlParam = lParam;
		mGLView.queueEvent(new Runnable() {
			public void run() {
				NDLoginEvent(NDLoginAdapter.this.mnEventID , NDLoginAdapter.this.mwParam, NDLoginAdapter.this.mlParam);
			}
		});
		return 0;
	};
	
	private TQWebDlg mWebDlg;
	private String strDestResPath;
	public void setDestResPath(String DestResPath)
	{
		strDestResPath = DestResPath;
	}
	
	public void openWeb(String strUrl, String strTitle){
		if (mainActivity == null || strUrl == "") {
			return ;
		}	
		
		final String strURLText = strUrl;
		final String strTitleText = strTitle;
				
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				mWebDlg = new TQWebDlg(mainActivity);
				mWebDlg.show();			
				mWebDlg.loadURL(strURLText, strTitleText);
			}
		});
	}
		    	
	public void openLocalWeb(String strFilePath, String strTitle){
			if (mainActivity == null || strFilePath == "") {
				return ;
			}
			
			final String strURL = strDestResPath + "/" + strFilePath;
			strURL.replaceAll("//", "/");
			final String strTitleText = strTitle;
					
			mainActivity.runOnUiThread(new Runnable() {
				public void run() {
					mWebDlg = new TQWebDlg(mainActivity);
					mWebDlg.show();			
					mWebDlg.loadURL(strURL, strTitleText);
				}
			});
	}	
	
	public void showWeb()
	{
		if(mWebDlg != null)
		{
			mWebDlg.show();
		}
	}
	
	public static native int NDLoginEvent(int nEventID, int wParam, int lParam);
}
