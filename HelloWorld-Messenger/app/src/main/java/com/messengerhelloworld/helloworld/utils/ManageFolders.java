package com.messengerhelloworld.helloworld.utils;

import java.io.File;

public class ManageFolders {

	public static boolean createFolder(File folder) {
		if(folder.exists()) {
			if(!folder.isDirectory()) {
				folder.delete();
				folder.mkdir();
			}
		}
		else
			folder.mkdir();

		return folder.exists() && folder.isDirectory();
	}

	// Creating all the folders required.
	public static boolean createAllFolders() {
		return createFoldersForProfileImages() && createFoldersForDocuments();
	}

	// Creating all the folders required for saving profile images.
	public static boolean createFoldersForProfileImages() {
		boolean folder1 = createFolder(new File(Base.getAndroidDataFolder()));
		boolean folder2 = createFolder(new File(Base.getProfileImagesFolder()));
		return folder1 && folder2;
	}

	// Creating all the folders required for saving documents.
	public static boolean createFoldersForDocuments() {
		boolean folder1 = createFolder(new File(Base.getHelloworldFolder()));
		boolean folder2 = createFolder(new File(Base.getDocumentsFolder()));
		return folder1 && folder2;
	}
}
