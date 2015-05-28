package com.aragaer.yama;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;


public class MemoProvider extends ContentProvider {

    public static final String AUTHORITY = "com.aragaer.yama.provider";

    public static final Uri MEMOS_URI = Uri.parse("content://com.aragaer.yama.provider/memos");

    public boolean onCreate() {
	return false;
    }

    public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
	MatrixCursor result = new MatrixCursor(new String[] {""});
	result.newRow();
	result.newRow();
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
