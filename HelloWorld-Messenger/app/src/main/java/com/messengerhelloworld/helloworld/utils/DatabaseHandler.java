package com.messengerhelloworld.helloworld.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler extends SQLiteOpenHelper {
	public DatabaseHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createAttachmentsTable = "CREATE TABLE attachments (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"msgid VARCHAR(100) UNIQUE NOT NULL," +
			"temp_filename VARCHAR(100) NOT NULL," +
			"filepath TEXT NOT NULL" +
		")";

		String createLastDeletedProfileImages = "CREATE TABLE last_deleted_profile_images (" +
			"date_time TEXT" +
		")";

		db.execSQL(createAttachmentsTable);
		db.execSQL(createLastDeletedProfileImages);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String drop = String.valueOf("DROP TABLE IF EXISTS");
		db.execSQL(drop, new String[]{"attachments", "last_deleted_profile_images"});
		onCreate(db);
	}

	public void addAttachment(Attachment attachment) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("msgid", attachment.getMsgid());
		values.put("temp_filename", attachment.getTemp_filename());
		values.put("filepath", attachment.getFilepath());
		db.insert("attachments", null, values);
		db.close();
	}

	public Map<String, String> getAttachment() {
		Map<String, String> attachment = new HashMap<>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("attachments",
				new String[]{"msgid", "temp_filename", "filepath"},
				null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			attachment.put("msgid", cursor.getString(0));
			attachment.put("temp_filename", cursor.getString(1));
			attachment.put("filepath", cursor.getString(2));
		}
		db.close();
		return attachment;
	}

	public void deleteAttachment(String msgid) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("attachments", "msgid=?", new String[]{String.valueOf(msgid)});
		db.close();
	}

	public int getAttachmentsCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("attachments",
				null, null, null, null, null, null);
		int count = cursor.getCount();
		db.close();
		return count;
	}


	public void insertLastDeletedProfileImagesDateTime(LastDeletedProfileImages lastDeletedProfileImages) {
		if(getLastDeletedProfileImagesCount() == 0) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("date_time", lastDeletedProfileImages.getDate_time());
			db.insert("last_deleted_profile_images", null, values);
			db.close();
		}
	}

	public int getDifferenceBetweenDateTime() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor1 = db.query("last_deleted_profile_images",
				null, null, null, null, null, null);
		String lastDeletedDateTime = (cursor1 != null && cursor1.moveToFirst()) ? cursor1.getString(0) : "0";
		String sql = "SELECT strftime('%s', 'now') - " + lastDeletedDateTime;
		Cursor cursor2 = db.rawQuery(sql, null);
		String difference = (cursor2 != null && cursor2.moveToFirst()) ? cursor2.getString(0) : "0";
		db.close();
		return Integer.parseInt(difference);
	}

	public void updateLastDeletedProfileImagesDateTime(String currentDateTime) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("date_time", currentDateTime);
		db.update("last_deleted_profile_images", values, null, null);
		db.close();
	}

	public int getLastDeletedProfileImagesCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("last_deleted_profile_images",
				null, null, null, null, null, null);
		int count = cursor.getCount();
		db.close();
		return count;
	}

	public String getCurrentDateTime() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT strftime('%s', 'now')", null);
		String currentDateTime = (cursor != null && cursor.moveToFirst()) ? cursor.getString(0) : "0";
		db.close();
		return currentDateTime;
	}
}
