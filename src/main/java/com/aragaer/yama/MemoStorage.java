package com.aragaer.yama;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public class MemoStorage {

    private final MemoFileProvider _fileProvider;
    private final MemoFormatter _formatter;

    public MemoStorage(MemoFileProvider fileProvider) {
	_fileProvider = fileProvider;
	_formatter = new OrgmodeFormatter();
	convertLegacyFile();
    }

    private void convertLegacyFile() {
	MemoFormatter formatter = new PlainFormatter();
	String legacyFile = getFileName(formatter);
	if (_fileProvider.fileList().contains(legacyFile)) {
	    writeMemos(readMemos(formatter));
	    _fileProvider.deleteFile(legacyFile);
	}
    }

    private String getFileName(MemoFormatter formatter) {
	return "memo"+formatter.getFileSuffix();
    }

    public List<Memo> readMemos() {
	return readMemos(_formatter);
    }

    private List<Memo> readMemos(MemoFormatter formatter) {
	InputStream stream = _fileProvider.openFileForReading(getFileName(formatter));
	LinkedList<Memo> result = new LinkedList<Memo>();
	if (stream != null) {
	    Scanner swallow = new Scanner(stream).useDelimiter("\\A");
	    String contents = swallow.hasNext() ? swallow.next() : "";
	    formatter.parseAllTo(result, contents);
	    _fileProvider.closeFile(stream);
	}
	return result;
    }

    public void writeMemos(List<Memo> memos) {
	writeMemos(memos, _formatter);
    }

    private void writeMemos(List<Memo> memos, MemoFormatter formatter) {
	OutputStream stream = _fileProvider.openFileForWriting(getFileName(formatter));
	StringBuilder builder = new StringBuilder();
	formatter.formatAllTo(memos, builder);
	try {
	    stream.write(builder.toString().getBytes());
	} catch (IOException e) {
	}
	_fileProvider.closeFile(stream);
    }
}
