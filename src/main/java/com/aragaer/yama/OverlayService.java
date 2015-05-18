package com.aragaer.yama;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;


public class OverlayService extends Service implements View.OnTouchListener {

    @Override public IBinder onBind(Intent intent) {
	return null;
    }

    @Override public void onCreate() {
	super.onCreate();

	WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
	DisplayMetrics dimensions = new DisplayMetrics();
	wm.getDefaultDisplay().getMetrics(dimensions);

	View t = new View(this);
	t.setBackgroundColor(0xffffffff);
	t.setOnTouchListener(this);

	// There's a params builder, I should use it instead
	LayoutParams params = new LayoutParams(10, dimensions.heightPixels/3, LayoutParams.TYPE_SYSTEM_ERROR,
					       LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_SHOW_WHEN_LOCKED,
					       PixelFormat.OPAQUE);
	params.gravity = Gravity.LEFT | Gravity.TOP;
	params.x = 0;
	params.y = dimensions.heightPixels/3;
	wm.addView(t, params);
    }

    public boolean onTouch(View view, MotionEvent event) {
	return false;
    }
}
