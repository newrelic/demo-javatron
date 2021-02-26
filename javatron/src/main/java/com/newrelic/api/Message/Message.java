package com.newrelic.api.Message;

import java.io.Serializable;

public class Message implements Serializable {

	private boolean result;

	public Message() {
	}

	public boolean getResult() {
		return this.result;
    }
	public void setResult(boolean result) {
		this.result = result;
	}
}
