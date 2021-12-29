package com.messengerhelloworld.helloworld.interfaces;

import android.view.View;

public interface ItemMsgIsLongPressed {
	void whenItemPressed(String msgId, String msg);
	void highlightBg(View msgLayout, View msgBg);
	String getItemLongPressedMsgId();
}
