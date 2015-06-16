package com.aragaer.yama;

import java.util.List;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class MemoCreateActivity extends ActionBarActivity {

    EditText memo;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.create);
	setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
	memo = (EditText) findViewById(R.id.new_memo_edit);
    }

    private static final int SAVE_BUTTON_ID = 0;
    private static final int DISCARD_BUTTON_ID = 1;

    MenuItem.OnMenuItemClickListener clickListener = new MenuItem.OnMenuItemClickListener() {
	    public boolean onMenuItemClick(MenuItem item) {
		if (item.getItemId() == SAVE_BUTTON_ID)
		    MemoCreateActivity.this.saveMemo();
		MemoCreateActivity.this.exitToList();
		return true;
	    }
	};

    @Override public void onBackPressed() {
	saveMemo();
	exitToList();
    }

    private void exitToList() {
	finish();
	KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
	if (!km.inKeyguardRestrictedInputMode())
	    startActivity(new Intent(this, MemoListActivity.class));
    }

    private void saveMemo() {
	List<String> lines = MemoProcessor.sanitize(memo.getText().toString().split("\n"));
	if (lines.isEmpty())
	    return;
	MemoFileProvider fileProvider = new AndroidFileProvider(this);
	MemoReaderWriter readerWriter = new PlainReaderWriter(fileProvider);
	MemoStorage storage = new MemoStorage(readerWriter);
	storage.updateFromReaderWriter();
	for (String line : lines)
	    storage.storeMemo(line);
	storage.dumpToReaderWriter();
	Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
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
