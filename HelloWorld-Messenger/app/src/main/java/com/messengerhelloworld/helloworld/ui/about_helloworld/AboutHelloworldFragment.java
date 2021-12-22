package com.messengerhelloworld.helloworld.ui.about_helloworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.utils.ShouldSync;

public class AboutHelloworldFragment extends Fragment {

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {

		View aboutHelloworldLayout = inflater.inflate(R.layout.fragment_about_helloworld, container, false);
		ShouldSync.setShouldSyncChats(false);

		return aboutHelloworldLayout;
	}
}
