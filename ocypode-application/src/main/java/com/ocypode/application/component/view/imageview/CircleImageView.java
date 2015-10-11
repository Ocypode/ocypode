package com.ocypode.application.component.view.imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.ImageView;

import roboguice.RoboGuice;

public class CircleImageView extends ImageView {

	public CircleImageView(Context context, AttributeSet attrs) {
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

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(createCircleBitmap(bm));
	}

	private Bitmap createCircleBitmap(Bitmap bitmap) {
		Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);

		BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
				TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setShader(shader);

		Canvas c = new Canvas(circleBitmap);
		c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
				bitmap.getWidth() / 2, paint);

		return circleBitmap;
	}

}
