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


public class EditActivity extends Activity {

    EditText memo;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	memo = new EditText(this);
	memo.setText(getIntent().getStringExtra("memo"));
	memo.setGravity(Gravity.TOP);

	setContentView(memo);
    }

    MenuItem.OnMenuItemClickListener cancelListener = new MenuItem.OnMenuItemClickListener() {
	    public boolean onMenuItemClick(MenuItem item) {
		EditActivity.this.setResult(Activity.RESULT_CANCELED);
		EditActivity.this.finish();
		return true;
	    }
	};

    MenuItem.OnMenuItemClickListener doneListener = new MenuItem.OnMenuItemClickListener() {
	    public boolean onMenuItemClick(MenuItem item) {
		EditActivity.this.saveMemo(memo.getText().toString());
		EditActivity.this.finish();
		return true;
	    }
	};

    MenuItem.OnMenuItemClickListener deleteListener = new MenuItem.OnMenuItemClickListener() {
	    public boolean onMenuItemClick(MenuItem item) {
		EditActivity.this.saveMemo("");
		EditActivity.this.finish();
		return true;
	    }
	};

    private void saveMemo(String memo) {
	Intent result = new Intent().putExtra("edited", memo);
	setResult(RESULT_OK, result);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
	MenuItem done = menu.add("Done");
	MenuItem cancel = menu.add("Cancel");
	MenuItem delete = menu.add("Delete");
	done.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	done.setOnMenuItemClickListener(doneListener);
	cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	cancel.setOnMenuItemClickListener(cancelListener);
	delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	delete.setOnMenuItemClickListener(deleteListener);
	return true;
    }
}
