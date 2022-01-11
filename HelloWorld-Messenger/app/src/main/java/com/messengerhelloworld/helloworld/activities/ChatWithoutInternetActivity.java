package com.messengerhelloworld.helloworld.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.adapters.ChatWithoutInternetAdapter;
import com.messengerhelloworld.helloworld.interfaces.ProgressBarForChatWithoutInternet;
import com.messengerhelloworld.helloworld.utils.Base;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatWithoutInternetActivity extends AppCompatActivity {
	private static final String TAG = "hwmLogNoInternetChat";
	private static final String IS_HOST_OR_CLIENT = "com.messengerhelloworld.helloworld.isHostOrClient";
	private static final String IP_ADDRESS = "com.messengerhelloworld.helloworld.ipAddress";
	private static final int portMsg = 5000, portFile = 8000;
	private static final String documentsFolderPath = Base.getDocumentsFolder();
	private String who;
	private Thread threadEstablishConnection, threadReadMessage, threadReadFile, threadWriteMessage, threadWriteFile;
	private Socket socketMsg, socketFile;
	private DataInputStream inputStreamMsg, inputStreamFile;
	private DataOutputStream outputStreamMsg, outputStreamFile;
	private String myUserName;
	private ProgressBar chatProgressBar;
	private RecyclerView chatRecyclerView;
	private LinearLayoutManager linearLayoutManager;
	private ChatWithoutInternetAdapter chatWithoutInternetAdapter;
	private JSONArray messages = null;
	private JSONObject message = null;
	private static int noOfMessages = 0, messageId = 0;
	private static boolean canSendFile = true;
	private static long fileSize = 1;
	private static double noOfBytesSentOrReceived = 0;
	private ProgressBar progressBarHorizontal = null;
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
		chatRecyclerView.setLayoutManager(linearLayoutManager);
		messages = new JSONArray();
		timeFormat = new SimpleDateFormat("HH:mm");

		if("host".equals(who))
			new Host();
		else if("client".equals(who))
			new Client(intent.getStringExtra(IP_ADDRESS));
	}

	// Starting the server & waiting for the client.
	private class Host {
		private ServerSocket serverSocketMsg, serverSocketFile;
		private Host() {
			threadEstablishConnection = new Thread(() -> {
				try {

					serverSocketMsg = new ServerSocket(portMsg);
					serverSocketFile = new ServerSocket(portFile);
					socketMsg = serverSocketMsg.accept();
					socketFile = serverSocketFile.accept();
					inputStreamMsg = new DataInputStream(socketMsg.getInputStream());
					inputStreamFile = new DataInputStream(socketFile.getInputStream());
					outputStreamMsg = new DataOutputStream(socketMsg.getOutputStream());
					outputStreamFile = new DataOutputStream(socketFile.getOutputStream());

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
					socketFile = new Socket(ipAddress, portFile);
					inputStreamMsg = new DataInputStream(socketMsg.getInputStream());
					inputStreamFile = new DataInputStream(socketFile.getInputStream());
					outputStreamMsg = new DataOutputStream(socketMsg.getOutputStream());
					outputStreamFile = new DataOutputStream(socketFile.getOutputStream());

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

			ImageButton sendFile = findViewById(R.id.attachFile_activityChat);
			sendFile.setOnClickListener(v -> {
				if(canSendFile) {
					if(ContextCompat.checkSelfPermission(this,
							Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
						Intent intentSelectFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
						intentSelectFile.setType("*/*");
						startActivityForResult(intentSelectFile, 1);
					}
					else
						Toast.makeText(this, "Please grant permission to Read External Storage.", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(this, "Please wait until the previous file is attached completely.", Toast.LENGTH_SHORT).show();
			});
		});
		readMessage();
		readFile();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == RESULT_OK)
			writeFile(data.getData());
	}

	// Receiving the message sent by the sender.
	private void readMessage() {
		threadReadMessage = new Thread(() -> {
			String messageReceived;
			while(true) {
				try {
					messageReceived = inputStreamMsg.readUTF();
					if(!messageReceived.equals("quit")) {
						updateMessageList(String.valueOf(++noOfMessages), messageReceived, "no", "null");
						updateRecyclerView();
					}

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
					updateMessageList(String.valueOf(++noOfMessages), messageToSend, "yes", "null");
					updateRecyclerView();

				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}
		});
		threadWriteMessage.start();
	}

	// Receiving the file sent by the sender.
	private void readFile() {
		threadReadFile = new Thread(() -> {
			String encodedStr;

			try {
				String filename = inputStreamFile.readUTF();
				canSendFile = false;
				updateMessageList(String.valueOf(++noOfMessages), filename, "no", "no");
				messageId = noOfMessages;
				updateRecyclerView();
				fileSize = Long.parseLong(inputStreamFile.readUTF());

				File file = new File(documentsFolderPath, filename);
				if(file.exists())
					file.delete();
				FileOutputStream outputStream = new FileOutputStream(file, true);

				int noOfBytesRead = Integer.parseInt(inputStreamFile.readUTF());
				noOfBytesSentOrReceived = 0;

				while(noOfBytesRead != -1) {
					encodedStr = inputStreamFile.readUTF();
					outputStream.write(android.util.Base64.decode(encodedStr, Base64.DEFAULT));
					outputStream.flush();
					noOfBytesSentOrReceived += (double) (noOfBytesRead * 100) / fileSize;
					runOnUiThread(() -> {
						if(progressBarHorizontal != null)
							progressBarHorizontal.setProgress((int) noOfBytesSentOrReceived);
					});
					noOfBytesRead = Integer.parseInt(inputStreamFile.readUTF());
				}

				try {
					messages.getJSONObject(messageId-1).put("isFileSent", "yes");
				} catch (JSONException e) {
					Log.e(TAG, e.toString());
				}
				updateRecyclerView();
				outputStream.close();
				runOnUiThread(() -> Toast.makeText(this, "Successfully received " + filename + ".", Toast.LENGTH_SHORT).show());

			} catch (IOException e) {
				Log.e(TAG, e.toString());
			} finally {
				canSendFile = true;
				readFile();
			}
		});
		threadReadFile.start();
	}

	// Sending the file to the recipient.
	private void writeFile(Uri filepath) {
		threadWriteFile = new Thread(() -> {
			Cursor cursor = getContentResolver().query(filepath, null, null, null, null);
			int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
			int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
			cursor.moveToFirst();
			String filename = cursor.getString(nameIndex);
			fileSize = Long.parseLong(cursor.getString(sizeIndex));

			try {
				outputStreamFile.writeUTF(filename);
				canSendFile = false;
				updateMessageList(String.valueOf(++noOfMessages), filename, "yes", "no");
				messageId = noOfMessages;
				updateRecyclerView();
				outputStreamFile.writeUTF(String.valueOf(fileSize));

				InputStream inputStream = getContentResolver().openInputStream(filepath);

				int noOfBytesRead;
				noOfBytesSentOrReceived = 0;
				byte[] buffer = new byte[10240];

				while((noOfBytesRead = inputStream.read(buffer)) != -1) {
					outputStreamFile.writeUTF(String.valueOf(noOfBytesRead));
					outputStreamFile.writeUTF(android.util.Base64.encodeToString(buffer, 0, noOfBytesRead, Base64.DEFAULT));
					noOfBytesSentOrReceived += (double) (noOfBytesRead * 100) / fileSize;
					runOnUiThread(() -> {
						if(progressBarHorizontal != null)
							progressBarHorizontal.setProgress((int) noOfBytesSentOrReceived);
					});
				}
				outputStreamFile.writeUTF(String.valueOf(-1));

				try {
					messages.getJSONObject(messageId-1).put("isFileSent", "yes");
				} catch (JSONException e) {
					Log.e(TAG, e.toString());
				}
				updateRecyclerView();
				inputStream.close();
				runOnUiThread(() -> Toast.makeText(this, "Successfully sent " + filename + ".", Toast.LENGTH_SHORT).show());

			} catch (IOException e) {
				Log.e(TAG, e.toString());
			} finally {
				canSendFile = true;
			}
		});
		threadWriteFile.start();
	}

	// Setting the progress bar for the current file which is being shared.
	private class SetProgressBar implements ProgressBarForChatWithoutInternet {
		@Override
		public void setProgressBar(ProgressBar progressBarHorizontal) {
			ChatWithoutInternetActivity.this.progressBarHorizontal = progressBarHorizontal;
		}
	}

	// Updating the messages JSON Array if either new message is sent or received.
	private void updateMessageList(String msgid, String msg, String isMsgSent, String isFileSent) {
		message = new JSONObject();
		try {
			message.put("msgid", msgid);
			message.put("message", msg);
			message.put("dateTime", timeFormat.format(new Date()));
			message.put("isMsgSent", isMsgSent);
			message.put("isFileSent", isFileSent);
			messages.put(message);
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
	}

	// Updating the recycler view if either new message is sent or received.
	private void updateRecyclerView() {
		runOnUiThread(() -> {
			chatWithoutInternetAdapter = new ChatWithoutInternetAdapter(messages, this, new SetProgressBar());
			chatRecyclerView.setAdapter(chatWithoutInternetAdapter);
		});
	}

	// Closing the connections.
	private void closeConnections() {
		try {
			if(inputStreamMsg != null)
				inputStreamMsg.close();
			if(inputStreamFile != null)
				inputStreamFile.close();
			if(outputStreamMsg != null)
				outputStreamMsg.close();
			if(outputStreamFile != null)
				outputStreamFile.close();
			if(socketMsg != null)
				socketMsg.close();
			if(socketFile != null)
				socketFile.close();
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
		if(socketMsg != null && socketMsg.isConnected() && socketFile != null && socketFile.isConnected())
			writeMessage("quit");
		else
			closeConnections();
	}
}