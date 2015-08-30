package com.aragaer.yama;

import java.util.List;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MemoCreateActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	EditorFragment fragment = (EditorFragment) getFragmentManager().findFragmentById(android.R.id.content);
	if (fragment == null) {
	    fragment = new EditorFragment();
	    getFragmentManager()
		.beginTransaction()
		.add(android.R.id.content, fragment)
		.commit();
	}
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
	EditorFragment fragment = (EditorFragment) getFragmentManager().findFragmentById(android.R.id.content);
	List<Memo> memos = fragment.getContents();
	if (memos.isEmpty())
	    return;
	MemoFileProvider fileProvider = new AndroidFileProvider(this);
	MemoStorage storage = new MemoStorage(fileProvider);
	MemoHandler handler = new MemoHandler(storage);
	handler.updateFromStorage();
	for (Memo memo : memos)
	    handler.storeMemo(memo);
	handler.dumpToStorage();
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
