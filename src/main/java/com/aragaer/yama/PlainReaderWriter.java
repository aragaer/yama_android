package com.aragaer.yama;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.StringBuilder;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;


public class PlainReaderWriter implements MemoReaderWriter {

    public static final String FILE_SUFFIX = PlainFormatter.FILE_SUFFIX;

    private static final TreeSet<String> KEY_SET = new TreeSet<String>();
    private final MemoFormatter formatter;

    static {
	KEY_SET.add("");
    }

    private final MemoFileProvider _fileProvider;

    public PlainReaderWriter(MemoFileProvider fileProvider) {
	_fileProvider = fileProvider;
	formatter = new PlainFormatter();
    }

    @Override public SortedSet<String> getKeys() {
	return KEY_SET;
    }

    @Override public void writeMemosForKey(String key, List<Memo> memos) {
	OutputStream stream = _fileProvider.openFileForWriting("memo");
	StringBuilder builder = new StringBuilder();
	formatter.formatAllTo(memos, builder);
	try {
	    stream.write(builder.toString().getBytes());
	} catch (Exception e) {
	    // oops
	}
	_fileProvider.closeFile(stream);
    }


    @Override public List<Memo> readMemosForKey(String key) {
	InputStream stream = _fileProvider.openFileForReading(getFileNameForKey("memo"));
	LinkedList<Memo> result = new LinkedList<Memo>();
	if (stream != null) {
	    Scanner swallow = new Scanner(stream).useDelimiter("\\A");
	    String contents = swallow.hasNext() ? swallow.next() : "";
	    formatter.parseAllTo(result, contents);
	    _fileProvider.closeFile(stream);
	}
	return result;
    }

    private String getFileNameForKey(String key) {
	return key+FILE_SUFFIX;
    }

    @Override public String getDefaultKey() {
	return "";
    }

    @Override public void dropKey(String key) {
    }
}
