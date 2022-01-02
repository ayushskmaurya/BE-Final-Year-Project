package com.messengerhelloworld.helloworld.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.messengerhelloworld.helloworld.R;

import org.json.JSONArray;
import org.json.JSONException;

public class ChatWithoutInternetAdapter extends RecyclerView.Adapter {
	private static final String TAG = "hwmLogNoNetChatAdapter";
	private final JSONArray localDataSet;

	public static class SentViewHolder extends RecyclerView.ViewHolder {
		private final TextView sentMsg;
		private final TextView sentMsgTime;
		private final ImageView msgReadReceipt;

		public SentViewHolder(View view) {
			super(view);
			sentMsg = view.findViewById(R.id.msg_rowItemMessageSent);
			sentMsgTime = view.findViewById(R.id.time_rowItemMessageSent);
			msgReadReceipt = view.findViewById(R.id.readReceipt_rowItemMessageSent);
		}

		public TextView getSentMsg() {
			return sentMsg;
		}
		public TextView getSentMsgTime() {
			return sentMsgTime;
		}
		public ImageView getMsgReadReceipt() {
			return msgReadReceipt;
		}
	}

	public static class ReceivedViewHolder extends RecyclerView.ViewHolder {
		private final TextView receivedMsg;
		private final TextView receivedMsgTime;

		public ReceivedViewHolder(View view) {
			super(view);
			receivedMsg = view.findViewById(R.id.msg_rowItemMessageReceived);
			receivedMsgTime = view.findViewById(R.id.time_rowItemMessageReceived);
		}

		public TextView getReceivedMsg() {
			return receivedMsg;
		}
		public TextView getReceivedMsgTime() {
			return receivedMsgTime;
		}
	}

	public static class BlankViewHolder extends RecyclerView.ViewHolder {
		public BlankViewHolder(View view) {
			super(view);
		}
	}

	public ChatWithoutInternetAdapter(JSONArray dataSet) {
		localDataSet = dataSet;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		if(viewType == 0) {
			View view = LayoutInflater.from(viewGroup.getContext())
					.inflate(R.layout.row_item_message_sent, viewGroup, false);
			return new ChatWithoutInternetAdapter.SentViewHolder(view);
		}
		else if(viewType == 1) {
			View view = LayoutInflater.from(viewGroup.getContext())
					.inflate(R.layout.row_item_message_received, viewGroup, false);
			return new ChatWithoutInternetAdapter.ReceivedViewHolder(view);
		}
		else {
			View view = LayoutInflater.from(viewGroup.getContext())
					.inflate(R.layout.row_item_blank, viewGroup, false);
			return new ChatWithoutInternetAdapter.BlankViewHolder(view);
		}
	}

	@Override
	public int getItemViewType(int position) {
		// 0 --> The message is sent by the user.
		// 1 --> The message is received by the user.
		// 2 --> If any of the above conditions are not met.

		try {
			if(localDataSet.getJSONObject(position).getString("isMsgSent").equals("yes"))
				return 0;
			else if(localDataSet.getJSONObject(position).getString("isMsgSent").equals("no"))
				return 1;
			else
				return 2;

		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return 2;
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

		if(viewHolder.getClass() == ChatWithoutInternetAdapter.SentViewHolder.class) {
			ChatWithoutInternetAdapter.SentViewHolder vHolder = (ChatWithoutInternetAdapter.SentViewHolder) viewHolder;
			try {
				vHolder.getMsgReadReceipt().setVisibility(View.GONE);
				vHolder.getSentMsg().setText(localDataSet.getJSONObject(position).getString("message"));
				vHolder.getSentMsgTime().setText(localDataSet.getJSONObject(position).getString("dateTime"));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}

		else if(viewHolder.getClass() == ChatWithoutInternetAdapter.ReceivedViewHolder.class) {
			ChatWithoutInternetAdapter.ReceivedViewHolder vHolder = (ChatWithoutInternetAdapter.ReceivedViewHolder) viewHolder;
			try {
				vHolder.getReceivedMsg().setText(localDataSet.getJSONObject(position).getString("message"));
				vHolder.getReceivedMsgTime().setText(localDataSet.getJSONObject(position).getString("dateTime"));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}
	}

	@Override
	public int getItemCount() {
		return localDataSet.length();
	}
}
