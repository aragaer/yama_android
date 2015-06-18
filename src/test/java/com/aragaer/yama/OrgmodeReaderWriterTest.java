package com.aragaer.yama;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	fileProvider.setContents("memo", "* \n  memo\n* \n  another memo\n  is multiline\n");
	List<? extends Memo> memos = readerWriter.readMemosForKey("memo");
	assertThat(memos.size(), equalTo(2));
	assertThat(memos.get(0).getText(), equalTo("memo"));
	assertThat(memos.get(1).getText(), equalTo("another memo\nis multiline"));
    }

    @Test public void writeMemoFile() {
	List<TestMemo> memos = Arrays.asList(new TestMemo("memo"), new TestMemo("other\nmulti"));
	readerWriter.writeMemosForKey("memo", memos);
	String data = fileProvider.files.get("memo");
	assertThat(data, equalTo("* \n  memo\n* \n  other\n  multi\n"));
    }

    private static class TestFileProvider implements MemoFileProvider {

	Map<String, String> files = new HashMap<String, String>();
	Map<ByteArrayOutputStream, String> opened = new HashMap<ByteArrayOutputStream, String>();

	void setContents(String fileName, String contents) {
	    files.put(fileName, contents);
	}

	@Override public List<String> fileList() {
	    return new ArrayList<String>(files.keySet());
	}

	@Override public InputStream openFileForReading(String name) {
	    String contents = files.get(name);
	    if (contents == null)
		return null;
	    else
		return new ByteArrayInputStream(contents.getBytes());
	}

	@Override public OutputStream openFileForWriting(String name) {
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    opened.put(stream, name);
	    return stream;
	}

	@Override public void closeFile(Closeable stream) {
	    if (stream instanceof ByteArrayOutputStream) {
		ByteArrayOutputStream baos = (ByteArrayOutputStream) stream;
		String filename = opened.get(baos);
		files.put(filename, baos.toString());
		opened.remove(baos);
	    }
	    try {
		stream.close();
	    } catch (IOException e) {
		fail("Failed to close stream: " + e);
	    }
	}
    }
}
