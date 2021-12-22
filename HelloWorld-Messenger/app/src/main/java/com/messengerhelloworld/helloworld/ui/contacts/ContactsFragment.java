package com.messengerhelloworld.helloworld.ui.contacts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.adapters.ContactsAdapter;
import com.messengerhelloworld.helloworld.interfaces.AfterJsonArrayResponseIsReceived;
import com.messengerhelloworld.helloworld.utils.DatabaseOperations;
import com.messengerhelloworld.helloworld.utils.ShouldSync;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactsFragment extends Fragment {
	private static final String TAG = "hwmLogContacts";
	private Context context;
	private ProgressBar progressBar;
	private String msg;
	private TextView showMsg;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.context = context;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {

		View contactsLayout = inflater.inflate(R.layout.fragment_contacts, container, false);
		ShouldSync.setShouldSyncChats(false);

		progressBar = contactsLayout.findViewById(R.id.progressBar_fragmentContacts);
		showMsg = contactsLayout.findViewById(R.id.showMsg_fragmentContacts);

		if(ContextCompat.checkSelfPermission(context,
				Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

			SharedPreferences sp = getActivity().getSharedPreferences("HelloWorldSharedPref", Context.MODE_PRIVATE);
			HashMap<String, String> data = new HashMap<>();
			data.put("phone_nos", getContacts());
			data.put("userid", sp.getString("HelloWorldUserId", null));

			DatabaseOperations databaseOperations = new DatabaseOperations((Activity) context);
			databaseOperations.retrieveContacts(data, new AfterJsonArrayResponseIsReceived() {
				@Override
				public void executeAfterResponse(JSONArray response) {
					progressBar.setVisibility(View.GONE);
					if(response.length() > 0) {
						RecyclerView contacts = contactsLayout.findViewById(R.id.contacts_fragmentContacts);
						contacts.setVisibility(View.VISIBLE);
						contacts.setLayoutManager(new LinearLayoutManager(context));
						ContactsAdapter contactsAdapter = new ContactsAdapter(context, response);
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
		}

		else {
			msg = "Allow HelloWorld to access your Contacts";
			showMsg.setText(msg);
			progressBar.setVisibility(View.GONE);
			showMsg.setVisibility(View.VISIBLE);
		}
		return contactsLayout;
	}

	private String getContacts() {
		ArrayList<String> phoneNos = new ArrayList<>();
		String mobNo;
		int len;

		Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null, null, null, null);
		while(cursor.moveToNext()) {
			mobNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			mobNo = mobNo.replaceAll("\\D", "");
			len = mobNo.length();
			if(len>=10)
				phoneNos.add("'" + mobNo.substring(len-10, len) + "'");
		}

		if(phoneNos.size() == 0)
			phoneNos.add("''");
		return phoneNos.toString();
	}
}
