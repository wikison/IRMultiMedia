package com.inroids.irmultimedia;
//IRTimeLabel.java
//Created by sealy on 2013-06-01.  
//Copyright 2013 Sealy, Inc. All rights reserved.

import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import inroids.common.MyLog;
import android.widget.FrameLayout;
import android.widget.TextView;

//{"type":"7","x":"156","y":"635","w":"241","h":"27"," content ":"yyyy年MM月dd日 HH:mm:ss","fontSize":"18px","textColor":"#000000","bgColor":"","fontStyle":"1","fontWeight":"1","radian":"0"}
public class IRTimeLabel extends TextView implements Runnable {
	private Activity actMain=null;	
	private String strTimeFormat="yyyy-MM-dd HH:mm:ss";

	//Create view-------------------------------------
	public IRTimeLabel(Context context) {
		super(context);
		this.setTag(7);
		this.actMain=(Activity)context;
	}
	
	//解析JSON对象--------------------------------
	public void initData(FrameLayout frame,JSONObject objT){
	  	try {  		
	  		//set Time Format
	  		String strFormat=ClassObject.getString(objT, "content");
	  		if(inroids.common.System.isNotNullString(strFormat))this.strTimeFormat=strFormat;//格式存在
	  		//set Font
	  		//TextColor
	  		ClassObject.setTextColor(this, objT, "textColor");				
	  		//set font size
	  		ClassObject.setTextSize(this, objT, "fontSize");	    	
	  		//set font bold
	  		ClassObject.setFakeBoldText(this, objT, "fontWeight");
	  		//set font style
	  		ClassObject.setTextSkew(this, objT, "fontStyle");			
	  		//set Back
	  		GradientDrawable gb =ClassObject.getGradientDrawable(objT);
	  		if(gb!=null)this.setBackgroundDrawable(gb);	    	
	  		//set Alignment style
	  		ClassObject.setGravity(this, objT, "alignment");
	  		//add View
	  		frame.addView(this, ClassObject.getParams(ClassObject.getRect(objT)));
	  		//启用时间
	  		this.removeCallbacks(this);
            this.post(this);
	  	} catch (Exception e) {
	  		MyLog.e(this.actMain.getString(R.string.app_key), "IRTimeLabel.initData:"+e.toString());
		}
	}
	//运行时钟控件
	@Override
    public void run() {
		if(this.actMain.getTaskId()>0){
			//更时钟
			this.setText(inroids.common.Convert.dateToString(new java.util.Date(), this.strTimeFormat)); 
			this.postDelayed(this, 1000);
			
		}else{
			this.removeCallbacks(this);
		}
	}
	
}
