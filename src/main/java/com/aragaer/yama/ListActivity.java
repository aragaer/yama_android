package com.aragaer.yama;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class ListActivity extends Activity {

    ListView memoList;
    ArrayAdapter<String> memoAdapter;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	memoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
	memoList = new ListView(this);
	memoList.setAdapter(memoAdapter);
	readMemos();

	setContentView(memoList);
    }

    private void readMemos() {
	try {
	    InputStream memos = openFileInput("memo");
	    BufferedReader reader = new BufferedReader(new InputStreamReader(memos));
	    while (true) {
		String line = reader.readLine();
		if (line == null)
		    break;
		memoAdapter.add(line);
	    }
	    memos.close();
	} catch (Exception e) {
	    Log.e("YAMA", "Error reading memos", e);
	}
    }
}
