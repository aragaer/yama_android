package com.aragaer.yama;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.StringBuilder;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;


public class OrgmodeReaderWriter implements MemoReaderWriter<String> {

    public static final String FILE_SUFFIX = ".org";

    private final MemoFileProvider _fileProvider;

    public OrgmodeReaderWriter(MemoFileProvider fileProvider) {
	_fileProvider = fileProvider;
    }

    @Override public SortedSet<String> getKeys() {
	return null;
    }

    private String getFileNameForKey(String key) {
	return key+FILE_SUFFIX;
    }

    @Override public void writeMemosForKey(String key, List<Memo> memos) {
	OutputStream stream = _fileProvider.openFileForWriting(getFileNameForKey(key));
	writeMemosToStream(stream, memos);
	_fileProvider.closeFile(stream);
    }

    private void writeMemosToStream(OutputStream stream, List<Memo> memos) {
	new WriteRunner(stream).writeAll(memos);
    }

    @Override public List<Memo> readMemosForKey(String key) {
	InputStream stream;
	LinkedList<Memo> result = new LinkedList<Memo>();
	stream = _fileProvider.openFileForReading(getFileNameForKey(key));
	if (stream != null) {
	    result.addAll(readMemosFromStream(stream));
	    _fileProvider.closeFile(stream);
	}
	return result;
    }

    private List<Memo> readMemosFromStream(InputStream stream) {
	LinkedList<Memo> result = new LinkedList<Memo>();
	new ReadRunner(stream).fill(result);
	return result;
    }

    @Override public String getDefaultKey() {
	return null;
    }

    @Override public void dropKey(String key) {
    }

    private static String join(List<String> lines) {
	StringBuilder builder = new StringBuilder();
	for (String line : lines)
	    builder.append(line).append('\n');
	return builder.substring(0, builder.length() - 1);
    }

    private static class WriteRunner {

	private final OutputStream stream;

	WriteRunner(OutputStream stream) {
	    this.stream = stream;
	}

	void writeAll(List<Memo> memos) {
	    try {
		for (Memo memo : memos)
		    write(memo);
	    } catch (IOException e) {
	    }
	}

	private void write(Memo memo) throws IOException {
	    stream.write("* \n".getBytes());
	    for (String line : memo.getText().split("\n")) {
		stream.write("  ".getBytes());
		stream.write(line.getBytes());
		stream.write('\n');
	    }
	}
    }

    private static class ReadRunner {

	private final BufferedReader reader;

	ReadRunner(InputStream stream) {
	    reader = new BufferedReader(new InputStreamReader(stream));
	}

	public void fill(List<Memo> list) {
	    while (true) {
		Memo memo = read();
		if (memo == null)
		    break;
		list.add(memo);
	    }
	}

	private Memo read() {
	    try {
		reader.readLine();
	    } catch (IOException e) {
		return null;
	    }
	    List<String> lines = new LinkedList<String>();
	    while (!memoEnded())
		try {
		    lines.add(reader.readLine().trim());
		} catch (IOException e) {
		    break;
		}
	    return lines.isEmpty() ? null : new Memo(join(lines));
	}

	private boolean memoEnded() {
	    try {
		if (!reader.ready())
		    return true;
		reader.mark(1);
		boolean result = reader.read() == '*';
		reader.reset();
		return result;
	    } catch (IOException e) {
		return true;
	    }
	}
    }
}
