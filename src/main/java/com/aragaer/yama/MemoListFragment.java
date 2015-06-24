package com.aragaer.yama;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.EditText;


public class MemoListFragment extends Fragment implements OnFocusChangeListener {

    ListView memoListView;
    ArrayAdapter<Memo> memoAdapter;
    List<Memo> memoList;

    public MemoListFragment() {
	memoList = Collections.synchronizedList(new ArrayList<Memo>());
	setHasOptionsMenu(true);
    }

    List<Memo> getList() {
	return memoList;
    }

    @Override public void onFocusChange(View v, boolean hasFocus) {
	ViewHolder holder = (ViewHolder) ((ViewGroup) v.getParent()).getTag();
	if (hasFocus)
	    return;
	Memo oldMemo = memoAdapter.getItem(holder.position);
	String oldText = oldMemo.getText().trim();
	String newText = holder.input.getText().toString();
	if (oldText.equals(newText))
	    return;
	((MemoListActivity) getActivity()).applyEdit(oldMemo, newText);
    }

    @Override public View onCreateView(LayoutInflater inflater,
				       ViewGroup root,
				       Bundle savedInstanceState) {
	View result = inflater.inflate(R.layout.list, root, false);

	memoAdapter = new MemoAdapter(getActivity(), memoList, this);
	memoListView = (ListView) result.findViewById(R.id.memo_list);
	memoListView.setAdapter(memoAdapter);
	return result;
    }

    private static class MemoAdapter extends ArrayAdapter<Memo> {
	private final OnFocusChangeListener focusChangeListener;

	MemoAdapter(Context context, List<Memo> list, OnFocusChangeListener focusChangeListener) {
	    super(context, R.layout.item, list);
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
	    holder.input.setText(getItem(position).getText());
	    holder.position = position;
	    return convertView;
	}
    }

    private static class ViewHolder {
	EditText input;
	int position;
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
}
