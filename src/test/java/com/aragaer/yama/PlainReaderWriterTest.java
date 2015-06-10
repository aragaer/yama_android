package com.aragaer.yama;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class PlainReaderWriterTest {

    TestFileProvider fileProvider;
    PlainReaderWriter readerWriter;

    @Before public void setUp() {
	fileProvider = new TestFileProvider();
	readerWriter = new PlainReaderWriter(fileProvider);
    }

    @Test public void shouldStoreToSingleList() {
	assertThat(readerWriter.getKeys().size(),
		   equalTo(1));
    }

    @Test public void noMemoFileIsOK() {
	List<? extends Memo> memos = readerWriter.readMemosForKey("memo");
	assertThat(fileProvider.opened, equalTo("memo"));
	assertThat(memos.size(), equalTo(0));
    }

    @Test public void readMemoFile() {
	fileProvider.setContents("memo\nanother memo\nYo\n");
	List<? extends Memo> memos = readerWriter.readMemosForKey("memo");
	assertThat(memos.size(), equalTo(3));
	assertThat(memos.get(0).getText(), equalTo("memo"));
	assertThat(memos.get(1).getText(), equalTo("another memo"));
    }

    @Test public void writeMemoFile() {
	List<TestMemo> memos = Arrays.asList(new TestMemo("memo"), new TestMemo("other"));
	readerWriter.writeMemosForKey("memo", memos);
	assertThat(fileProvider.opened, equalTo("memo"));
	String data = fileProvider.written.toString();
	assertThat(data, equalTo("memo\nother\n"));
    }

    private static class TestMemo implements Memo {
	private String text;

	TestMemo(String text) {
	    this.text = text;
	}

	@Override public String getText() {
	    return text;
	}
    }

    private static class TestFileProvider implements MemoFileProvider {

	String opened;
	String contents;
	ByteArrayOutputStream written;

	void setContents(String newContents) {
	    contents = newContents;
	}

	@Override public InputStream openFileForReading(String name) {
	    opened = name;
	    if (contents == null)
		return null;
	    else
		return new ByteArrayInputStream(contents.getBytes());
	}

	@Override public OutputStream openFileForWriting(String name) {
	    opened = name;
	    written = new ByteArrayOutputStream();
	    return written;
	}
    }
}
