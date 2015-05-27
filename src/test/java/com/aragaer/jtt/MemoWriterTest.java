package com.aragaer.yama;

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
}
