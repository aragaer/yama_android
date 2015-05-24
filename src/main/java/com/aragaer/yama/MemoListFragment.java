package com.aragaer.yama;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class MemoListFragment extends Fragment implements OnItemClickListener {

    ListView memoListView;
    ArrayAdapter<String> memoAdapter;
    List<String> memoList;
    int scrollTo = -1;

    public MemoListFragment() {
	memoList = Collections.synchronizedList(new ArrayList<String>());
	setHasOptionsMenu(true);
    }

    List<String> getList() {
	return memoList;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	((MemoListActivity) getActivity()).openEditor(position);
    }

    @Override public View onCreateView(LayoutInflater inflater,
				       ViewGroup root,
				       Bundle savedInstanceState) {
	View result = inflater.inflate(R.layout.list, root, false);

	memoAdapter = new ArrayAdapter<String>(getActivity(),
					       android.R.layout.simple_list_item_1,
					       memoList);
	memoListView = (ListView) result.findViewById(R.id.memo_list);
	memoListView.setAdapter(memoAdapter);
	memoListView.setOnItemClickListener(this);
	return result;
    }

    @Override public void onResume(){
	super.onResume();
	if (scrollTo == -1)
	    memoListView.setSelection(memoAdapter.getCount() - 1);
	else
	    memoListView.setSelection(scrollTo);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	inflater.inflate(R.menu.list_menu, menu);
    }
}
