package com.aragaer.yama;

import java.util.List;
import java.util.SortedSet;


public class OrgmodeReaderWriter implements MemoReaderWriter<String> {

    @Override public SortedSet<String> getKeys() {
	return null;
    }

    @Override public void writeMemosForKey(String key, List<? extends Memo> memos) {
    }

    @Override public List<? extends Memo> readMemosForKey(String key) {
	return null;
    }

    @Override public String getDefaultKey() {
	return null;
    }

    @Override public void dropKey(String key) {
    }
}
