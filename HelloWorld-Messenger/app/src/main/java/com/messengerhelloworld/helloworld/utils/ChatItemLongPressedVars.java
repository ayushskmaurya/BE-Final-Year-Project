package com.messengerhelloworld.helloworld.utils;

import android.view.View;
import android.widget.TextView;

public class ChatItemLongPressedVars {
	private static String chatId = null;
	private static View chatView = null;
	private static TextView lastMsg = null;
	private static String isNewMsg = null;

	public static String getChatId() {
		return chatId;
	}

	public static void setChatId(String chatId) {
		ChatItemLongPressedVars.chatId = chatId;
	}

	public static View getChatView() {
		return chatView;
	}

	public static void setChatView(View chatView) {
		ChatItemLongPressedVars.chatView = chatView;
	}

	public static TextView getLastMsg() {
		return lastMsg;
	}

	public static void setLastMsg(TextView lastMsg) {
		ChatItemLongPressedVars.lastMsg = lastMsg;
	}

	public static String getIsNewMsg() {
		return isNewMsg;
	}

	public static void setIsNewMsg(String isNewMsg) {
		ChatItemLongPressedVars.isNewMsg = isNewMsg;
	}

	public static void setAllVars(String chatId, View chatView, TextView lastMsg, String isNewMsg) {
		setChatId(chatId);
		setChatView(chatView);
		setLastMsg(lastMsg);
		setIsNewMsg(isNewMsg);
	}

	public static void setAllVarsAsNull() {
		setChatId(null);
		setChatView(null);
		setLastMsg(null);
		setIsNewMsg(null);
	}
}
