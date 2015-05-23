package com.aragaer.yama;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class EditActivity extends Activity {

    private EditFragment fragment;

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	fragment = new EditFragment();
	getFragmentManager()
	    .beginTransaction()
	    .add(android.R.id.content, fragment)
	    .commit();
    }

    void saveMemo(String memo) {
	Intent result = new Intent().putExtra("edited", memo);
	Toast.makeText(this, memo.isEmpty() ? "Deleted" : "Saved", Toast.LENGTH_SHORT).show();
	setResult(RESULT_OK, result);
    }

    @Override public void onBackPressed() {
	saveMemo(fragment.getMemo());
	finish();
    }
    
}
