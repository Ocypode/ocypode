package com.ocypode.application.component.view.edittext;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.ocypode.application.component.Typefaced;

public class TypefacedEditText extends EditText {

	public TypefacedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (isInEditMode()) {
			return;
		}

		Typeface typeface = Typefaced.createTypeface(context, attrs);
		if (typeface != null) {			
			setTypeface(typeface);
		}
	}

}
