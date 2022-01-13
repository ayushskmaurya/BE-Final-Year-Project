package com.messengerhelloworld.helloworld.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.adapters.ChatAdapter;
import com.messengerhelloworld.helloworld.interfaces.AfterJsonArrayResponseIsReceived;
import com.messengerhelloworld.helloworld.interfaces.AfterStringResponseIsReceived;
import com.messengerhelloworld.helloworld.interfaces.ItemMsgIsLongPressed;
import com.messengerhelloworld.helloworld.utils.DatabaseOperations;
import com.messengerhelloworld.helloworld.utils.ShouldSync;

import org.json.JSONArray;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
	private static final String TAG = "hwmLogChat";
	private static final String IS_GROUP = "com.messengerhelloworld.helloworld.isGroup";
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

	private String itemLongPressedMsgId = null;
	private String itemLongPressedMsg = null;
	private View itemLongPressedMsgLayout = null;
	private View itemLongPressedMsgBg = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		ShouldSync.setShouldSyncChats(false);

		Intent intent = getIntent();
		String isGroup = intent.getStringExtra(IS_GROUP);
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
		postData_retrieveMsgs.put("isGroup", isGroup);
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
					ChatAdapter chatAdapter = new ChatAdapter(response, senderId, isGroup, ChatActivity.this, new ItemMsgIsLongPressed() {

						@Override
						public void whenItemPressed(String msgId, String msg) {
							itemLongPressedMsgId = msgId;
							itemLongPressedMsg = msg;
							invalidateOptionsMenu();
						}

						@Override
						public void highlightBg(View msgLayout, View msgBg) {
							itemLongPressedMsgLayout = msgLayout;
							itemLongPressedMsgBg = msgBg;
							itemLongPressedMsgLayout.setBackgroundColor(ContextCompat.getColor(
									ChatActivity.this, R.color.color3));
							itemLongPressedMsgBg.setBackgroundColor(ContextCompat.getColor(
									ChatActivity.this, R.color.color3));
						}

						@Override
						public String getItemLongPressedMsgId() {
							return itemLongPressedMsgId;
						}
					});

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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_chat, menu);
		MenuItem menuItemEditMsg = menu.findItem(R.id.editMsgButton_menuActivityChat);

		if(itemLongPressedMsgId != null) {
			menuItemEditMsg.setVisible(true);

			Dialog dialogEditMsg = new Dialog(this);
			dialogEditMsg.setContentView(R.layout.dialog_edit_message);
			dialogEditMsg.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			dialogEditMsg.setCancelable(false);

			EditText newMessage = dialogEditMsg.findViewById(R.id.newMsg_dialogEditMessage);
			newMessage.setText(itemLongPressedMsg);

			menuItemEditMsg.setOnMenuItemClickListener(item -> {
				dialogEditMsg.show();
				Button cancelButton = dialogEditMsg.findViewById(R.id.no_dialogEditMessage);
				Button confirmButton = dialogEditMsg.findViewById(R.id.yes_dialogEditMessage);

				cancelButton.setOnClickListener(v -> {
					whenBackButtonIsPressed();
					dialogEditMsg.dismiss();
				});

				confirmButton.setOnClickListener(v -> {
					HashMap<String, String> postData = new HashMap<>();
					postData.put("table_name", "messages");
					postData.put("columns", "message='" + newMessage.getText().toString() + "'");
					postData.put("WHERE", "msgid=" + itemLongPressedMsgId + " AND isMsgSeen=0");

					databaseOperations.update(postData, new AfterStringResponseIsReceived() {
						@Override
						public void executeAfterResponse(String response) {
							whenBackButtonIsPressed();
							dialogEditMsg.dismiss();
						}

						@Override
						public void executeAfterErrorResponse(String error) {
							Log.e(TAG, error);
							whenBackButtonIsPressed();
							dialogEditMsg.dismiss();
						}
					});
				});
				return true;
			});
		}
		else
			menuItemEditMsg.setVisible(false);
		return true;
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

	// Do the following when back button is pressed.
	private void whenBackButtonIsPressed() {
		// If message item isn't long pressed
		if(itemLongPressedMsgId == null) {
			ShouldSync.setShouldSyncMessages(false);
			NavUtils.navigateUpFromSameTask(this);
		}

		// If message item was long pressed.
		else {
			itemLongPressedMsgId = null;
			itemLongPressedMsg = null;
			itemLongPressedMsgLayout.setBackgroundColor(ContextCompat.getColor(
					ChatActivity.this, R.color.white));
			itemLongPressedMsgBg.setBackgroundColor(ContextCompat.getColor(
					ChatActivity.this, R.color.white));
			itemLongPressedMsgLayout = null;
			itemLongPressedMsgBg = null;
			invalidateOptionsMenu();
		}
	}
}
