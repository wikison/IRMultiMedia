//ClassObject.java
//Created by sealy on 2013-06-01.  
//Copyright 2013 Sealy, Inc. All rights reserved.

package com.inroids.irmultimedia;

import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import inroids.common.MyLog;

public abstract class ClassObject {
	private static final String TAG="AdForInroids";
	
	//获取区域大小
	/**
	 * get rectangle
	 * @param objT a JSON Object.
	 */
	public static Rect getRect(JSONObject objT){
		return getRect(objT,"x","y","w","h");
    }
	
	/**
	 * get rectangle
	 * @param objT a JSON Object.
	 */
	public static Rect getRect(JSONObject objT,String... params){
    	int x=0,y=0,w=0,h=0;
    	try {
    		if(params.length>0)
    			x=getInt(objT,params[0]);
    		if(params.length>1)
    			y=getInt(objT,params[1]);
    		if(params.length>2)
    			w=getInt(objT,params[2]);
    		if(params.length>3)
    			h=getInt(objT,params[3]);
    	} catch (Exception e) {
    		MyLog.e(TAG, "ClassObject.getRect:"+e.toString());
		}
		return new Rect(x,y,w+x,y+h);
    }
		
	/**
	 * get Params
	 * @param aRect a JSON Object.
	 */
	public static LayoutParams getParams(Rect aRect){
		LayoutParams params = new LayoutParams(aRect.right-aRect.left,aRect.bottom-aRect.top);
		params.setMargins(aRect.left, aRect.top, aRect.right, aRect.bottom);
		return params;
	}
	
	/**
	 * get Gravity
	 * @param objT a JSON Object.
	 */
	public static int getGravity(JSONObject objT,String strKey){
    	int intResult=Gravity.CENTER;
    	try {
    		String strAlignment=getString(objT,strKey);    		
    		if (inroids.common.System.isNotNullString(strAlignment)){    			
    			if(strAlignment.equals("tl")){
    				intResult=Gravity.TOP|Gravity.LEFT;
    			}else if(strAlignment.equals("tm")){
    				intResult=Gravity.TOP|Gravity.CENTER_HORIZONTAL;
    			}else if(strAlignment.equals("tr")){
    				intResult=Gravity.TOP|Gravity.RIGHT;
    			}else if(strAlignment.equals("ml")){
    				intResult=Gravity.CENTER_VERTICAL|Gravity.LEFT;
    			}else if(strAlignment.equals("mm")){
    				intResult=Gravity.CENTER;
    			}else if(strAlignment.equals("mr")){
    				intResult=Gravity.CENTER_VERTICAL|Gravity.RIGHT;
    			}else if(strAlignment.equals("bl")){
    				intResult=Gravity.BOTTOM|Gravity.LEFT;
    			}else if(strAlignment.equals("bm")){
    				intResult=Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
    			}else if(strAlignment.equals("br")){
    				intResult=Gravity.BOTTOM|Gravity.RIGHT;
    			}else if(strAlignment.equals("left")){
    				intResult=Gravity.CENTER_VERTICAL|Gravity.LEFT;
    			}else if(strAlignment.equals("right")){
    				intResult=Gravity.CENTER_VERTICAL|Gravity.RIGHT;
    			}else{
    				intResult=Gravity.CENTER;
    			}
    		}
    	} catch (Exception e) {
    		MyLog.e(TAG, "ClassObject.getGravity:"+e.toString());
		}
    	return intResult;
    }
	
	public static int getGravity(JSONObject objT){
		return getGravity(objT,"alignment");
	}
	//set text Gravity
	public static void setGravity(TextView txtT,JSONObject objT,String sKey){
		txtT.setGravity( getGravity(objT,sKey));
	}
	/**
	 * set text Color
	 * @param txtT TextView object
	 * @param objT JSON Object
	 * @param sKey JSON Object Key
	 */
	public static boolean setTextColor(TextView txtT,JSONObject objT,String sKey){
		int intT=ClassObject.getColor(objT,sKey);
		if (intT<0)txtT.setTextColor(intT);
		return intT<0;
	}
	/**
	 * set font Size
	 * @param txtT TextView object
	 * @param objT JSON Object
	 * @param sKey JSON Object Key
	 */
	public static boolean setTextSize(TextView txtT,JSONObject objT,String sKey){
		int iFontSize=ClassObject.getFontSize(objT, sKey);
    	if(iFontSize>0) txtT.setTextSize(iFontSize);	
		return iFontSize>0;
	}

