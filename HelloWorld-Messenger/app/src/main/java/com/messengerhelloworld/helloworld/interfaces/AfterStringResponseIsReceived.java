package com.messengerhelloworld.helloworld.interfaces;

public interface AfterStringResponseIsReceived {
	void executeAfterResponse(String response);
	void executeAfterErrorResponse(String error);
}
