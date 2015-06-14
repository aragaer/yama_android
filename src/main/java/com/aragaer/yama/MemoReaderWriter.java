package com.aragaer.yama;

import java.util.List;
import java.util.SortedSet;


public interface MemoReaderWriter<Key> {
    public SortedSet<Key> getKeys();
    public List<? extends Memo> readMemosForKey(Key key);
    public void writeMemosForKey(Key key, List<? extends Memo> memos);
    public Key getDefaultKey();
    public void dropKey(Key key);
}
