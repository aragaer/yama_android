package com.aragaer.yama;

import java.lang.StringBuilder;
import java.util.List;


public class PlainFormatter implements MemoFormatter {
    public static final String FILE_SUFFIX = "";

    @Override public void formatAllTo(List<Memo> memos, StringBuilder out) {
	for (Memo memo : memos)
	    out.append(memo.getText()).append('\n');
    }

    @Override public void parseAllTo(List<Memo> memos, String contents) {
	for (String line : contents.split("\n"))
	    if (!line.isEmpty())
		memos.add(new Memo(line));
    }

}
