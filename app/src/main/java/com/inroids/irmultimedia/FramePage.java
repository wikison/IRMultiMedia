package com.inroids.irmultimedia;


import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;
import android.widget.TextView;
import inroids.common.MyLog;

public class FramePage extends android.widget.FrameLayout{
	private static final String TAG="IRMultimedia";
	private PageButton btnLeft=null,btnRight=null;
	private TextView txtPage=null;
	private OnMyEvent mListener=null;
	/**
     * 设置具体点击监听器
     * @param listener 点击监听器实现类
     */
    public void setOnMyEventListener(OnMyEvent listener) {
        this.mListener = listener;
    }
    
	public FramePage(Context context, Rect rctTemp,JSONObject objT) {
		super(context);
		
		//		
		this.initData(context, rctTemp, objT, "buttonPosition","showPages");
	}
	public FramePage(Context context, Rect rctTemp,JSONObject objT,String... params) {
		super(context);
		this.initData(context, rctTemp, objT, params);
	}
	
	private void initData(Context context,Rect rctTemp,JSONObject objT,String... params){
		try{
			if(params.length==2){
				//page Control
				if(ClassObject.getBoolean(objT, params[1])){
					this.txtPage=new TextView(context);
					this.txtPage.setGravity(Gravity.CENTER);
					this.txtPage.setTextColor(Color.LTGRAY);
					this.txtPage.setTextSize(17);
					this.txtPage.getPaint().setFakeBoldText(true);					
					LayoutParams layParam = new LayoutParams(rctTemp.width(),20);
					layParam.setMargins(0,rctTemp.height()-20,rctTemp.width(),rctTemp.height());
					this.addView(this.txtPage, 0, layParam);
				}			
				//left +right button
				String strButtonPosition=ClassObject.getString(objT, params[0]);
				if(inroids.common.System.isNotNullString(strButtonPosition)){
					double dPosition=0;
					if(strButtonPosition.equals("1/2")){
						dPosition=0.5f;
					}else if(strButtonPosition.equals("1/4")){
						dPosition=0.25f;
					}else if(strButtonPosition.equals("3/4")){
						dPosition=0.75f;
					}else{
						dPosition=Double.valueOf(strButtonPosition);
					}
					
					//Log.e(sTag,"FramePage.initData :"+dPosition); 
					
					if(dPosition>0 && dPosition<1.0){						
						int intWH=rctTemp.width()>rctTemp.height()?rctTemp.height():rctTemp.width();
						int intHi=(int)(intWH*0.1);
						int intH=(int)(rctTemp.height()*dPosition);
						//add left 
						this.btnLeft=new PageButton(context);
						this.btnLeft.initData("ButtonLeft.png");
						this.btnLeft.setOnMyEventListener(new OnMyEvent(){							
							@Override
							public void onMy(Object objT, int iTag) {
								if(FramePage.this.mListener!=null){
									FramePage.this.mListener.onMy(null,0);
					        	}
							}
						});	
						this.addView(this.btnLeft, 0,  ClassObject.getParams(new Rect(2,intH-(int)(intHi*0.5),2+intHi,intH+(int)(intHi*0.5))));
						
						//add Right 
						this.btnRight=new PageButton(context);
						this.btnRight.initData("ButtonRight.png");
						this.btnRight.setOnMyEventListener(new OnMyEvent(){							
							@Override
							public void onMy(Object objT, int iTag) {
								if(FramePage.this.mListener!=null){
									FramePage.this.mListener.onMy(null,1);
					        	}
							}
						});	
						this.addView(this.btnRight, 0,  ClassObject.getParams(new Rect(rctTemp.width()-2-intHi,intH-(int)(intHi*0.5),rctTemp.width()-2,intH+(int)(intHi*0.5))));
					}
				}
				
			}else{
				MyLog.e(TAG,"FramePage.initData params format is error!"); 
			}
		} catch (Exception e) {
			MyLog.e(TAG,"FramePage.initData:"+e.toString()); 
		}
		
	}
	//
	public void setButtonVisibility(boolean isLeft,boolean isShow){
		if(isLeft && this.btnLeft!=null){
			if(isShow){
				this.btnLeft.setVisibility(0);
			}else{
				this.btnLeft.setVisibility(8);
			}
		}else if(this.btnRight!=null){
			if(isShow){
				this.btnRight.setVisibility(0);
			}else{
				this.btnRight.setVisibility(8);
			}
		}
	}
	//upate Page
	public void updatePage(int iCur,int intAll){
		try{
			if(this.txtPage!=null){
				String strT=" ";
	    		for(int i=0;i<intAll;i++){
	    			if(i==iCur)strT+=(iCur+1)+" ";
	    			else strT+="• ";
	    		}
	    		this.txtPage.setText(strT);  
			}
		} catch (Exception e) {
			MyLog.e(TAG,"FramePage.initData:"+e.toString()); 
		}		
    }
}
