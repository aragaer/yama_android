package com.aragaer.yama;

import java.util.List;


public class PlainFormatter implements MemoFormatter {
    @Override public String getFileSuffix() {
	return "";
    }

    @Override public void formatAllTo(List<Memo> memos, StringBuilder out) {
	throw new UnsupportedOperationException("Never write plain format memos again");
    }

    @Override public void parseAllTo(List<Memo> memos, String contents) {
	for (String line : contents.split("\n"))
	    if (!line.isEmpty())
		memos.add(new Memo(line));
    }
}
