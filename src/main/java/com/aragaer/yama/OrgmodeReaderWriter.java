package com.aragaer.yama;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.StringBuffer;
import java.lang.StringBuilder;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;


public class OrgmodeReaderWriter implements MemoReaderWriter {

    public static final String FILE_SUFFIX = OrgmodeFormatter.FILE_SUFFIX;

    private final MemoFileProvider _fileProvider;
    private final MemoFormatter formatter;

    public OrgmodeReaderWriter(MemoFileProvider fileProvider) {
	_fileProvider = fileProvider;
	formatter = new OrgmodeFormatter();
    }

    @Override public SortedSet<String> getKeys() {
	return null;
    }

    private String getFileNameForKey(String key) {
	return key+FILE_SUFFIX;
    }

    @Override public void writeMemosForKey(String key, List<Memo> memos) {
	OutputStream stream = _fileProvider.openFileForWriting(getFileNameForKey(key));
	StringBuilder builder = new StringBuilder();
	formatter.formatAllTo(memos, builder);
	try {
	    stream.write(builder.toString().getBytes());
	} catch (IOException e) {
	}
	_fileProvider.closeFile(stream);
    }

    @Override public List<Memo> readMemosForKey(String key) {
	InputStream stream;
	LinkedList<Memo> result = new LinkedList<Memo>();
	stream = _fileProvider.openFileForReading(getFileNameForKey("memo"));
	if (stream != null) {
	    Scanner swallow = new Scanner(stream).useDelimiter("\\A");
	    String contents = swallow.hasNext() ? swallow.next() : "";
	    formatter.parseAllTo(result, contents);
	    _fileProvider.closeFile(stream);
	}
	return result;
    }

    @Override public String getDefaultKey() {
	return null;
    }

    @Override public void dropKey(String key) {
    }
}
