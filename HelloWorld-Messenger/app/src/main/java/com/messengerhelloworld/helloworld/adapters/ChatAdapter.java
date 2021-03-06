package com.messengerhelloworld.helloworld.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.messengerhelloworld.helloworld.R;
import com.messengerhelloworld.helloworld.interfaces.AfterStringResponseIsReceived;
import com.messengerhelloworld.helloworld.interfaces.ItemMsgIsLongPressed;
import com.messengerhelloworld.helloworld.utils.Base;
import com.messengerhelloworld.helloworld.utils.DatabaseHandler;
import com.messengerhelloworld.helloworld.utils.DatabaseOperations;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

public class ChatAdapter extends RecyclerView.Adapter {
	private static final String TAG = "hwmLogChatAdapter";
	private final JSONArray localDataSet;
	private final String userid;
	private final String isGroup;
	private final Activity activity;
	private final ItemMsgIsLongPressed itemMsgIsLongPressed;
	private DatabaseOperations databaseOperations;
	private final DatabaseHandler databaseHandler;
	private String itemLongPressedMsgId;
	private String readReceipt;

	public static class SentViewHolder extends RecyclerView.ViewHolder {
		private final View sentMsgBg;
		private final View sentMsgLayout;
		private final TextView sentMsg;
		private final TextView sentMsgTime;
		private final ImageView msgReadReceipt;

		public SentViewHolder(View view) {
			super(view);
			sentMsgBg = view.findViewById(R.id.bg_rowItemMessageSent);
			sentMsgLayout = view.findViewById(R.id.msgLayout_rowItemMessageSent);
			sentMsg = view.findViewById(R.id.msg_rowItemMessageSent);
			sentMsgTime = view.findViewById(R.id.time_rowItemMessageSent);
			msgReadReceipt = view.findViewById(R.id.readReceipt_rowItemMessageSent);
		}

