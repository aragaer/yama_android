package com.aragaer.yama;


class TestMemo implements Memo {
    private String text;

    public TestMemo(String text) {
	this.text = text;
    }

    public String getText() {
	return text;
    }
}
