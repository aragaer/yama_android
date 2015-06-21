package com.aragaer.yama;

import java.util.List;


public class OrgmodeFormatter implements MemoFormatter {
    @Override public String getFileSuffix() {
	return ".org";
    }

    @Override public void formatAllTo(List<Memo> memos, StringBuilder out) {
	new Formatter(out).formatAll(memos);
    }

    @Override public void parseAllTo(List<Memo> memos, String contents) {
	new Parser(contents).parseAll(memos);
    }

    private static class Formatter {

	private final StringBuilder out;

	Formatter(StringBuilder out) {
	    this.out = out;
	}

	void formatAll(List<Memo> memos) {
	    for (Memo memo : memos)
		format(memo);
	}

	private void format(Memo memo) {
	    out.append("* \n");
	    for (String line : memo.getText().split("\n"))
		out.append("  ").append(line).append('\n');
	}
    }

    private static class Parser {

	private final String data;

	Parser(String data) {
	    this.data = data;
	}

	void parseAll(List<Memo> list) {
	    for (String memo: data.split("\\* \n")) {
		String memoContents = parse(memo);
		if (!memoContents.isEmpty())
		    list.add(new Memo(memoContents));
	    }
	}

	private String parse(String memo) {
	    StringBuilder builder = new StringBuilder();
	    for (String line : memo.split("\n"))
		builder.append(line.trim()).append('\n');
	    return builder.substring(0, builder.length() - 1);
	}
    }
}
