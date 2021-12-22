package com.messengerhelloworld.helloworld.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.activities.ChatActivity;
import com.messengerhelloworld.helloworld.activities.ViewProfileImageActivity;
import com.messengerhelloworld.helloworld.utils.DisplayProfileImage;

import org.json.JSONArray;
import org.json.JSONException;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
	private static final String TAG = "hwmLogChatsAdapter";
	private static final String CHAT_ID = "com.messengerhelloworld.helloworld.chatId";
	private static final String RECEIVER_USER_NAME = "com.messengerhelloworld.helloworld.receiverUserName";
	private static final String RECEIVER_USER_ID = "com.messengerhelloworld.helloworld.receiverUserId";
	private static final String PROFILE_IMAGE_NAME = "com.messengerhelloworld.helloworld.profileImageName";
	private final Context context;
	private final JSONArray localDataSet;
	private Intent intentProfileImage;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final View userChat;
		private final TextView userName;
		private final TextView lastMsg;
		private final TextView lastMsgTime;
		private final ImageView profileImg;

		public ViewHolder(View view) {
			super(view);
			userChat = view.findViewById(R.id.chat_rowItemChats);
			userName = view.findViewById(R.id.name_rowItemChats);
			lastMsg = view.findViewById(R.id.lastMsg_rowItemChats);
			lastMsgTime = view.findViewById(R.id.lastMsgTime_rowItemChats);
			profileImg = view.findViewById(R.id.profileImg_rowItemChats);
		}

		public View getUserChat() {
			return userChat;
		}
		public TextView getUserName() {
			return userName;
		}
		public TextView getLastMsg() {
			return lastMsg;
		}
		public TextView getLastMsgTime() {
			return lastMsgTime;
		}
		public ImageView getProfileImg() {
			return profileImg;
		}
	}

	public ChatsAdapter(Context context, JSONArray dataSet) {
		this.context = context;
		localDataSet = dataSet;
		intentProfileImage = new Intent(this.context, ViewProfileImageActivity.class);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View view = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.row_item_chats, viewGroup, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, final int position) {
		viewHolder.getUserChat().setOnClickListener(v -> {
			try {
				Intent intent = new Intent(context, ChatActivity.class);
				intent.putExtra(CHAT_ID, localDataSet.getJSONObject(position).getString("chatid"));
				intent.putExtra(RECEIVER_USER_NAME, localDataSet.getJSONObject(position).getString("name"));
				intent.putExtra(RECEIVER_USER_ID, "null");
				context.startActivity(intent);
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		});
		try {
			viewHolder.getUserName().setText(localDataSet.getJSONObject(position).getString("name"));

			if(localDataSet.getJSONObject(position).getString("isNewMsg").equals("1")) {
				viewHolder.getUserChat().setBackgroundColor(context.getResources().getColor(R.color.grey3));
				viewHolder.getLastMsg().setTextColor(context.getResources().getColor(R.color.black));
				viewHolder.getLastMsg().setTextSize(16);
			}
			viewHolder.getLastMsg().setText(localDataSet.getJSONObject(position).getString("message"));

			try {
				viewHolder.getLastMsgTime().setText(localDataSet.getJSONObject(position).getString("dateTime").substring(11, 16));
			} catch (StringIndexOutOfBoundsException e) {
				viewHolder.getLastMsgTime().setText("");
			}

			String profileImgName = localDataSet.getJSONObject(position).getString("profile_image");
			if(!profileImgName.equals("null")) {
				DisplayProfileImage.display((Activity) context, profileImgName, viewHolder.getProfileImg());
				viewHolder.getProfileImg().setOnClickListener(v -> {
					intentProfileImage.putExtra(PROFILE_IMAGE_NAME, profileImgName);
					context.startActivity(intentProfileImage);
				});
			}

		} catch (JSONException e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public int getItemCount() {
		return localDataSet.length();
	}
}
