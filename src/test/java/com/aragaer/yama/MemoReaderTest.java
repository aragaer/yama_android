package com.aragaer.yama;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;


public class MemoReaderTest {
    @Test public void readMemo() throws Exception {
	String fileContents = ""
	    + "* \n"
	    + "  line1\n"
	    + "  line2\n"
	    + "* \n"
	    + "  other one\n";
	ByteArrayInputStream stream = new ByteArrayInputStream(fileContents.getBytes());
	MemoReader reader = new MemoReader(stream);

	assertThat(reader.readMemo().getText(),
		   equalTo("line1\nline2"));
	assertThat(reader.readMemo().getText(),
		   equalTo("other one"));
    }
}
