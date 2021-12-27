package com.messengerhelloworld.helloworld.utils;

import com.messengerhelloworld.helloworld.interfaces.AfterJsonArrayResponseIsReceived;

import java.util.HashMap;

public class ShouldSync {
	private static boolean isCurrentlySyncingChats = false;
	private static HashMap<String, String> chatsPostData = null;
	private static AfterJsonArrayResponseIsReceived afterJsonArrayResponseIsReceived = null;
	private static boolean shouldRestartSync = false;
	private static boolean shouldSyncChats;
	private static boolean shouldSyncMessages;

	public static boolean getIsCurrentlySyncingChats() {
		return isCurrentlySyncingChats;
	}
	public static void setIsCurrentlySyncingChats(boolean isCurrentlySyncingChats) {
		ShouldSync.isCurrentlySyncingChats = isCurrentlySyncingChats;
	}

	public static HashMap<String, String> getChatsPostData() {
		return chatsPostData;
	}
	public static void setChatsPostData(HashMap<String, String> chatsPostData) {
		ShouldSync.chatsPostData = chatsPostData;
	}

	public static AfterJsonArrayResponseIsReceived getAfterJsonArrayResponseIsReceived() {
		return afterJsonArrayResponseIsReceived;
	}
	public static void setAfterJsonArrayResponseIsReceived(AfterJsonArrayResponseIsReceived afterJsonArrayResponseIsReceived) {
		ShouldSync.afterJsonArrayResponseIsReceived = afterJsonArrayResponseIsReceived;
	}

	public static boolean getShouldRestartSync() {
		return shouldRestartSync;
	}
	public static void setShouldRestartSync(boolean shouldRestartSync) {
		ShouldSync.shouldRestartSync = shouldRestartSync;
	}

	public static boolean getShouldSyncChats() {
		return shouldSyncChats;
	}
	public static void setShouldSyncChats(boolean shouldSyncChats) {
		ShouldSync.shouldSyncChats = shouldSyncChats;
	}

	public static boolean getShouldSyncMessages() {
		return shouldSyncMessages;
	}
	public static void setShouldSyncMessages(boolean shouldSyncMessages) {
		ShouldSync.shouldSyncMessages = shouldSyncMessages;
	}
}
