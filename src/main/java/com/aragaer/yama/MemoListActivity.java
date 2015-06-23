package com.aragaer.yama;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


public class MemoListActivity extends Activity {

    private List<Memo> memoList;
    private MemoListFragment listFragment;
    private MemoStorage storage;
    private MemoHandler memoKeeper;

    @Override protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	MemoFileProvider fileProvider = new AndroidFileProvider(this);
	storage = new MemoStorage(fileProvider);
	memoKeeper = new MemoHandler(storage);

	listFragment = (MemoListFragment) getFragmentManager().findFragmentById(android.R.id.content);
	if (listFragment == null) {
	    listFragment = new MemoListFragment();
	    getFragmentManager()
		.beginTransaction()
		.add(android.R.id.content, listFragment)
		.commit();
	}
	memoList = listFragment.getList();
    }

    protected void onResume() {
	memoKeeper.updateFromStorage();
	updateFromKeeper();
	super.onResume();
    }

    private void updateFromKeeper() {
	memoList.clear();
	List<Memo> memos = memoKeeper.getAllActiveMemos();
	memoList.addAll(memos);
    }

    public MemoHandler getMemoHandler() {
	return memoKeeper;
    }

    public void applyEdit(Memo editedMemo, String result) {
	memoKeeper.replaceMemo(editedMemo, Arrays.asList(result));
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
