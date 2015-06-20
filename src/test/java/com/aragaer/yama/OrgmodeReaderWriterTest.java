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
import java.util.SortedSet;

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

    @Test public void fileSuffix() {
	assertThat(OrgmodeReaderWriter.FILE_SUFFIX, equalTo(".org"));
    }

    @Test public void readMemoFile() {
	fileProvider.setContents("memo.org", "* \n  memo\n* \n  another memo\n  is multiline\n");
	List<? extends Memo> memos = readerWriter.readMemosForKey("memo");
	assertThat(memos.size(), equalTo(2));
	assertThat(memos.get(0).getText(), equalTo("memo"));
	assertThat(memos.get(1).getText(), equalTo("another memo\nis multiline"));
    }

    @Test public void writeMemoFile() {
	List<TestMemo> memos = Arrays.asList(new TestMemo("memo"), new TestMemo("other\nmulti"));
	readerWriter.writeMemosForKey("memo", memos);
	String data = fileProvider.files.get("memo.org");
	assertThat(data, equalTo("* \n  memo\n* \n  other\n  multi\n"));
    }
}
