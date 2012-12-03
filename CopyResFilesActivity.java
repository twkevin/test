package com.TQFramework;

import java.io.*; 
import java.util.HashSet;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.TQFramework.*;
//import demo.tqframework.R;
import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.res.*;

public class CopyResFilesActivity extends Activity {
	private static final int INT_UPDATE_PROGERSS_TEXE_MSG = 0x1000;
	private static final int INT_EXIT_ACTIVITY = 0x1001;
	private static final int INT_DETAIL_INFO_MSG = 0x1002;
	private static final int INT_TOTAL_INFO_MSG = 0x1003;
	private static final int INT_RETURN_MAINVIEW = 0x1010;
//	public static final String STR_RES_RES_INTEGRITY = "res_intergrity"; 
	private Button mCancelBtn = null;
	private ProgressBar mProgressBar = null;
	private ProgressBar mProgressDetailBar = null;
	private TextView mProgressText = null;
	private TextView m_TotalInfo = null;
	private TextView m_DetailInfo = null;
	private AssetManager mAssetManager = null;
	private boolean mIsThreadRunning = false;
	private SharedPreferences mSharedPreferences = null;
	//private CopyThread mCopyThread = null;
	private CopyHandler mCopyHandler = null;
	private Handler m_handler = null;
	private HashSet<String> m_SplitFileSet = new HashSet<String>();
	
	private  String sSrcResPath="";
	private  String sDestResPath="";
	private  int    nCurVer=0;
	public  static  Class<?> Maincls;
	public static Context MainContext;
		
	
	private String strcopyfirstlogin="";
	private String strcopycurstep="";
	private String strcopysigposper="";
	private String strcopycurper="";
	private String strcopymergper="";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		
		DynamicActivityId.InitActivityID(MainContext);
		
		strcopyfirstlogin=getString(DynamicActivityId.strid.str_copyfirstlogin).toString();
		strcopycurstep=getString(DynamicActivityId.strid.str_copycurstep).toString();
		strcopysigposper=getString(DynamicActivityId.strid.str_copysigposper).toString();
		strcopycurper=getString(DynamicActivityId.strid.str_copycurper).toString();
		strcopymergper=getString(DynamicActivityId.strid.str_copymergper).toString();
		
