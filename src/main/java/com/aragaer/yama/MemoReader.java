package com.aragaer.yama;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Date;


public class MemoReader {

    private BufferedReader reader_;

    public MemoReader(InputStream stream) {
	reader_ = new BufferedReader(new InputStreamReader(stream));
    }

    public Memo readMemo() throws IOException {
	return MemoReaderWriter.read(reader_);
    }

    public static Date dateFromFileName(String fileName) {
	return MemoReaderWriter.dateFromFileName(fileName);
    }
}
