package com.aragaer.yama;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowContentResolver;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk=18)
public class MemoProviderTest {

    private MemoProvider provider;
    private ContentResolver resolver;
    private ShadowContentResolver shadowResolver;

    private void deleteAllFiles() {
	for (String fileName : Robolectric.application.fileList())
	    Robolectric.application.deleteFile(fileName);
    }

    @Before public void setUp() throws Exception {
	deleteAllFiles();
	OutputStream output = Robolectric.application.openFileOutput("2015-05-11.txt",
								     Context.MODE_PRIVATE);
	output.write("* \n  line1\n  line2\n* \n  Other memo\n".getBytes());
	provider = new MemoProvider();
	resolver = Robolectric.application.getContentResolver();
	shadowResolver = Robolectric.shadowOf(resolver);
	provider.onCreate();
	shadowResolver.registerProvider(MemoProvider.AUTHORITY, provider);
    }

    @After public void tearDown() {
	deleteAllFiles();
    }

    @Test public void queryProvider() throws Exception {
	Cursor cursor = shadowResolver.query(MemoProvider.MEMOS_URI, null, null, null, null);

	assertThat(cursor.getCount(), equalTo(2));
	verifyMemos(cursor, "line1\nline2", "Other memo");
    }

    @Test public void convertFromOldOne() throws Exception {
	OutputStream memoFile = Robolectric.application.openFileOutput("memo",
								       Context.MODE_PRIVATE);
	memoFile.write("a memo\nother one\n".getBytes());
	memoFile.close();

	provider.onCreate();  // Force reread older memo file

	assertThat(Robolectric.application.fileList(),
		   not(hasItemInArray("memo")));
	assertThat(Robolectric.application.fileList(),
		   hasItemInArray("2015-05-10.txt"));  // One day earlier than earliest existing

	Cursor cursor = shadowResolver.query(MemoProvider.MEMOS_URI, null, null, null, null);

	assertThat(cursor.getCount(), equalTo(4));
	verifyMemos(cursor, "a memo", "other one", "line1\nline2", "Other memo");

	InputStream converted = Robolectric.application.openFileInput("2015-05-10.txt");
	MemoReader reader = new MemoReader(converted);
	List<String> memos = new LinkedList<String>();
	while (true) {
	    Memo memo = reader.readMemo();
	    if (memo == null)
		break;
	    memos.add(memo.getText());
	}
	assertThat(memos, equalTo(Arrays.asList("a memo", "other one")));
    }

    private void verifyMemos(Cursor cursor, String... memos) {
	cursor.moveToFirst();
	for (String memo : memos) {
	    assertThat(MemoProvider.readMemoFromCursor(cursor).getText(),
		       equalTo(memo));
	    cursor.moveToNext();
	}
	assertTrue(cursor.isAfterLast());
    }
}
