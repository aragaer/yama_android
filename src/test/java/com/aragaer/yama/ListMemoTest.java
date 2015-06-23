package com.aragaer.yama;
// vim: et ts=4 sts=4 sw=4

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.tester.android.view.TestMenuItem;
import org.robolectric.util.ActivityController;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static android.content.Context.MODE_PRIVATE;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk=18)
public class ListMemoTest {

    @Test public void shouldAllowCreateNewMemo() {
	MemoListActivity activity = Robolectric.setupActivity(MemoListActivity.class);
	activity.onOptionsItemSelected(new TestMenuItem(R.id.new_memo_btn));

	Intent expectedIntent = new Intent(activity, MemoCreateActivity.class);
	assertThat(Robolectric.shadowOf(activity).getNextStartedActivity(),
		   equalTo(expectedIntent));
    }

    @Test public void readFile() throws Exception {
	ActivityController<MemoListActivity> controller =
		Robolectric.buildActivity(MemoListActivity.class);
	controller.create();
	write("* \n  test\n");
	controller.start();
	controller.resume();
	ListView list = (ListView) controller.get().findViewById(R.id.memo_list);
	ListAdapter adapter = list.getAdapter();
	assertThat(adapter.getCount(), equalTo(1));
	assertThat(((Memo) adapter.getItem(0)).getText(), equalTo("test"));
    }

    @Test public void shouldResetBeforeReading() throws Exception {
	ActivityController<MemoListActivity> controller =
		Robolectric.buildActivity(MemoListActivity.class);
	controller.create();
	write("* \n  test\n");
	controller.start();
	controller.resume();
	controller.pause();
	write("* \n  test\n* \n  test2\n");
	controller.resume();
	ListView list = (ListView) controller.get().findViewById(R.id.memo_list);
	ListAdapter adapter = list.getAdapter();
	assertThat(adapter.getCount(), equalTo(2));
	assertThat(((Memo) adapter.getItem(0)).getText(), equalTo("test"));
	assertThat(((Memo) adapter.getItem(1)).getText(), equalTo("test2"));
    }

    @Test public void shouldEdit() throws Exception {
	ActivityController<MemoListActivity> controller =
		Robolectric.buildActivity(MemoListActivity.class);
	controller.create();
	write("* \n  test\n");
	controller.start();
	controller.resume();
	MemoListActivity activity = controller.get();

	ListView list = (ListView) activity.findViewById(R.id.memo_list);
	ListAdapter adapter = list.getAdapter();
	EditText edit = (EditText) adapter.getView(0, null, list).findViewById(R.id.text);
	assertThat(edit.getText().toString(), equalTo("test"));

	edit.requestFocus();
	edit.setText("modified");
	edit.clearFocus();

	assertThat(read(), equalTo("* \n  modified\n"));
    }

    static void write(String text) throws Exception {
	OutputStream stream = Robolectric.application.openFileOutput("memo.org", MODE_PRIVATE);
	stream.write(text.getBytes());
	stream.close();
    }

    static String read() throws Exception {
	byte[] buffer = new byte[80];
	InputStream is = Robolectric.application.openFileInput("memo.org");
	int read = is.read(buffer, 0, 80);
	return Charset.defaultCharset().decode(ByteBuffer.wrap(buffer, 0, read)).toString();
    }
}
