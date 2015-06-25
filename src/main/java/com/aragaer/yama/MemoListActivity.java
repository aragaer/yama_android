package com.aragaer.yama;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


public class MemoListActivity extends Activity {

    private MemoListFragment listFragment;
    private MemoHandler memoKeeper;

    @Override protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	MemoFileProvider fileProvider = new AndroidFileProvider(this);
	memoKeeper = new MemoHandler(new MemoStorage(fileProvider));

	listFragment = (MemoListFragment) getFragmentManager().findFragmentById(android.R.id.content);
	if (listFragment == null) {
	    listFragment = new MemoListFragment();
	    getFragmentManager()
		.beginTransaction()
		.add(android.R.id.content, listFragment)
		.commit();
	}
    }

    protected void onResume() {
	memoKeeper.updateFromStorage();
	updateFromKeeper();
	super.onResume();
    }

    private void updateFromKeeper() {
	listFragment.setContents(memoKeeper.getAllActiveMemos());
    }

    public void applyEdit(Memo editedMemo, String newText) {
	memoKeeper.replaceMemo(editedMemo, MemoProcessor.sanitize(newText));
	memoKeeper.dumpToStorage();
	updateFromKeeper();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.new_memo_btn:
	    createNew();
	    return true;
	default:
	    return false;
	}
    }

    private void createNew() {
	startActivity(new Intent(this, MemoCreateActivity.class));
    }
}
