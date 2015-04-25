package com.aragaer.yama;

import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


public class MainActivity extends Activity {

    EditText memo;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	memo = new EditText(this);
	memo.setGravity(Gravity.TOP);

	setContentView(memo);
    }

    MenuItem.OnMenuItemClickListener cancelListener = new MenuItem.OnMenuItemClickListener() {
	    public boolean onMenuItemClick(MenuItem item) {
		MainActivity.this.finish();
		return true;
	    }
	};

    MenuItem.OnMenuItemClickListener doneListener = new MenuItem.OnMenuItemClickListener() {
	    public boolean onMenuItemClick(MenuItem item) {
		MainActivity.this.saveMemo();
		MainActivity.this.finish();
		return true;
	    }
	};

    private void saveMemo() {
	String[] lines = memo.getText().toString().split("\n");
	try {
	    OutputStream file = openFileOutput("memo", Context.MODE_PRIVATE | Context.MODE_APPEND);
	    for (String line : lines) {
		String trimmed = line.trim();
		if (trimmed.isEmpty())
		    continue;
		file.write(trimmed.getBytes());
		file.write("\n".getBytes());
	    }
	    file.close();
	} catch (Exception e) {
	    Log.e("YAMA", "Error writing memo", e);
	}
    }

    public boolean onCreateOptionsMenu(Menu menu) {
	MenuItem done = menu.add("Done");
	MenuItem cancel = menu.add("Cancel");
	done.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	done.setOnMenuItemClickListener(doneListener);
	cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	cancel.setOnMenuItemClickListener(cancelListener);
	return true;
    }
}
