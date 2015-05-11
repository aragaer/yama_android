package com.aragaer.yama;

import java.io.*;
import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;


public class MemoListActivity extends Activity {

    ListView memoListView;
    ArrayAdapter<String> memoAdapter;
    List<String> memoList;


    OnItemClickListener clickListener = new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("YAMA", "There's a click on memo " + memoAdapter.getItem(position));
		Intent intent = new Intent(MemoListActivity.this, EditActivity.class);
		intent.putExtra("memo", memoAdapter.getItem(position));
		startActivityForResult(intent, position);
	    }
	};

    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.list);

	memoList = Collections.synchronizedList(new ArrayList<String>());
	memoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, memoList);
	memoListView = (ListView) findViewById(R.id.memo_list);
	memoListView.setAdapter(memoAdapter);
	memoListView.setOnItemClickListener(clickListener);
    }

    protected void onResume() {
	super.onResume();
	readMemos();
    }

    protected void onStart() {
	super.onStart();
	memoListView.setSelection(memoAdapter.getCount() - 1);
	Log.d("YAMA", "started");
    }

    private void readMemos() {
	try {
	    InputStream memos = openFileInput("memo");
	    BufferedReader reader = new BufferedReader(new InputStreamReader(memos));
	    while (true) {
		String line = reader.readLine();
		if (line == null)
		    break;
		memoList.add(line);
		Log.d("YAMA", "Got line: " + line);
	    }
	    memos.close();
	} catch (Exception e) {
	    Log.e("YAMA", "Error reading memos", e);
	}
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
	if (resultCode != RESULT_OK)
	    return;
	int position = requestCode;
	String edited = result.getStringExtra("edited");
	memoList.remove(position);
	for (String new_line : edited.split("\n")) {
	    new_line = new_line.trim();
	    if (new_line.isEmpty())
		continue;
	    memoList.add(position++, new_line);
	}
	runOnUiThread(new Runnable() {
		public void run() {
		    memoAdapter.notifyDataSetChanged();
		}
	    });
	try {
	    OutputStream file = openFileOutput("memo", Context.MODE_PRIVATE);
	    for (String line : memoList) {
		file.write(line.getBytes());
		file.write("\n".getBytes());
	    }
	    file.close();
	} catch (Exception e) {
	    Log.e("YAMA", "Error writing memo", e);
	}
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.new_memo_btn:
	    Intent intent = new Intent(this, MemoCreateActivity.class);
	    startActivity(intent);
	    return true;
	default:
	    return false;
	}
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.list_menu, menu);
	return true;
    }
}
