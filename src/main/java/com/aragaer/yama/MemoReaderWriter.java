package com.aragaer.yama;

import java.util.List;
import java.util.SortedSet;


public interface MemoReaderWriter<Key> {
    public static final String FILE_SUFFIX = null;

    public SortedSet<Key> getKeys();
    public List<Memo> readMemosForKey(Key key);
    public void writeMemosForKey(Key key, List<Memo> memos);
    public Key getDefaultKey();
    public void dropKey(Key key);
}
