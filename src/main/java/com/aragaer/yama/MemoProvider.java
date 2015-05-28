package com.aragaer.yama;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;


public class MemoProvider extends ContentProvider {

    public static final String AUTHORITY = "com.aragaer.yama.provider";

    public static final Uri MEMOS_URI = Uri.parse("content://com.aragaer.yama.provider/memos");

    private List<Memo> memos_;

    private void readMemoFile(String fileName) throws IOException {
	InputStream stream = getContext().openFileInput(fileName);
	MemoReader reader = new MemoReader(stream);
	while (true) {
	    Memo memo = reader.readMemo();
	    if (memo == null)
		return;
	    memos_.add(memo);
	}
    }

    private String getLegacyMemoFilename() {
	Date earliest = new Date(Long.MAX_VALUE);
	for (String fileName : getContext().fileList())
	    if (fileName.endsWith(".txt")) {
		Date parsed = MemoReader.dateFromFileName(fileName);
		if (parsed.before(earliest))
		    earliest = parsed;
	    }
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(earliest);
	calendar.add(Calendar.DATE, -1);
	return MemoWriter.fileNameForDate(calendar.getTime());
    }

    private void handleOldMemoFile() {
	try {
	    getContext().openFileOutput(getLegacyMemoFilename(), Context.MODE_PRIVATE);
	} catch (IOException e) {
	}
	List<String> out = new LinkedList<String>();
	MemoFile.read(getContext(), out);
	getContext().deleteFile("memo");
	for (String line : out)
	    memos_.add(new Memo(""));
    }

    public boolean onCreate() {
	memos_ = new LinkedList<Memo>();
	handleOldMemoFile();
	try {
	    for (String fileName : getContext().fileList())
		if (fileName.endsWith(".txt"))
		    readMemoFile(fileName);
	} catch (IOException e) {
	    return false;
	}

	return false;
    }

    public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
	MatrixCursor result = new MatrixCursor(new String[] {""});
	for (Memo memo : memos_)
	    result.newRow().add(memo.getText());
	return result;
    }

    public Uri insert(Uri uri, ContentValues values) {
	return null;
    }

    public int update(Uri uri, ContentValues values, String selection,
		      String[] selectionArgs) {
	return 0;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
	return 0;
    }

    public String getType(Uri uri) {
	return null;
    }
}
