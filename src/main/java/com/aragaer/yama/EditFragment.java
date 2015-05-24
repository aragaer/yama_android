package com.aragaer.yama;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class EditFragment extends Fragment {

    private EditText memo;
    private String text = "";

    @Override public View onCreateView(LayoutInflater inflater,
				       ViewGroup root,
				       Bundle savedInstanceState) {
	View result = inflater.inflate(R.layout.edit, root, false);
	memo = (EditText) result.findViewById(R.id.memo_edit);
	memo.setText(text);
	setHasOptionsMenu(true);
	return result;
    }

    void setMemo(String new_text) {
	text = new_text;
	if (memo != null)
	    memo.setText(text);
    }

    String getMemo() {
	return memo.getText().toString();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	inflater.inflate(R.menu.edit_menu, menu);
    }
}
