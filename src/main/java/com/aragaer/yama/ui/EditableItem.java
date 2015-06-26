package com.aragaer.yama.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.aragaer.yama.Memo;
import com.aragaer.yama.R;


public class EditableItem extends RelativeLayout {

    private final EditText input;
    private Memo memo;

    public EditableItem(Context context, View.OnFocusChangeListener focusChangeListener) {
	super(context);
	View.inflate(context, R.layout.item, this);
	input = (EditText) findViewById(R.id.text);
	input.setOnFocusChangeListener(focusChangeListener);
    }

    static public EditableItem getFromInput(View v) {
	return (EditableItem) v.getParent();
    }

    public void setMemo(Memo newMemo) {
	memo = newMemo;
	input.setText(memo.getText());
    }

    public String getText() {
	return input.getText().toString();
    }

    public Memo getMemo() {
	return memo;
    }
}
