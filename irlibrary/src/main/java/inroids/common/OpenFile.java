/*
 * open file(
 * htm\png\gif\jpg\bmp
 * 
 * Created by sealy on 2013-07-01. 
 * Copyright 2013 Sealy, Inc. All rights reserved.
 */
package inroids.common;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public abstract class OpenFile {
	private static final String sTag="IRLibrary";
	
	/**
	 * open HTML file
	 * @param context using a context for this function 
	 * @param sFilePath a file path(.htm/.html/.php/.jsp)
	 */
	public static boolean openHtmlFile(Context context,String sFilePath){
		try{
			File filT=new File(sFilePath);
			if (filT.exists()) {
				Uri uri = Uri.parse(sFilePath).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(sFilePath).build();
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.setDataAndType(uri, "text/html");
				context.startActivity(intent);
				return true;
			}else{
				MyLog.w(sTag, "OpenFile.openHtmlFile:"+sFilePath+" is't exists!");
			}
		}catch (Exception e) {
			MyLog.e(sTag, "OpenFile.openHtmlFile:"+e.toString());
	    }
		return false;
	}
		
	/**
	 * open Image file
	 * @param context using a context for this function 
	 * @param sFilePath a file path(.png/.gif/.jpg/.jpeg/.bmp)
	 */
	public static boolean openImageFile(Context context,String sFilePath){
		try{
			File filT=new File(sFilePath);
			if (filT.exists()) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(filT), "image/*");
				context.startActivity(intent);
				return true;
			}else{
				MyLog.w(sTag, "OpenFile.openImageFile:"+sFilePath+" is't exists!");
			}
		}catch (Exception e) {
			MyLog.e(sTag, "OpenFile.openImageFile:"+e.toString());
	    }
		return false;
	}
	
	/**
	 * open PDF file
	 * @param context using a context for this function 
	 * @param sFilePath a file path(.pdf)
	 */
	public static boolean openPdfFile(Context context,String sFilePath){
		try{
			File filT=new File(sFilePath);
			if (filT.exists()) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(filT), "application/pdf");
				context.startActivity(intent);
				return true;
			}else{
				MyLog.w(sTag, "OpenFile.openPdfFile:"+sFilePath+" is't exists!");
			}
		}catch (Exception e) {
			MyLog.e(sTag, "OpenFile.openPdfFile:"+e.toString());
	    }
		return false;
	}
	
	/**
	 * open Text file
	 * @param context using a context for this function 
	 * @param sFilePath a file path(.txt/.java/.c/.cpp/.py/.xml/.json/.log)
	 */
	public static boolean openTextFile(Context context,String sFilePath){
		try{
			File filT=new File(sFilePath);
			if (filT.exists()) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(filT), "text/plain");
				context.startActivity(intent);
				return true;
			}else{
				MyLog.w(sTag, "OpenFile.openTextFile:"+sFilePath+" is't exists!");
			}
		}catch (Exception e) {
			MyLog.e(sTag, "OpenFile.openTextFile:"+e.toString());
	    }
		return false;
	}
	
	/**
	 * open Audio file
	 * @param context using a context for this function 
	 * @param sFilePath a file path(.mp3/.wav/.ogg/.midi)
	 */
	public static boolean openAudioFile(Context context,String sFilePath){
		try{
			File filT=new File(sFilePath);
			if (filT.exists()) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("oneshot", 0);
				intent.putExtra("configchange", 0);
				intent.setDataAndType(Uri.fromFile(filT), "audio/*");
				context.startActivity(intent);
				return true;
			}else{
				MyLog.w(sTag, "OpenFile.openAudioFile:"+sFilePath+" is't exists!");
			}
		}catch (Exception e) {
			MyLog.e(sTag, "OpenFile.openAudioFile:"+e.toString());
	    }
		return false;
	}

	/**
	 * open Video file
	 * @param context using a context for this function 
	 * @param sFilePath a file path(.mp4/.rmvb/.avi/.flv)
	 */
	public static boolean openVideoFile(Context context,String sFilePath){
		try{
			File filT=new File(sFilePath);
			if (filT.exists()) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("oneshot", 0);
				intent.putExtra("configchange", 0);
				intent.setDataAndType(Uri.fromFile(filT), "video/*");
				context.startActivity(intent);
				return true;
			}else{
				MyLog.w(sTag, "OpenFile.openVideoFile:"+sFilePath+" is't exists!");
			}
		}catch (Exception e) {
			MyLog.e(sTag, "OpenFile.openVideoFile:"+e.toString());
	    }
		return false;
	}
	
	/**
	 * open CHM file
	 * @param context using a context for this function 
	 * @param sFilePath a file path(.chm)
	 */
	public static boolean openChmFile(Context context,String sFilePath){
		try{
			File filT=new File(sFilePath);
			if (filT.exists()) {
				Intent intent = new Intent("android.intent.action.VIEW");
				 intent.addCategory("android.intent.category.DEFAULT");
				 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 intent.setDataAndType(Uri.fromFile(filT), "application/x-chm");
				 context.startActivity(intent);
				 return true;
			}else{
				MyLog.w(sTag, "OpenFile.openChmFile:"+sFilePath+" is't exists!");
			}
		}catch (Exception e) {
			MyLog.e(sTag, "OpenFile.openChmFile:"+e.toString());
	    }
		return false;
	}
	
	/**
	 * open Word file
	 * @param context using a context for this function
	 * @param sFilePath a file path(.doc/.docx)
	 */
	public static boolean openWordFile(Context context,String sFilePath){
		try{
			File filT=new File(sFilePath);
			if (filT.exists()) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(filT), "application/msword");
				context.startActivity(intent);
				return true;
			}else{
				MyLog.w(sTag, "OpenFile.openWordFile:"+sFilePath+" is't exists!");
			}
		}catch (Exception e) {
			MyLog.e(sTag, "OpenFile.openWordFile:"+e.toString());
	    }
		return false;
	}
	
	/**
	 * open Excel file
	 * @param context using a context for this function
	 * @param sFilePath a file path(.xls/.xlsx)
	 */
	public static boolean openExcelFile(Context context,String sFilePath){
		try{
			File filT=new File(sFilePath);
			if (filT.exists()) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(filT), "application/vnd.ms-excel");
				context.startActivity(intent);
				return true;
			}else{
				MyLog.w(sTag, "OpenFile.openExcelFile:"+sFilePath+" is't exists!");
			}
		}catch (Exception e) {
			MyLog.e(sTag, "OpenFile.openExcelFile:"+e.toString());
	    }
		return false;
	}
	
	/**
	 * open PPT file
	 * @param context using a context for this function
	 * @param sFilePath a file path(.ppt/.pptx)
	 */
	public static boolean openPPTFile(Context context,String sFilePath){
		try{
			File filT=new File(sFilePath);
			if (filT.exists()) {
				Intent intent = new Intent("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(filT), "application/vnd.ms-powerpoint");
				context.startActivity(intent);
				return true;
			}else{
				MyLog.w(sTag, "OpenFile.openPPTFile:"+sFilePath+" is't exists!");
			}
		}catch (Exception e) {
			MyLog.e(sTag, "OpenFile.openPPTFile:"+e.toString());
	    }
		return false;
	}
	
	/**
	 * open apk file
	 * @param context using a context for this function
	 * @param sFilePath a file path(.apk)
	 */
	public static boolean openApkFile(Context context,String sFilePath){
		try{
			File filT=new File(sFilePath);
			if (filT.exists()) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(filT), "application/vnd.android.package-archive");
				context.startActivity(intent);
				return true;
			}else{
				MyLog.w(sTag, "OpenFile.openApkFile:"+sFilePath+" is't exists!");
			}
		}catch (Exception e) {
			MyLog.e(sTag, "OpenFile.openApkFile:"+e.toString());
	    }
		return false;
	}
	
	/**
	 * open autoOpen file
	 * @param context using a context for this function
	 * @param sFilePath a file path
	 */
	public static boolean autoOpenFile(Context context,String sFilePath){
		String strFile=sFilePath.toLowerCase();
		if(strFile.endsWith(".htm")||strFile.endsWith(".html")||strFile.endsWith(".php")||strFile.endsWith(".jsp")){
			return openHtmlFile(context,sFilePath);
		}else if(strFile.endsWith(".png")||strFile.endsWith(".gif")||strFile.endsWith(".jpg")||strFile.endsWith(".jpeg")||strFile.endsWith(".bmp")){
			return openImageFile(context,sFilePath);
		}else if(strFile.endsWith(".pdf")){
			return openPdfFile(context,sFilePath);
		}else if(strFile.endsWith(".txt")||strFile.endsWith(".java")||strFile.endsWith(".c")||strFile.endsWith(".cpp")||strFile.endsWith(".py")||strFile.endsWith(".xml")||strFile.endsWith(".json")||strFile.endsWith(".log")){
			return openTextFile(context,sFilePath);
		}else if(strFile.endsWith(".mp3")||strFile.endsWith(".wav")||strFile.endsWith(".ogg")||strFile.endsWith(".midi")){
			return openAudioFile(context,sFilePath);
		}else if(strFile.endsWith(".mp4")||strFile.endsWith(".rmvb")||strFile.endsWith(".avi")||strFile.endsWith(".flv")){
			return openVideoFile(context,sFilePath);
		}else if(strFile.endsWith(".chm")){
			return openChmFile(context,sFilePath);
		}else if(strFile.endsWith(".doc")||strFile.endsWith(".docx")){
			return openWordFile(context,sFilePath);
		}else if(strFile.endsWith(".xls")||strFile.endsWith(".xlsx")){
			return openExcelFile(context,sFilePath);
		}else if(strFile.endsWith(".ppt")||strFile.endsWith(".pptx")){
			return openPPTFile(context,sFilePath);
		}else if(strFile.endsWith(".apk")){
			return openApkFile(context,sFilePath);
		}else {
			MyLog.w(sTag, "OpenFile.autoOpenFile:"+sFilePath+" is't opened!");
		}
		return false;
	}
}
