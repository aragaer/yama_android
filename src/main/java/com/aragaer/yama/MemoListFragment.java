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


public class MemoListFragment extends Fragment {

    ListView memoListView;
    ArrayAdapter<String> memoAdapter;
    List<String> memoList;
    int scrollPosition = -1;

    OnItemClickListener clickListener = new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("YAMA", "There's a click on memo " + memoAdapter.getItem(position));
		Intent intent = new Intent(getActivity(), EditActivity.class);
		intent.putExtra("memo", memoAdapter.getItem(position));
		getActivity().startActivityForResult(intent, position);
	    }
	};

    @Override public View onCreateView(LayoutInflater inflater,
				       ViewGroup root,
				       Bundle savedInstanceState) {
	View result = inflater.inflate(R.layout.list, root, false);

	memoList = Collections.synchronizedList(new ArrayList<String>());
	memoAdapter = new ArrayAdapter<String>(getActivity(),
					       android.R.layout.simple_list_item_1,
					       memoList);
	memoListView = (ListView) result.findViewById(R.id.memo_list);
	memoListView.setAdapter(memoAdapter);
	memoListView.setOnItemClickListener(clickListener);
	setHasOptionsMenu(true);
	return result;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	inflater.inflate(R.menu.list_menu, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.new_memo_btn:
	    Intent intent = new Intent(getActivity(), MemoCreateActivity.class);
	    getActivity().startActivity(intent);
	    return true;
	default:
	    return false;
	}
    }
}
