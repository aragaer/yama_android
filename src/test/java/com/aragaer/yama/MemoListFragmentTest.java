package com.aragaer.yama;

import java.util.Arrays;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import com.aragaer.yama.ui.EditableItem;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.robolectric.util.FragmentTestUtil.*;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk=18)
public class MemoListFragmentTest {

    private MemoListFragment fragment;
    private ListView list;
    private ArrayAdapter<Memo> adapter;
    private MemoFileProvider fileProvider;
    private MemoStorage storage;
    private MemoHandler handler;

    @Before public void setUp() {
	fileProvider = new TestFileProvider();
	storage = new MemoStorage(fileProvider);
	handler = new MemoHandler(storage);
	fragment = new MemoListFragment();
	fragment.setMemoHandler(handler);
	startFragment(fragment);
	list = (ListView) fragment.getView().findViewById(R.id.memo_list);
	adapter = (ArrayAdapter<Memo>) list.getAdapter();
    }

    @Test public void hasMemoList() {
	Memo stored = handler.storeMemo("a memo");
	handler.dumpToStorage();
	fragment.onResume();

	assertThat(adapter.getCount(), equalTo(1));
    }

    @Test public void canEditMemo() {
	Memo stored = handler.storeMemo("a memo");
	handler.dumpToStorage();
	fragment.onResume();
	View item = adapter.getView(0, null, list);
	EditText edit = (EditText) item.findViewById(R.id.text);

	edit.requestFocus();
	edit.setText("new value");
	fragment.onPause();

	assertThat(handler.getAllActiveMemos().size(), equalTo(1));
	assertThat(handler.getAllActiveMemos().get(0).getText(),
		   equalTo("new value"));
    }
}
