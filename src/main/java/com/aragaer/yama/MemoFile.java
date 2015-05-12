package com.aragaer.yama;

import java.io.OutputStream;

import android.content.Context;
import android.util.Log;


public class MemoFile {

    public static void write(Context context, String[] lines) {
	try {
	    OutputStream file = context.openFileOutput("memo",
	            Context.MODE_PRIVATE | Context.MODE_APPEND);
	    for (String line : lines) {
		String trimmed = line.trim();
		if (trimmed.isEmpty())
		    continue;
		file.write(trimmed.getBytes());
		file.write("\n".getBytes());
		Log.d("YAMA", "Write: " + trimmed);
	    }
	    file.close();
	} catch (Exception e) {
	    Log.e("YAMA", "Error writing memo", e);
	}
    }

}
