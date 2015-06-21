package com.aragaer.yama;

import java.util.LinkedList;
import java.util.List;


public class MemoHandler {

    private List<Memo> memos;
    private final MemoStorage _storage;

    public MemoHandler(MemoStorage storage) {
	memos = new LinkedList<Memo>();
	_storage = storage;
    }

    public List<Memo> getAllActiveMemos() {
	List<Memo> result = new LinkedList<Memo>();
	result.addAll(memos);
	return result;
    }

    public Memo storeMemo(String text) {
	Memo result = new Memo(text);
	memos.add(result);
	return result;
    }

    public void replaceMemo(Memo memo, List<String> lines) {
	int dayIndex = memos.indexOf(memo);
	memos.remove(dayIndex);
	for (int i = 0; i < lines.size(); i++) {
	    Memo newMemo = new Memo(lines.get(i));
	    memos.add(dayIndex+i, newMemo);
	}
    }

    public void deleteMemo(Memo memo) {
	memos.remove(memo);
    }

    public void updateFromStorage() {
	memos = _storage.readMemos();
    }

    public void dumpToStorage() {
	_storage.writeMemos(memos);
    }
}
