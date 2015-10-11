package com.ocypode.application.activity;

import android.content.Intent;

public interface IResultCallbackActivity {

	void onResultOk(Intent data);

	void onResultCancel(Intent data);
}
