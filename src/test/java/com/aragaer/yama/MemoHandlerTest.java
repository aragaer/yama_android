package com.aragaer.yama;

import java.util.*;
import org.junit.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class MemoHandlerTest {

    private MemoHandler<Integer> storage;
    private TestReaderWriter readerWriter;

    @Before public void setUp() {
	readerWriter = new TestReaderWriter();
	storage = new MemoHandler<Integer>(readerWriter);
	assertThat(storage.getAllActiveMemos().size(), equalTo(0));
    }

    @Test public void addMemo() {
	Memo memo = storage.storeMemo("a memo");
	assertThat(memo.getText(), equalTo("a memo"));

	List<Memo> memos = storage.getAllActiveMemos();
	assertThat(memos.size(), equalTo(1));
	assertThat(memos.get(0).getText(), equalTo("a memo"));

	storage.dumpToReaderWriter();

	assertThat(readerWriter.memos.size(), equalTo(1));
	assertThat(readerWriter.memos.get(1).size(), equalTo(1));
	assertThat(readerWriter.memos.get(1).get(0).getText(), equalTo("a memo"));
    }

    @Test public void replaceMemo() {
	Memo memo = storage.storeMemo("a memo");

	storage.replaceMemo(memo, Arrays.asList("a new memo"));

	List<Memo> memos = storage.getAllActiveMemos();
	assertThat(memos.size(), equalTo(1));
	assertThat(memos.get(0).getText(), equalTo("a new memo"));

	storage.dumpToReaderWriter();

	assertThat(readerWriter.memos.size(), equalTo(1));
	assertThat(readerWriter.memos.get(1).size(), equalTo(1));
	assertThat(readerWriter.memos.get(1).get(0).getText(), equalTo("a new memo"));
    }

    @Test public void deleteMemo() {
	Memo memo = storage.storeMemo("a memo");

	storage.deleteMemo(memo);

	List<Memo> memos = storage.getAllActiveMemos();
	assertThat(memos.size(), equalTo(0));

	storage.dumpToReaderWriter();

	assertThat(readerWriter.memos.size(), equalTo(0));
    }

    @Test public void useReaderWriter() {
	List<Memo> test_memos = new LinkedList<Memo>();
	test_memos.add(new Memo("a memo"));
	readerWriter.writeMemosForKey(0, test_memos);

	storage.updateFromReaderWriter();

	List<Memo> memos = storage.getAllActiveMemos();
	assertThat(memos.size(), equalTo(1));
	assertThat(memos.get(0).getText(), equalTo("a memo"));

	storage.storeMemo("new memo");

	memos = storage.getAllActiveMemos();
	assertThat(memos.size(), equalTo(2));
	assertThat(memos.get(0).getText(), equalTo("a memo"));
	assertThat(memos.get(1).getText(), equalTo("new memo"));

	storage.dumpToReaderWriter();

	assertThat(readerWriter.memos.size(), equalTo(2));
	assertThat(readerWriter.memos.get(0).size(), equalTo(1));
	assertThat(readerWriter.memos.get(1).size(), equalTo(1));
    }

    private static class TestReaderWriter implements MemoReaderWriter<Integer> {
	public TreeMap<Integer, List<Memo>> memos;

	public TestReaderWriter() {
	    memos = new TreeMap<Integer, List<Memo>>();
	}

	public SortedSet<Integer> getKeys() {
	    return ((NavigableMap<Integer, ?>) memos).navigableKeySet();
	}

	public List<Memo> readMemosForKey(Integer key) {
	    return memos.get(key);
	}

	public void writeMemosForKey(Integer key, List<Memo> memos) {
	    this.memos.put(key, memos);
	}

	public Integer getDefaultKey() {
	    return 1;
	}

	public void dropKey(Integer key) {
	    this.memos.remove(key);
	}
    }
}
