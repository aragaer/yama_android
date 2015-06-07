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
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;


public class MemoProvider extends ContentProvider {

    public static final String AUTHORITY = "com.aragaer.yama.provider";

    public static final Uri MEMOS_URI = Uri.parse("content://com.aragaer.yama.provider/memos");

    private static final String[] fields = "id text".split(" ");

    private static final UriMatcher matcher = new UriMatcher(0);
    private static final int CODE_ALL = 0;
    private static final int CODE_ONE = 1;

    static {
	matcher.addURI(AUTHORITY, "memos", CODE_ALL);
	matcher.addURI(AUTHORITY, "memos/#", CODE_ONE);
    }

    private SortedMap<Date, List<Memo>> memosByDate_;
    private List<Memo> allMemos_;

    private void storeMemo(Date date, Memo memo) {
	List<Memo> list = memosByDate_.get(date);
	if (list == null) {
	    list = new LinkedList<Memo>();
	    memosByDate_.put(date, list);
	}
	storeMemo(list, memo);
    }

    private void storeMemo(List<Memo> list, Memo memo) {
	list.add(memo);
	memo.setId(allMemos_.size());
	allMemos_.add(memo);
    }

    private void readMemoFile(String fileName) throws IOException {
	InputStream stream = getContext().openFileInput(fileName);
	MemoReader reader = new MemoReader(stream);
	Date date = MemoReader.dateFromFileName(fileName);
	List<Memo> todayList = new LinkedList<Memo>();
	memosByDate_.put(date, todayList);
	while (true) {
	    Memo memo = reader.readMemo();
	    if (memo == null)
		break;
	    storeMemo(todayList, memo);
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
	memosByDate_ = new TreeMap<Date, List<Memo>>();
	allMemos_ = new LinkedList<Memo>();
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
	MatrixCursor result = new MatrixCursor(fields);
	switch (matcher.match(uri)) {
	case CODE_ALL:
	    for (List<Memo> list : memosByDate_.values())
		for (Memo memo : list)
		    putMemoToCursor(memo, result);
	    break;
	case CODE_ONE:
	    int id = (int) ContentUris.parseId(uri);
	    putMemoToCursor(allMemos_.get(id), result);
	    break;
	}
	return result;
    }

    public Uri insert(Uri uri, ContentValues values) {
	Date today = new Date();
	String fileName = MemoWriter.fileNameForDate(today);
	today = MemoReader.dateFromFileName(fileName);
	Memo newMemo = memoFromContentValues(values);
	storeMemo(today, newMemo);
	try {
	    OutputStream todayStream = getContext().openFileOutput(fileName,
								   Context.MODE_PRIVATE | Context.MODE_APPEND);
	    MemoWriter writer = new MemoWriter(todayStream);
	    writer.write(newMemo);
	} catch (IOException e) {
	    // ugh
	}
	return ContentUris.withAppendedId(MEMOS_URI, newMemo.getId());
    }

    public int update(Uri uri, ContentValues values, String selection,
		      String[] selectionArgs) {
	return 0;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
	Date last = memosByDate_.lastKey();
	memosByDate_.get(last).remove(0);
	getContext().deleteFile(MemoWriter.fileNameForDate(last));
	return 1;
    }

    public String getType(Uri uri) {
	return null;
    }

    private static void putMemoToCursor(Memo memo, MatrixCursor cursor) {
	cursor.newRow().add(memo.getId()).add(memo.getText());
    }

    public static Memo readMemoFromCursor(Cursor cursor) {
	return new Memo(cursor.getLong(0), cursor.getString(1));
    }

    public static ContentValues getMemoContentValues(Memo memo) {
	ContentValues result = new ContentValues();
	result.put("id", memo.getId());
	result.put("text", memo.getText());
	return result;
    }

    private static Memo memoFromContentValues(ContentValues values) {
	return new Memo(values.getAsLong("id"), values.getAsString("text"));
    }
}