		public View getSentMsgBg() {
			return sentMsgBg;
		}
		public View getSentMsgLayout() {
			return sentMsgLayout;
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
		private final TextView msgSenderName;
		private final TextView receivedMsg;
		private final TextView receivedMsgTime;

		public ReceivedViewHolder(View view) {
			super(view);
			msgSenderName = view.findViewById(R.id.sendername_rowItemMessageReceived);
			receivedMsg = view.findViewById(R.id.msg_rowItemMessageReceived);
			receivedMsgTime = view.findViewById(R.id.time_rowItemMessageReceived);
		}

		public TextView getMsgSenderName() {
			return msgSenderName;
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
		private final TextView sentCaption;
		private final TextView sentCaptionTime;
		private final ProgressBar progressBar;
		private final ImageView cancelUpload;
		private final ImageView downloadSentFile;
		private final ImageView fileReadReceipt;

		public SentFileViewHolder(View view) {
			super(view);
			sentFilename = view.findViewById(R.id.filename_rowItemAttachmentSent);
			sentCaption = view.findViewById(R.id.caption_rowItemAttachmentSent);
			sentCaptionTime = view.findViewById(R.id.time_rowItemAttachmentSent);
			progressBar = view.findViewById(R.id.progressBar_rowItemAttachmentSent);
			cancelUpload = view.findViewById(R.id.cancel_rowItemAttachmentSent);
			downloadSentFile = view.findViewById(R.id.download_rowItemAttachmentSent);
			fileReadReceipt = view.findViewById(R.id.readReceipt_rowItemAttachmentSent);
		}

		public TextView getSentFilename() {
			return sentFilename;
		}
		public TextView getSentCaption() {
			return sentCaption;
		}
		public TextView getSentCaptionTime() {
			return sentCaptionTime;
		}
		public ProgressBar getProgressBar() {
			return progressBar;
		}
		public ImageView getCancelUpload() {
			return cancelUpload;
		}
		public ImageView getDownloadSentFile() {
			return downloadSentFile;
		}
		public ImageView getFileReadReceipt() {
			return fileReadReceipt;
		}
	}

	public static class ReceivedFileViewHolder extends RecyclerView.ViewHolder {
		private final TextView fileSenderName;
		private final TextView receivedFilename;
		private final TextView receivedCaption;
		private final TextView receivedCaptionTime;
		private final ImageView downloadReceivedFile;

		public ReceivedFileViewHolder(View view) {
			super(view);
			fileSenderName = view.findViewById(R.id.senderName_rowItemAttachmentReceived);
			receivedFilename = view.findViewById(R.id.filename_rowItemAttachmentReceived);
			receivedCaption = view.findViewById(R.id.caption_rowItemAttachmentReceived);
			receivedCaptionTime = view.findViewById(R.id.time_rowItemAttachmentReceived);
			downloadReceivedFile = view.findViewById(R.id.download_rowItemAttachmentReceived);
		}

		public TextView getFileSenderName() {
			return fileSenderName;
		}
		public TextView getReceivedFilename() {
			return receivedFilename;
		}
		public TextView getReceivedCaption() {
			return receivedCaption;
		}
		public TextView getReceivedCaptionTime() {
			return receivedCaptionTime;
		}
		public ImageView getDownloadReceivedFile() {
			return downloadReceivedFile;
		}
	}

	public static class BlankViewHolder extends RecyclerView.ViewHolder {
		public BlankViewHolder(View view) {
			super(view);
		}
	}

	public ChatAdapter(JSONArray dataSet, String userid, String isGroup, Activity activity, ItemMsgIsLongPressed itemMsgIsLongPressed) {
		localDataSet = dataSet;
		this.userid = userid;
		this.isGroup = isGroup;
		this.activity = activity;
		this.itemMsgIsLongPressed = itemMsgIsLongPressed;
		databaseOperations = new DatabaseOperations(this.activity);
		databaseHandler = new DatabaseHandler(this.activity, "helloworld", null, 1);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		if(viewType == 0) {
			View view = LayoutInflater.from(viewGroup.getContext())
					.inflate(R.layout.row_item_message_sent, viewGroup, false);
			return new SentViewHolder(view);
		}
		else if(viewType == 1) {
			View view = LayoutInflater.from(viewGroup.getContext())
					.inflate(R.layout.row_item_message_received, viewGroup, false);
			return new ReceivedViewHolder(view);
		}
		else if(viewType == 2) {
			View view = LayoutInflater.from(viewGroup.getContext())
					.inflate(R.layout.row_item_attachment_sent, viewGroup, false);
			return new SentFileViewHolder(view);
		}
		else if(viewType == 3) {
			View view = LayoutInflater.from(viewGroup.getContext())
					.inflate(R.layout.row_item_attachment_received, viewGroup, false);
			return new ReceivedFileViewHolder(view);
		}
		else {
			View view = LayoutInflater.from(viewGroup.getContext())
					.inflate(R.layout.row_item_blank, viewGroup, false);
			return new BlankViewHolder(view);
		}
	}

	@Override
	public int getItemViewType(int position) {
		// 0 --> The message is sent by the user.
		// 1 --> The message is received by the user.
		// 2 --> The attachment is sent by the user.
		// 3 --> The attachment is received by the user.
		// 4 --> The attachment is not received by the user Or if any of the above conditions is not met.

		try {
			if(localDataSet.getJSONObject(position).getString("senderid").equals(userid)) {
				if(localDataSet.getJSONObject(position).getString("filename").equals(""))
					return 0;
				return 2;
			}
			else {
				if(localDataSet.getJSONObject(position).getString("filename").equals(""))
					return 1;
				else if(localDataSet.getJSONObject(position).getString("isFileUploaded").equals("1"))
					return 3;
				return 4;
			}
		} catch (JSONException e) {
			Log.e(TAG, e.toString());
			return 4;
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

		if(viewHolder.getClass() == SentViewHolder.class) {
			SentViewHolder vHolder = (SentViewHolder) viewHolder;
			try {
				vHolder.getSentMsg().setText(localDataSet.getJSONObject(position).getString("message"));
				vHolder.getSentMsgTime().setText(localDataSet.getJSONObject(position).getString("dateTime").substring(11, 16));
				if(isGroup.equals("no")) {
					readReceipt = localDataSet.getJSONObject(position).getString("isMsgSeen");
					if (readReceipt.equals("1"))
						vHolder.getMsgReadReceipt().setImageResource(R.drawable.icon_read_receipt_seen);
				}
				else
					vHolder.getMsgReadReceipt().setVisibility(View.GONE);

				itemLongPressedMsgId = itemMsgIsLongPressed.getItemLongPressedMsgId();
				if(itemLongPressedMsgId != null && itemLongPressedMsgId.equals(localDataSet.getJSONObject(position).getString("msgid")))
					itemMsgIsLongPressed.highlightBg(vHolder.getSentMsgLayout(), vHolder.getSentMsgBg());

			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}

			vHolder.getSentMsgBg().setOnLongClickListener(v -> {
				try {
					if(isGroup.equals("no") && itemMsgIsLongPressed.getItemLongPressedMsgId() == null && localDataSet.getJSONObject(position).getString("isMsgSeen").equals("0")) {
						itemMsgIsLongPressed.whenItemPressed(localDataSet.getJSONObject(position).getString("msgid"),
								localDataSet.getJSONObject(position).getString("message"));
						itemMsgIsLongPressed.highlightBg(vHolder.getSentMsgLayout(), vHolder.getSentMsgBg());
					}
				} catch (JSONException e) {
					Log.e(TAG, e.toString());
				}
				return true;
			});
		}

		else if(viewHolder.getClass() == ReceivedViewHolder.class) {
			ReceivedViewHolder vHolder = (ReceivedViewHolder) viewHolder;
			try {
				if(isGroup.equals("yes")) {
					vHolder.getMsgSenderName().setText(localDataSet.getJSONObject(position).getString("sendername"));
					vHolder.getMsgSenderName().setVisibility(View.VISIBLE);
				}
				vHolder.getReceivedMsg().setText(localDataSet.getJSONObject(position).getString("message"));
				vHolder.getReceivedMsgTime().setText(localDataSet.getJSONObject(position).getString("dateTime").substring(11, 16));
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}

		else if(viewHolder.getClass() == SentFileViewHolder.class) {
			SentFileViewHolder vHolder = (SentFileViewHolder) viewHolder;
			try {
				vHolder.getSentFilename().setText(localDataSet.getJSONObject(position).getString("filename"));
				vHolder.getSentCaption().setText(localDataSet.getJSONObject(position).getString("message"));
				vHolder.getSentCaptionTime().setText(localDataSet.getJSONObject(position).getString("dateTime").substring(11, 16));
				if(localDataSet.getJSONObject(position).getString("isFileUploaded").equals("1")) {
					vHolder.getProgressBar().setVisibility(View.GONE);
					vHolder.getCancelUpload().setVisibility(View.GONE);
					vHolder.getDownloadSentFile().setVisibility(View.VISIBLE);
					if(isGroup.equals("no")) {
						readReceipt = localDataSet.getJSONObject(position).getString("isMsgSeen");
						if(readReceipt.equals("1"))
							vHolder.getFileReadReceipt().setImageResource(R.drawable.icon_read_receipt_seen);
						else if(readReceipt.equals("0"))
							vHolder.getFileReadReceipt().setImageResource(R.drawable.icon_read_receipt_sent);
					}
					else
						vHolder.getFileReadReceipt().setVisibility(View.GONE);

					vHolder.getDownloadSentFile().setOnClickListener(v -> {
						try {
							downloadFile(
									localDataSet.getJSONObject(position).getString("msgid"),
									localDataSet.getJSONObject(position).getString("filename")
							);
						} catch (JSONException e) {
							Log.e(TAG, e.toString());
						}
					});
				}
				else {
					vHolder.getDownloadSentFile().setVisibility(View.GONE);
					vHolder.getProgressBar().setVisibility(View.VISIBLE);
					vHolder.getCancelUpload().setVisibility(View.VISIBLE);

					vHolder.getCancelUpload().setOnClickListener(v -> {
						HashMap<String, String> data = new HashMap<>();
						try {
							String msgid = localDataSet.getJSONObject(position).getString("msgid");
							data.put("whatToDo", "cancelUpload");
							data.put("msgid", msgid);
							databaseOperations.cancelUploadingAttachment(data, new AfterStringResponseIsReceived() {
								@Override
								public void executeAfterResponse(String response) {
									if(response.equals("1"))
										databaseHandler.deleteAttachment(msgid);
								}

								@Override
								public void executeAfterErrorResponse(String error) {
									Log.e(TAG, error);
								}
							});
						} catch (JSONException e) {
							Log.e(TAG, e.toString());
						}
					});
				}
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}

		else if(viewHolder.getClass() == ReceivedFileViewHolder.class) {
			ReceivedFileViewHolder vHolder = (ReceivedFileViewHolder) viewHolder;
			try {
				if(isGroup.equals("yes")) {
					vHolder.getFileSenderName().setText(localDataSet.getJSONObject(position).getString("sendername"));
					vHolder.getFileSenderName().setVisibility(View.VISIBLE);
				}
				vHolder.getReceivedFilename().setText(localDataSet.getJSONObject(position).getString("filename"));
				vHolder.getReceivedCaption().setText(localDataSet.getJSONObject(position).getString("message"));
				vHolder.getReceivedCaptionTime().setText(localDataSet.getJSONObject(position).getString("dateTime").substring(11, 16));

				vHolder.getDownloadReceivedFile().setOnClickListener(v -> {
					try {
						downloadFile(
								localDataSet.getJSONObject(position).getString("msgid"),
								localDataSet.getJSONObject(position).getString("filename")
						);
					} catch (JSONException e) {
						Log.e(TAG, e.toString());
					}
				});
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}
	}

	@Override
	public int getItemCount() {
		return localDataSet.length();
	}

	private void downloadFile(String msgid, String filename) {
		if(ContextCompat.checkSelfPermission(activity,
				Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

			DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
			Uri uri = Uri.parse(Base.getBaseUrl() + "/manageAttachment.php?whatToDo=downloadFile&msgid=" + msgid);
			DownloadManager.Request request = new DownloadManager.Request(uri);
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
			downloadManager.enqueue(request);
		}
		else
			Toast.makeText(activity, "Please grant permission to Write External Storage.", Toast.LENGTH_SHORT).show();
	}
}
