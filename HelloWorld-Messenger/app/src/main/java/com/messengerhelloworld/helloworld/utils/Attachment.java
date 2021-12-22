package com.messengerhelloworld.helloworld.utils;

public class Attachment {
	private int id;
	private String msgid;
	private String temp_filename;
	private String filepath;

	public Attachment(String msgid, String temp_filename, String filepath) {
		this.msgid = msgid;
		this.temp_filename = temp_filename;
		this.filepath = filepath;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getTemp_filename() {
		return temp_filename;
	}

	public void setTemp_filename(String temp_filename) {
		this.temp_filename = temp_filename;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
}
