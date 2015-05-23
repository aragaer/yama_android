package com.aragaer.yama;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class EditFragment extends Fragment {

    EditText memo;

    @Override public View onCreateView(LayoutInflater inflater,
				       ViewGroup root,
				       Bundle savedInstanceState) {
	View result = inflater.inflate(R.layout.edit, root, false);
	memo = (EditText) result.findViewById(R.id.memo_edit);
	memo.setText(getActivity().getIntent().getStringExtra("memo"));
	setHasOptionsMenu(true);
	return result;
    }

    MenuItem.OnMenuItemClickListener cancelListener = new MenuItem.OnMenuItemClickListener() {
	    public boolean onMenuItemClick(MenuItem item) {
		getActivity().setResult(Activity.RESULT_CANCELED);
		getActivity().finish();
		return true;
	    }
	};

    MenuItem.OnMenuItemClickListener doneListener = new MenuItem.OnMenuItemClickListener() {
	    public boolean onMenuItemClick(MenuItem item) {
		((EditActivity) getActivity()).saveMemo(getMemo());
		getActivity().finish();
		return true;
	    }
	};

    MenuItem.OnMenuItemClickListener deleteListener = new MenuItem.OnMenuItemClickListener() {
	    public boolean onMenuItemClick(MenuItem item) {
		((EditActivity) getActivity()).saveMemo("");
		getActivity().finish();
		return true;
	    }
	};

    String getMemo() {
	return memo.getText().toString();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	MenuItem done = menu.add("Done");
	MenuItem cancel = menu.add("Cancel");
	MenuItem delete = menu.add("Delete");
	done.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	done.setOnMenuItemClickListener(doneListener);
	cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	cancel.setOnMenuItemClickListener(cancelListener);
	delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	delete.setOnMenuItemClickListener(deleteListener);
    }
}
