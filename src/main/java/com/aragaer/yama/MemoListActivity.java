package com.aragaer.yama;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;


public class MemoListActivity extends Activity {

    private List<Memo> memoList;
    private MemoListFragment listFragment;
    private int editPosition = -1;
    private Editor editor;
    private MemoStorage storage;
    private MemoHandler memoKeeper;

    @Override protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	editor = new Editor();
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
	memoKeeper.updateFromReaderWriter();
	updateFromKeeper();
	super.onResume();
    }

    private void updateFromKeeper() {
	memoList.clear();
	List<Memo> memos = memoKeeper.getAllActiveMemos();
	memoList.addAll(memos);
    }

    void openEditor(int position, Memo memo) {
	getFragmentManager()
	    .beginTransaction()
	    .replace(android.R.id.content, editor.startFor(memo))
	    .addToBackStack(null)
	    .commit();
	editPosition = position;
    }

    private void closeEditor() {
	getFragmentManager().popBackStack();
	editor.reset();
    }

    private void applyEdit(List<String> result) {
	List<? extends Memo> list = memoKeeper.getAllActiveMemos();
	memoKeeper.replaceMemo(list.get(editPosition), result);
	memoKeeper.dumpToReaderWriter();
	updateFromKeeper();
	listFragment.scrollTo = editPosition + result.size() - 1;
	Toast.makeText(this,
		       result.size() == 0 ? "Deleted" : "Saved",
		       Toast.LENGTH_SHORT)
	    .show();
    }

    @Override public void onBackPressed() {
	if (editor.isActive())
	    applyEdit(editor.getResult());
	super.onBackPressed();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.new_memo_btn:
	    createNew();
	    return true;
	case R.id.edit_done_btn:
	    applyEdit(editor.getResult());
	    closeEditor();
	    return true;
	case R.id.edit_cancel_btn:
	    closeEditor();
	    return true;
	case R.id.edit_delete_btn:
	    applyEdit(Collections.<String>emptyList());
	    closeEditor();
	    return true;
	default:
	    return false;
	}
    }

    private void createNew() {
	startActivity(new Intent(this, MemoCreateActivity.class));
    }

    static class Editor {
	private EditFragment fragment;

	Fragment startFor(Memo memo) {
	    fragment = new EditFragment();
	    fragment.setMemo(memo.getText());
	    return fragment;
	}

	boolean isActive() {
	    return fragment != null;
	}

	List<String> getResult() {
	    return MemoProcessor.sanitize(fragment.getMemo().split("\n"));
	}

	void reset() {
	    fragment = null;
	}
    }
}
