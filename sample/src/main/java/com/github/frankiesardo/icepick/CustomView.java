package com.github.frankiesardo.icepick;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.frankiesardo.icepick.annotation.Icicle;
import com.github.frankiesardo.icepick.bundle.Bundles;

public class CustomView extends TextView {

    @Icicle
    int color;

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setBackgroundWithAnotherMethod(int color) {
        this.color = color;
        setBackgroundColor(color);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Bundles.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Bundles.restoreInstanceState(this, state));
        setBackgroundWithAnotherMethod(color);
    }
}
