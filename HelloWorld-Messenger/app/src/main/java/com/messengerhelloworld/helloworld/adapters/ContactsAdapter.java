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

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
	private static final String TAG = "hwmLogContactsAdapter";
	private static final String CHAT_ID = "com.messengerhelloworld.helloworld.chatId";
	private static final String RECEIVER_USER_NAME = "com.messengerhelloworld.helloworld.receiverUserName";
	private static final String RECEIVER_USER_ID = "com.messengerhelloworld.helloworld.receiverUserId";
	private static final String PROFILE_IMAGE_NAME = "com.messengerhelloworld.helloworld.profileImageName";
	private final Context context;
	private final JSONArray localDataSet;
	private Intent intentProfileImage;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final View contact;
		private final TextView userName;
		private final ImageView profileImg;

		public ViewHolder(View view) {
			super(view);
			contact = view.findViewById(R.id.contact_rowItemContacts);
			userName = view.findViewById(R.id.userName_rowItemContacts);
			profileImg = view.findViewById(R.id.profileImg_rowItemContacts);
		}

		public View getContact() {
			return contact;
		}
		public TextView getUserName() {
			return userName;
		}
		public ImageView getProfileImg() {
			return profileImg;
		}
	}

	public ContactsAdapter(Context context, JSONArray dataSet) {
		this.context = context;
		localDataSet = dataSet;
		intentProfileImage = new Intent(this.context, ViewProfileImageActivity.class);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View view = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.row_item_contacts, viewGroup, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, final int position) {
		viewHolder.getContact().setOnClickListener(v -> {
			try {
				Intent intent = new Intent(context, ChatActivity.class);
				intent.putExtra(CHAT_ID, localDataSet.getJSONObject(position).getString("chatid"));
				intent.putExtra(RECEIVER_USER_NAME, localDataSet.getJSONObject(position).getString("name"));
				intent.putExtra(RECEIVER_USER_ID, localDataSet.getJSONObject(position).getString("userid"));
				context.startActivity(intent);
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		});
		try {
			viewHolder.getUserName().setText(localDataSet.getJSONObject(position).getString("name"));

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
