package com.aragaer.yama;

import java.io.*;
import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;


public class ListActivity extends Activity {

    ListView memoListView;
    ArrayAdapter<String> memoAdapter;
    List<String> memoList;


    OnItemClickListener clickListener = new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("YAMA", "There's a click on memo " + memoAdapter.getItem(position));
		Intent intent = new Intent(ListActivity.this, EditActivity.class);
		intent.putExtra("memo", memoAdapter.getItem(position));
		startActivityForResult(intent, position);
	    }
	};

    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	memoList = Collections.synchronizedList(new ArrayList<String>());
	memoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, memoList);
	memoListView = new ListView(this);
	memoListView.setAdapter(memoAdapter);
	memoListView.setOnItemClickListener(clickListener);
	readMemos();

	setContentView(memoListView);
    }

    protected void onStart() {
	super.onStart();
	memoListView.setSelection(memoAdapter.getCount() - 1);
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
}
