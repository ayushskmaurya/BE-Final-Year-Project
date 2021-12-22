package com.messengerhelloworld.helloworld.interfaces;

import org.json.JSONArray;

public interface AfterJsonArrayResponseIsReceived {
	void executeAfterResponse(JSONArray response);
	void executeAfterErrorResponse(String error);
}
