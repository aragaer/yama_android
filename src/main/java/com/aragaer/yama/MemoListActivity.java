package com.aragaer.yama;

import java.io.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class MemoListActivity extends Activity {

    private MemoListFragment fragment;

    @Override protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	fragment = new MemoListFragment();
	getFragmentManager()
	    .beginTransaction()
	    .add(android.R.id.content, fragment)
	    .commit();
    }

    protected void onResume() {
	super.onResume();
	readMemos();
	if (fragment.scrollPosition == -1)
	    fragment.memoListView.setSelection(fragment.memoAdapter.getCount() - 1);
    }

    private void readMemos() {
	fragment.memoList.clear();
	try {
	    InputStream memos = openFileInput("memo");
	    BufferedReader reader = new BufferedReader(new InputStreamReader(memos));
	    while (true) {
		String line = reader.readLine();
		if (line == null)
		    break;
		fragment.memoList.add(line);
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
	fragment.memoList.remove(position);
	for (String new_line : edited.split("\n")) {
	    new_line = new_line.trim();
	    if (new_line.isEmpty())
		continue;
	    fragment.memoList.add(position++, new_line);
	}
	fragment.scrollPosition = position - 1;
	runOnUiThread(new Runnable() {
		public void run() {
		    fragment.memoAdapter.notifyDataSetChanged();
		}
	    });
	try {
	    OutputStream file = openFileOutput("memo", Context.MODE_PRIVATE);
	    for (String line : fragment.memoList) {
		file.write(line.getBytes());
		file.write("\n".getBytes());
		Log.d("YAMA", "Writing from list: " + line);
	    }
	    file.close();
	} catch (Exception e) {
	    Log.e("YAMA", "Error writing memo", e);
	}
    }
}
