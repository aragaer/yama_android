package com.aragaer.yama;

import java.io.*;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class TestFileProvider implements MemoFileProvider {

    public Map<String, String> files = new HashMap<String, String>();
    public Map<ByteArrayOutputStream, String> opened = new HashMap<ByteArrayOutputStream, String>();

    void setContents(String fileName, String contents) {
	files.put(fileName, contents);
    }

    @Override public List<String> fileList() {
	return new ArrayList<String>(files.keySet());
    }

    @Override public InputStream openFileForReading(String name) {
	String contents = files.get(name);
	if (contents == null)
	    return new ByteArrayInputStream(new byte[0]);
	else
	    return new ByteArrayInputStream(contents.getBytes());
    }

    @Override public OutputStream openFileForWriting(String name) {
	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	opened.put(stream, name);
	return stream;
    }

    @Override public void closeFile(Closeable stream) {
	if (stream instanceof ByteArrayOutputStream) {
	    ByteArrayOutputStream baos = (ByteArrayOutputStream) stream;
	    String filename = opened.get(baos);
	    files.put(filename, baos.toString());
	    opened.remove(baos);
	} else
	    assertThat(stream, instanceOf(ByteArrayInputStream.class));
	try {
	    stream.close();
	} catch (IOException e) {
	    fail("Failed to close stream: " + e);
	}
    }

    @Override public void deleteFile(String fileName) {
	files.remove(fileName);
    }
}
