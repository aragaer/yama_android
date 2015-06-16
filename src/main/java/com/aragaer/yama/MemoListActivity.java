package com.aragaer.yama;

import java.util.Collections;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;


public class MemoListActivity extends ActionBarActivity {

    private List<Memo> memoList;
    private MemoListFragment listFragment;
    private int editPosition = -1;
    private Editor editor;
    private MemoStorage storage;

    @Override protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	editor = new Editor();
	MemoFileProvider fileProvider = new AndroidFileProvider(this);
	MemoReaderWriter readerWriter = new PlainReaderWriter(fileProvider);
	storage = new MemoStorage(readerWriter);

	listFragment = (MemoListFragment) getFragmentManager().findFragmentById(R.id.app_content);
	if (listFragment == null) {
	    listFragment = new MemoListFragment();
	    getFragmentManager()
		.beginTransaction()
		.add(R.id.app_content, listFragment)
		.commit();
	}
	memoList = listFragment.getList();
    }

    protected void onResume() {
	super.onResume();
	storage.updateFromReaderWriter();
	updateFromStorage();
    }

    private void updateFromStorage() {
	memoList.clear();
	List<? extends Memo> memos = storage.getAllActiveMemos();
	memoList.addAll(memos);
    }

    void openEditor(int position, Memo memo) {
	getFragmentManager()
	    .beginTransaction()
	    .replace(R.id.app_content, editor.startFor(memo))
	    .addToBackStack(null)
	    .commit();
	editPosition = position;
    }

    private void closeEditor() {
	getFragmentManager().popBackStack();
	editor.reset();
    }

    private void applyEdit(List<String> result) {
	List<? extends Memo> list = storage.getAllActiveMemos();
	storage.replaceMemo(list.get(editPosition), result);
	storage.dumpToReaderWriter();
	updateFromStorage();
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
