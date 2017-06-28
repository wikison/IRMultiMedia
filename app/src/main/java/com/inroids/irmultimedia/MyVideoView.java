package com.inroids.irmultimedia;

import android.content.Context;
import android.widget.VideoView;

import inroids.common.MyLog;

public class MyVideoView  extends VideoView {
	private static final String TAG="IRMultimedia";
	public MyVideoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	 }
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try{
			setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		} catch (Exception e) {
			MyLog.e(TAG, "MyVideoView.onMeasure:"+e.toString());
		}  
	}
}
