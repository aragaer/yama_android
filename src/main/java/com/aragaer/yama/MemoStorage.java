package com.aragaer.yama;

import java.util.LinkedList;
import java.util.List;


public class MemoStorage {

    private List<Memo> allMemos;

    public MemoStorage() {
	allMemos = new LinkedList<Memo>();
    }

    public List<Memo> getAllActiveMemos() {
	return allMemos;
    }

    public Memo storeMemo(String text) {
	Memo result = new _Memo(text);
	allMemos.add(result);
	return result;
    }

    public void replaceMemo(Memo memo, String newText) {
	int index = allMemos.indexOf(memo);
	allMemos.remove(index);
	allMemos.add(index, new _Memo(newText));
    }

    public void deleteMemo(Memo memo) {
	allMemos.remove(memo);
    }

    private static class _Memo implements Memo {
	String text;

	_Memo(String text) {
	    this.text = text;
	}

	@Override public String getText() {
	    return text;
	}
    }
}
