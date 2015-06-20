package com.aragaer.yama;

import java.util.List;
import java.util.SortedSet;


public interface MemoReaderWriter {
    public static final String FILE_SUFFIX = null;

    public SortedSet<String> getKeys();
    public List<Memo> readMemosForKey(String key);
    public void writeMemosForKey(String key, List<Memo> memos);
    public String getDefaultKey();
    public void dropKey(String key);
}
