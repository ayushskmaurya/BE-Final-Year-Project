package com.messengerhelloworld.helloworld.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DisplayProfileImage {
	private static final String TAG = "hwmLogDisplayProfileImg";
	private static final String profileImagesFolderPath = Base.getProfileImagesFolder();

	public static void display(Activity activity, String profileImgName, ImageView profileImgView) {
		File profileImg = new File(profileImagesFolderPath, profileImgName);

		// Displaying profile image if already present in the folder.
		if(profileImg.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(profileImg.getAbsolutePath());
			profileImgView.setImageBitmap(bitmap);
		}

		// Displaying profile image by retrieving from the server.
		else {
			Map<String, String> postData = new HashMap<>();
			postData.put("whatToDo", "retrieveProfileImage");
			postData.put("image_quality", "low");
			postData.put("profile_image_name", profileImgName);

			Volley.newRequestQueue(activity).add(
					new StringRequest(
							Request.Method.POST,
							Base.getBaseUrl() + "/manageProfileImage.php",
							response -> {
								byte[] bytesOfImage = displayReceivedImage(response, profileImgView);
								saveReceivedImage(bytesOfImage, profileImg);
							},
							error -> Log.e(TAG, error.toString())
					) {
						@Override
						protected Map<String, String> getParams() {
							return postData;
						}
					}
			);
		}
	}

	// Displaying profile image of the user received from the server.
	private static byte[] displayReceivedImage(String encodedImageStr, ImageView profileImgView) {
		byte[] bytesOfImage = android.util.Base64.decode(encodedImageStr, Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytesOfImage, 0, bytesOfImage.length);
		profileImgView.setImageBitmap(bitmap);
		return bytesOfImage;
	}

	// Saving profile image of the user received from the server.
	private static void saveReceivedImage(byte[] bytesOfImage, File profileImg) {
		if(ManageFolders.createFoldersForProfileImages()) {
			try {
				FileOutputStream outputStream = new FileOutputStream(profileImg);
				outputStream.write(bytesOfImage);
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}
	}
}
