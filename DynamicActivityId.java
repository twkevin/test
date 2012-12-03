package com.TQFramework;

import android.content.Context;
import android.content.res.Resources;

public final class DynamicActivityId
{  public static void InitActivityID(Context paramContext)
	{
	Resources localResources = paramContext.getResources();
    String str = paramContext.getPackageName();  
    id.ap_cancelbutton = localResources.getIdentifier("ap_cancelbutton","id", str);
    id.ap_copyprogressBar = localResources.getIdentifier("ap_copyprogressBar","id", str);
    id.ap_copyprogressDetailBar = localResources.getIdentifier("ap_copyprogressDetailBar","id", str);
    id.totalPercent = localResources.getIdentifier("totalPercent","id", str);
    id.detailPercent = localResources.getIdentifier("detailPercent","id", str);
    id.ap_copyprogressTextView = localResources.getIdentifier("ap_copyprogressTextView","id", str);
    
    layout.copyresfiles = localResources.getIdentifier("tq_copyresfiles","layout", str);
    layout.tqupdate = localResources.getIdentifier("tq_update","layout", str);
    layout.tq_webdlg = localResources.getIdentifier("tq_webdlg","layout", str);
    
    id.ap_upmessage = localResources.getIdentifier("ap_message","id", str);
    id.ap_upprogressTextView = localResources.getIdentifier("ap_progressTextView","id", str);
    id.ap_upprogressBardetail = localResources.getIdentifier("ap_progressBardetail","id", str);
    id.ap_updownbutton = localResources.getIdentifier("ap_updownbutton","id", str);
    id.ap_upcancelbutton = localResources.getIdentifier("ap_upcancelbutton","id", str);
    id.ap_progressrateview = localResources.getIdentifier("ap_progressrateview","id", str);
    id.ap_pchecknewver = localResources.getIdentifier("ap_pchecknewver","id", str);
    id.tq_btn_exit = localResources.getIdentifier("tq_btn_exit","id", str);
    id.tq_btn_back = localResources.getIdentifier("tq_btn_back","id", str);
    id.tq_txt_Title = localResources.getIdentifier("tq_txt_Title","id", str);
    id.tq_wv_browser= localResources.getIdentifier("tq_wv_browser","id", str);
    
    
    strid.str_copyfirstlogin=localResources.getIdentifier("copyfirstlogin","string", str);
    strid.str_copycurstep=localResources.getIdentifier("copycurstep","string", str);
    strid.str_copysigposper=localResources.getIdentifier("copysigposper","string", str);
    strid.str_copycurper=localResources.getIdentifier("copycurper","string", str);
    strid.str_copymergper=localResources.getIdentifier("copymergper","string", str);
	 
    strid.str_updfirstupdate=localResources.getIdentifier("updfirstupdate","string", str);
    strid.str_upddownloading=localResources.getIdentifier("upddownloading","string", str);
    strid.str_updupdatefaile=localResources.getIdentifier("updupdatefaile","string", str);
    strid.str_updupdateinitfaile=localResources.getIdentifier("updupdateinitfaile","string", str);
    strid.str_updupdatefailecon=localResources.getIdentifier("updupdatefailecon","string", str);
    strid.str_updunknowerror=localResources.getIdentifier("updunknowerror","string", str);
    strid.str_updok=localResources.getIdentifier("updok","string", str);
    strid.str_updcurper=localResources.getIdentifier("updcurper","string", str);
    
    strid.str_appexitgametitle=localResources.getIdentifier("appexitgametitle","string", str);
    strid.str_appexitgame=localResources.getIdentifier("appexitgame","string", str);
    strid.str_appexitok=localResources.getIdentifier("appexitok","string", str);
    strid.str_appexitcancel=localResources.getIdentifier("appexitcancel","string", str);
    
    style.tq_webdialog=localResources.getIdentifier("tq_webdialog","style", str);
	} 

	public static final class id
	{
	  public static int ap_cancelbutton;
	  public static int ap_copyprogressBar;
	  public static int ap_copyprogressDetailBar;
	  public static int totalPercent;
	  public static int detailPercent;
	  public static int ap_copyprogressTextView;  
	  
	  public static int ap_upmessage;
	  public static int ap_upprogressTextView;
	  public static int ap_upprogressBardetail;
	  public static int ap_updownbutton;
	  public static int ap_upcancelbutton;
	  public static int  ap_progressrateview;
	  public static int ap_pchecknewver;

	  public static int tq_btn_exit;
	  public static int tq_btn_back;
	  public static int tq_txt_Title;
	  public static int tq_wv_browser;
	  
	  
	}
	
	 public static final class layout
	 {
		 public static int copyresfiles;  
		 public static int tqupdate;
		 public static int tq_webdlg;
	 }
	 
	 public static final class strid
	 {
		 public static int 	str_copyfirstlogin;
		 public static int 	str_copycurstep;
		 public static int 	str_copysigposper;
		 public static int 	str_copycurper;
		 public static int 	str_copymergper;
		 
		 public static int str_updfirstupdate;
		 public static int str_upddownloading;
		 public static int str_updupdatefaile;
		 public static int str_updupdateinitfaile;
		 public static int str_updupdatefailecon;
		 public static int str_updunknowerror;
		 public static int str_updok;
		 public static int str_updcurper;
		 
		 public static int str_appexitgametitle;
		 public static int str_appexitgame;
		 public static int str_appexitok;
		 public static int str_appexitcancel;
		 
	 
	 }
	 public static final class style
	 {
		 public static int tq_webdialog; 
	 }
}
