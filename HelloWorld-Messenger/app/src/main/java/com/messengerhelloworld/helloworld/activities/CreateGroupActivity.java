package com.messengerhelloworld.helloworld.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.adapters.ContactsAdapter;
import com.messengerhelloworld.helloworld.interfaces.AfterJsonArrayResponseIsReceived;
import com.messengerhelloworld.helloworld.interfaces.AfterStringResponseIsReceived;
import com.messengerhelloworld.helloworld.utils.DatabaseOperations;
import com.messengerhelloworld.helloworld.utils.ReadAllContacts;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateGroupActivity extends AppCompatActivity {
	private static final String TAG = "hwmLogCreateGroup";
	private ProgressBar progressBar;
	private String msg;
	private TextView showMsg;
	private ArrayList<String> selectedMembers = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);

		EditText groupNameView = findViewById(R.id.groupName_activityCreateGroup);
		progressBar = findViewById(R.id.progressBar_activityCreateGroup);
		showMsg = findViewById(R.id.showMsg_activityCreateGroup);
		Button createGroup = findViewById(R.id.createGroup_activityCreateGroup);

		if(ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

			SharedPreferences sp = getSharedPreferences("HelloWorldSharedPref", Context.MODE_PRIVATE);
			HashMap<String, String> data = new HashMap<>();
			data.put("phone_nos", ReadAllContacts.getContacts(this));
			data.put("userid", sp.getString("HelloWorldUserId", null));

			DatabaseOperations databaseOperations = new DatabaseOperations(this);
			databaseOperations.retrieveContacts(data, new AfterJsonArrayResponseIsReceived() {
				@Override
				public void executeAfterResponse(JSONArray response) {
					progressBar.setVisibility(View.GONE);
					if(response.length() > 0) {
						RecyclerView contacts = findViewById(R.id.contacts_activityCreateGroup);
						contacts.setVisibility(View.VISIBLE);
						contacts.setLayoutManager(new LinearLayoutManager(CreateGroupActivity.this));
						ContactsAdapter contactsAdapter = new ContactsAdapter(
								CreateGroupActivity.this, response, (userId, contactView) -> {

							if(!selectedMembers.contains(userId)) {
								selectedMembers.add(userId);
								getSupportActionBar().setTitle(selectedMembers.size() + " selected");
								contactView.setBackgroundColor(ContextCompat.getColor(
										CreateGroupActivity.this, R.color.color3));
							}

							else {
								selectedMembers.remove(userId);
								getSupportActionBar().setTitle(selectedMembers.size() + " selected");
								contactView.setBackgroundColor(ContextCompat.getColor(
										CreateGroupActivity.this, R.color.white));
							}

						});
						contacts.setAdapter(contactsAdapter);
					}
					else {
						msg = "No Contacts to show!";
						showMsg.setText(msg);
						showMsg.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void executeAfterErrorResponse(String error) {
					Log.e(TAG, error);
					msg = "Unable to display your Contacts. Please try again later.";
					showMsg.setText(msg);
					progressBar.setVisibility(View.GONE);
					showMsg.setVisibility(View.VISIBLE);
				}
			});

			createGroup.setOnClickListener(v -> {
				String groupName = groupNameView.getText().toString().trim();

				if(groupName.length() > 0) {
					NavUtils.navigateUpFromSameTask(this);
					if(!selectedMembers.contains(sp.getString("HelloWorldUserId", null)))
						selectedMembers.add(sp.getString("HelloWorldUserId", null));

					HashMap<String, String> postDataCreateGroup = new HashMap<>();
					postDataCreateGroup.put("whatToDo", "createGroup");
					postDataCreateGroup.put("groupName", groupName);
					postDataCreateGroup.put("membersId", new JSONArray(selectedMembers).toString());

					databaseOperations.createGroup(postDataCreateGroup, new AfterStringResponseIsReceived() {
						@Override
						public void executeAfterResponse(String response) {
							if(response.equals("1"))
								Toast.makeText(CreateGroupActivity.this, groupName + " is created successfully.", Toast.LENGTH_SHORT).show();
						}

						@Override
						public void executeAfterErrorResponse(String error) {
							Log.e(TAG, error);
							Toast.makeText(CreateGroupActivity.this, "Unable to create group, please try again.", Toast.LENGTH_SHORT).show();
						}
					});
				}
				else
					Toast.makeText(this, "Please enter the valid Group Name.", Toast.LENGTH_SHORT).show();
			});
		}

		else {
			msg = "Allow HelloWorld to access your Contacts";
			showMsg.setText(msg);
			progressBar.setVisibility(View.GONE);
			showMsg.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		NavUtils.navigateUpFromSameTask(this);
	}
}