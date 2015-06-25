package com.aragaer.yama;

import java.util.List;

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


public class MemoListFragment extends Fragment implements OnFocusChangeListener {

    ListView memoListView;
    ArrayAdapter<Memo> memoAdapter;
    List<Memo> memoList;
    ViewHolder edited;

    public MemoListFragment() {
	setHasOptionsMenu(true);
    }

    @Override public void onFocusChange(View v, boolean hasFocus) {
	ViewHolder holder = (ViewHolder) ((ViewGroup) v.getParent()).getTag();
	if (hasFocus)
	    edited = holder;
	else
	    applyEdit();
    }

    private void applyEdit() {
	if (edited == null)
	    return;
	if (!memoList.contains(edited.memo)) {
	    edited = null;
	    return;
	}
	String oldText = edited.memo.getText();
	String newText = edited.input.getText().toString();
	if (!oldText.equals(newText))
	    ((MemoListActivity) getActivity()).applyEdit(edited.memo, newText);
	edited = null;
    }

    void setContents(List<Memo> memos) {
	edited = null;
	memoList = memos;
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
	    super(context, R.layout.item);
	    this.focusChangeListener = focusChangeListener;
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
	    ViewHolder holder;
	    if (convertView == null) {
		convertView = View.inflate(parent.getContext(), R.layout.item, null);
		holder = new ViewHolder();
		holder.input = (EditText) convertView.findViewById(R.id.text);
		holder.input.setOnFocusChangeListener(focusChangeListener);
		convertView.setTag(holder);
	    } else
		holder = (ViewHolder) convertView.getTag();
	    Memo memo = getItem(position);
	    holder.input.setText(memo.getText());
	    holder.memo = memo;
	    return convertView;
	}
    }

    private static class ViewHolder {
	EditText input;
	Memo memo;
    }

    public void scrollTo(int position) {
	memoListView.setSelection(position);
    }

    @Override public void onResume() {
	super.onResume();
	scrollTo(memoList.size() - 1);
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
