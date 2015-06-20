package com.aragaer.yama;

import java.util.List;


public class MemoStorage {

    private final MemoFileProvider _fileProvider;
    private final MemoReaderWriter _readerWriter;

    public MemoStorage(MemoFileProvider fileProvider) {
	_fileProvider = fileProvider;
	_readerWriter = new OrgmodeReaderWriter(_fileProvider);
	convertLegacyFile();
    }

    private void convertLegacyFile() {
	if (_fileProvider.fileList().contains("memo")) {
	    List<Memo> legacy = new PlainReaderWriter(_fileProvider).readMemosForKey("");
	    _readerWriter.writeMemosForKey("memo", legacy);
	    _fileProvider.deleteFile("memo");
	}
    }

    public List<? extends Memo> readMemos() {
	return _readerWriter.readMemosForKey("memo");
    }

    public void writeMemos(List<Memo> memos) {
	_readerWriter.writeMemosForKey("memo", memos);
    }
}
