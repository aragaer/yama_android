package com.aragaer.yama;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;


public class PlainReaderWriter implements MemoReaderWriter<String> {

    private static final TreeSet<String> KEY_SET = new TreeSet<String>();

    static {
	KEY_SET.add("");
    }

    private final MemoFileProvider fileProvider;

    public PlainReaderWriter(MemoFileProvider fileProvider) {
	this.fileProvider = fileProvider;
    }

    @Override public SortedSet<String> getKeys() {
	return KEY_SET;
    }

    @Override public void writeMemosForKey(String key, List<? extends Memo> memos) {
	OutputStream stream = fileProvider.openFileForWriting("memo");
	try {
	    for (Memo memo : memos) {
		stream.write(memo.getText().getBytes());
		stream.write("\n".getBytes());
	    }
	    stream.close();
	} catch (Exception e) {
	    // oops
	}
    }

    @Override public List<? extends Memo> readMemosForKey(String key) {
	InputStream stream = fileProvider.openFileForReading("memo");
	List<_Memo> result = new LinkedList<_Memo>();
	if (stream == null)
	    return result;
	BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
	try {
	    while (true) {
		String line = reader.readLine();
		if (line == null)
		    break;
		result.add(new _Memo(line));
	    }
	    stream.close();
	} catch (Exception e) {
	    // oops?
	}
	return result;
    }

    @Override public String getDefaultKey() {
	return "";
    }

    @Override public void dropKey(String key) {
    }

    private static class _Memo implements Memo {

	private String text;

	_Memo(String text) {
	    this.text = text;
	}

	@Override public String getText() {
	    return text;
	}
    }
}
