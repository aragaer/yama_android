package com.aragaer.yama;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public interface MemoFileProvider {
    public List<String> fileList();
    public InputStream openFileForReading(String fileName);
    public OutputStream openFileForWriting(String fileName);
}
