package com.messengerhelloworld.helloworld.utils;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class IpForTesting {
	private static final String TAG = "hwmLogIpForTesting";

	public static String getIpForTesting(String helloworldFolder) {
		String ipForTesting = "";
		File ipFile = new File(helloworldFolder, "ipForTesting.txt");
		if(ipFile.exists()) {
			try {
				Scanner sc = new Scanner(ipFile);
				while(sc.hasNext())
					ipForTesting += sc.next();
				ipForTesting.trim();
			} catch (FileNotFoundException e) {
				Log.e(TAG, e.toString());
			}
		}
		return (ipForTesting.length() > 0) ? ipForTesting : "127.0.0.1";
	}
}
