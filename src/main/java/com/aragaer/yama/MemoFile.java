package com.aragaer.yama;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;


public class MemoFile {

    private static final int MODE_APPEND = Context.MODE_PRIVATE | Context.MODE_APPEND;
    private static final int MODE_OVERWRITE = Context.MODE_PRIVATE;

    public static void append(Context context, String[] lines) {
	write(context, sanitize(lines), MODE_APPEND);
    }

    public static void save(Context context, List<String> lines) {
	write(context, lines, MODE_OVERWRITE);
    }

    public static void write(Context context, List<String> lines, int mode) {
	try {
	    OutputStream file = context.openFileOutput("memo", mode);
	    for (String line : lines) {
		file.write(line.getBytes());
		file.write("\n".getBytes());
		Log.d("YAMA", "Write: " + line);
	    }
	    file.close();
	} catch (Exception e) {
	    Log.e("YAMA", "Error writing memo", e);
	}
    }

    public static void read(Context context, List<String> lines) {
	lines.clear();
	try {
	    InputStream memos = context.openFileInput("memo");
	    BufferedReader reader = new BufferedReader(new InputStreamReader(memos));
	    while (true) {
		String line = reader.readLine();
		if (line == null)
		    break;
		lines.add(line);
		Log.d("YAMA", "Got line: " + line);
	    }
	    memos.close();
	} catch (Exception e) {
	    Log.e("YAMA", "Error reading memos", e);
	}
    }

    public static List<String> sanitize(String[] lines) {
	List<String> result = new ArrayList<String>();
	for (String line : lines) {
	    String trimmed = line.trim();
	    if (trimmed.isEmpty())
		continue;
	    result.add(trimmed);
	}
	return result;
    }
}
