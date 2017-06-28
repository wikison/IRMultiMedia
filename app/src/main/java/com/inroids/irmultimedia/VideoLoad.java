package com.inroids.irmultimedia;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

import inroids.common.MyLog;

public class VideoLoad extends android.widget.FrameLayout {
	private static final String TAG="IRMultimedia";
	private Activity actMain=null;
	private Timer tmrMain=null; 
	private MyVideoView vidMain=null;
	private int intCurPos=0;
	private OnMyEvent mListener=null;
	private String strCurFile;
	
	//Create this control------------------------
	public VideoLoad(Context context) {
		super(context);
		if(this.actMain==null)
			this.actMain=(Activity)context;	
	}
	
	//init data with file
	public void initData(Rect rctTemp,String sFile){
		try{
			this.strCurFile=sFile;
			this.vidMain=new MyVideoView(this.actMain);
			this.vidMain.setVideoPath(sFile);
	       	this.vidMain.start();
	       	this.addView(this.vidMain,0);
	    	this.startTimer();//start timer
		} catch (Exception e) {
			MyLog.e(TAG,"VideoLoad.initData:"+e.toString());
		}
	}
	//set listener
	public void setOnMyListener(OnMyEvent listener) {
        this.mListener = listener;
    }
	//start timer
	private void startTimer(){
		try{
			if(this.tmrMain==null){
				this.tmrMain= new Timer();
	    		//time update
				this.tmrMain.schedule(new TimerTask() {           
			        @Override
			        public void run() {		        	
			            if(VideoLoad.this.actMain.getTaskId()<0){
			            	VideoLoad.this.tmrMain.cancel();
			            	VideoLoad.this.tmrMain=null;
			            }
			            else{
			            	Message message = new Message();
			            	message.what = 1;
			            	VideoLoad.this.hndDuration.sendMessage(message);
			            }
			        }
			    }, 0, 1000);
			}
		} catch (Exception e) {
			MyLog.e(TAG,"VideoLoad.startTimer:"+e.toString());
		}
	}
	//check play status
    Handler hndDuration = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	try{
        		//finish:1,load:0, failed:-1
	        	//delayed 3s check video state
	        	if(VideoLoad.this.intCurPos>4){            			
	        		if(!VideoLoad.this.vidMain.isPlaying()){	        			
	        			//finish play release timer
	        			if(VideoLoad.this.tmrMain!=null){
	            			VideoLoad.this.tmrMain.cancel();
	            			VideoLoad.this.tmrMain=null;
	            		}        		
	            		if(VideoLoad.this.mListener!=null){
	            			VideoLoad.this.mListener.onMy(null,1);//finish play
	    				}
	            	}
	    		}else{
	    			VideoLoad.this.intCurPos++;
	    			//load finish
	    			if(VideoLoad.this.intCurPos==1){
		        		if(VideoLoad.this.mListener!=null){
		        			VideoLoad.this.mListener.onMy(null,0);//finish load
						}
		        	}
	    		}
        	} catch (Exception e) {
    			MyLog.e(TAG,"VideoLoad.startTimer:"+e.toString());
    		}
            super.handleMessage(msg);
        }
    };  
    //视频重播
    public void replay(){
    	this.intCurPos=0;
    	this.vidMain.setVideoPath(this.strCurFile);
       	this.vidMain.start();
       	this.startTimer();
    }
}
