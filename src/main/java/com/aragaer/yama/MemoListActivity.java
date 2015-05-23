package com.aragaer.yama;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;


public class MemoListActivity extends Activity {

    private List<String> memoList;
    private MemoListFragment listFragment;
    private EditFragment editFragment;
    private int editPosition = -1;

    @Override protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

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

    void openEditor(String text, int position) {
	editFragment = new EditFragment();
	editFragment.setMemo(text);
	getFragmentManager()
	    .beginTransaction()
	    .replace(android.R.id.content, editFragment)
	    .addToBackStack(null)
	    .commit();
	editPosition = position;
    }

    @Override public void onBackPressed() {
	if (editFragment != null)
	    finalizeEdit();
	else
	    super.onBackPressed();
    }

    private void createNew() {
	startActivity(new Intent(this, MemoCreateActivity.class));
    }

    private void closeEditor() {
	getFragmentManager().popBackStack();
	editFragment = null;
    }

    private void finalizeEdit() {
	saveMemo(editFragment.getMemo());
	MemoFile.save(this, memoList);
	closeEditor();
    }

    private void saveMemo(String string) {
	List<String> created = MemoFile.sanitize(string.split("\n"));
	listFragment.scrollTo = editPosition + created.size() - 1;
	deleteMemo();
	memoList.addAll(editPosition, created);
	Toast.makeText(this, created.size() == 0 ? "Deleted" : "Saved", Toast.LENGTH_SHORT).show();
    }

    private void deleteMemo() {
	memoList.remove(editPosition);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.new_memo_btn:
	    createNew();
	    return true;
	case R.id.edit_done_btn:
	    finalizeEdit();
	    return true;
	case R.id.edit_cancel_btn:
	    closeEditor();
	    return true;
	case R.id.edit_delete_btn:
	    deleteMemo();
	    MemoFile.save(this, memoList);
	    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
	    closeEditor();
	    return true;
	default:
	    return false;
	}
    }
}
