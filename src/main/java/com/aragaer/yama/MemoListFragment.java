package com.aragaer.yama;

import java.util.Collection;
import java.util.HashSet;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.EditText;

import com.aragaer.yama.ui.EditableItem;


public class MemoListFragment extends Fragment implements OnFocusChangeListener {

    private MemoHandler handler;
    private ListView memoListView;
    private ArrayAdapter<Memo> memoAdapter;
    private Collection<Memo> memoList;
    private EditableItem edited;

    public MemoListFragment() {
	setHasOptionsMenu(true);
    }

    public void setMemoHandler(MemoHandler newHandler) {
	handler = newHandler;
    }

    @Override public void onFocusChange(View v, boolean hasFocus) {
	EditableItem item = EditableItem.getFromInput(v);
	if (hasFocus)
	    edited = item;
	else
	    applyEdit();
    }

    private void applyEdit() {
	if (edited == null)
	    return;
	if (!memoList.contains(edited.getMemo())) {
	    edited = null;
	    return;
	}
	String oldText = edited.getMemo().getText();
	String newText = edited.getText();
	if (!oldText.equals(newText)) {
	    handler.replaceMemo(edited.getMemo(), MemoProcessor.sanitize(newText));
	    handler.dumpToStorage();
	    refreshContents();
	}
	edited = null;
    }

    private void refreshContents() {
	Collection<Memo> memos = handler.getAllActiveMemos();
	memoList = new HashSet<Memo>(memos);
	memoAdapter.clear();
	memoAdapter.addAll(memos);
    }

    @Override public View onCreateView(LayoutInflater inflater,
				       ViewGroup root,
				       Bundle savedInstanceState) {
	View result = inflater.inflate(R.layout.list, root, false);

	memoListView = (ListView) result.findViewById(R.id.memo_list);
	memoAdapter = new MemoAdapter(memoListView.getContext(), this);
	memoListView.setAdapter(memoAdapter);
	return result;
    }

    private static class MemoAdapter extends ArrayAdapter<Memo> {
	private final OnFocusChangeListener focusChangeListener;

	MemoAdapter(Context context, OnFocusChangeListener focusChangeListener) {
	    super(context, 0);
	    this.focusChangeListener = focusChangeListener;
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
	    EditableItem item;
	    if (convertView == null)
		item = new EditableItem(parent.getContext(), focusChangeListener);
	    else
		item = (EditableItem) convertView;
	    item.setMemo(getItem(position));
	    return item;
	}
    }

    @Override public void onResume() {
	if (handler != null) {
	    handler.updateFromStorage();
	    refreshContents();
	}
	super.onResume();
	if (memoList != null)
	    memoListView.setSelection(memoList.size() - 1);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	inflater.inflate(R.menu.list_menu, menu);
    }

    @Override public void onPause() {
	if (!memoList.isEmpty())
	    applyEdit();
	super.onPause();
    }
}
