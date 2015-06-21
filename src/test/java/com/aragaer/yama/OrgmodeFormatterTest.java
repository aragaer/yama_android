package com.aragaer.yama;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class OrgmodeFormatterTest {

    MemoFormatter formatter;

    @Before public void setUp() {
	formatter = new OrgmodeFormatter();
    }

    @Test public void fileSuffix() {
	assertThat(formatter.getFileSuffix(), equalTo(".org"));
    }

    @Test public void parseMemos() {
	String contents =  "* \n  memo\n* \n  another memo\n  is multiline\n";
	List<Memo> memos = new LinkedList<Memo>();

	formatter.parseAllTo(memos, contents);

	assertThat(memos.size(), equalTo(2));
	assertThat(memos.get(0).getText(), equalTo("memo"));
	assertThat(memos.get(1).getText(), equalTo("another memo\nis multiline"));
    }

    @Test public void formatMemos() {
	List<Memo> memos = Arrays.asList(new Memo("memo"), new Memo("other\nmulti"));
	StringBuilder builder = new StringBuilder();

	formatter.formatAllTo(memos, builder);

	assertThat(builder.toString(), equalTo("* \n  memo\n* \n  other\n  multi\n"));
    }
}
