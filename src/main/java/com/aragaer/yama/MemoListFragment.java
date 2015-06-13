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
import android.widget.TextView;


public class MemoListFragment extends Fragment implements OnItemClickListener {

    ListView memoListView;
    ArrayAdapter<Memo> memoAdapter;
    List<Memo> memoList;
    int scrollTo = -1;

    public MemoListFragment() {
	memoList = Collections.synchronizedList(new ArrayList<Memo>());
	setHasOptionsMenu(true);
    }

    List<Memo> getList() {
	return memoList;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	((MemoListActivity) getActivity()).openEditor(position, memoList.get(position));
    }

    @Override public View onCreateView(LayoutInflater inflater,
				       ViewGroup root,
				       Bundle savedInstanceState) {
	View result = inflater.inflate(R.layout.list, root, false);

	memoAdapter = new ArrayAdapter<Memo>(getActivity(),
					     android.R.layout.simple_list_item_1,
					     memoList) {
		@Override public View getView(int position, View convertView, ViewGroup parent) {
		    View result = super.getView(position, convertView, parent);
		    ((TextView) result).setText(getItem(position).getText());
		    return result;
		}
	    };
	memoListView = (ListView) result.findViewById(R.id.memo_list);
	memoListView.setAdapter(memoAdapter);
	memoListView.setOnItemClickListener(this);
	return result;
    }

    public void scrollTo(int position) {
	memoListView.setSelection(position);
    }

    @Override public void onResume() {
	super.onResume();
	if (scrollTo == -1)
	    scrollTo(memoList.size() - 1);
	else
	    scrollTo(scrollTo);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	inflater.inflate(R.menu.list_menu, menu);
    }
}
