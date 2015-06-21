package com.aragaer.yama;

import java.util.LinkedList;
import java.util.List;

import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class PlainFormatterTest {

    @Rule public ExpectedException exception = ExpectedException.none();

    PlainFormatter formatter;

    @Before public void setUp() {
	formatter = new PlainFormatter();
    }

    @Test public void parsePlainList() {
	List<Memo> memos = new LinkedList<Memo>();
	String contents = "memo\nanother memo\nYo\n";
	formatter.parseAllTo(memos, contents);
	assertThat(memos.size(), equalTo(3));
	assertThat(memos.get(0).getText(), equalTo("memo"));
	assertThat(memos.get(1).getText(), equalTo("another memo"));
	assertThat(memos.get(2).getText(), equalTo("Yo"));
    }

    @Test public void neverWrite() {
	exception.expect(UnsupportedOperationException.class);
	formatter.formatAllTo(null, null);
    }

    @Test public void fileSuffix() {
	assertThat(formatter.getFileSuffix(), equalTo(""));
    }
}
