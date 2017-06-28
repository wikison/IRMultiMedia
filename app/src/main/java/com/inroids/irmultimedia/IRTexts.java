//ViewTexts.java
//Created by sealy on 2013-06-01.  
//Copyright 2013 Sealy, Inc. All rights reserved.
package com.inroids.irmultimedia;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import inroids.common.MyLog;

public class IRTexts extends ScrollView {
    private static final String TAG = "IRMultimedia";

    private MultiPlayActivity actMain = null;

    // Create Text Control----------------------
    public IRTexts(Context context) {
        super(context);
        // Main Activity
        this.actMain = (MultiPlayActivity)context;
        // Touch Event
        this.setOnTouchListener(this.actMain);
    }

    public void initData(FrameLayout frame, JSONObject objT) {
        try {
            // Rect rctT=ClassObject.getRect(objT);
            // add Text View
            TextView vwText = new TextView(this.actMain);
            this.addView(vwText);
            // set text
            vwText.setText(objT.getString("content").replace("<br>", "\n"));
            // TextColor
            ClassObject.setTextColor(vwText, objT, "textColor");

            // set font size
            ClassObject.setTextSize(vwText, objT, "fontSize");

            // set font bold
            ClassObject.setFakeBoldText(vwText, objT, "fontWeight");

            // set font style
            ClassObject.setTextSkew(vwText, objT, "fontStyle");

            // set Back
            GradientDrawable gb = ClassObject.getGradientDrawable(objT);
            if (gb != null)
                this.setBackgroundDrawable(gb);
            // set Alignment style
            ClassObject.setGravity(vwText, objT, "alignment");

            // add content View
            frame.addView(this, ClassObject.getParams(ClassObject.getRect(objT)));
        } catch (Exception e) {
            MyLog.e(TAG, "IRTexts.initData:" + e.toString());
        }
    }
}
