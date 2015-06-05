package com.aragaer.yama;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;


public class MemoReaderWriter {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'.txt'");

    public static Date dateFromFileName(String fileName) {
	return format.parse(fileName, new ParsePosition(0));
    }

    public static String fileNameForDate(Date date) {
	return format.format(date);
    }

    public static void write(Memo memo, OutputStream stream) throws IOException {
	stream.write("* \n".getBytes());
	for (String line : memo.getText().split("\n")) {
	    stream.write("  ".getBytes());
	    stream.write(line.getBytes());
	    stream.write('\n');
	}
    }

    public static Memo read(BufferedReader reader) throws IOException {
	reader.readLine();
	List<String> lines = new LinkedList<String>();
	while (!memoEnded(reader))
	    lines.add(reader.readLine().trim());
	return lines.isEmpty() ? null : new Memo(lines);
    }

    private static boolean memoEnded(BufferedReader reader) throws IOException {
	if (!reader.ready())
	    return true;
	reader.mark(1);
	boolean result = reader.read() == '*';
	reader.reset();
	return result;
    }
}
