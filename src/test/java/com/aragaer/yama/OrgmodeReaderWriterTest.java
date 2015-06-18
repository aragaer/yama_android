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


public class OrgmodeReaderWriterTest {

    TestFileProvider fileProvider;
    OrgmodeReaderWriter readerWriter;

    @Before public void setUp() {
	fileProvider = new TestFileProvider();
	readerWriter = new OrgmodeReaderWriter(fileProvider);
    }

    @Test public void readMemoFile() {
	fileProvider.setContents("* \n  memo\n* \n  another memo\n  is multiline\n");
	List<? extends Memo> memos = readerWriter.readMemosForKey("memo");
	assertThat(memos.size(), equalTo(2));
	assertThat(memos.get(0).getText(), equalTo("memo"));
	assertThat(memos.get(1).getText(), equalTo("another memo\nis multiline"));
    }

    @Test public void writeMemoFile() {
	List<TestMemo> memos = Arrays.asList(new TestMemo("memo"), new TestMemo("other\nmulti"));
	readerWriter.writeMemosForKey("memo", memos);
	assertThat(fileProvider.opened, equalTo("memo"));
	String data = fileProvider.written.toString();
	assertThat(data, equalTo("* \n  memo\n* \n  other\n  multi\n"));
    }

    private static class TestFileProvider implements MemoFileProvider {

	String opened;
	String contents;
	ByteArrayOutputStream written;

	void setContents(String newContents) {
	    contents = newContents;
	}

	@Override public List<String> fileList() {
	    return Arrays.asList(contents == null ? new String[0] : new String[] {"memo"});
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