	/**
	 * set text bold
	 * @param txtT TextView object
	 * @param objT JSON Object
	 * @param sKey JSON Object Key
	 */
	public static boolean setFakeBoldText(TextView txtT,JSONObject objT,String sKey){
		int intT=ClassObject.getInt(objT,sKey);
		if (intT==1)txtT.getPaint().setFakeBoldText(true);
		return intT==1;
	}
	
	/**
	 * set text Skew
	 * @param txtT TextView object
	 * @param objT JSON Object
	 * @param sKey JSON Object Key
	 */
	public static boolean setTextSkew(TextView txtT,JSONObject objT,String sKey){
		int intT=ClassObject.getInt(objT,sKey);
		if (intT==1)txtT.getPaint().setTextSkewX(-0.5f);
		return intT==1;
	}
			
	/**
	 * get GradientDrawable("bgColor","radian")
	 * @param objT a JSON Object.
	 */
	public static GradientDrawable getGradientDrawable(JSONObject objT){
		return getGradientDrawable(objT,"bgColor","radian");
	}
	
	/**
	 * get GradientDrawable("bgColor","radian")
	 * @param objT a JSON Object.
	 * @param sBgColor a key string with bgColor
	 * @param sRadian a key string with Radian
	 */
	public static GradientDrawable getGradientDrawable(JSONObject objT,String sBgColor,String sRadian){
		GradientDrawable gd=null;
		int intRadian=0;
		try {
			//Background Color
			int intBgColor=getColor(objT,sBgColor);
	    	//set background color
	    	if (intBgColor<0){
	    		intRadian=getInt(objT,sRadian);
	    		gd = new GradientDrawable();
	    		gd.setColor(intBgColor);
				if(intRadian>0)
					gd.setCornerRadii(new float[] { intRadian, intRadian, intRadian, intRadian,intRadian, intRadian, intRadian, intRadian });
			}
		} catch (Exception e) {
			MyLog.e(TAG, "ClassObject.getGradientDrawable:"+e.toString());
		}
    	return gd;
	}
	
	/**
	 * get string value
	 * @param objT a JSON Object.
	 * @param sKey a key string
	 */
	public static String getString(JSONObject objT,String sKey){
		try {
			//set font size
		    if (!objT.isNull(sKey) && objT.getString(sKey).length()>0){
		    	return objT.getString(sKey);
		    }
		} catch (JSONException e) {
			MyLog.e(TAG, "ClassObject.getString:"+e.toString()+" key:"+sKey);
		}
		return null;
	}
	
	/**
	 * get integer value    
	 * @param objT a JSON Object.
	 * @param sKey a key string
	 */
	public static int getInt(JSONObject objT,String sKey){
		try {
			//Check Key is NULL
		    if (!objT.isNull(sKey)){
		    	String strT=getString(objT,sKey);
		    	if(inroids.common.System.isNotNullString(strT)){
		    		return objT.getInt(sKey);
		    	}
		    }
		} catch (Exception e) {
			MyLog.e(TAG, "ClassObject.getInt:"+e.toString()+" key:"+sKey);
		}
		return -1;
	}
	
	/**
	 * get Color value
	 * @param objT a JSON Object.
	 * @param sKey a key string
	 */
	public static int getColor(JSONObject objT,String sKey){
		try {
		    if (!objT.isNull(sKey) && objT.getString(sKey).length()>0){
		    	String sColor= objT.getString(sKey);
		    	if(inroids.common.System.isNotNullString(sColor)){
		    		return inroids.common.Convert.stringToColor(objT.getString(sKey));
		    	}
		    }
		} catch (JSONException e) {
			MyLog.e(TAG, "ClassObject.getColor:"+e.toString()+" key:"+sKey);
		}
		return 0;
	}
	
	/**
	 * get ture or false
	 * @param objT a JSON Object.
	 * @param sKey a key string
	 */
	public static boolean getBoolean(JSONObject objT,String sKey){
		try {
		    if (!objT.isNull(sKey)&& objT.getString(sKey).length()>0){
		    	int i=objT.getInt(sKey);		    	
		    	return i!=0;
		    }
		} catch (JSONException e) {
			MyLog.e(TAG, "ClassObject.getBoolean:"+e.toString()+" key:"+sKey);
		}
		return false;
	}
	/**
	 * get Font Size
	 * @param objT a JSON Object.
	 * @param sKey a key string
	 */
	public static int getFontSize(JSONObject objT,String sKey){
		try {
			if (!objT.isNull(sKey) && objT.getString(sKey).length()>0){
    			return Integer.valueOf(objT.getString(sKey).replace("px", ""));
    		}
		} catch (JSONException e) {
			MyLog.e(TAG, "ClassObject.getFontSize:"+e.toString()+" key:"+sKey);
		}
		return 17;
	}
}
