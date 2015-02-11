package com.ocypode.volleyrestclient.infrastructure.handler;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.ocypode.volleyrestclient.infrastructure.request.VolleyErrorHelper;

public class RequestHandler<T> implements Handler<T> {
	
	// TODO: create this context separated from the view. the app crashes if you
	// finish the context and do not cancel the request
	private Context mContext;
	
	public RequestHandler(Context context) {
		mContext = context;
	}
	
	@Override
	public void onSuccess(T response) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFail(Exception error) {
		try {
			String message = VolleyErrorHelper.getMessage(error, mContext);
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setMessage(message);
			builder.create();
			
		} catch(Exception e) {
			Log.e("onFail", "RequestHandler.onFail", e);
		}
	}

}
