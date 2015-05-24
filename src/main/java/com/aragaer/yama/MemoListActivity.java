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

    private List<String> memoList;
    private MemoListFragment listFragment;
    private int editPosition = -1;
    private Editor editor;

    @Override protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	editor = new Editor();

	listFragment = new MemoListFragment();
	memoList = listFragment.getList();
	getFragmentManager()
	    .beginTransaction()
	    .add(android.R.id.content, listFragment)
	    .commit();
    }

    protected void onResume() {
	super.onResume();
	MemoFile.read(this, memoList);
    }

    void openEditor(int position) {
	getFragmentManager()
	    .beginTransaction()
	    .replace(android.R.id.content, editor.startFor(memoList.get(position)))
	    .addToBackStack(null)
	    .commit();
	editPosition = position;
    }

    private void closeEditor() {
	getFragmentManager().popBackStack();
	editor.reset();
    }

    private void applyEdit(List<String> result) {
	listFragment.scrollTo = editPosition + result.size() - 1;
	memoList.remove(editPosition);
	memoList.addAll(editPosition, result);
	MemoFile.save(this, memoList);
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

	Fragment startFor(String text) {
	    fragment = new EditFragment();
	    fragment.setMemo(text);
	    return fragment;
	}

	boolean isActive() {
	    return fragment != null;
	}

	List<String> getResult() {
	    return MemoFile.sanitize(fragment.getMemo().split("\n"));
	}

	void reset() {
	    fragment = null;
	}
    }
}
