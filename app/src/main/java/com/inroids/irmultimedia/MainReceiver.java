package com.inroids.irmultimedia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MainReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            try {
                if (inroids.common.AppManage.checkAppActivityState(context, "com.inroids.irmultimedia") < 0) {
                    // new Intent
                    Intent newIntent = new Intent(context, LoadingActivity.class);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(newIntent);
                }

            } catch (Exception e) {
                Log.e(context.getString(R.string.app_key), e.toString());
            }
        } else {
            android.widget.Toast.makeText(context, "Inroids multimedia fails to start!",
                android.widget.Toast.LENGTH_LONG).show();
        }
    }

}
