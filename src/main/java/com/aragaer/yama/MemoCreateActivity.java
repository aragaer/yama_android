package com.aragaer.yama;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class MemoCreateActivity extends Activity {

    EditText memo;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.create);
	memo = (EditText) findViewById(R.id.new_memo_edit);
    }

    private static final int SAVE_BUTTON_ID = 0;
    private static final int DISCARD_BUTTON_ID = 1;

    MenuItem.OnMenuItemClickListener clickListener = new MenuItem.OnMenuItemClickListener() {
	    public boolean onMenuItemClick(MenuItem item) {
		if (item.getItemId() == SAVE_BUTTON_ID)
		    MemoCreateActivity.this.saveMemo();
		MemoCreateActivity.this.finish();
		return true;
	    }
	};

    @Override public void onBackPressed() {
	saveMemo();
	finish();
    }

    private void saveMemo() {
	MemoFile.write(this, memo.getText().toString().split("\n"));
    }

    private void createMenuItem(Menu menu, int itemId, int resId) {
	menu.add(Menu.NONE, itemId, Menu.NONE, resId)
	    .setOnMenuItemClickListener(clickListener)
	    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
	createMenuItem(menu, SAVE_BUTTON_ID, R.string.action_save);
	createMenuItem(menu, DISCARD_BUTTON_ID, R.string.action_discard);
	return true;
    }
}
