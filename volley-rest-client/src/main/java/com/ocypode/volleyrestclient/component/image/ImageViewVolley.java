package com.ocypode.volleyrestclient.component.image;

/**
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.ocypode.volleyrestclient.R;
/**
 * Handles fetching an image from a URL as well as the life-cycle of the
 * associated request.
 */
public class ImageViewVolley extends ImageView {
	
	/** The URL of the network image to load */
	private String mUrl;

	/**
	 * Resource ID of the image to be used as a placeholder until the network
	 * image is loaded.
	 */
	private int mDefaultImageId;

	/**
	 * Resource ID of the image to be used if the network response fails.
	 */
	private int mErrorImageId;

	/** Local copy of the ImageLoader. */
	private ImageLoader mImageLoader;

	/** Current ImageContainer. (either in-flight or finished) */
	private ImageContainer mImageContainer;

	private ScaleType mPreviousScaleType;

	private int mSpinnerDrawable;

	private boolean mSpinnerHide;
	
	private boolean isCircle = false;

    private Animation mAnimation;

	public ImageViewVolley(Context context) {
		this(context, null);
	}

	public ImageViewVolley(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImageViewVolley(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		int[][] spinnerColorSize = new int[2][3];
		spinnerColorSize[0][0] = R.drawable.spinner_white_small;
		spinnerColorSize[0][1] = R.drawable.spinner_white_normal;
		spinnerColorSize[0][2] = R.drawable.spinner_white_large;
		spinnerColorSize[1][0] = R.drawable.spinner_black_small;
		spinnerColorSize[1][1] = R.drawable.spinner_black_normal;
		spinnerColorSize[1][2] = R.drawable.spinner_black_large;
		
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ImageViewVolley, defStyle, 0);

		mSpinnerHide = a.getBoolean(R.styleable.ImageViewVolley_spinnerHide, false);
		int spinnerColor = a.getInt(R.styleable.ImageViewVolley_spinnerColor, 0);
		int spinnerSize = a.getInt(R.styleable.ImageViewVolley_spinnerSize, 1);
		mSpinnerDrawable = spinnerColorSize[spinnerColor][spinnerSize];
		
		a.recycle();

        mAnimation = createRotateAnimation();
	}

	/**
	 * Sets URL of the image that should be loaded into this view. Note that
	 * calling this will immediately either set the cached image (if available)
	 * or the default image specified by
	 * {@link com.android.volley.toolbox.NetworkImageView#setDefaultImageResId(int)} on the view.
	 * 
	 * NOTE: If applicable, {@link com.android.volley.toolbox.NetworkImageView#setDefaultImageResId(int)}
	 * and {@link com.android.volley.toolbox.NetworkImageView#setErrorImageResId(int)} should be called
	 * prior to calling this function.
	 * 
	 * @param url
	 *            The URL that should be loaded into this ImageView.
	 * @param imageLoader
	 *            ImageLoader that will be used to make the request.
	 */
	public void setImageUrl(String url, ImageLoader imageLoader) {
        setImageUrl(url, imageLoader, false);
	}
	
	public void setImageUrl(String url, ImageLoader imageLoader, boolean isCircle) {
		this.isCircle = isCircle;

        mUrl = url.replace(" ", "%20");
        mImageLoader = imageLoader;
        // The URL has potentially changed. See if we need to load it.
        loadImageIfNecessary(false);
	}

	/**
	 * Sets the default image resource ID to be used for this view until the
	 * attempt to load it completes.
	 */
	public void setDefaultImageResId(int defaultImage) {
		mDefaultImageId = defaultImage;
	}

	/**
	 * Sets the error image resource ID to be used for this view in the event
	 * that the image requested fails to load.
	 */
	public void setErrorImageResId(int errorImage) {
		mErrorImageId = errorImage;
	}

