package com.aragaer.yama;

import java.lang.StringBuilder;
import java.util.Arrays;
import java.util.List;


public class Memo {
    private String text_;
    
    public Memo(String... text) {
	this(Arrays.asList(text));
    }

    public Memo(List<String> text) {
	StringBuilder builder = new StringBuilder();
	for (String line : text)
	    builder.append(line).append('\n');
	text_ = builder.substring(0, builder.length() - 1);
    }

    public String getText() {
	return text_;
    }
}