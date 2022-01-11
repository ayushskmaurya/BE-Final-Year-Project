package com.messengerhelloworld.helloworld.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.interfaces.ProgressBarForChatWithoutInternet;

import org.json.JSONArray;
import org.json.JSONException;

public class ChatWithoutInternetAdapter extends RecyclerView.Adapter {
	private static final String TAG = "hwmLogNoNetChatAdapter";
	private final JSONArray localDataSet;
	private final Activity activity;
	private final ProgressBarForChatWithoutInternet progressBarForChatWithoutInternet;

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

	public static class SentFileViewHolder extends RecyclerView.ViewHolder {
		private final TextView sentFilename;
		private final TextView sentFileTime;
		private final ImageView fileReadReceipt;
		private final ProgressBar senderProgressBarHorizontal;

		public SentFileViewHolder(View view) {
			super(view);
			sentFilename = view.findViewById(R.id.filename_rowItemAttachmentSent);
			sentFileTime = view.findViewById(R.id.time_rowItemAttachmentSent);
			fileReadReceipt = view.findViewById(R.id.readReceipt_rowItemAttachmentSent);
			senderProgressBarHorizontal = view.findViewById(R.id.progressBarHorizontal_rowItemAttachmentSent);
		}

		public TextView getSentFilename() {
			return sentFilename;
		}
		public TextView getSentFileTime() {
			return sentFileTime;
		}
		public ImageView getFileReadReceipt() {
			return fileReadReceipt;
		}
		public ProgressBar getSenderProgressBarHorizontal() {
			return senderProgressBarHorizontal;
		}
	}

	public static class ReceivedFileViewHolder extends RecyclerView.ViewHolder {
		private final ImageView downloadReceivedFile;
		private final TextView receivedFilename;
		private final TextView receivedFileTime;
		private final ProgressBar receiverProgressBarHorizontal;

		public ReceivedFileViewHolder(View view) {
			super(view);
			downloadReceivedFile = view.findViewById(R.id.download_rowItemAttachmentReceived);
			receivedFilename = view.findViewById(R.id.filename_rowItemAttachmentReceived);
			receivedFileTime = view.findViewById(R.id.time_rowItemAttachmentReceived);
			receiverProgressBarHorizontal = view.findViewById(R.id.progressBarHorizontal_rowItemAttachmentReceived);
		}

		public ImageView getDownloadReceivedFile() {
			return downloadReceivedFile;
		}
		public TextView getReceivedFilename() {
			return receivedFilename;
		}
		public TextView getReceivedFileTime() {
			return receivedFileTime;
		}
		public ProgressBar getReceiverProgressBarHorizontal() {
			return receiverProgressBarHorizontal;
		}
	}

	public static class BlankViewHolder extends RecyclerView.ViewHolder {
		public BlankViewHolder(View view) {
			super(view);
		}
	}

	public ChatWithoutInternetAdapter(JSONArray dataSet, Activity activity, ProgressBarForChatWithoutInternet progressBarForChatWithoutInternet) {
		localDataSet = dataSet;
		this.activity = activity;
		this.progressBarForChatWithoutInternet = progressBarForChatWithoutInternet;
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
		else if(viewType == 2) {
			View view = LayoutInflater.from(viewGroup.getContext())
					.inflate(R.layout.row_item_attachment_sent, viewGroup, false);
			return new ChatWithoutInternetAdapter.SentFileViewHolder(view);
		}
		else if(viewType == 3) {
			View view = LayoutInflater.from(viewGroup.getContext())
					.inflate(R.layout.row_item_attachment_received, viewGroup, false);
			return new ChatWithoutInternetAdapter.ReceivedFileViewHolder(view);
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
		// 2 --> The attachment is sent by the user.
		// 3 --> The attachment is received by the user.
		// 4 --> If any of the above conditions is not met.

		try {
			if(localDataSet.getJSONObject(position).getString("isMsgSent").equals("yes")) {
				if(localDataSet.getJSONObject(position).getString("isFileSent").equals("null"))
					return 0;
				return 2;
			}
			else {
				if(localDataSet.getJSONObject(position).getString("isFileSent").equals("null"))
					return 1;
				return 3;
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return 4;
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

		else if(viewHolder.getClass() == ChatWithoutInternetAdapter.SentFileViewHolder.class) {
			ChatWithoutInternetAdapter.SentFileViewHolder vHolder = (ChatWithoutInternetAdapter.SentFileViewHolder) viewHolder;
			try {
				vHolder.getFileReadReceipt().setVisibility(View.GONE);
				vHolder.getSentFilename().setText(localDataSet.getJSONObject(position).getString("message"));
				vHolder.getSentFileTime().setText(localDataSet.getJSONObject(position).getString("dateTime"));
				if(localDataSet.getJSONObject(position).getString("isFileSent").equals("yes"))
					vHolder.getSenderProgressBarHorizontal().setVisibility(View.GONE);
				else {
					progressBarForChatWithoutInternet.setProgressBar(vHolder.getSenderProgressBarHorizontal());
					vHolder.getSenderProgressBarHorizontal().getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
					vHolder.getSenderProgressBarHorizontal().setVisibility(View.VISIBLE);
				}
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}

		else if(viewHolder.getClass() == ChatWithoutInternetAdapter.ReceivedFileViewHolder.class) {
			ChatWithoutInternetAdapter.ReceivedFileViewHolder vHolder = (ChatWithoutInternetAdapter.ReceivedFileViewHolder) viewHolder;
			try {
				vHolder.getDownloadReceivedFile().setVisibility(View.GONE);
				vHolder.getReceivedFilename().setText(localDataSet.getJSONObject(position).getString("message"));
				vHolder.getReceivedFileTime().setText(localDataSet.getJSONObject(position).getString("dateTime"));
				if(localDataSet.getJSONObject(position).getString("isFileSent").equals("yes"))
					vHolder.getReceiverProgressBarHorizontal().setVisibility(View.GONE);
				else {
					progressBarForChatWithoutInternet.setProgressBar(vHolder.getReceiverProgressBarHorizontal());
					vHolder.getReceiverProgressBarHorizontal().getProgressDrawable().setColorFilter(ContextCompat.getColor(
							activity, R.color.color1), PorterDuff.Mode.SRC_ATOP);
					vHolder.getReceiverProgressBarHorizontal().setVisibility(View.VISIBLE);
				}
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
