package com.messengerhelloworld.helloworld.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.adapters.ChatAdapter;
import com.messengerhelloworld.helloworld.interfaces.AfterJsonArrayResponseIsReceived;
import com.messengerhelloworld.helloworld.interfaces.AfterStringResponseIsReceived;
import com.messengerhelloworld.helloworld.utils.DatabaseOperations;
import com.messengerhelloworld.helloworld.utils.ShouldSync;

import org.json.JSONArray;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
	private static final String TAG = "hwmLogChat";
	private static final String CHAT_ID = "com.messengerhelloworld.helloworld.chatId";
	private static final String RECEIVER_USER_NAME = "com.messengerhelloworld.helloworld.receiverUserName";
	private static final String RECEIVER_USER_ID = "com.messengerhelloworld.helloworld.receiverUserId";
	private final DatabaseOperations databaseOperations = new DatabaseOperations(this);
	private ProgressBar chatProgressBar;
	private RecyclerView chatRecyclerView;
	private LinearLayoutManager linearLayoutManager;
	private String chatId, receiverId;
	private String userMessages = null;
	private HashMap<String, String> postData_retrieveMsgs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		ShouldSync.setShouldSyncChats(false);

		Intent intent = getIntent();
		chatId = intent.getStringExtra(CHAT_ID);
		String receiverUserName = intent.getStringExtra(RECEIVER_USER_NAME);
		receiverId = intent.getStringExtra(RECEIVER_USER_ID);
		SharedPreferences sp = getSharedPreferences("HelloWorldSharedPref", Context.MODE_PRIVATE);
		String senderId = sp.getString("HelloWorldUserId", null);

		getSupportActionBar().setTitle(receiverUserName);

		// Retrieving messages.
		chatProgressBar = findViewById(R.id.progressBar_activityChat);
		chatRecyclerView = findViewById(R.id.chat_activityChat);
		linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
		linearLayoutManager.setStackFromEnd(true);

		postData_retrieveMsgs = new HashMap<>();
		postData_retrieveMsgs.put("chatid", chatId);
		postData_retrieveMsgs.put("userid", senderId);

		ShouldSync.setShouldSyncMessages(true);
		databaseOperations.retrieveMessages(postData_retrieveMsgs, new AfterJsonArrayResponseIsReceived() {
			@Override
			public void executeAfterResponse(JSONArray response) {
				chatProgressBar.setVisibility(View.GONE);
				chatRecyclerView.setVisibility(View.VISIBLE);
				if(!String.valueOf(response).equals(userMessages)) {
					chatRecyclerView.setLayoutManager(linearLayoutManager);
					ChatAdapter chatAdapter = new ChatAdapter(response, senderId, ChatActivity.this);
					chatRecyclerView.setAdapter(chatAdapter);
					userMessages = String.valueOf(response);
				}
			}

			@Override
			public void executeAfterErrorResponse(String error) {
				Log.e(TAG, error);
				chatRecyclerView.setVisibility(View.GONE);
				chatProgressBar.setVisibility(View.VISIBLE);
			}
		});

		// Sending attachment.
		ImageButton attachFile = findViewById(R.id.attachFile_activityChat);
		attachFile.setOnClickListener(v -> {
			Intent intent2 = new Intent(this, AttachFileActivity.class);
			intent2.putExtra(CHAT_ID, chatId);
			intent2.putExtra(RECEIVER_USER_ID, receiverId);
			startActivityForResult(intent2, 2);
		});

		// Sending message.
		TextView messageTextView = findViewById(R.id.msg_activityChat);
		ImageButton sendMessage = findViewById(R.id.sendMsg_activityChat);
		sendMessage.setOnClickListener(v -> {
			String message = messageTextView.getText().toString().trim();
			if(message.length() > 0) {
				messageTextView.setText("");
				HashMap<String, String> data = new HashMap<>();
				data.put("chatid", chatId);
				data.put("senderid", senderId);
				data.put("receiverid", receiverId);
				data.put("message", message);

				databaseOperations.insertMessage(data, new AfterStringResponseIsReceived() {
					@Override
					public void executeAfterResponse(String response) {
						chatId = response;
						postData_retrieveMsgs.put("chatid", chatId);
					}

					@Override
					public void executeAfterErrorResponse(String error) {
						Log.e(TAG, error);
						Toast.makeText(ChatActivity.this, "Unable to send message, please try again.", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == RESULT_OK) {
			chatId = data.getStringExtra(CHAT_ID);
			postData_retrieveMsgs.put("chatid", chatId);
		}
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				ShouldSync.setShouldSyncMessages(false);
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		ShouldSync.setShouldSyncMessages(false);
		NavUtils.navigateUpFromSameTask(this);
	}
}
