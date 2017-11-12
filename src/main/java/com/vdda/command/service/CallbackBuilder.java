package com.vdda.command.service;


public class CallbackBuilder {

	public static final String SEPARATOR = "|";

	private CallbackBuilder() {
		throw new UnsupportedOperationException();
	}

	public static String callbackIdBuilder(String callbackId, String userId) {
		return callbackId + SEPARATOR + userId;
	}

	public static String callbackIdBuilder(String callbackId, String userId, String outcome) {
		return callbackId + SEPARATOR + userId + SEPARATOR + outcome;
	}
}
