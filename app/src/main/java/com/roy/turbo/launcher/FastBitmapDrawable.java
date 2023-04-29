package com.roy.turbo.launcher;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class FastBitmapDrawable extends Drawable {
    private final Bitmap mBitmap;
    private int mAlpha;
    private int mWidth;
    private int mHeight;
    private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

    public FastBitmapDrawable(Bitmap b) {
        mAlpha = 255;
        mBitmap = b;
        if (b != null) {
            setBounds(0, 0, b.getWidth(), b.getHeight());
        } else {
            mWidth = mHeight = 0;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect r = getBounds();
        // Draw the bitmap into the bounding rect
        canvas.drawBitmap(mBitmap, null, r, mPaint);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        mPaint.setAlpha(alpha);
    }

    public void setFilterBitmap(boolean filterBitmap) {
        mPaint.setFilterBitmap(filterBitmap);
        mPaint.setAntiAlias(filterBitmap);
    }

    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public int getIntrinsicWidth() {
        int width = getBounds().width();
        if (width == 0) {
            width = mBitmap.getWidth();
        }
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        int height = getBounds().height();
        if (height == 0) {
            height = mBitmap.getHeight();
        }
        return height;
    }

    @Override
    public int getMinimumWidth() {
        return getBounds().width();
    }

    @Override
    public int getMinimumHeight() {
        return getBounds().height();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
