package com.ocypode.volleyrestclient.infrastructure.handler;

public interface Handler<T> {

	void onSuccess(T response);
	
	void onFail(Exception error);
}
