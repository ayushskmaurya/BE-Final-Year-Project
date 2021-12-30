package com.messengerhelloworld.helloworld.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.activities.ChatWithoutInternetActivity;
import com.messengerhelloworld.helloworld.utils.ShouldSync;

public class ChatWithoutInternetFragment extends Fragment {
	private static final String IS_HOST_OR_CLIENT = "com.messengerhelloworld.helloworld.isHostOrClient";

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {

		View chatWithoutInternetLayout = inflater.inflate(R.layout.fragment_chat_without_internet, container, false);
		ShouldSync.setShouldSyncChats(false);

		Button host = chatWithoutInternetLayout.findViewById(R.id.hostButton_fragmentChatWithoutInternet);
		Button client = chatWithoutInternetLayout.findViewById(R.id.clientButton_fragmentChatWithoutInternet);

		host.setOnClickListener(v -> {
			Intent intent = new Intent(getActivity(), ChatWithoutInternetActivity.class);
			intent.putExtra(IS_HOST_OR_CLIENT, "host");
			getActivity().startActivity(intent);
		});
		client.setOnClickListener(v -> {
			Intent intent = new Intent(getActivity(), ChatWithoutInternetActivity.class);
			intent.putExtra(IS_HOST_OR_CLIENT, "client");
			getActivity().startActivity(intent);
		});

		return chatWithoutInternetLayout;
	}
}