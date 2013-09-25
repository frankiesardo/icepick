package com.github.frankiesardo.icepick;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;

import icepick.annotation.Icicle;

public class CustomView extends BaseCustomView {

    @Icicle
    Integer textColor = Integer.MAX_VALUE;

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTextColorWithAnotherMethod(int color) {
        this.textColor = color;
        setTextColor(textColor);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (textColor != Integer.MAX_VALUE) {
            setTextColorWithAnotherMethod(textColor);
        }
    }
}
