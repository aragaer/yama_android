package com.aragaer.yama;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenOnReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
	Log.i("YAMA", "Yo");
	context.startActivity(new Intent(context,
					 ClickMeActivity.class)
			      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_NO_HISTORY
					| Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }
}
