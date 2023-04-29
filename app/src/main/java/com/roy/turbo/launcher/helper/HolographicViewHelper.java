package com.roy.turbo.launcher.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.ImageView;

import com.roy.turbo.launcher.FastBitmapDrawable;
import com.roy.turbo.launcher.R;

public class HolographicViewHelper {

    private final Canvas mTempCanvas = new Canvas();

    private boolean mStatesUpdated;
    private final int mHighlightColor;
    private final int mHotwordColor;

    public HolographicViewHelper(Context context) {
        Resources res = context.getResources();
        mHighlightColor = res.getColor(android.R.color.holo_blue_light);
        mHotwordColor = res.getColor(android.R.color.holo_green_light);
    }

    /**
     * Generate the pressed/focused states if necessary.
     */
    public void generatePressedFocusedStates(ImageView v) {
        if (!mStatesUpdated && v != null) {
            mStatesUpdated = true;
            Bitmap original = createOriginalImage(v, mTempCanvas);
            Bitmap outline = createImageWithOverlay(v, mTempCanvas, mHighlightColor);
            Bitmap hotword = createImageWithOverlay(v, mTempCanvas, mHotwordColor);
            FastBitmapDrawable originalD = new FastBitmapDrawable(original);
            FastBitmapDrawable outlineD = new FastBitmapDrawable(outline);
            FastBitmapDrawable hotwordD = new FastBitmapDrawable(hotword);

            StateListDrawable states = new StateListDrawable();

            states.addState(new int[] {android.R.attr.state_pressed}, outlineD);
            states.addState(new int[] {android.R.attr.state_focused}, outlineD);
            states.addState(new int[] {R.attr.stateHotwordOn}, hotwordD);
            states.addState(new int[] {}, originalD);
            v.setImageDrawable(states);
        }
    }

    /**
     * Invalidates the pressed/focused states.
     */
    public void invalidatePressedFocusedStates(ImageView v) {
        mStatesUpdated = false;
        if (v != null) {
            v.invalidate();
        }
    }

    /**
     * Creates a copy of the original image.
     */
    private Bitmap createOriginalImage(ImageView v, Canvas canvas) {
        final Drawable d = v.getDrawable();
        final Bitmap b = Bitmap.createBitmap(
                d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        canvas.setBitmap(b);
        canvas.save();
        d.draw(canvas);
        canvas.restore();
        canvas.setBitmap(null);

        return b;
    }

    /**
     * Creates a new press state image which is the old image with a blue overlay.
     * Responsibility for the bitmap is transferred to the caller.
     */
    private Bitmap createImageWithOverlay(ImageView v, Canvas canvas, int color) {
        final Drawable d = v.getDrawable();
        final Bitmap b = Bitmap.createBitmap(
                d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        canvas.setBitmap(b);
        canvas.save();
        d.draw(canvas);
        canvas.restore();
        canvas.drawColor(color, PorterDuff.Mode.SRC_IN);
        canvas.setBitmap(null);

        return b;
    }
}
