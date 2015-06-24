package com.aragaer.yama;

import android.content.Context;
import android.view.KeyEvent;
import android.util.AttributeSet;
import android.widget.EditText;


public class MemoEdit extends EditText {

    public MemoEdit(Context context, AttributeSet attr) {
	super(context, attr);
    }

    public MemoEdit(Context context, AttributeSet attr, int defStyleAttr) {
	super(context, attr, defStyleAttr);
    }

    @Override public boolean onKeyPreIme(int keyCode, KeyEvent event) {
	if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
	    clearFocus();
	return super.dispatchKeyEvent(event);
    }
}
