package com.imra.mynews.ui.utils;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Date: 10.06.2020
 * Time: 15:40
 *
 * @author IMRA027
 */
public class UrlDrawable extends Drawable implements Drawable.Callback {

    private Drawable mDrawable;

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mDrawable != null) {
            mDrawable.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (mDrawable != null) {
            mDrawable.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (mDrawable != null) {
            mDrawable.setColorFilter(colorFilter);
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public int getOpacity() {
        if (mDrawable != null) {
            return mDrawable.getOpacity();
        }
        return 0;
    }


    public void setDrawable(Drawable drawable) {
        if (mDrawable != null) {
            mDrawable.setCallback(null);
        }
        drawable.setCallback(this);
        mDrawable = drawable;
    }


    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        if (getCallback() != null) {
            getCallback().invalidateDrawable(who);
        }
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
        if (getCallback() != null) {
            getCallback().scheduleDrawable(who, what, when);
        }
    }


    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
        if (getCallback() != null) {
            getCallback().unscheduleDrawable(who, what);
        }
    }


}
