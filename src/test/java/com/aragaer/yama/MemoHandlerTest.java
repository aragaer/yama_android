package com.aragaer.yama;

import java.util.*;
import org.junit.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class MemoHandlerTest {

    private MemoHandler handler;
    private TestMemoStorage storage;

    @Before public void setUp() {
	storage = new TestMemoStorage();
	handler = new MemoHandler(storage);
	assertThat(handler.getAllActiveMemos().size(), equalTo(0));
    }

    @Test public void addMemo() {
	Memo memo = handler.storeMemo("a memo");
	assertThat(memo.getText(), equalTo("a memo"));

	List<Memo> memos = handler.getAllActiveMemos();
	assertThat(memos.size(), equalTo(1));
	assertThat(memos.get(0).getText(), equalTo("a memo"));

	handler.dumpToStorage();

	assertThat(storage.memos.size(), equalTo(1));
	assertThat(storage.memos.get(0).getText(), equalTo("a memo"));
    }

    @Test public void replaceMemo() {
	handler.storeMemo("a memo");
	Memo memo = handler.getAllActiveMemos().get(0);

	handler.replaceMemo(memo, Arrays.asList("a new memo"));

	List<Memo> memos = handler.getAllActiveMemos();
	assertThat(memos.size(), equalTo(1));
	assertThat(memos.get(0).getText(), equalTo("a new memo"));

	handler.dumpToStorage();

	assertThat(storage.memos.size(), equalTo(1));
	assertThat(storage.memos.get(0).getText(), equalTo("a new memo"));
    }

    @Test public void deleteMemo() {
	Memo memo = handler.storeMemo("a memo");

	handler.deleteMemo(memo);

	List<Memo> memos = handler.getAllActiveMemos();
	assertThat(memos.size(), equalTo(0));

	handler.dumpToStorage();

	assertThat(storage.memos.size(), equalTo(0));
    }

    @Test public void useStorage() {
	List<Memo> test_memos = new LinkedList<Memo>();
	test_memos.add(new Memo("a memo"));
	storage.writeMemos(test_memos);

	handler.updateFromStorage();

	List<Memo> memos = handler.getAllActiveMemos();
	assertThat(memos.size(), equalTo(1));
	assertThat(memos.get(0).getText(), equalTo("a memo"));

	handler.storeMemo("new memo");

	memos = handler.getAllActiveMemos();
	assertThat(memos.size(), equalTo(2));
	assertThat(memos.get(0).getText(), equalTo("a memo"));
	assertThat(memos.get(1).getText(), equalTo("new memo"));

	handler.dumpToStorage();

	assertThat(storage.memos.size(), equalTo(2));
    }

    private static class TestMemoStorage extends MemoStorage {
	List<Memo> memos;

	TestMemoStorage() {
	    super(new TestFileProvider());
	    memos = new LinkedList<Memo>();
	}

	public List<Memo> readMemos() {
	    return memos;
	}

	public void writeMemos(List<Memo> memos) {
	    this.memos = memos;
	}
    }
}
