package com.messengerhelloworld.helloworld.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.activities.ChatActivity;

import org.json.JSONArray;
import org.json.JSONException;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
	private static final String TAG = "hwmLogGroupsAdapter";
	private static final String IS_GROUP = "com.messengerhelloworld.helloworld.isGroup";
	private static final String CHAT_ID = "com.messengerhelloworld.helloworld.chatId";
	private static final String RECEIVER_USER_NAME = "com.messengerhelloworld.helloworld.receiverUserName";
	private static final String RECEIVER_USER_ID = "com.messengerhelloworld.helloworld.receiverUserId";
	private final Activity activity;
	private final JSONArray localDataSet;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final View group;
		private final TextView groupName;

		public ViewHolder(View view) {
			super(view);
			group = view.findViewById(R.id.group_rowItemGroups);
			groupName = view.findViewById(R.id.groupName_rowItemGroups);
		}

		public View getGroup() {
			return group;
		}
		public TextView getGroupName() {
			return groupName;
		}
	}

	public GroupsAdapter(Activity activity, JSONArray dataSet) {
		this.activity = activity;
		localDataSet = dataSet;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View view = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.row_item_groups, viewGroup, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, final int position) {
		viewHolder.getGroup().setOnClickListener(v -> {
			try {
				Intent intent = new Intent(activity, ChatActivity.class);
				intent.putExtra(IS_GROUP, "yes");
				intent.putExtra(CHAT_ID, localDataSet.getJSONObject(position).getString("chatid"));
				intent.putExtra(RECEIVER_USER_NAME, localDataSet.getJSONObject(position).getString("name"));
				intent.putExtra(RECEIVER_USER_ID, "null");
				activity.startActivity(intent);
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		});
		try {
			viewHolder.getGroupName().setText(localDataSet.getJSONObject(position).getString("name"));
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public int getItemCount() {
		return localDataSet.length();
	}
}
