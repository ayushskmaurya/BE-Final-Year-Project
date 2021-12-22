package com.messengerhelloworld.helloworld.utils;

public class ShouldSync {
	private static boolean shouldSyncChats;
	private static boolean shouldSyncMessages;

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
