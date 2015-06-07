package com.aragaer.yama;

import java.io.*;
import java.util.*;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowContentResolver;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;


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
	assertThat(readMemoFile("2015-05-10.txt"),
		   equalTo(Arrays.asList("a memo", "other one")));
    }

    @Test public void writeMemo() throws Exception {
	Memo memo1 = new Memo("a new memo 1");
	Memo memo2 = new Memo("a new memo 2");

	Uri uri1 = shadowResolver.insert(MemoProvider.MEMOS_URI,
					 MemoProvider.getMemoContentValues(memo1));
	Uri uri2 = shadowResolver.insert(MemoProvider.MEMOS_URI,
					 MemoProvider.getMemoContentValues(memo2));
	Cursor cursor = shadowResolver.query(MemoProvider.MEMOS_URI, null, null, null, null);

	assertThat(cursor.getCount(), equalTo(4));
	verifyMemos(cursor, "line1\nline2", "Other memo", "a new memo 1", "a new memo 2");

	String latestFile = MemoWriter.fileNameForDate(new Date());
	assertThat(readMemoFile(latestFile),
		   equalTo(Arrays.asList("a new memo 1", "a new memo 2")));

	Cursor cursor2 = shadowResolver.query(uri1, null, null, null, null);

	assertThat(cursor2.getCount(), equalTo(1));
	//verifyMemos(cursor2, "a new memo 1");
    }

    @Test public void deleteMemoAndEmptyFile() throws Exception {
	Memo memo = new Memo("a memo");
	Uri uri = shadowResolver.insert(MemoProvider.MEMOS_URI,
					MemoProvider.getMemoContentValues(memo));

	int deleteResult = shadowResolver.delete(uri, null, null);
	assertThat(deleteResult, equalTo(1));

	Cursor cursor = shadowResolver.query(MemoProvider.MEMOS_URI, null, null, null, null);

	assertThat(cursor.getCount(), equalTo(2));
	verifyMemos(cursor, "line1\nline2", "Other memo");

	String latestFile = MemoWriter.fileNameForDate(new Date());
	try {
	    readMemoFile(latestFile);
	    fail("File should not exist");
	} catch (FileNotFoundException e) {
	    // expected
	}
    }

    @Test public void memoIds() throws Exception {
	Cursor cursor = shadowResolver.query(MemoProvider.MEMOS_URI, null, null, null, null);
	cursor.moveToFirst();
	Memo memo1 = MemoProvider.readMemoFromCursor(cursor);
	cursor.moveToNext();
	Memo memo2 = MemoProvider.readMemoFromCursor(cursor);

	assertThat(memo1.getId(), not(equalTo(Memo.NO_ID)));
	assertThat(memo1.getId(), not(equalTo(memo2.getId())));

	long id = memo2.getId();
	Cursor cursor2 = shadowResolver.query(ContentUris.withAppendedId(MemoProvider.MEMOS_URI, id),
					      null, null, null, null);
	assertThat(cursor2.getCount(), equalTo(1));
	cursor2.moveToFirst();
	Memo memo3 = MemoProvider.readMemoFromCursor(cursor2);
	assertThat(memo3.getId(), equalTo(memo2.getId()));
	assertThat(memo3.getText(), equalTo(memo2.getText()));
    }

    @Test public void update() throws Exception {
	Cursor cursor = shadowResolver.query(MemoProvider.MEMOS_URI, null, null, null, null);
	cursor.moveToFirst();
	Memo memo = MemoProvider.readMemoFromCursor(cursor);
	Memo newMemo = new Memo("a new text");

	int updateResult = shadowResolver.update(ContentUris.withAppendedId(MemoProvider.MEMOS_URI, memo.getId()),
						 MemoProvider.getMemoContentValues(newMemo), null, null);

	assertThat(updateResult, equalTo(1));
	Cursor cursor2 = shadowResolver.query(MemoProvider.MEMOS_URI, null, null, null, null);
	assertThat(cursor2.getCount(), equalTo(2));
	verifyMemos(cursor2, "a new text", "Other memo");

	cursor2.moveToFirst();
	Memo newMemoFromProvider = MemoProvider.readMemoFromCursor(cursor2);
	assertThat(memo.getId(), equalTo(newMemoFromProvider.getId()));

	Cursor cursor3 = shadowResolver.query(ContentUris.withAppendedId(MemoProvider.MEMOS_URI, memo.getId()),
					      null, null, null, null);
	assertThat(cursor3.getCount(), equalTo(1));
	verifyMemos(cursor3, "a new text");
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

    private List<String> readMemoFile(String fileName) throws Exception {
	InputStream latest = Robolectric.application.openFileInput(fileName);
	MemoReader reader = new MemoReader(latest);
	List<String> memos = new LinkedList<String>();
	while (true) {
	    Memo memo = reader.readMemo();
	    if (memo == null)
		return memos;
	    memos.add(memo.getText());
	}
    }
}
