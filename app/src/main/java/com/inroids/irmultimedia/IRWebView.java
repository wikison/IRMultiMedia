//IRWebView.java
//Created by sealy on 2013-06-01.  
//Copyright 2013 Sealy, Inc. All rights reserved.

package com.inroids.irmultimedia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import org.json.JSONObject;

import java.io.File;

import inroids.common.ClassObject;
import inroids.common.FileManage;
import inroids.common.Graphics;
import inroids.common.MyLog;

//{"type":"10","x":"62","y":"159","w":"573","h":"453","bgColor":"","content":"http://www.baidu.com","openFile":""}
public class IRWebView extends WebView {
    private MultiPlayActivity actMain;

    // Create--------------------------------------
    public IRWebView(Context context) {
        super(context);
        this.setTag(10);
        this.actMain = (MultiPlayActivity)context;
        this.setOnTouchListener(this.actMain);
    }

    // 解析JSON对象--------------------------------
    @SuppressLint("SetJavaScriptEnabled")
    public void initData(FrameLayout frame, JSONObject objT, String sRes) {
        try {
            String strUrl = null, strOpenFile = null;
            // Set Web property
            this.getSettings().setJavaScriptEnabled(true); // 启用JavaScript
            //this.getSettings().setPluginsEnabled(true); // 启用插件
            // open new Url in webview
            this.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

            });
            // 不缓存
            this.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            // set URL
            strUrl = ClassObject.getString(objT, "content");
            // set open File
            strOpenFile = ClassObject.getString(objT, "openFile");
            // set background color
            int intBgColor = ClassObject.getColor(objT, "bgColor");
            if (intBgColor < 0)
                this.setBackgroundColor(intBgColor);
            // 加载网页
            if (inroids.common.System.isNotNullString(strUrl)) {// Open URL Web
                this.loadUrl(strUrl);
                frame.addView(this, ClassObject.getParams(ClassObject.getRect(objT)));
            } else if (inroids.common.System.isNotNullString(strOpenFile)) {
                if (FileManage.isExistsFile(sRes + File.separator + strOpenFile)) {
                    // 加载本地网页
                    if (strOpenFile.endsWith("zip")) {// 网页包
                        this.findHtmlFileInZipFile(sRes, strOpenFile);
                    } else if (strOpenFile.endsWith("htm") || strOpenFile.endsWith("html")) {// 加载单个网页
                        this.loadUrl("file://" + sRes + File.separator + strOpenFile);
                    }
                    frame.addView(this, ClassObject.getParams(ClassObject.getRect(objT)));
                } else {
                    Graphics.addOverdueView(this.getContext(), frame, ClassObject.getRect(objT), "Overdue.png");
                }

            }
            // add Control

        } catch (Exception e) {
            MyLog.e(this.actMain.getString(R.string.app_key), "IRWebView.initData:" + e.toString());
        }
    }

    // 查找html文件中zip解压后的目录中
    private void findHtmlFileInZipFile(String sRes, String sOpenFile) {
        boolean hasIndex = false;
        String strFileName = "";
        // 获取zip解压后的目录
        String strFolder = sRes + File.separator + sOpenFile.replace(".zip", "");
        // search html/htm file
        File filT = new File(strFolder);
        if (filT.exists() && filT.isDirectory()) {// 目录存在
            File files[] = filT.listFiles(); // 查找目录中所有文件
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    strFileName = files[i].getName();
                    if (strFileName.equalsIgnoreCase("index.htm") || strFileName.equalsIgnoreCase("index.html")) {
                        hasIndex = true;
                        break;
                    }
                }
            }
            if (!hasIndex) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        strFileName = files[i].getName();
                        if (strFileName.endsWith(".htm") || strFileName.endsWith(".html")) {
                            hasIndex = false;
                            break;
                        }
                    }
                }
            }
        }
        if (inroids.common.System.isNotNullString(strFileName))
            this.loadUrl("file://" + strFolder + File.separator + strFileName);
    }

}
