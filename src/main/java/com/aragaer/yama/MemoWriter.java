package com.aragaer.yama;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MemoWriter {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'.txt'");

    static String fileNameForDate(Date date) {
	return format.format(date);
    }
}
