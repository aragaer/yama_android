package com.aragaer.yama;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.widget.TextView;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.robolectric.util.FragmentTestUtil.*;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk=18)
public class EditorFragmentTest {

    private EditorFragment fragment;
    private TextView editor;

    @Before public void setUp() {
	fragment = new EditorFragment();
	startFragment(fragment);
	editor = (TextView) fragment.getView().findViewById(R.id.editor);
    }

    @Test public void showsMemos() {
	assertNotNull(editor);
	fragment.setContents(memos());
	assertThat(editor.getText().toString(), equalTo(""));
	fragment.setContents(memos("one memo"));
	assertThat(editor.getText().toString(), equalTo("one memo\n"));
    }

    @Test public void showsEdits() {
	assertEquals(fragment.getContents(), Arrays.asList());
	editor.setText("some stuff");
	assertEquals(texts(fragment.getContents()),
		     Arrays.asList("some stuff"));
    }

    @Test public void complexEdits() {
	assertEquals(fragment.getContents(), Arrays.asList());
	editor.setText("some stuff\n\n  \ntest\n");
	assertEquals(texts(fragment.getContents()),
		     Arrays.asList("some stuff", "test"));
    }


    static private List<Memo> memos(String... memos) {
	List<Memo> result = new ArrayList<Memo>(memos.length);
	for (String text : memos)
	    result.add(new Memo(text));
	return result;
    }

    static private List<String> texts(List<Memo> memos) {
	List<String> result = new ArrayList<String>(memos.size());
	for (Memo memo : memos)
	    result.add(memo.getText());
	return result;
    }
}
