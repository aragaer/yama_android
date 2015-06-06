package com.aragaer.yama;

import java.util.Arrays;

import org.junit.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;


public class MemoTest {
    @Test public void testCreate() {
	Memo memo = new Memo("A memo");

	assertThat(memo.getText(),
		   equalTo("A memo"));
    }

    @Test public void lines() {
	Memo memo = new Memo("A line", "Another line");

	assertThat(memo.getText(),
		   equalTo("A line\nAnother line"));
    }

    @Test public void linesList() {
	Memo memo = new Memo(Arrays.asList("A line", "Another line"));

	assertThat(memo.getText(),
		   equalTo("A line\nAnother line"));
    }

    @Test public void id() {
	Memo memo = new Memo("a memo");
	assertThat(memo.getId(), equalTo(Memo.NO_ID));

	Memo memo2 = new Memo(10, "a memo");
	assertThat(memo2.getId(), equalTo(10L));

	memo.setId(20);
	assertThat(memo.getId(), equalTo(20L));
    }
}
