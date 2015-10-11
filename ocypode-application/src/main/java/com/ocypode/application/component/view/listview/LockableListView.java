package com.ocypode.application.component.view.listview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import roboguice.RoboGuice;

public class LockableListView extends ListView implements OnPageChangeListener{
	private boolean isLocked = false;
	private boolean mScrollInit = false;
	
	public LockableListView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (isInEditMode()) {
			return;
		}
		initializeRoboGuiceInject();
	}

	private void initializeRoboGuiceInject() {
		RoboGuice.getInjector(getContext()).injectMembers(this);
		RoboGuice.getInjector(getContext()).injectViewMembers(this);
	}

	public void setLock(boolean lock){
		isLocked = lock;
	}
	
	@Override
	public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
		if(isLocked){
			return false;
		}else{
			return super.onInterceptTouchEvent(ev);			
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		checkStateScroll(state);
	}

	@Override
	public void onPageScrolled(int arg0, float offSet, int arg2) {
		if (offSet > 0 && mScrollInit) {
			setLock(true);
		}
	}

	@Override
	public void onPageSelected(int arg0) {

	}
	
	private void checkStateScroll(int state){
		if(state == 1){
			mScrollInit = true;
		}else if(state == 0){
			mScrollInit = false;
			setLock(false);
		}
	}
}
