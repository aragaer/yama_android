package com.aragaer.yama;

import java.util.Arrays;
import java.util.List;

import org.junit.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class MemoStorageTest {

    TestFileProvider fileProvider;
    MemoStorage storage;

    @Before public void setUp() {
	fileProvider = new TestFileProvider();
    }

    @Test public void readsPlainFile() {
	fileProvider.setContents("memo", "a memo\nother memo\n");
	storage = new MemoStorage(fileProvider);
	List<? extends Memo> memos = storage.readMemos();

	assertThat(memos.size(), equalTo(2));
    }

    @Test public void readsOrgmodeFile() {
	fileProvider.setContents("memo.org", "* \n  a memo\n");
	storage = new MemoStorage(fileProvider);
	List<? extends Memo> memos = storage.readMemos();

	assertThat(memos.size(), equalTo(1));
    }

    @Test public void convertsPlainToOrgmode() {
	fileProvider.setContents("memo", "a memo\nother memo\n");
	storage = new MemoStorage(fileProvider);
	List<? extends Memo> memos = storage.readMemos();

	assertThat(fileProvider.fileList().size(), equalTo(1));
	assertThat(fileProvider.fileList().get(0), equalTo("memo.org"));
	assertThat(fileProvider.files.get("memo.org"),
		   equalTo("* \n  a memo\n* \n  other memo\n"));
    }

    @Test public void writesOrgmodeFile() {
	storage = new MemoStorage(fileProvider);

	storage.writeMemos(Arrays.asList(new TestMemo("some memo")));

	assertThat(fileProvider.fileList().size(), equalTo(1));
	assertThat(fileProvider.fileList().get(0), equalTo("memo.org"));
	assertThat(fileProvider.files.get("memo.org"),
		   equalTo("* \n  some memo\n"));

	storage.writeMemos(Arrays.asList(new TestMemo("edited memo")));

	assertThat(fileProvider.fileList().size(), equalTo(1));
	assertThat(fileProvider.fileList().get(0), equalTo("memo.org"));
	assertThat(fileProvider.files.get("memo.org"),
		   equalTo("* \n  edited memo\n"));
    }
}
