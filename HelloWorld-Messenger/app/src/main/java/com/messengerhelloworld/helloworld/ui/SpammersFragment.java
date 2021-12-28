package com.messengerhelloworld.helloworld.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messengerhelloworld.helloworld.activities.MainActivity;

public class SpammersFragment extends Fragment {

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {

		SharedPreferences sp = getActivity().getSharedPreferences("HelloWorldSharedPref", Context.MODE_PRIVATE);
		SharedPreferences.Editor ed = sp.edit();
		ed.putString("whichFragment", "spammers");
		ed.apply();

		((MainActivity) getActivity()).replaceFragmentSpammersToChats();
		return null;
	}
}