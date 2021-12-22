package com.messengerhelloworld.helloworld.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import android.os.Handler;

import com.messengerhelloworld.helloworld.interfaces.AfterJsonObjectResponseIsReceived;
import com.messengerhelloworld.helloworld.interfaces.AfterStringResponseIsReceived;
import com.messengerhelloworld.helloworld.interfaces.AfterJsonArrayResponseIsReceived;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseOperations {
	private static final String TAG = "hwmLogDbOperations";
	private final Activity activity;
	private final Handler handler1 = new Handler();
	private final Handler handler2 = new Handler();

	public DatabaseOperations(Activity activity) {
		this.activity = activity;
	}

	// Inserting new row in the database table.
	public void insert(HashMap<String, String> data, AfterStringResponseIsReceived afterStringResponseIsReceived) {
		Volley.newRequestQueue(activity).add(
				new StringRequest(
						Request.Method.POST,
						Base.getBaseUrl() + "/insert.php",
						response -> afterStringResponseIsReceived.executeAfterResponse(response),
						error -> afterStringResponseIsReceived.executeAfterErrorResponse(error.toString())
				) {
					@Override
					protected Map<String, String> getParams() {
						return data;
					}
				}
		);
	}

	// Inserting new message in the database table.
	public void insertMessage(HashMap<String, String> data, AfterStringResponseIsReceived afterStringResponseIsReceived) {
		Volley.newRequestQueue(activity).add(
				new StringRequest(
						Request.Method.POST,
						Base.getBaseUrl() + "/insertMessage.php",
						response -> afterStringResponseIsReceived.executeAfterResponse(response),
						error -> afterStringResponseIsReceived.executeAfterErrorResponse(error.toString())
				) {
					@Override
					protected Map<String, String> getParams() {
						return data;
					}
				}
		);
	}

	// Inserting new caption in the database table.
	public void insertCaption(HashMap<String, String> data, AfterJsonObjectResponseIsReceived afterJsonObjectResponseIsReceived) {
		Volley.newRequestQueue(activity).add(
				new StringRequest(
						Request.Method.POST,
						Base.getBaseUrl() + "/manageAttachment.php",
						response -> {
							try {
								afterJsonObjectResponseIsReceived.executeAfterResponse(new JSONObject(response));
							} catch (JSONException e) {
								Log.e(TAG, e.toString());
							}
						},
						error -> afterJsonObjectResponseIsReceived.executeAfterErrorResponse(error.toString())
				) {
					@Override
					protected Map<String, String> getParams() {
						return data;
					}
				}
		);
	}

	// Retrieving data from the database table.
	public void retrieve(HashMap<String, String> data, AfterJsonArrayResponseIsReceived afterJsonArrayResponseIsReceived) {
		Volley.newRequestQueue(activity).add(
				new StringRequest(
						Request.Method.POST,
						Base.getBaseUrl() + "/retrieve.php",
						response -> {
							try {
								afterJsonArrayResponseIsReceived.executeAfterResponse(new JSONArray(response));
							} catch (JSONException e) {
								Log.e(TAG, e.toString());
								Toast.makeText(activity, "Unable to complete the process, please try again.", Toast.LENGTH_SHORT).show();
							}
						},
						error -> afterJsonArrayResponseIsReceived.executeAfterErrorResponse(error.toString())
				) {
					@Override
					protected Map<String, String> getParams() {
						return data;
					}
				}
		);
	}

	// Retrieving all the chats.
	public void retrieveChats(HashMap<String, String> data, AfterJsonArrayResponseIsReceived afterJsonArrayResponseIsReceived) {
		Volley.newRequestQueue(activity).add(
				new StringRequest(
						Request.Method.POST,
						Base.getBaseUrl() + "/retrieveChats.php",
						response -> {
							try {
								afterJsonArrayResponseIsReceived.executeAfterResponse(new JSONArray(response));
							} catch (JSONException e) {
								Log.e(TAG, e.toString());
							}
							finally {
								if(ShouldSync.getShouldSyncChats())
									syncChats(data, afterJsonArrayResponseIsReceived);
							}
						},
						error -> {
							afterJsonArrayResponseIsReceived.executeAfterErrorResponse(error.toString());
							if(ShouldSync.getShouldSyncChats())
								syncChats(data, afterJsonArrayResponseIsReceived);
						}
				) {
					@Override
					protected Map<String, String> getParams() {
						return data;
					}
				}
		);
	}
	// Synchronising all the chats.
	private void syncChats(HashMap<String, String> data, AfterJsonArrayResponseIsReceived afterJsonArrayResponseIsReceived) {
		handler1.postDelayed(new Runnable() {
			@Override
			public void run() {
				retrieveChats(data, afterJsonArrayResponseIsReceived);
			}
		}, 3000);
	}

	// Retrieving contacts.
	public void retrieveContacts(HashMap<String, String> data, AfterJsonArrayResponseIsReceived afterJsonArrayResponseIsReceived) {
		Volley.newRequestQueue(activity).add(
				new StringRequest(
						Request.Method.POST,
						Base.getBaseUrl() + "/retrieveContacts.php",
						response -> {
							try {
								afterJsonArrayResponseIsReceived.executeAfterResponse(new JSONArray(response));
							} catch (JSONException e) {
								Log.e(TAG, e.toString());
							}
						},
						error -> afterJsonArrayResponseIsReceived.executeAfterErrorResponse(error.toString())
				) {
					@Override
					protected Map<String, String> getParams() {
						return data;
					}
				}
		);
	}

	// Retrieving messages.
	public void retrieveMessages(HashMap<String, String> data, AfterJsonArrayResponseIsReceived afterJsonArrayResponseIsReceived) {
		Volley.newRequestQueue(activity).add(
				new StringRequest(
						Request.Method.POST,
						Base.getBaseUrl() + "/retrieveMessages.php",
						response -> {
							try {
								afterJsonArrayResponseIsReceived.executeAfterResponse(new JSONArray(response));
							} catch (JSONException e) {
								Log.e(TAG, e.toString());
							}
							finally {
								if(ShouldSync.getShouldSyncMessages())
									syncMessages(data, afterJsonArrayResponseIsReceived);
							}
						},
						error -> {
							afterJsonArrayResponseIsReceived.executeAfterErrorResponse(error.toString());
							if(ShouldSync.getShouldSyncMessages())
								syncMessages(data, afterJsonArrayResponseIsReceived);
						}
				) {
					@Override
					protected Map<String, String> getParams() {
						return data;
					}
				}
		);
	}
	// Synchronising messages.
	private void syncMessages(HashMap<String, String> data, AfterJsonArrayResponseIsReceived afterJsonArrayResponseIsReceived) {
		handler2.postDelayed(new Runnable() {
			@Override
			public void run() {
				retrieveMessages(data, afterJsonArrayResponseIsReceived);
			}
		}, 1000);
	}

	// Cancel Uploading Attachment.
	public void cancelUploadingAttachment(HashMap<String, String> data, AfterStringResponseIsReceived afterStringResponseIsReceived) {
		Volley.newRequestQueue(activity).add(
				new StringRequest(
						Request.Method.POST,
						Base.getBaseUrl() + "/manageAttachment.php",
						response -> afterStringResponseIsReceived.executeAfterResponse(response),
						error -> afterStringResponseIsReceived.executeAfterErrorResponse(error.toString())
				) {
					@Override
					protected Map<String, String> getParams() {
						return data;
					}
				}
		);
	}
}
