package com.TQFramework;

//import android.app.Activity;
//import android.os.Bundle;
//
//public class Upgrade extends Activity {
//	
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//    }
//}


import java.io.File;  
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;  
import java.io.InputStream;  
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;   
import org.apache.http.HttpResponse;   
import org.apache.http.client.ClientProtocolException;   
import org.apache.http.client.HttpClient;   
import org.apache.http.client.methods.HttpGet;   
import org.apache.http.impl.client.DefaultHttpClient;   
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.TQFramework.DynamicActivityId.id;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;   
import android.app.AlertDialog.Builder;
import android.app.Dialog;   
import android.app.ProgressDialog;   
import android.content.Context;
import android.content.DialogInterface;   
import android.content.Intent;   
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;   
import android.os.Bundle;   
import android.os.Debug;
import android.os.Environment;   
import android.os.Handler;   
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TQUpdateActivity extends Activity {   
	//private  String sSrcUrl="";
	private  String gsSrcResPath="";
	private String gsSrcUrl = "";
	private static boolean  bCheckVer = false;
	public  static  Class<?> Maincls;
	
	private long gtotalSize=0;
	private long gcurdetail=0;
	private ProgressBar pDownBar = null;
	private Button downbutton=null;
	private Button cancelbutton=null;
	private TextView mProgressText = null;
	private TextView mTextmessage = null;
	private TextView mProgressrateview = null;
	private TextView mCheckvertext = null;
    
    public static Context MainContext;
    
    final int	MSG_DOLOADFAILED = 0x001;
    final int	MSG_DOLOADPROGRESS = 0x002;
    final int	MSG_DOLOADCOMPLETE = 0x003;
    final int   MSG_INITCOPYLIBSO = 0X004;
    final int   MSG_INITDOWNAPK  = 0X005;
    final int   MSG_Checknewver  = 0X006;
    
    final int	UPDATEAPK = 1;
    final int   INITCOPYFAILED = 2;
    final int   NETCONFAILED = 3 ;
    final int   HAVE_NEWVER = 4 ;
    final int   NOTHAVE_NEWVER = 5 ;
    
    
    private String strupdfirstupdate="";
    private String strupddownloading="";
    private String strupdupdatefaile="";
    private String strupdateinitfaile="";
    private String strupdupdatefailecon="";
    private String strupdunknowerror="";
    private String strupdok="";
    private String strupdcurper="";
    
    
    
    //http://192.168.213.228/update/NDChannelId.xml下载路径
    final String	m_sDownPathchanelxml = Environment.getExternalStorageDirectory() + "/channelxml/";
    //final String	m_sDownPathupdateapkxml = Environment.getExternalStorageDirectory() + "/updateapkxml/";
 
    protected int ncurversion=0;
    protected String m_sApkUrl="";
    protected String m_ChannelUrl="";
    
    private Handler handler = null;
	
    //取程序包版本号
  /*  protected int GetLocalVersion()
    {
    	PackageInfo pkg = null;
    	int nVersionCode =0;
		try {
			pkg = getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);
			nVersionCode = pkg.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    	return nVersionCode;
    }*/
    
    //取渠道号
    protected String GetAppChanelId()
    {
    	DocumentBuilderFactory	docBuilderFactory = null;
    	DocumentBuilder 		docBuilder = null;
    	Document				doc = null;
    	String					strRet = "";
    	try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(this.getBaseContext().getResources().getAssets().open("NdChannelId.xml"));
			
			Element root = doc.getDocumentElement();
			root.getNodeName();
			NodeList nodeList = root.getElementsByTagName("chl");
			for(int i = 0; i < nodeList.getLength(); i++)
			{
				Element node = (Element) nodeList.item(i);
				strRet = node.getTextContent();
			}
		} catch (Exception e) {
			
		}
		return strRet;
    }
    
    //下载更新配置的XML文件
    protected boolean GetXmlConfigFile(String sUrl,String sDownPath)
    {
    	boolean bRet = false;
    	HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 4 * 1000);
		HttpConnectionParams.setSoTimeout(params, 4 * 1000);
    	HttpClient client = new DefaultHttpClient(params);   
        HttpGet get=new HttpGet(sUrl); 
        HttpResponse response;   
        try {   
            response = client.execute(get);   
            HttpEntity entity = response.getEntity();   
            long length = entity.getContentLength();   
            InputStream is= entity.getContent();   
            FileOutputStream fileOutputStream =null;   
            if (is !=null) {
            	new File(sDownPath).mkdirs();
                File file =new File(GetLocalFileFromURL(sUrl,sDownPath));   
                fileOutputStream =new FileOutputStream(file);   
                byte[] buf =new byte[1024];   
                int ch = -1;   
                int count = 0;   
                while ((ch = is.read(buf)) != -1) {     
                    fileOutputStream.write(buf, 0, ch);   
                    count += ch;
                }
                fileOutputStream.flush();   
                if (fileOutputStream !=null) {
                    fileOutputStream.close();
                    if(count == length){
                    	bRet  = true;
                    }
                }
            }   
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    	return bRet;
    }
    
    //根据渠道号和版本号，取渠道号对应的xml
    protected String GetChannelIdUrl(String sUrl,String sChennelID, int nVerCode)
    {
    	DocumentBuilderFactory	docBuilderFactory = null;
    	DocumentBuilder 		docBuilder = null;
    	Document				doc = null;
    	String					strRet = "";
    	try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			InputStream inputFile = null;
			String sFileName = GetLocalFileFromURL(sUrl,m_sDownPathchanelxml);
			inputFile = new FileInputStream(sFileName);
			doc = docBuilder.parse(inputFile);
			Element root = doc.getDocumentElement();
			root.getNodeName();
			NodeList nodeList = root.getElementsByTagName("chl");
			String strChlTxt = "";
			for(int i = 0; i < nodeList.getLength(); i++)
			{
				Element node = (Element) nodeList.item(i);
				strChlTxt = node.getAttribute("Key");
				if(strChlTxt.equalsIgnoreCase(sChennelID))
				{
					String sValue = node.getAttribute("Version");
					if(sValue != null && sValue.length() > 0)
					{
						int nNewVerCode = Integer.parseInt(sValue);
						if(nNewVerCode > nVerCode)
						{
							strRet = node.getAttribute("Url");
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return strRet;    	
    }
    
    //从URL得到下载的本地文件
    String GetLocalFileFromURL(String url,String sDownPath)
    {
    	String sLocalFile = "";
    	try {
    		//http://121.207.242.194:8081/jifeng_android_91_ZZ.apk
			URL objUrl = new URL(url);
			sLocalFile = objUrl.getFile();
			sLocalFile = sLocalFile.substring(sLocalFile.lastIndexOf("/")+1);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
    	if(sLocalFile.length() == 0)
    	{
    		sLocalFile = "NdChannelId.xml";
    	}
    	
    	sLocalFile = sDownPath + sLocalFile;
    	return sLocalFile;
    }
    
    //检测是否需要更新
    public boolean CheckNewVersion(String sUrl,int nCurVer){
    	if(GetXmlConfigFile(sUrl,m_sDownPathchanelxml))
        {
        	String sValue = GetAppChanelId();
        	m_ChannelUrl = GetChannelIdUrl(sUrl,sValue, nCurVer);
            if(m_ChannelUrl.length() > 0)
            {
            	return true;
            }
        }
		return false;
	}
   
    public boolean GetApkurl()
    {
    	if(GetXmlConfigFile(m_ChannelUrl,m_sDownPathchanelxml))
        {
    		DocumentBuilderFactory	docBuilderFactory = null;
        	DocumentBuilder 		docBuilder = null;
        	Document				doc = null;
        	String					strRet = "";
        	try {
    			docBuilderFactory = DocumentBuilderFactory.newInstance();
    			docBuilder = docBuilderFactory.newDocumentBuilder();
    			InputStream inputFile = null;
    			String sFileName = GetLocalFileFromURL(m_ChannelUrl,m_sDownPathchanelxml);
    			inputFile = new FileInputStream(sFileName);
    			doc = docBuilder.parse(inputFile);
    			Element root = doc.getDocumentElement();
    			root.getNodeName();
    			NodeList nodeList = root.getElementsByTagName("Version");
    			String stridTxt = "";
    			for(int i = 0; i < nodeList.getLength(); i++)
    			{
    				Element node = (Element) nodeList.item(i);
    				stridTxt = node.getAttribute("id");//获得id
    				int nVerCode = Integer.parseInt(stridTxt);
    				if(nVerCode == ncurversion )
    				{
    					m_sApkUrl = node.getAttribute("packet");
    					break;
    				}
    			}
    		} catch (Exception e) {
                e.printStackTrace();
            }
    		if (m_sApkUrl.length() > 0)
    		{
    			return true;
    		}
        }
    	return false;
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }
    protected void ReturnMainView()
    {
    	Intent intentUpgrade = new Intent();
    	intentUpgrade.setClass(TQUpdateActivity.this, Maincls);
    	startActivity(intentUpgrade);  
    	finish();
    }
    
	@Override   
    protected void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState); 
        
        DynamicActivityId.InitActivityID(MainContext);
        setContentView(DynamicActivityId.layout.tqupdate);     
       // View viewNULL = new View(this); 
       // viewNULL.setBackgroundResource(DynamicActivityId.drawable.startback_android);
        //setContentView(viewNULL);
        Intent intent=getIntent();
        gsSrcUrl	= intent.getStringExtra("srcUrl");
        ncurversion = intent.getIntExtra("nCurVer", 0);
	    gsSrcResPath = intent.getStringExtra("srcResPath");
	    
	    strupdfirstupdate=getString(DynamicActivityId.strid.str_updfirstupdate).toString();
	    strupddownloading=getString(DynamicActivityId.strid.str_upddownloading).toString();
	    strupdupdatefaile=getString(DynamicActivityId.strid.str_updupdatefaile).toString();
	    strupdateinitfaile=getString(DynamicActivityId.strid.str_updupdateinitfaile).toString();
	    strupdupdatefailecon=getString(DynamicActivityId.strid.str_updupdatefailecon).toString();
	    strupdunknowerror=getString(DynamicActivityId.strid.str_updunknowerror).toString();
	    strupdok=getString(DynamicActivityId.strid.str_updok).toString();
	    strupdcurper=getString(DynamicActivityId.strid.str_updcurper).toString();
	    
        pDownBar = (ProgressBar)this.findViewById(DynamicActivityId.id.ap_upprogressBardetail); 
        downbutton = (Button)this.findViewById(DynamicActivityId.id.ap_updownbutton); 
        cancelbutton = (Button)this.findViewById(DynamicActivityId.id.ap_upcancelbutton);
        mProgressText = (TextView)this.findViewById(DynamicActivityId.id.ap_upprogressTextView);
        mProgressrateview = (TextView)this.findViewById(DynamicActivityId.id.ap_progressrateview);
        mTextmessage = (TextView)this.findViewById(DynamicActivityId.id.ap_upmessage);
        mCheckvertext = (TextView)this.findViewById(DynamicActivityId.id.ap_pchecknewver);
        
        if(ncurversion < 1000)
        	ncurversion = 1000;
        
        mProgressrateview.setVisibility(View.INVISIBLE);
        mTextmessage.setVisibility(View.INVISIBLE);    
        pDownBar.setVisibility(View.INVISIBLE);
        mProgressText.setVisibility(View.INVISIBLE);
		downbutton.setVisibility(View.INVISIBLE);
		cancelbutton.setVisibility(View.INVISIBLE);
		/*boolean bReturnMain = true;
        if(CheckNewVersion(sSrcUrl,ncurversion))
        {
        	bReturnMain = false;
        }
        if(bReturnMain)
        {
        	ReturnMainView();  
	    	finish();
        }*/
        
        handler = new Handler()
        {
        	@Override
        	public void handleMessage(Message msg) {
        		if(msg.what == MSG_Checknewver)
        		{
        			if(msg.arg1 == HAVE_NEWVER)
        			{
		    			mTextmessage.setVisibility(View.VISIBLE);  
		    	        mProgressText.setVisibility(View.VISIBLE);
		    	        mProgressrateview.setVisibility(View.VISIBLE);
		    	        pDownBar.setVisibility(View.VISIBLE);
		    	        downbutton.setVisibility(View.VISIBLE);
						cancelbutton.setVisibility(View.VISIBLE);
						
						mCheckvertext.setVisibility(View.INVISIBLE);
        			}else
        			{
        				ReturnMainView();  
        		    	finish();
        			}
        		}
        		else if(msg.what == MSG_INITCOPYLIBSO)
        		{
        			mProgressText.setText(strupdfirstupdate);
        		    pDownBar.setProgress(0);
        		}
        		else if (msg.what == MSG_INITDOWNAPK)
        		{
        			mProgressText.setText(strupddownloading);
                    pDownBar.setProgress(0);
        		}
        		else if(msg.what == MSG_DOLOADFAILED)
        		{
        			Builder dialog =  new AlertDialog.Builder(TQUpdateActivity.this);
        			dialog.setTitle(strupdupdatefaile);  
        			if(msg.arg1 == INITCOPYFAILED)
        			{
        				dialog.setMessage(strupdateinitfaile);// 设置内容 
        			}else if (msg.arg1 == NETCONFAILED)
        			{
        				dialog.setMessage(strupdupdatefailecon);// 设置内容 
        			}else
        			{
        				dialog.setMessage(strupdunknowerror);
        			}
        			
                    dialog .setPositiveButton(strupdok,// 设置确定按钮   
                            new DialogInterface.OnClickListener() {               			
                                public void onClick(DialogInterface dialog,int which) {
                                	//进入游戏 
                                	finish();
                                }   
                            });// 创建   
        			dialog.create().show();
        		}
        		else if (msg.what == MSG_DOLOADPROGRESS)
        		{
        			float farg1 = (float)msg.arg1;        			
        			int nPer=(int)((msg.arg2/farg1)*100);
        			String strInfo = String.format("%s(%d%%)",strupdcurper,nPer);	
        			mProgressrateview.setText(strInfo);
        			pDownBar.setMax(msg.arg1);
        			pDownBar.setProgress(msg.arg2);
        		}
        		else if(msg.what == MSG_DOLOADCOMPLETE)
        		{
        			if(msg.arg1 == UPDATEAPK)
        			{
        				InstallApk();
        			}
        			else
        			{
        				ReturnMainView();        				
        			}
        			finish();
        		}
        		super.handleMessage(msg); 
        	}
        };
            
        
        downbutton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				downbutton.setVisibility(View.INVISIBLE);
				cancelbutton.setVisibility(View.INVISIBLE);
				DownLoadApkFile();
			}
		});
        cancelbutton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				downbutton.setVisibility(View.INVISIBLE);
				cancelbutton.setVisibility(View.INVISIBLE);
				ReturnMainView();  
		    	finish();
			}
		});
    
        StartCheckVersion();
	}
    
	void StartCheckVersion() {     
        new Thread() {   
            public void run() {
            	
            	if(!bCheckVer)
        		{
        			bCheckVer=true;
        			Message msg = new Message();
        			msg.what = MSG_Checknewver;
        	        if(CheckNewVersion(gsSrcUrl,ncurversion))
        	        {      	        	           			
                        msg.arg1 = HAVE_NEWVER ;
        	        }else
        	        {
        	        	msg.arg1 = NOTHAVE_NEWVER;
        	        }
                   
                    handler.sendMessage(msg);
        		}
            }
        }.start(); 
	}
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {			
			finish();
			android.os.Process.killProcess(android.os.Process.myPid());
		}					
		return super.onKeyDown(keyCode, event);
	}

    //是否是整包
   public static boolean IsWholePackage()
    {
    	String sPageName=MainContext.getPackageName();
    	String sSrcDir="/data/data/"+sPageName+"/lib/";
    	String sTqcopyso=sSrcDir+"libtqcopyso.so";
    	File ftqsoxml=new File(sTqcopyso);
    	
    	if(ftqsoxml.exists())
    	{
    		return true;
    	}
    	return false;
    }
    
    boolean  CopyLibSoToSdcrad()
    {
    	DocumentBuilderFactory	docBuilderFactory = null;
    	DocumentBuilder 		docBuilder = null;
    	Document				doc = null;
    	String sPageName=getApplication().getPackageName();
    	String sSrcDir="/data/data/"+sPageName+"/lib/";
    	String sDstDir =gsSrcResPath + "/libso/";
    	String sTqcopyso=sSrcDir+"libtqcopyso.so";
    	NodeList nodeSofileList;
    	NodeList nodeFontfileList;
    	Map<String, String> libsofileMap = new HashMap<String, String>();
	
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			InputStream inputFile = null;
			inputFile = new FileInputStream(sTqcopyso);
			doc = docBuilder.parse(inputFile);
			Element root = doc.getDocumentElement();
			root.getNodeName();
			nodeSofileList = root.getElementsByTagName("SoFile");
			nodeFontfileList = root.getElementsByTagName("fontinfo");
		} catch (Exception e)
		{
    		return false; // 如果不存在tqcopyso.xml
		}
		
		gcurdetail = 0;
		gtotalSize = 0;
		
		//枚举字体so信息
		for(int i = 0; i < nodeFontfileList.getLength(); i++)
		{
			String FileName = "";
			String dstFileName="";
			String fontsize="";
			Element node = (Element) nodeFontfileList.item(i);
			FileName = node.getAttribute("filelibso");
			dstFileName = node.getAttribute("filename");
			fontsize = node.getAttribute("fontsize");
			
			String srcfullfilepath= sSrcDir + FileName;
			String dstfullfilepath= gsSrcResPath + "/"+dstFileName;
			File SrcFile = new File(srcfullfilepath); 
			gtotalSize += SrcFile.length();
			
			libsofileMap.put(srcfullfilepath, dstfullfilepath);

			//写入字体的信息到ini/font.ini
			String sfontinifile= gsSrcResPath + "/ini/font.ini"; 
			String sWritecontent=dstFileName +" " +fontsize;
			File fontiniFile = new File(sfontinifile);
			File parentDir=new File(fontiniFile.getParent());
	    	  if(!parentDir.exists())//如果所在目录不存在,则新建.
	    		 parentDir.mkdirs();
	    	  try {
	    		  FileOutputStream output = new FileOutputStream(fontiniFile); 	    	  
	    		  output.write(sWritecontent.getBytes(), 0, sWritecontent.length()); 
	    		  output.close();
	    	  }catch (Exception e)
	    	  {
	    		  return false;
	    	  }
		}
		
		//枚举字体以外的文件so信息
		for(int i = 0; i < nodeSofileList.getLength(); i++)
		{
			Element node = (Element) nodeSofileList.item(i);
			String FileName = "";
			FileName = node.getAttribute("filelibso");
			String srcfullfilepath= sSrcDir + FileName;
			String dstfullfilepath= sDstDir + FileName;
			if(FileName != null && FileName.length() > 0)
			{
				File SrcFile = new File(srcfullfilepath); 
				gtotalSize += SrcFile.length();
				libsofileMap.put(srcfullfilepath, dstfullfilepath);						
			}
		}
		
		//真正的拷贝
	   for(Object objKey : libsofileMap.keySet())
	   {
		   Object value = libsofileMap.get(objKey);
		   try {
				copysoFile(objKey.toString(),value.toString());
			}catch (Exception e)
			{
				return false;//拷贝失败
			}		
	    }
		
    	return true;
    }
    
    private void copysoFile(String ScrFileName, String DstFileName) throws IOException 
    {       
    	FileChannel inChannel = null;        
    	FileChannel outChannel = null;       
    	FileInputStream inStream = null;       
    	FileOutputStream outStream = null;  
        File SrcFile = new File(ScrFileName); 
        File DstFile = new File(DstFileName);
    	try { 
    		
    	  File parentDir=new File(DstFile.getParent());
    	  if(!parentDir.exists())//如果所在目录不存在,则新建.
    		 parentDir.mkdirs();
  		   	
    		inStream = new FileInputStream(SrcFile);            
    		outStream = new FileOutputStream(DstFile);            
    		inChannel = inStream.getChannel();           
    		outChannel = outStream.getChannel();            
    		ByteBuffer buffer = ByteBuffer.allocate(30000);   
    		int ch = -1;
    		while ((ch = inChannel.read(buffer)) != -1) {               
    			buffer.flip();                
    			outChannel.write(buffer);                
    			buffer.clear();
    			
    			gcurdetail += ch;
    			Message msg = new Message();
                msg.what = MSG_DOLOADPROGRESS;
                msg.arg1 = (int) gtotalSize;
                msg.arg2 = (int)gcurdetail;
                handler.sendMessage(msg);
                //pDownBar.setProgress(msg.arg2);
    			}        
    		} catch (IOException e) {
    			e.printStackTrace();        
    			} finally {           
    				inStream.close();           
    				inChannel.close();           
    				outStream.close();         
    				outChannel.close();      
    				}   
    	}
    
    void DownLoadApkFile() {     
        new Thread() {   
            public void run() {
            
            success:{
            	if(IsWholePackage())
            	{
            		Message msgcopyso = new Message();
            		msgcopyso.what = MSG_INITCOPYLIBSO;	                                   
                    handler.sendMessage(msgcopyso);           		
            		if (!CopyLibSoToSdcrad())
            		{
            			Message msg = new Message();
                        msg.what = MSG_DOLOADFAILED;
                        msg.arg1 = INITCOPYFAILED;		                                   
                        handler.sendMessage(msg);
                        break success;
            		}		                        		
            	}//如果是第一次更新那么必须拷贝so文件到sdcard
 
                if (!GetApkurl())
                {
                	handler.sendEmptyMessage(MSG_DOLOADFAILED);		                            	 
                }
                String url = m_sApkUrl;
                
               Message msgdowninit = new Message();
                msgdowninit.what = MSG_INITDOWNAPK;	                                   
                handler.sendMessage(msgdowninit);             
               // DownLoadApkFile(m_sApkUrl);  
                              
            	long lApkFileLength = -1;
            	int nDownFileBytes = 0;
                HttpClient client = new DefaultHttpClient();   
                HttpGet get=new HttpGet(url); 
                HttpResponse response;   
                try {   
                    response = client.execute(get);   
                    HttpEntity entity = response.getEntity();   
                    lApkFileLength = entity.getContentLength();   
                    InputStream is= entity.getContent();   
                    FileOutputStream fileOutputStream =null;   
                    if (is !=null) {
   
                        File file =new File(GetLocalFileFromURL(url,m_sDownPathchanelxml));   
                        fileOutputStream =new FileOutputStream(file);   
                         
                        byte[] buf =new byte[30000];   
                        int ch = -1;   
                        while ((ch = is.read(buf)) !=-1) {     
                            fileOutputStream.write(buf, 0, ch);   
                            nDownFileBytes += ch;
                            
                            Message msg = new Message();
                            msg.what = MSG_DOLOADPROGRESS;
                            msg.arg1 = (int) lApkFileLength;
                            msg.arg2 = nDownFileBytes;
                            handler.sendMessage(msg);
                        }   
                    }   
                    fileOutputStream.flush();   
                    if (fileOutputStream !=null) {   
                        fileOutputStream.close();   
                    } 
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = MSG_DOLOADFAILED;
                    msg.arg1 = NETCONFAILED;		                                   
                    handler.sendMessage(msg);   
                    break success;
                }
                Message msg = new Message();
                msg.what = MSG_DOLOADCOMPLETE;
                msg.arg1 = 0;
                if(lApkFileLength == nDownFileBytes)
                {
                	msg.arg1 = UPDATEAPK;
                }
                handler.sendMessage(msg);
            }
           }
        }.start();   
    }   

    void InstallApk() {   
   
        Intent intent = new Intent(Intent.ACTION_VIEW);   
        intent.setDataAndType(Uri.fromFile(new File(GetLocalFileFromURL(m_sApkUrl,m_sDownPathchanelxml))),   
                "application/vnd.android.package-archive");   
        startActivity(intent);
    }   
   
}  

