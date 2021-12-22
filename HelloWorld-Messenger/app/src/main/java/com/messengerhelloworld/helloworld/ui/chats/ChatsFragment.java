package com.messengerhelloworld.helloworld.ui.chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.adapters.ChatsAdapter;
import com.messengerhelloworld.helloworld.interfaces.AfterJsonArrayResponseIsReceived;
import com.messengerhelloworld.helloworld.utils.DatabaseOperations;
import com.messengerhelloworld.helloworld.utils.ShouldSync;

import org.json.JSONArray;

import java.util.HashMap;

public class ChatsFragment extends Fragment {
	private static final String TAG = "hwmLogChats";
	private Context context;
	private DatabaseOperations databaseOperations;
	private ProgressBar chatsProgressBar;
	private RecyclerView chatsRecyclerView;
	private View noChats;
	private String userChats = null;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.context = context;
		databaseOperations = new DatabaseOperations((Activity) context);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {

		View chatsLayout = inflater.inflate(R.layout.fragment_chats, container, false);

		chatsProgressBar = chatsLayout.findViewById(R.id.progressBar_fragmentChats);
		chatsRecyclerView = chatsLayout.findViewById(R.id.chats_fragmentChats);
		noChats = chatsLayout.findViewById(R.id.noChats_fragmentChats);

		SharedPreferences sp = getActivity().getSharedPreferences("HelloWorldSharedPref", Context.MODE_PRIVATE);
		HashMap<String, String> data = new HashMap<>();
		data.put("userid", sp.getString("HelloWorldUserId", null));

		ShouldSync.setShouldSyncChats(true);
		databaseOperations.retrieveChats(data, new AfterJsonArrayResponseIsReceived() {
			@Override
			public void executeAfterResponse(JSONArray response) {
				chatsProgressBar.setVisibility(View.GONE);
				if(!String.valueOf(response).equals(userChats)) {
					if(response.length() != 0) {
						noChats.setVisibility(View.GONE);
						chatsRecyclerView.setVisibility(View.VISIBLE);
						chatsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
						ChatsAdapter chatsAdapter = new ChatsAdapter(context, response);
						chatsRecyclerView.setAdapter(chatsAdapter);
					}
					else {
						chatsRecyclerView.setVisibility(View.GONE);
						noChats.setVisibility(View.VISIBLE);
					}
					userChats = String.valueOf(response);
				}
			}

			@Override
			public void executeAfterErrorResponse(String error) {
				Log.e(TAG, error);
			}
		});
		return chatsLayout;
	}
}
