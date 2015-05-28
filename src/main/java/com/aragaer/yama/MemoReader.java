package com.aragaer.yama;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class MemoReader {

    private BufferedReader reader_;

    public MemoReader(InputStream stream) {
	reader_ = new BufferedReader(new InputStreamReader(stream));
    }

    public Memo readMemo() throws IOException {
	reader_.readLine();
	List<String> lines = new LinkedList<String>();
	while (reader_.ready()) {
	    reader_.mark(1);
	    if (reader_.read() == '*') {
		reader_.reset();
		break;
	    }
	    String line = reader_.readLine();
	    lines.add(line.trim());
	}
	return lines.isEmpty() ? null : new Memo(lines);
    }

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'.txt'");

    public static Date dateFromFileName(String fileName) {
	return format.parse(fileName, new ParsePosition(0));
    }
}
