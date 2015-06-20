package com.aragaer.yama;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;


public class MemoHandler<Key> {

    private TreeMap<Key, List<_Memo>> memos;
    private MemoReaderWriter<Key> readerWriter;

    public MemoHandler(MemoReaderWriter<Key> readerWriter) {
	memos = new TreeMap<Key, List<_Memo>>();
	this.readerWriter = readerWriter;
    }

    public List<Memo> getAllActiveMemos() {
	List<Memo> result = new LinkedList<Memo>();
	for (List<_Memo> list : memos.values())
	    result.addAll(list);
	return result;
    }

    public Memo storeMemo(String text) {
	Key key = readerWriter.getDefaultKey();
	_Memo result = new _Memo(text, key);
	List<_Memo> list = memos.get(key);
	if (list == null) {
	    list = new LinkedList<_Memo>();
	    memos.put(key, list);
	}
	list.add(result);
	return result;
    }

    public void replaceMemo(Memo memo, List<String> lines) {
	Key key = ((_Memo) memo).getKey();
	int dayIndex = memos.get(key).indexOf(memo);
	memos.get(key).remove(dayIndex);
	for (int i = 0; i < lines.size(); i++) {
	    _Memo newMemo = new _Memo(lines.get(i), key);
	    memos.get(key).add(dayIndex+i, newMemo);
	}
    }

    public void deleteMemo(Memo memo) {
	Key key = ((_Memo) memo).getKey();
	memos.get(key).remove(memo);
    }

    public void updateFromReaderWriter() {
	for (Key key : readerWriter.getKeys()) {
	    List<_Memo> list = new LinkedList<_Memo>();
	    for (Memo memo : readerWriter.readMemosForKey(key))
		list.add(new _Memo(memo.getText(), key));
	    memos.put(key, list);
	}
    }

    public void dumpToReaderWriter() {
	for (Key key : memos.keySet()) {
	    List<_Memo> list = memos.get(key);
	    if (list.isEmpty())
		readerWriter.dropKey(key);
	    else
		readerWriter.writeMemosForKey(key, new ArrayList<Memo>(list));
	}
    }

    private class _Memo extends Memo {
	private Key key;

	_Memo(String text, Key key) {
	    super(text);
	    this.key = key;
	}

	Key getKey() {
	    return key;
	}
    }
}
