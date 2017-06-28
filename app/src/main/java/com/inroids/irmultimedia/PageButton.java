package com.inroids.irmultimedia;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import java.io.InputStream;

import inroids.common.MyLog;

public class PageButton extends ImageView implements OnTouchListener {
	private static final String TAG="IRMultimedia";
	private MultiPlayActivity actMain=null;
	private GestureDetector gd;
	private OnMyEvent mListener=null;
    
	/**
     * 设置具体点击监听器
     * @param listener 点击监听器实现类
     */
    public void setOnMyEventListener(OnMyEvent listener) {
        this.mListener = listener;
    }
    
	//Create Image Control----------------------
	public PageButton(Context context) {
		super(context);
		//Main Activity
    	this.actMain=(MultiPlayActivity)context;
		//Touch Event
		this.setOnTouchListener(this);
	 }
	
	
	//get bitmap from assets
	private Bitmap getImageFromAssetsFile(String fileName){  
	      Bitmap image = null;  
	      AssetManager am = getResources().getAssets();  
	      try  {  
	          InputStream is = am.open(fileName);  
	          image = BitmapFactory.decodeStream(is);  
	          is.close();  
	      }  
	      catch (Exception e)  {  
	    	  MyLog.e(TAG,"PageButton.getImageFromAssetsFile:"+e.toString());
	      }
	      return image;
	}  
	
	//init data
	public void initData(String fileName){
  		try {
  			this.setImageBitmap(this.getImageFromAssetsFile(fileName));
			this.setScaleType(ScaleType.FIT_XY);
			this.gd = new GestureDetector(this.getContext(),this.gdS);
			this.gd.setIsLongpressEnabled(false);
  		} catch (Exception e) {
  			MyLog.e(TAG,"ViewPageButton.initData:"+e.toString());
  		}  		
  	}
	
	
	//Event------------------------------------------
	//touch down event
	private void touchDown(){
		try {
			this.setAlpha(64);
		} catch (Exception e) {
			MyLog.e(TAG,"ViewPageButton.touchDown:"+e.toString());
		}
	}
	//touch up event
	private void touchUp(){
		try {
			Thread.sleep(250);
			this.setAlpha(255);
		} catch (Exception e) {
			MyLog.e(TAG,"ViewPageButton.touchUp:"+e.toString());
		}
	}
	//Touch Event	
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		this.actMain.clearCurPos();	 
		if(this.actMain.isCanTouch){
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				this.touchDown();
			}
			
			if(event.getAction()==MotionEvent.ACTION_CANCEL){
				this.touchUp();
			}
			
			if(event.getAction()==MotionEvent.ACTION_UP){
				this.touchUp();
			}	
			if(this.gd!=null){
				if(this.gd.onTouchEvent(event))
				    return true;
			}
		}
		return false;
	}
	
	//页码单击	
	GestureDetector.SimpleOnGestureListener gdS=new  GestureDetector.SimpleOnGestureListener(){	 	
        @Override
        public boolean onDown(MotionEvent e) {return true;}
        
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
        	if(PageButton.this.mListener!=null){
        		PageButton.this.mListener.onMy(null,0);//单击
        	}
            return false;
        }
	};
}
