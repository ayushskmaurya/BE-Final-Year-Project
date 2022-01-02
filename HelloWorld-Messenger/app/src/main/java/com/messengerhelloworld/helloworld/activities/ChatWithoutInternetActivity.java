package com.messengerhelloworld.helloworld.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.adapters.ChatWithoutInternetAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatWithoutInternetActivity extends AppCompatActivity {
	private static final String TAG = "hwmLogNoInternetChat";
	private static final String IS_HOST_OR_CLIENT = "com.messengerhelloworld.helloworld.isHostOrClient";
	private static final String IP_ADDRESS = "com.messengerhelloworld.helloworld.ipAddress";
	private static final int portMsg = 5000;
	private String who;
	private Thread threadEstablishConnection, threadReadMessage, threadWriteMessage;
	private Socket socketMsg;
	private DataInputStream inputStreamMsg;
	private DataOutputStream outputStreamMsg;
	private String myUserName;
	private ProgressBar chatProgressBar;
	private RecyclerView chatRecyclerView;
	private LinearLayoutManager linearLayoutManager;
	private JSONArray messages = null;
	private JSONObject message = null;
	private SimpleDateFormat timeFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		getSupportActionBar().setTitle("Connecting...");

		Intent intent = getIntent();
		who = intent.getStringExtra(IS_HOST_OR_CLIENT);
		SharedPreferences sp = getSharedPreferences("HelloWorldSharedPref", Context.MODE_PRIVATE);
		myUserName = sp.getString("HelloWorldUserName", null);

		chatProgressBar = findViewById(R.id.progressBar_activityChat);
		chatRecyclerView = findViewById(R.id.chat_activityChat);
		linearLayoutManager = new LinearLayoutManager(ChatWithoutInternetActivity.this);
		linearLayoutManager.setStackFromEnd(true);
		messages = new JSONArray();
		timeFormat = new SimpleDateFormat("HH:mm");

		if("host".equals(who))
			new Host();
		else if("client".equals(who))
			new Client(intent.getStringExtra(IP_ADDRESS));
	}

	// Starting the server & waiting for the client.
	private class Host {
		private ServerSocket serverSocketMsg;
		private Host() {
			threadEstablishConnection = new Thread(() -> {
				try {

					serverSocketMsg = new ServerSocket(portMsg);
					socketMsg = serverSocketMsg.accept();
					inputStreamMsg = new DataInputStream(socketMsg.getInputStream());
					outputStreamMsg = new DataOutputStream(socketMsg.getOutputStream());

					String clientUserName = inputStreamMsg.readUTF();
					outputStreamMsg.writeUTF(myUserName);
					outputStreamMsg.flush();
					afterConnectionIsEstablished(clientUserName);

				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			});
			threadEstablishConnection.start();
		}
	}

	// Establishing connection with the server.
	private class Client {
		private Client(String ipAddress) {
			threadEstablishConnection = new Thread(() -> {
				try {

					socketMsg = new Socket(ipAddress, portMsg);
					inputStreamMsg = new DataInputStream(socketMsg.getInputStream());
					outputStreamMsg = new DataOutputStream(socketMsg.getOutputStream());

					outputStreamMsg.writeUTF(myUserName);
					outputStreamMsg.flush();
					String hostUserName = inputStreamMsg.readUTF();
					afterConnectionIsEstablished(hostUserName);

				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			});
			threadEstablishConnection.start();
		}
	}

	// Once the connection is established perform the following.
	private void afterConnectionIsEstablished(String title) {
		runOnUiThread(() -> {
			getSupportActionBar().setTitle(title);
			chatProgressBar.setVisibility(View.GONE);
			chatRecyclerView.setVisibility(View.VISIBLE);

			EditText msgView = findViewById(R.id.msg_activityChat);
			ImageButton sendMsg = findViewById(R.id.sendMsg_activityChat);
			sendMsg.setOnClickListener(v -> {
				String msg = msgView.getText().toString().trim();
				msgView.setText("");
				if(msg.length() > 0)
					writeMessage(msg);
			});
		});
		readMessage();
	}

	// Receiving the message sent by the sender.
	private void readMessage() {
		threadReadMessage = new Thread(() -> {
			String messageReceived;
			while(true) {
				try {
					messageReceived = inputStreamMsg.readUTF();
					if(!messageReceived.equals("quit"))
						updateRecyclerView(messageReceived, "no");

					else {
						outputStreamMsg.writeUTF("quit");
						outputStreamMsg.flush();
						break;
					}

				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}
			closeConnections();
		});
		threadReadMessage.start();
	}

	// Sending the message to the recipient.
	private void writeMessage(String messageToSend) {
		threadWriteMessage = new Thread(() -> {
			if(socketMsg != null && socketMsg.isConnected()) {
				try {
					outputStreamMsg.writeUTF(messageToSend);
					outputStreamMsg.flush();
					updateRecyclerView(messageToSend, "yes");

				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}
		});
		threadWriteMessage.start();
	}

	// Updating the recycler view if either new message is sent or received.
	private void updateRecyclerView(String msg, String isMsgSent) {
		runOnUiThread(() -> {
			message = new JSONObject();
			try {
				message.put("message", msg);
				message.put("dateTime", timeFormat.format(new Date()));
				message.put("isMsgSent", isMsgSent);
				messages.put(message);
				chatRecyclerView.setLayoutManager(linearLayoutManager);
				ChatWithoutInternetAdapter chatWithoutInternetAdapter = new ChatWithoutInternetAdapter(messages);
				chatRecyclerView.setAdapter(chatWithoutInternetAdapter);

			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		});
	}

	// Closing the connections.
	private void closeConnections() {
		try {
			if(inputStreamMsg != null)
				inputStreamMsg.close();
			if(outputStreamMsg != null)
				outputStreamMsg.close();
			if(socketMsg != null)
				socketMsg.close();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		}
		ChatWithoutInternetActivity.this.finish();
		System.exit(0);
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
		if(socketMsg != null && socketMsg.isConnected())
			writeMessage("quit");
		else
			closeConnections();
	}
}