package com.messengerhelloworld.helloworld.utils;

import android.os.Environment;

public class Base {
	private static final String ANDROID_DATA_FOLDER = Environment.getExternalStorageDirectory() + "/Android/data/com.messengerhelloworld.helloworld";
	private static final String PROFILE_IMAGES_FOLDER = ANDROID_DATA_FOLDER + "/profile_images";
	private static final String HELLOWORLD_FOLDER = Environment.getExternalStorageDirectory() + "/HelloWorld";
	private static final String DOCUMENTS_FOLDER = HELLOWORLD_FOLDER + "/Documents";
	private static final String BASE_URL = "http://" + IpForTesting.getIpForTesting(HELLOWORLD_FOLDER) + "/BE-Final-Year-Project/HelloWorld-Server";

	public static String getAndroidDataFolder() {
		return ANDROID_DATA_FOLDER;
	}
	public static String getProfileImagesFolder() {
		return PROFILE_IMAGES_FOLDER;
	}
	public static String getHelloworldFolder() {
		return HELLOWORLD_FOLDER;
	}
	public static String getDocumentsFolder() {
		return DOCUMENTS_FOLDER;
	}
	public static String getBaseUrl() {
		return BASE_URL;
	}
}
