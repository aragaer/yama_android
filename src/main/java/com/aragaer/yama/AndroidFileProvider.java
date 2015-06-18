package com.aragaer.yama;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.Log;


public class AndroidFileProvider implements MemoFileProvider {

    private Context context_;

    public AndroidFileProvider(Context context) {
	context_ = context;
    }

    @Override public List<String> fileList() {
	return Arrays.asList(context_.fileList());
    }

    @Override public InputStream openFileForReading(String fileName) {
	try {
	    return context_.openFileInput(fileName);
	} catch (FileNotFoundException e) {
	    Log.e("YAMA", "File not found: " + e.toString());
	    return null;
	}
    }

    @Override public OutputStream openFileForWriting(String fileName) {
	try {
	    return context_.openFileOutput(fileName, Context.MODE_PRIVATE);
	} catch (FileNotFoundException e) {
	    Log.e("YAMA", "File not found: " + e.toString());
	    return null;
	}
    }

    @Override public void closeFile(Closeable stream) {
    }
}
