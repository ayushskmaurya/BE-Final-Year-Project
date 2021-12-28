package com.messengerhelloworld.helloworld.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.activities.MainActivity;
import com.messengerhelloworld.helloworld.adapters.ChatsAdapter;
import com.messengerhelloworld.helloworld.interfaces.AfterJsonArrayResponseIsReceived;
import com.messengerhelloworld.helloworld.interfaces.AfterStringResponseIsReceived;
import com.messengerhelloworld.helloworld.utils.ChatItemLongPressedVars;
import com.messengerhelloworld.helloworld.utils.DatabaseOperations;
import com.messengerhelloworld.helloworld.utils.ShouldSync;

import org.json.JSONArray;

import java.util.HashMap;

public class ChatsFragment extends Fragment {
	private static final String TAG = "hwmLogChats";
	private Context context;
	private String currentFragment;
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
		ShouldSync.setShouldSyncChats(false);

		SharedPreferences sp = getActivity().getSharedPreferences("HelloWorldSharedPref", Context.MODE_PRIVATE);
		String whichFragment = sp.getString("whichFragment", null);
		if(whichFragment != null && whichFragment.equals("spammers")) {
			currentFragment = "spammers";
			SharedPreferences.Editor ed = sp.edit();
			ed.putString("whichFragment", null);
			ed.apply();
		}
		else
			currentFragment = "chats";

		View toolbar = chatsLayout.findViewById(R.id.toolbar_fragmentChats);
		ImageButton backButton = chatsLayout.findViewById(R.id.backButton_fragmentChats);
		ImageButton spammerButton = chatsLayout.findViewById(R.id.spammerButton_fragmentChats);
		chatsProgressBar = chatsLayout.findViewById(R.id.progressBar_fragmentChats);
		chatsRecyclerView = chatsLayout.findViewById(R.id.chats_fragmentChats);
		noChats = chatsLayout.findViewById(R.id.noChats_fragmentChats);

		if(currentFragment.equals("spammers"))
			spammerButton.setImageResource(R.drawable.icon_spammer_remove);

		HashMap<String, String> data = new HashMap<>();
		data.put("whichFragment", currentFragment);
		data.put("userid", sp.getString("HelloWorldUserId", null));

		ChatItemLongPressedVars.setAllVarsAsNull();

		if(!ShouldSync.getIsCurrentlySyncingChats()) {
			ShouldSync.setShouldSyncChats(true);
			ShouldSync.setShouldRestartSync(false);
			ShouldSync.setIsCurrentlySyncingChats(true);
		}
		else
			ShouldSync.setShouldRestartSync(true);

		databaseOperations.retrieveChats(ShouldSync.getShouldRestartSync(), data, new AfterJsonArrayResponseIsReceived() {
			@Override
			public void executeAfterResponse(JSONArray response) {
				chatsProgressBar.setVisibility(View.GONE);
				if(!String.valueOf(response).equals(userChats)) {
					if(response.length() != 0) {
						noChats.setVisibility(View.GONE);
						chatsRecyclerView.setVisibility(View.VISIBLE);
						chatsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

						ChatsAdapter chatsAdapter = new ChatsAdapter(context, response, (String userId, String userName) -> {
							((MainActivity) getActivity()).enableOrDisableNavigation(
									DrawerLayout.LOCK_MODE_LOCKED_CLOSED, true);
							toolbar.setVisibility(View.VISIBLE);
							ChatItemLongPressedVars.getChatView().setBackgroundColor(context.getResources().getColor(R.color.color3));

							backButton.setOnClickListener(v -> afterTasksArePerformed(toolbar));

							Dialog dialogSpammer = new Dialog(context);
							dialogSpammer.setContentView(R.layout.dialog_spammer);
							dialogSpammer.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							dialogSpammer.setCancelable(false);

							String message = currentFragment.equals("chats") ? "Do you" : "Do you not";
							message += " want to state " + userName + " as Spammer?";
							TextView msgView = dialogSpammer.findViewById(R.id.message_dialogSpammer);
							msgView.setText(message);

							spammerButton.setOnClickListener(v1 -> {
								dialogSpammer.show();
								Button cancelButton = dialogSpammer.findViewById(R.id.no_dialogSpammer);
								Button confirmButton = dialogSpammer.findViewById(R.id.yes_dialogSpammer);

								cancelButton.setOnClickListener(v2 -> {
									afterTasksArePerformed(toolbar);
									dialogSpammer.dismiss();
								});

								confirmButton.setOnClickListener(v2 -> {
									HashMap<String, String> postData = new HashMap<>();
									postData.put("whatToDo", currentFragment.equals("chats") ? "stateAsSpammer" : "unstateAsSpammer");
									postData.put("chatid", ChatItemLongPressedVars.getChatId());
									postData.put("myuserid", sp.getString("HelloWorldUserId", null));
									postData.put("userid", userId);

									databaseOperations.manageSpammer(postData, new AfterStringResponseIsReceived() {
										@Override
										public void executeAfterResponse(String response) {
											afterTasksArePerformed(toolbar);
											dialogSpammer.dismiss();
										}

										@Override
										public void executeAfterErrorResponse(String error) {
											Log.e(TAG, error);
											afterTasksArePerformed(toolbar);
											dialogSpammer.dismiss();
										}
									});
								});
							});
						});

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

	// If the chat item was long pressed
	// then if the back or cancel button is pressed
	// or if the work is done
	// then execute the following
	private void afterTasksArePerformed(View toolbar) {
		toolbar.setVisibility(View.GONE);
		((MainActivity) getActivity()).enableOrDisableNavigation(
				DrawerLayout.LOCK_MODE_UNLOCKED, false);
		if(ChatItemLongPressedVars.getIsNewMsg().equals("1")) {
			ChatItemLongPressedVars.getChatView().setBackgroundColor(getResources().getColor(R.color.grey3));
			ChatItemLongPressedVars.getLastMsg().setTextColor(context.getResources().getColor(R.color.black));
			ChatItemLongPressedVars.getLastMsg().setTextSize(16);
		}
		else
			ChatItemLongPressedVars.getChatView().setBackgroundColor(getResources().getColor(R.color.white));
		ChatItemLongPressedVars.setAllVarsAsNull();
	}
}
