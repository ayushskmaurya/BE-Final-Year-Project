package com.messengerhelloworld.helloworld.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.activities.CreateGroupActivity;
import com.messengerhelloworld.helloworld.adapters.GroupsAdapter;
import com.messengerhelloworld.helloworld.interfaces.AfterJsonArrayResponseIsReceived;
import com.messengerhelloworld.helloworld.utils.DatabaseOperations;
import com.messengerhelloworld.helloworld.utils.ShouldSync;

import org.json.JSONArray;

import java.util.HashMap;

public class GroupsFragment extends Fragment {
	private static final String TAG = "hwmLogGroups";
	private ProgressBar progressBar;
	private String msg;
	private TextView showMsg;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {

		View groupsLayout = inflater.inflate(R.layout.fragment_groups, container, false);
		ShouldSync.setShouldSyncChats(false);

		progressBar = groupsLayout.findViewById(R.id.progressBar_fragmentGroups);
		showMsg = groupsLayout.findViewById(R.id.showMsg_fragmentGroups);

		SharedPreferences sp = getActivity().getSharedPreferences("HelloWorldSharedPref", Context.MODE_PRIVATE);
		HashMap<String, String> data = new HashMap<>();
		data.put("whatToDo", "retrieveGroups");
		data.put("userid", sp.getString("HelloWorldUserId", null));

		DatabaseOperations databaseOperations = new DatabaseOperations(getActivity());
		databaseOperations.retrieveGroups(data, new AfterJsonArrayResponseIsReceived() {
			@Override
			public void executeAfterResponse(JSONArray response) {
				progressBar.setVisibility(View.GONE);
				if(response.length() > 0) {
					RecyclerView groups = groupsLayout.findViewById(R.id.groups_fragmentGroups);
					groups.setVisibility(View.VISIBLE);
					groups.setLayoutManager(new LinearLayoutManager(getActivity()));
					GroupsAdapter groupsAdapter = new GroupsAdapter(getActivity(), response);
					groups.setAdapter(groupsAdapter);
				}
				else {
					msg = "No Groups to show!";
					showMsg.setText(msg);
					showMsg.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void executeAfterErrorResponse(String error) {
				Log.e(TAG, error);
				msg = "Unable to display your Groups. Please try again later.";
				showMsg.setText(msg);
				progressBar.setVisibility(View.GONE);
				showMsg.setVisibility(View.VISIBLE);
			}
		});

		ImageView createGroup = groupsLayout.findViewById(R.id.createGroup_fragmentGroups);
		createGroup.setOnClickListener(v ->
				getActivity().startActivity(new Intent(getActivity(), CreateGroupActivity.class)));

		return groupsLayout;
	}
}