package com.aragaer.yama;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import static android.view.WindowManager.LayoutParams.*;


public class ClickMeActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	TextView view = new TextView(this);
	view.setText("Are we locked now?");

	setContentView(view);

	getWindow().addFlags(FLAG_SHOW_WHEN_LOCKED);
    }
}
