package com.messengerhelloworld.helloworld.interfaces;

import org.json.JSONObject;

public interface AfterJsonObjectResponseIsReceived {
	void executeAfterResponse(JSONObject response);
	void executeAfterErrorResponse(String error);
}
