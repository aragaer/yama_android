package com.aragaer.yama;
// vim: et ts=4 sts=4 sw=4

import java.io.*;
import java.util.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.widget.EditText;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk=18)
public class CreateMemoTest {

    @Test public void testBackSaves() throws Exception {
	MemoCreateActivity activity = Robolectric.setupActivity(MemoCreateActivity.class);
	EditText text = (EditText) activity.findViewById(R.id.new_memo_edit);
	text.setText("hello, world");

	activity.onBackPressed();

	assertThat(readMemosFile(activity),
		   equalTo(Arrays.asList("hello, world")));
    }

    private List<String> readMemosFile(MemoCreateActivity activity) throws Exception {
	List<String> result = new LinkedList<String>();
	InputStream file = activity.openFileInput("memo");
	BufferedReader reader = new BufferedReader(new InputStreamReader(file));
	while (true) {
	    String line = reader.readLine();
	    if (line == null)
		break;
	    result.add(line);
	}
	file.close();
	return result;
    }
}
