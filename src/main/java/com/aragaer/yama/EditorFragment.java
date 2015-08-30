package com.aragaer.yama;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import static java.util.Arrays.asList;


public class EditorFragment extends Fragment {

    private EditText edit;

    @Override public View onCreateView(LayoutInflater inflater,
				       ViewGroup root,
				       Bundle savedInstanceState) {
	View result = inflater.inflate(R.layout.editor, root, false);
	edit = (EditText) result.findViewById(R.id.editor);
	return result;
    }

    public void setContents(List<Memo> memos) {
	String new_contents = "";
	for (Memo memo : memos)
	    new_contents += memo.getText()+"\n";
	edit.setText(new_contents);
    }

    public List<Memo> getContents() {
	List<String> contents = MemoProcessor.sanitize(edit.getText().toString().split("\n"));
	List<Memo> result = new ArrayList<Memo>(contents.size());
	for (String record : contents)
	    if (!record.isEmpty())
		result.add(new Memo(record));
	return result;
    }
}
