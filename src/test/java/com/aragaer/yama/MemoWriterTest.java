package com.aragaer.yama;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.junit.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;


public class MemoWriterTest {

    @Test public void fileNameTest() {
	Calendar calendar = Calendar.getInstance();
	calendar.setTimeInMillis(0);
	calendar.set(2015, 4, 26, 12, 4, 0);
	assertThat(MemoWriter.fileNameForDate(calendar.getTime()),
		   equalTo("2015-05-26.txt"));
    }

    @Test public void writeOneLineMemo() {
	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	MemoWriter writer = new MemoWriter(stream);
        Memo memo = new Memo("A line");

	try {
	    writer.write(memo);
	} catch (IOException e) {
	    fail("Exception thrown when writing to a buffer");
	}

	assertThat(stream.toString(),
		   equalTo("* \n  A line\n"));
    }

    @Test public void writeMultiLineMemo() {
	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	MemoWriter writer = new MemoWriter(stream);
        Memo memo = new Memo("line1\nline2");

	try {
	    writer.write(memo);
	} catch (IOException e) {
	    fail("Exception thrown when writing to a buffer");
	}

	assertThat(stream.toString(),
		   equalTo("* \n  line1\n  line2\n"));
    }
}
