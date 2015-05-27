package com.aragaer.yama;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MemoWriter {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'.txt'");

    static String fileNameForDate(Date date) {
	return format.format(date);
    }

    private OutputStream stream_;

    public MemoWriter(OutputStream stream) {
	stream_ = stream;
    }

    public void write(Memo memo) throws IOException {
	stream_.write("* \n".getBytes());
	for (String line : memo.getText().split("\n")) {
	    stream_.write("  ".getBytes());
	    stream_.write(line.getBytes());
	    stream_.write(10);
	}
    }
}
