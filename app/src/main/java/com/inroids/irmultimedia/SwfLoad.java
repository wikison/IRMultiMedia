package com.inroids.irmultimedia;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import inroids.common.MyLog;

public class SwfLoad extends android.widget.FrameLayout {
	private static final String TAG="IRMultimedia";
	private Activity actMain=null;
	private Timer tmrMain=null; 
	private VideoView vidMain=null;
	private int intCurPos=0;
	private OnMyEvent mListener=null;
	private String strCurFile;
	//Create this control------------------------
	public SwfLoad(Context context) {
		super(context);
		if(this.actMain==null)
			this.actMain=(Activity)context;	
	}
	//初始化控件
	public void initData(Rect rctTemp,String sFile){
		try{
			this.intCurPos=0;
			this.strCurFile=sFile;
			this.vidMain=new VideoView(this.actMain);
			this.vidMain.setVideoPath(sFile);
		   	this.vidMain.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
		   	this.vidMain.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
//		   	this.vidMain.intVideoWidth=rctTemp.width();
//		   	this.vidMain.intVideoHeight=rctTemp.height();
		   	this.vidMain.start();	
		    this.addView(this.vidMain,0);
		    this.startTimer();//start timer
		} catch (Exception e) {
			MyLog.e(TAG,"SwfLoad.initData:"+e.toString()); 
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
			        public void run() {//release memory        	
			            if(SwfLoad.this.actMain.getTaskId()<0){
			            	SwfLoad.this.tmrMain.cancel();
			            	SwfLoad.this.tmrMain=null;
			            }
			            else{
			            	Message message = new Message();
			            	message.what = 1;
			            	SwfLoad.this.hndDuration.sendMessage(message);
			            }
			        }
			    }, 0, 1000);
			}
		} catch (Exception e) {
			MyLog.e(TAG,"SwfLoad.startTimer:"+e.toString());
		}
	}
		
	//check play status
    Handler hndDuration = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	try{        	
	        	if(SwfLoad.this.intCurPos>5){            			
	        		if(!SwfLoad.this.vidMain.isPlaying()){
	        			//finish play release timer
	        			if(SwfLoad.this.tmrMain!=null){
	        				SwfLoad.this.tmrMain.cancel();
	        				SwfLoad.this.tmrMain=null;
	            		}        		
	            		if(SwfLoad.this.mListener!=null){
	            			SwfLoad.this.mListener.onMy(null,1);//finish play
	    				}
	            	}
	    		}else{
	    			SwfLoad.this.intCurPos++;
	    			if(SwfLoad.this.intCurPos==1){
		        		if(SwfLoad.this.mListener!=null){
		        			SwfLoad.this.mListener.onMy(null,0);//finish load
						}
		        	}
	    		}
        	} catch (Exception e) {
    			MyLog.e(TAG,"SwfLoad.startTimer:"+e.toString());
    		}
            super.handleMessage(msg);
        }
    };  

    //单个视频重播
    public void replay(){
    	this.intCurPos=0;
    	this.vidMain.setVideoPath(this.strCurFile);
       	this.vidMain.start();
       	this.startTimer();
    }
}
