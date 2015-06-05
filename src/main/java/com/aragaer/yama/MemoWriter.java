package com.aragaer.yama;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;


public class MemoWriter {

    static String fileNameForDate(Date date) {
	return MemoReaderWriter.fileNameForDate(date);
    }

    private final OutputStream stream_;

    public MemoWriter(OutputStream stream) {
	stream_ = stream;
    }

    public void write(Memo memo) throws IOException {
	MemoReaderWriter.write(memo, stream_);
    }
}