		Intent intent=getIntent();
		sSrcResPath=intent.getStringExtra("srcResPath");
		sDestResPath=intent.getStringExtra("destResPath");
		nCurVer=intent.getIntExtra("nCurVer", 0);
		if (nCurVer < 1000)
			nCurVer = 1000;
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); 
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);	
		setContentView(DynamicActivityId.layout.copyresfiles);
		Log.i("CopyFile", "onCreate");
		mAssetManager = this.getAssets();
		
		mSharedPreferences = this.getSharedPreferences(TQFrameworkActivity.STR_RES_RES_INTEGRITY, Context.MODE_PRIVATE);
		
		mCancelBtn = (Button)this.findViewById(DynamicActivityId.id.ap_cancelbutton);
		mCancelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//if(mIsThreadRunning)
				{
					mIsThreadRunning = false;
				}
			}
		});
		
		mProgressBar = (ProgressBar)this.findViewById(DynamicActivityId.id.ap_copyprogressBar);
		mProgressDetailBar = (ProgressBar)this.findViewById(DynamicActivityId.id.ap_copyprogressDetailBar);
		m_TotalInfo = (TextView)this.findViewById(DynamicActivityId.id.totalPercent);
		//m_TotalInfo.setTextColor(color.white);
		m_DetailInfo = (TextView)this.findViewById(DynamicActivityId.id.detailPercent);
		//m_DetailInfo.setTextColor(color.white);
		mProgressBar.setProgress(0);
		mProgressDetailBar.setProgress(0);
		
		mProgressText = (TextView)this.findViewById(DynamicActivityId.id.ap_copyprogressTextView);
		
		m_handler = new Handler() 
		{
			// TODO Auto-generated method stub
		 public void handleMessage(Message msg)
		 {
			switch (msg.what) 
			{
			case INT_UPDATE_PROGERSS_TEXE_MSG:
				String strText = (String)msg.obj;
				mProgressText.setText(strcopyfirstlogin + strText);
				mProgressBar.setProgress(0);
				mProgressDetailBar.setProgress(0);
				break;
				
			case INT_DETAIL_INFO_MSG:
			{
				String strDetailText = (String)msg.obj;
				m_DetailInfo.setText(strDetailText);
				m_DetailInfo.setVisibility(View.VISIBLE);
			}
				break;
				
			case INT_TOTAL_INFO_MSG:
			{
				String strTotalText = (String)msg.obj;
				m_TotalInfo.setText(strTotalText);
				m_TotalInfo.setVisibility(View.VISIBLE);
			}
				break;
			case INT_RETURN_MAINVIEW:
				ReturnMainView();
				break;
			case INT_EXIT_ACTIVITY:
			default:
				break;
			}
			
			super.handleMessage(msg);
		 }
		};
		
		if(!mIsThreadRunning)
		{
			HandlerThread thread = new HandlerThread("threadhandler");
			thread.start();
			mCopyHandler = new CopyHandler(thread.getLooper());
			mCopyHandler.postDelayed(runnable, 1000);		
		}
		
		mIsThreadRunning = true;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mCopyHandler.removeCallbacks(runnable);
		super.onDestroy();
	}
	
	/*public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this).setTitle("取消更新？")
					.setMessage("请确定是否要取消更新?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									android.os.Process.killProcess(android.os.Process.myPid());
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							}).show();
		}
		
		return super.onKeyDown(keyCode, event);
	}*/

	 public void onConfigurationChanged(Configuration newConfig) {
	        // TODO Auto-generated method stub
	        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
	            System.out.println("ORIENTATION_LANDSCAPE="
	                    + Configuration.ORIENTATION_LANDSCAPE);// 当前为横屏

	        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
	            System.out.println("ORIENTATION_PORTRAIT="
	                    + Configuration.ORIENTATION_PORTRAIT);// 当前为竖屏

	        }
	        super.onConfigurationChanged(newConfig);
	    }
	 
	 protected void ReturnMainView()
	    {
	    	Intent intentMain = new Intent();
	    	intentMain.setClass(CopyResFilesActivity.this,Maincls);
	    	startActivity(intentMain);	
	    	finish();
	    }
	
	private class CopyHandler extends Handler{
		public CopyHandler(Looper looper){
			super(looper);
		}

		
	}
	
	private Runnable runnable = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
			//while(mIsThreadRunning)
			{
				if(CopyAssetsToSd(sSrcResPath, sDestResPath))
				{
					/*SharedPreferences.Editor editor = mSharedPreferences.edit();
					String strSourceVer = sDestResPath + "/" + "version.ini";
					String strVersion="0";
					try {
						strVersion = ConfigurationFile.getProfileString(strSourceVer, "public", "version", "0");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					int nVersion = Integer.parseInt(strVersion.trim());
					editor.putInt(STR_RES_RES_INTEGRITY, nVersion);*/
					SharedPreferences.Editor editor = mSharedPreferences.edit();
					editor.putInt(TQFrameworkActivity.STR_RES_RES_INTEGRITY, nCurVer);
					
					setResult(RESULT_OK);
					editor.commit();
					//ReturnMainView();
					Message msg = new Message();
					msg.what = INT_RETURN_MAINVIEW;
					m_handler.sendMessage(msg);					
				}
				else
				{
					SharedPreferences.Editor editor = mSharedPreferences.edit();
					editor.putBoolean(TQFrameworkActivity.STR_RES_RES_INTEGRITY, false);
					setResult(RESULT_CANCELED);
					editor.commit();
					finish();
				}
				//mCopyHandler.postDelayed(runnable, 3000);
			}
		}
	};
	
	private boolean CopyAssetsToSd(String strAssetsPath, String strSDPath){	
		try {
			File targetFileDir = new File(strSDPath);
			if(!targetFileDir.exists())
			{
				targetFileDir.mkdir();
			}
			
			String strNoMedia = strSDPath + "/" + ".nomedia/";
			File targetNoMedia = new File(strNoMedia);
			if(!targetNoMedia.exists())
			{
				targetNoMedia.mkdir();
			}						
						
			String[] ResFiles = mAssetManager.list(strAssetsPath);
			int nSize = ResFiles.length+1;
			mProgressBar.setMax(nSize);
			int nIndex = 0;
			
			mProgressBar.setProgress(++nIndex);
			String strInfo = String.format("%s(%d/%d)",strcopycurstep, nIndex, nSize);			
			Message split_msg = new Message();
			split_msg.what = INT_TOTAL_INFO_MSG;
			split_msg.obj = strInfo;
			m_handler.sendMessage(split_msg);		
			ReadSplitFiles(strAssetsPath, strSDPath, "splites.xml");
			
			for(String resFile : ResFiles)
			{			
				mProgressBar.setProgress(++nIndex);
				strInfo = String.format("%s(%d/%d)",strcopysigposper, nIndex, nSize);
				
				Message msg = new Message();
				msg.what = INT_TOTAL_INFO_MSG;
				msg.obj = strInfo;
				m_handler.sendMessage(msg);				
				
				String srcFile = strAssetsPath + "/" + resFile;
				String dstFile = strSDPath+"/"+resFile;
				
				boolean bFile = resFile.contains(".");
				if(bFile) 
				{   
	                // 澶嶅埗鏂囦欢  
	                copyFile(srcFile, dstFile);   
	            }   
				else 
				{   
	                // 澶嶅埗鐩綍
	                String sourceDir=strAssetsPath+File.separator+resFile;   
	                String targetDir=strSDPath+File.separator+resFile;   
	                copyDirectiory(sourceDir, targetDir);   
	            }   
			}		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public  void copyFile(String srcFile, String dstFile)    
			throws IOException{   
					if(m_SplitFileSet.contains(srcFile))
					{
						Log.i("SplitFile", "SrcFIle: "+srcFile+ " has dealed, return");
						return;
					}
		            Log.i("CopyFile", "SrcFIle: "+srcFile+ " DstFile:"+dstFile);
		            InputStream input = mAssetManager.open(srcFile);
					File targetFile = new File(dstFile);
			        BufferedInputStream inBuff=new BufferedInputStream(input);   
			        FileOutputStream output = new FileOutputStream(targetFile);   
			        // 缂撳啿鏁扮粍 
			        byte[] buf = new byte[1024 * 30];   
			        int len;   
			        while ((len =inBuff.read(buf)) != -1) 
			        {   
			            output.write(buf, 0, len);   
			        }   
			        
			        inBuff.close();      
			        output.close();   
			        input.close();   
			    }   
	
	public void copyDirectiory(String sourceDir, String targetDir)   
            throws IOException {   
        //Create new Dir
        (new File(targetDir)).mkdirs();   
        //Get Cur Dir res
        Log.i("CopyDir", "sourceDir: "+sourceDir+ " targetDir:"+targetDir);
        String[] ResFiles = mAssetManager.list(sourceDir);
        int nSize = ResFiles.length;
        mProgressDetailBar.setMax(nSize);
		int nIndex = 0;
		
		for(String resFile : ResFiles)
		{
			mProgressDetailBar.setProgress(++nIndex);
			
			if(nIndex%10==1)
			{
				String strInfo = String.format("单步进度：(%d%%)", (nIndex*100/nSize));
				Message msg = new Message();
				msg.what = INT_DETAIL_INFO_MSG;
				msg.obj = strInfo;
				m_handler.sendMessage(msg);
			}
			
			
			String strSrc = sourceDir + File.separator + resFile;
			String strDst = targetDir+File.separator+resFile;
			boolean bFile = resFile.contains(".");
			
			if (bFile) 
			{   
                // 澶嶅埗鏂囦欢
				copyFile(strSrc, strDst);   
            }   
			else 
			{   
                // 澶嶅埗鐩綍  
                String srcDir=sourceDir+File.separator+resFile;   
                String dstDir=targetDir+File.separator+resFile;   
                copyDirectiory(srcDir, dstDir);   
            }   
		}
    }   

	
    protected String ReadSplitFiles(String srcDir, String dstDir, String strFileName)
    {
    	DocumentBuilderFactory	docBuilderFactory = null;
    	DocumentBuilder 		docBuilder = null;
    	Document				doc = null;
    	String					strRet = "";
    	try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			InputStream inputFile = null;
			
			m_SplitFileSet.clear();
			String splitXML = srcDir+File.separator+strFileName;
			m_SplitFileSet.add(splitXML);

			inputFile = mAssetManager.open(splitXML);
			doc = docBuilder.parse(inputFile);
			Element root = doc.getDocumentElement();
			root.getNodeName();
			NodeList nodeList = root.getElementsByTagName("SplitFile");
			String strPath = "";
			String strCnt = "";
			int nIndex = 0;	
			for(; nIndex < nodeList.getLength(); nIndex++)
			{
				mProgressDetailBar.setMax(nodeList.getLength());
				Element node = (Element) nodeList.item(nIndex);
				strPath = node.getAttribute("Path");
				strCnt = node.getAttribute("cnt");		
				mergeSplitFiles(strPath, srcDir, dstDir, Integer.parseInt(strCnt));
				
				mProgressDetailBar.setProgress(nIndex+1);
				String strInfo = String.format("%s(%d%%)", strcopycurper,((nIndex+1)*100/nodeList.getLength()));
				Message msg = new Message();
				msg.what = INT_DETAIL_INFO_MSG;
				msg.obj = strInfo;
				m_handler.sendMessage(msg);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return strRet;    	
    }
    
    protected void mergeSplitFiles(String strFile, String srcDir, String dstDir, int nCnt)
    		throws IOException{
    	try 
    	{ 
    		String strDstFile = dstDir + File.separator + strFile;
        	int nPos = strDstFile.lastIndexOf(File.separator);
        	if(nPos<1)
        		return;
        	
        	String strDstFolder = strDstFile.substring(0, nPos);
        	(new File(strDstFolder)).mkdirs();
        	
        	File targetFile = new File(strDstFile);
            OutputStream out = new FileOutputStream(targetFile);  
            byte[] buffer = new byte[1024*30];  
            InputStream in;  
            int readLen = 0;  
            mProgressDetailBar.setMax(nCnt);
            for(int i=1;i<=nCnt;i++){  
                
            	String strSrcFile = srcDir+File.separator+strFile+"."+i;
            	
                in = mAssetManager.open(strSrcFile);  
                m_SplitFileSet.add(strSrcFile);
                while((readLen = in.read(buffer)) != -1){  
                    out.write(buffer, 0, readLen);  
                }  
                out.flush();  
                in.close();  
                mProgressDetailBar.setProgress(i+1);
				String strInfo = String.format("%s(%d%%)",strcopymergper, ((i+1)*100/nCnt));
				Message msg = new Message();
				msg.what = INT_DETAIL_INFO_MSG;
				msg.obj = strInfo;
				m_handler.sendMessage(msg);
            }  
              
            out.close();  
        }  
    	catch (Exception e) {
			// TODO: handle exception
		}
    
    }
    
    public String readresouceversion(String versionfile)
	{
		String version="0";
		File f=new File(versionfile);
		if(f.exists()==false)
			return version;
		
		FileInputStream is;
		try {
			is = new FileInputStream(f);
			byte[] buffer = new byte[40];
			try {
				long len = is.read(buffer);
				buffer[(int)len]=0;
				version=new String(buffer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return version;
	}

}
