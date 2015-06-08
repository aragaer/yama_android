package com.aragaer.yama;

import java.util.List;
import org.junit.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class MemoStorageTest {

    private MemoStorage storage;

    @Before public void setUp() {
	storage = new MemoStorage();
    }

    @Test public void isEmpty() {
	List<Memo> memos = storage.getAllActiveMemos();
	assertThat(memos.size(), equalTo(0));
    }

    @Test public void addMemo() {
	Memo memo = storage.storeMemo("a memo");
	assertThat(memo.getText(), equalTo("a memo"));

	List<Memo> memos = storage.getAllActiveMemos();
	assertThat(memos.size(), equalTo(1));
	assertThat(memos.get(0).getText(), equalTo("a memo"));
    }

    @Test public void replaceMemo() {
	Memo memo = storage.storeMemo("a memo");

	storage.replaceMemo(memo, "a new memo");

	List<Memo> memos = storage.getAllActiveMemos();
	assertThat(memos.size(), equalTo(1));
	assertThat(memos.get(0).getText(), equalTo("a new memo"));
    }

    @Test public void deleteMemo() {
	Memo memo = storage.storeMemo("a memo");

	storage.deleteMemo(memo);

	List<Memo> memos = storage.getAllActiveMemos();
	assertThat(memos.size(), equalTo(0));
    }
}
