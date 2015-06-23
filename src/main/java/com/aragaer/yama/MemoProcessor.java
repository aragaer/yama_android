package com.aragaer.yama;

import java.util.ArrayList;
import java.util.List;


public class MemoProcessor {

    public static List<String> sanitize(String... lines) {
	List<String> result = new ArrayList<String>(lines.length);
	for (String line : lines) {
	    String trimmed = line.trim();
	    if (trimmed.isEmpty())
		continue;
	    result.add(trimmed);
	}
	return result;
    }
}