	/**
	 * Loads the image for the view if it isn't already loaded.
	 * 
	 * @param isInLayoutPass
	 *            True if this was invoked from a layout pass, false otherwise.
	 */
	void loadImageIfNecessary(final boolean isInLayoutPass) {
		int width = getWidth();
		int height = getHeight();

		boolean wrapWidth = false, wrapHeight = false;
		if (getLayoutParams() != null) {
            wrapWidth = getLayoutParams().width == LayoutParams.WRAP_CONTENT;
			wrapHeight = getLayoutParams().height == LayoutParams.WRAP_CONTENT;
		}

		// if the view's bounds aren't known yet, and this is not a
		// wrap-content/wrap-content
		// view, hold off on loading the image.
		boolean isFullyWrapContent = wrapWidth && wrapHeight;
		if (width == 0 && height == 0 && !isFullyWrapContent) {
			return;
		}

		// if the URL to be loaded in this view is empty, cancel any old
		// requests and clear the
		// currently loaded image.
		if (TextUtils.isEmpty(mUrl)) {
			if (mImageContainer != null) {
				mImageContainer.cancelRequest();
				mImageContainer = null;
			}
			setDefaultImageOrNull();
			return;
		}

		// if there was an old request in this view, check if it needs to be
		// canceled.
		if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
			if (mImageContainer.getRequestUrl().equals(mUrl)) {
				// if the request is from the same URL, return.
				return;
			} else {
				// if there is a pre-existing request, cancel it if it's
				// fetching a different URL.
				mImageContainer.cancelRequest();
				setDefaultImageOrNull();
			}
		}

		// Calculate the max image width / height to use while ignoring
		// WRAP_CONTENT dimens.
		int maxWidth = wrapWidth ? 0 : width;
		int maxHeight = wrapHeight ? 0 : height;

		// The pre-existing content of this view didn't match the current URL.
		// Load the new image
		// from the network.
		ImageContainer newContainer = mImageLoader.get(mUrl,
				new ImageListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (mErrorImageId != 0) {
							stopAnimation();
							setImageResource(mErrorImageId);
						}
					}

					@Override
					public void onResponse(final ImageContainer response,
							boolean isImmediate) {
						// If this was an immediate response that was delivered
						// inside of a layout
						// pass do not set the image immediately as it will
						// trigger a requestLayout
						// inside of a layout. Instead, defer setting the image
						// by posting back to
						// the main thread.
						if (isImmediate && isInLayoutPass) {
							post(new Runnable() {
								@Override
								public void run() {
									onResponse(response, false);
								}
							});
							return;
						}

						if (response.getBitmap() != null) {
                            if (isCircle) {
                                Bitmap bitmap = response.getBitmap();

                                final Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

                                final Path path = new Path();
                                path.addCircle((float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2), (float) Math.min(bitmap.getWidth(), (bitmap.getHeight() / 2)), Path.Direction.CCW);

                                final Canvas canvas = new Canvas(outputBitmap);
                                canvas.clipPath(path);
                                canvas.drawBitmap(response.getBitmap(), 0, 0, null);

                                setImageBitmap(outputBitmap);
                            } else {
                                setImageBitmap(response.getBitmap());
                            }
						} else if (mDefaultImageId != 0) {
							setImageResource(mDefaultImageId);
						}

                        stopAnimation();
					}
				}, maxWidth, maxHeight);
		
		// update the ImageContainer to be the new bitmap container.
		mImageContainer = newContainer;
		
		addSpinnerIfNeeded();
	}

	private void setDefaultImageOrNull() {
		if (mDefaultImageId != 0) {
			setImageResource(mDefaultImageId);
		} else {
			setImageBitmap(null);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		loadImageIfNecessary(true);
	}
	

	private void addSpinnerIfNeeded() {
		if (getAnimation() == null && !mSpinnerHide) {
			setImageResource(mSpinnerDrawable);
			super.setScaleType(ScaleType.CENTER_INSIDE);
			startAnimation(mAnimation);
		}
	}
	
	@Override
	public void setScaleType(ScaleType scaleType) {
		super.setScaleType(ScaleType.CENTER_INSIDE);
		
		mPreviousScaleType = scaleType;
	}
	
	private Animation createRotateAnimation() {
		RotateAnimation anim = new RotateAnimation(0.0f, 360.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(700);
		return anim;
	}
	
	@Override
	protected void onDetachedFromWindow() {
		if (mImageContainer != null) {
			// If the view was bound to an image request, cancel it and clear
			// out the image from the view.
			mImageContainer.cancelRequest();
			stopAnimation();
			setImageBitmap(null);
			// also clear out the container so we can reload the image if
			// necessary.
			mImageContainer = null;
		}
		super.onDetachedFromWindow();
	}

	private void stopAnimation() {
		setAnimation(null);
		if (mPreviousScaleType != null) {
			super.setScaleType(mPreviousScaleType);
		}
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		invalidate();
	}
}