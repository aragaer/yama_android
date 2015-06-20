package com.aragaer.yama;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
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
	assertThat(memos.size(), equalTo(0));
    }

    @Test public void readMemoFile() {
	fileProvider.setContents("memo", "memo\nanother memo\nYo\n");
	List<? extends Memo> memos = readerWriter.readMemosForKey("memo");
	assertThat(memos.size(), equalTo(3));
	assertThat(memos.get(0).getText(), equalTo("memo"));
	assertThat(memos.get(1).getText(), equalTo("another memo"));
    }

    @Test public void writeMemoFile() {
	List<TestMemo> memos = Arrays.asList(new TestMemo("memo"), new TestMemo("other"));
	readerWriter.writeMemosForKey("memo", memos);
	String data = fileProvider.files.get("memo");
	assertThat(data, equalTo("memo\nother\n"));
    }

    @Test public void fileSuffix() {
	assertThat(PlainReaderWriter.FILE_SUFFIX, equalTo(""));
    }
}
