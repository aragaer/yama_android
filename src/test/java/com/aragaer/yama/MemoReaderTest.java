package com.aragaer.yama;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;

import org.junit.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
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

	assertThat(reader.readMemo().getText(), equalTo("line1\nline2"));
	assertThat(reader.readMemo().getText(), equalTo("other one"));
	assertThat(reader.readMemo(), is(nullValue()));
    }

    @Test public void parseFileNameDate() {
	Calendar calendar = Calendar.getInstance();
	calendar.setTimeInMillis(0);
	calendar.set(2015, 4, 26, 0, 0, 0);
	assertThat(MemoReader.dateFromFileName("2015-05-26.txt"),
		   equalTo(calendar.getTime()));
    }
}
