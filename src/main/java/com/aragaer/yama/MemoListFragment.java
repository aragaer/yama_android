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

    ListView memoListView;
    ArrayAdapter<Memo> memoAdapter;
    Collection<Memo> memoList;
    EditableItem edited;

    public MemoListFragment() {
	setHasOptionsMenu(true);
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
	if (!oldText.equals(newText))
	    ((MemoListActivity) getActivity()).applyEdit(edited.getMemo(), newText);
	edited = null;
    }

    void setContents(Collection<Memo> memos) {
	edited = null;
	memoList = new HashSet<Memo>(memos);
	memoAdapter.clear();
	memoAdapter.addAll(memos);
    }

    @Override public View onCreateView(LayoutInflater inflater,
				       ViewGroup root,
				       Bundle savedInstanceState) {
	View result = inflater.inflate(R.layout.list, root, false);

	memoAdapter = new MemoAdapter(getActivity(), this);
	memoListView = (ListView) result.findViewById(R.id.memo_list);
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
	super.onResume();
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
