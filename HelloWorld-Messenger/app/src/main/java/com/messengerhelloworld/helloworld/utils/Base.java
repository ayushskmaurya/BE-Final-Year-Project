package com.messengerhelloworld.helloworld.utils;

import android.os.Environment;

public class Base {
	private static final String BASE_URL = "http://localhost/BE-Final-Year-Project/HelloWorld-Server";
	private static final String ANDROID_DATA_FOLDER = Environment.getExternalStorageDirectory() + "/Android/data/com.messengerhelloworld.helloworld";
	private static final String PROFILE_IMAGES_FOLDER = ANDROID_DATA_FOLDER + "/profile_images";

	public static String getBaseUrl() {
		return BASE_URL;
	}
	public static String getAndroidDataFolder() {
		return ANDROID_DATA_FOLDER;
	}
	public static String getProfileImagesFolder() {
		return PROFILE_IMAGES_FOLDER;
	}
}
