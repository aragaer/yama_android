package com.aragaer.yama;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;


public class MemoProvider extends ContentProvider {

    public static final String AUTHORITY = "com.aragaer.yama.provider";

    public static final Uri MEMOS_URI = Uri.parse("content://com.aragaer.yama.provider/memos");

    private SortedMap<Date, List<Memo>> memos_;

    private void readMemoFile(String fileName) throws IOException {
	InputStream stream = getContext().openFileInput(fileName);
	MemoReader reader = new MemoReader(stream);
	List<Memo> thisDateList = new LinkedList<Memo>();
	while (true) {
	    Memo memo = reader.readMemo();
	    if (memo == null)
		break;
	    thisDateList.add(memo);
	}
	memos_.put(MemoReader.dateFromFileName(fileName), thisDateList);
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

    private void convertOldMemoFile() {
	List<String> out = new LinkedList<String>();
	MemoFile.read(getContext(), out);
	if (out.isEmpty())
	    return;
	try {
	    String fileName = getLegacyMemoFilename();
	    OutputStream converted = getContext().openFileOutput(fileName,
								 Context.MODE_PRIVATE);
	    MemoWriter writer = new MemoWriter(converted);
	    for (String memo : out)
		writer.write(new Memo(memo));
	} catch (IOException e) {
	    return;
	}
	getContext().deleteFile("memo");
    }

    public boolean onCreate() {
	memos_ = new TreeMap<Date, List<Memo>>();
	convertOldMemoFile();
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
	for (Map.Entry<Date, List<Memo>> entry : memos_.entrySet())
	    for (Memo memo : entry.getValue())
		putMemoToCursor(memo, result);
	return result;
    }

    public Uri insert(Uri uri, ContentValues values) {
	Date today = new Date();
	String fileName = MemoWriter.fileNameForDate(today);
	today = MemoReader.dateFromFileName(fileName);
	List<Memo> todayList = new LinkedList<Memo>();
	Memo newMemo = memoFromContentValues(values);
	todayList.add(newMemo);
	memos_.put(today, todayList);
	try {
	    OutputStream todayStream = getContext().openFileOutput(fileName,
								   Context.MODE_PRIVATE);
	    MemoWriter writer = new MemoWriter(todayStream);
	    writer.write(newMemo);
	} catch (IOException e) {
	    // ugh
	}
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

    private static void putMemoToCursor(Memo memo, MatrixCursor cursor) {
	cursor.newRow().add(memo.getText());
    }

    public static Memo readMemoFromCursor(Cursor cursor) {
	return new Memo(cursor.getString(0));
    }

    public static ContentValues getMemoContentValues(Memo memo) {
	ContentValues result = new ContentValues();
	result.put("", memo.getText());
	return result;
    }

    private static Memo memoFromContentValues(ContentValues values) {
	return new Memo(values.getAsString(""));
    }
}
