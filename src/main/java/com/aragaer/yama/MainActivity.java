package com.aragaer.yama;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	TextView view = new TextView(this);
	view.setText("Hello, world");

	setContentView(view);
    }
}
