package com.aragaer.yama;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


public class MemoListActivity extends Activity {

    @Override protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	MemoFileProvider fileProvider = new AndroidFileProvider(this);
	MemoHandler handler = new MemoHandler(new MemoStorage(fileProvider));

	MemoListFragment listFragment = (MemoListFragment) getFragmentManager().findFragmentById(android.R.id.content);
	if (listFragment == null) {
	    listFragment = new MemoListFragment();
	    getFragmentManager()
		.beginTransaction()
		.add(android.R.id.content, listFragment)
		.commit();
	}
	listFragment.setMemoHandler(handler);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == R.id.new_memo_btn) {
	    startActivity(new Intent(this, MemoCreateActivity.class));
	    return true;
	}
	return false;
    }
}
