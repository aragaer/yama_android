package com.aragaer.yama;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.StringBuilder;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;


public class OrgmodeReaderWriter implements MemoReaderWriter<String> {

    private final MemoFileProvider _fileProvider;

    public OrgmodeReaderWriter(MemoFileProvider fileProvider) {
	_fileProvider = fileProvider;
    }

    @Override public SortedSet<String> getKeys() {
	return null;
    }

    @Override public void writeMemosForKey(String key, List<? extends Memo> memos) {
	OutputStream stream = _fileProvider.openFileForWriting(key);
	new WriteRunner(stream).writeAll(memos);
	_fileProvider.closeFile(stream);
    }

    @Override public List<? extends Memo> readMemosForKey(String key) {
	LinkedList<_Memo> result = new LinkedList<_Memo>();
	InputStream stream = _fileProvider.openFileForReading(key);
	new ReadRunner(stream).fill(result);
	try {
	    stream.close();
	} catch (IOException e) {
	    // oops?
	}
	return result;
    }

    @Override public String getDefaultKey() {
	return null;
    }

    @Override public void dropKey(String key) {
    }

    private static class _Memo implements Memo {

	private final String _text;

	_Memo(List<String> lines) {
	    StringBuilder builder = new StringBuilder();
	    for (String line : lines)
		builder.append(line).append('\n');
	    _text = builder.substring(0, builder.length() - 1);
	}

	@Override public String getText() {
	    return _text;
	}
    }

    private static class WriteRunner {

	private final OutputStream stream;

	WriteRunner(OutputStream stream) {
	    this.stream = stream;
	}

	void writeAll(List<? extends Memo> memos) {
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

	public void fill(List<_Memo> list) {
	    while (true) {
		_Memo memo = read();
		if (memo == null)
		    break;
		list.add(memo);
	    }
	}

	private _Memo read() {
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
	    return lines.isEmpty() ? null : new _Memo(lines);
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
