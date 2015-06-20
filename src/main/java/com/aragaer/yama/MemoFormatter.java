package com.aragaer.yama;

import java.lang.StringBuilder;
import java.util.List;


public interface MemoFormatter {
    public void formatAllTo(List<Memo> memos, StringBuilder out);
    public void parseAllTo(List<Memo> memos, String data);
}
