package com.aragaer.yama;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	TextView view = new TextView(this);
	view.setText("Hello, world");

	setContentView(view);

	IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
	getApplicationContext().registerReceiver(new ScreenOnReceiver(), filter);
    }
}
