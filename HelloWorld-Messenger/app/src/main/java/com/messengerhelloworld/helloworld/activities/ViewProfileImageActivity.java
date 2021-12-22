package com.messengerhelloworld.helloworld.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.utils.Base;
import com.messengerhelloworld.helloworld.utils.ShouldSync;

import java.util.HashMap;
import java.util.Map;

public class ViewProfileImageActivity extends AppCompatActivity {
	private static final String TAG = "hwmLogProfileImage";
	private static final String PROFILE_IMAGE_NAME = "com.messengerhelloworld.helloworld.profileImageName";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_profile_image);
		ShouldSync.setShouldSyncChats(false);

		ImageButton backButton = findViewById(R.id.backButton_activityViewProfileImage);
		backButton.setOnClickListener(v -> onBackPressed());

		Intent intent = getIntent();
		String profileImageName = intent.getStringExtra(PROFILE_IMAGE_NAME);
		SharedPreferences sp = getSharedPreferences("HelloWorldSharedPref", Context.MODE_PRIVATE);
		String myProfileImageName = sp.getString("HelloWorldUserProfilePhoto", null);

		if(profileImageName.equals(myProfileImageName)) {
			ImageButton editButton = findViewById(R.id.editButton_activityViewProfileImage);
			editButton.setOnClickListener(v -> {
				MainActivity.setShouldUpdateProfilePhoto(true);
				onBackPressed();
			});
			editButton.setVisibility(View.VISIBLE);
		}

		viewImage(profileImageName);
	}

	// Viewing Profile image of the user.
	private void viewImage(String profileImgName) {
		ProgressBar progressBar = findViewById(R.id.progressBar_activityViewProfileImage);
		progressBar.setVisibility(View.VISIBLE);

		Map<String, String> postData = new HashMap<>();
		postData.put("whatToDo", "retrieveProfileImage");
		postData.put("image_quality", "high");
		postData.put("profile_image_name", profileImgName);

		Volley.newRequestQueue(this).add(
				new StringRequest(
						Request.Method.POST,
						Base.getBaseUrl() + "/manageProfileImage.php",
						response -> {
							byte[] bytesOfImage = android.util.Base64.decode(response, Base64.DEFAULT);
							Bitmap bitmap = BitmapFactory.decodeByteArray(bytesOfImage, 0, bytesOfImage.length);
							int width = findViewById(R.id.layout_activityViewProfileImage).getWidth();
							Bitmap profileImg = Bitmap.createScaledBitmap(bitmap, width, width, false);
							ImageView profileImgView = findViewById(R.id.profileImg_activityViewProfileImage);
							profileImgView.setImageBitmap(profileImg);
							progressBar.setVisibility(View.GONE);
							profileImgView.setVisibility(View.VISIBLE);
						},
						error -> {
							Log.e(TAG, error.toString());
							Toast.makeText(this, "Unable to display photo, please try again.", Toast.LENGTH_SHORT).show();
						}
				) {
					@Override
					protected Map<String, String> getParams() {
						return postData;
					}
				}
		);
	}

	@Override
	public void onBackPressed() {
		NavUtils.navigateUpFromSameTask(this);
	}
}