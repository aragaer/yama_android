package com.aragaer.yama;

import java.io.InputStream;
import java.io.OutputStream;


public interface MemoFileProvider {
    public InputStream openFileForReading(String fileName);
    public OutputStream openFileForWriting(String fileName);
}
