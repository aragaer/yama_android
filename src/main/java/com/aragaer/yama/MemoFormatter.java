package com.aragaer.yama;

import java.util.List;


public interface MemoFormatter {
    public String getFileSuffix();
    public void formatAllTo(List<Memo> memos, StringBuilder out);
    public void parseAllTo(List<Memo> memos, String data);
}
