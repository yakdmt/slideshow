package com.stereo23.slideshow.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stereo23.slideshow.MainActivity;

/**
 * Created by Username on 09.09.2014.
 */
public class PowerConnected extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.ACTION_POWER_CONNECTED")) {
            intent = new Intent();
            intent.setClass(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
