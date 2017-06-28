//ViewText.java
//Created by sealy on 2013-06-01.  
//Copyright 2013 Sealy, Inc. All rights reserved.
package com.inroids.irmultimedia;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONObject;

import inroids.common.MyLog;

public class IRText extends TextView {
    private static final String TAG = "IRMultimedia";

    // Create Text Control----------------------
    public IRText(Context context) {
        super(context);
    }

    public void initData(FrameLayout frame, JSONObject objT) {
        try {
            // set text
            this.setText(objT.getString("content"));
            // TextColor
            ClassObject.setTextColor(this, objT, "textColor");
            // set font size
            ClassObject.setTextSize(this, objT, "fontSize");
            // set font bold
            ClassObject.setFakeBoldText(this, objT, "fontWeight");
            // set font style
            ClassObject.setTextSkew(this, objT, "fontStyle");
            // set Back
            GradientDrawable gb = ClassObject.getGradientDrawable(objT);
            if (gb != null)
                this.setBackgroundDrawable(gb);
            // set Alignment style
            ClassObject.setGravity(this, objT, "alignment");
            // add content View
            frame.addView(this, ClassObject.getParams(ClassObject.getRect(objT)));
        } catch (Exception e) {
            MyLog.e(TAG, "IRText.initData:" + e.toString());
        }
    }
}
