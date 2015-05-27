package com.aragaer.yama;

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
}
