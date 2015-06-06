package com.aragaer.yama;

import java.lang.StringBuilder;
import java.util.Arrays;
import java.util.List;


public class Memo {
    private String text_;
    private long id_;

    public static long NO_ID = -1;
    
    public Memo(String... text) {
	this(NO_ID, Arrays.asList(text));
    }

    public Memo(long id, String... text) {
	this(id, Arrays.asList(text));
    }

    public Memo(List<String> text) {
	this(NO_ID, text);
    }

    public Memo(long id, List<String> text) {
	id_ = id;
	StringBuilder builder = new StringBuilder();
	for (String line : text)
	    builder.append(line).append('\n');
	text_ = builder.substring(0, builder.length() - 1);
    }

    public String getText() {
	return text_;
    }

    public long getId() {
	return id_;
    }

    public void setId(long id) {
	id_ = id;
    }
}
