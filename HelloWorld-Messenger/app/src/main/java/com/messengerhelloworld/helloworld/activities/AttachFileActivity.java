package com.messengerhelloworld.helloworld.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.interfaces.AfterJsonObjectResponseIsReceived;
import com.messengerhelloworld.helloworld.utils.Attachment;
import com.messengerhelloworld.helloworld.utils.DatabaseHandler;
import com.messengerhelloworld.helloworld.utils.DatabaseOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AttachFileActivity extends AppCompatActivity {
	private static final String TAG = "hwmLogAttachFile";
	private static final String CHAT_ID = "com.messengerhelloworld.helloworld.chatId";
	private static final String RECEIVER_USER_ID = "com.messengerhelloworld.helloworld.receiverUserId";
	private final DatabaseOperations databaseOperations = new DatabaseOperations(this);
	private final DatabaseHandler databaseHandler = new DatabaseHandler(this, "helloworld", null, 1);
	private String chatId, receiverId;
	private String senderId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attach_file);

		Intent intent2 = getIntent();
		chatId = intent2.getStringExtra(CHAT_ID);
		receiverId = intent2.getStringExtra(RECEIVER_USER_ID);
		SharedPreferences sp = getSharedPreferences("HelloWorldSharedPref", Context.MODE_PRIVATE);
		senderId = sp.getString("HelloWorldUserId", null);

		// Selecting File to send as Attachment.
		if(ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			intent.setType("*/*");
			startActivityForResult(intent, 1);
		}
		else
			Toast.makeText(this, "Please grant permission to Read External Storage.", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 1 && resultCode == RESULT_OK) {
			Uri filepath = data.getData();
			Cursor cursor = getContentResolver().query(filepath, null, null, null, null);
			int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
			cursor.moveToFirst();
			String filename = cursor.getString(nameIndex);

			// Sending caption.
			TextView captionTextView = findViewById(R.id.caption_activityAttachFile);
			ImageButton sendCaption = findViewById(R.id.sendFile_activityAttachFile);
			sendCaption.setOnClickListener(v -> {
				String caption = captionTextView.getText().toString().trim();
				if(filename.length() > 0) {
					HashMap<String, String> postData = new HashMap<>();
					postData.put("whatToDo", "insertCaption");
					postData.put("chatid", chatId);
					postData.put("senderid", senderId);
					postData.put("receiverid", receiverId);
					postData.put("message", caption);
					postData.put("filename", filename);

					databaseOperations.insertCaption(postData, new AfterJsonObjectResponseIsReceived() {
						@Override
						public void executeAfterResponse(JSONObject response) {
							try {
								chatId = response.getString("chatid");
								onBackPressed();
								databaseHandler.addAttachment(new Attachment(
										response.getString("msgid"),
										response.getString("temp_filename"),
										filepath.toString()
								));
							} catch (JSONException e) {
								onBackPressed();
								Log.e(TAG, e.toString());
								Toast.makeText(AttachFileActivity.this, "Unable to send file, please try again.", Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void executeAfterErrorResponse(String error) {
							onBackPressed();
							Log.e(TAG, error);
							Toast.makeText(AttachFileActivity.this, "Unable to send file, please try again.", Toast.LENGTH_SHORT).show();
						}
					});
				}
			});
		}
	}

	// When back button is pressed, do the following.
	private void whenBackButtonIsPressed() {
		Intent intent = new Intent();
		intent.putExtra(CHAT_ID, chatId);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				whenBackButtonIsPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		whenBackButtonIsPressed();
	}
}