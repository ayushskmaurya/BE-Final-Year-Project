package com.messengerhelloworld.helloworld.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Permissions {
	public static void reqPermissions(Activity activity) {
		String[] permissionsRequired = {
				Manifest.permission.READ_CONTACTS,
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE
		};
		ArrayList<String> permissions = new ArrayList<>();
		for(String permission : permissionsRequired)
			if(ContextCompat.checkSelfPermission(activity,
					permission) != PackageManager.PERMISSION_GRANTED)
				permissions.add(permission);
		if(permissions.size() > 0)
			ActivityCompat.requestPermissions(activity,
					permissions.toArray(new String[permissions.size()]), 1);
	}
}
