package com.messengerhelloworld.helloworld.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.messengerhelloworld.helloworld.R;

public class ChatWithoutInternetActivity extends AppCompatActivity {
	private static final String IS_HOST_OR_CLIENT = "com.messengerhelloworld.helloworld.isHostOrClient";
	private String who;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		Intent intent = getIntent();
		who = intent.getStringExtra(IS_HOST_OR_CLIENT);

		Log.d("hwmLog", who);
	}

	@Override
	public void onBackPressed() {
		NavUtils.navigateUpFromSameTask(this);
	}
}