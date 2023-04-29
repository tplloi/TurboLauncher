package com.roy.turbo.launcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HolographicLinearLayout extends LinearLayout {
    private final HolographicViewHelper mHolographicHelper;
    private ImageView mImageView;
    private final int mImageViewId;

    private boolean mHotwordOn;
    private boolean mIsPressed;
    private boolean mIsFocused;

    public HolographicLinearLayout(Context context) {
        this(context, null);
    }

    public HolographicLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    public HolographicLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HolographicLinearLayout,
                defStyle, 0);
        mImageViewId = a.getResourceId(R.styleable.HolographicLinearLayout_sourceImageViewId, -1);
        mHotwordOn = a.getBoolean(R.styleable.HolographicLinearLayout_stateHotwordOn, false);
        a.recycle();


        setWillNotDraw(false);
        mHolographicHelper = new HolographicViewHelper(context);

        setOnTouchListener((v, event) -> {
            if (isPressed() != mIsPressed) {
                mIsPressed = isPressed();
                refreshDrawableState();
            }
            return false;
        });

        setOnFocusChangeListener((v, hasFocus) -> {
            if (isFocused() != mIsFocused) {
                mIsFocused = isFocused();
                refreshDrawableState();
            }
        });
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (mImageView != null) {
            Drawable d = mImageView.getDrawable();
            if (d instanceof StateListDrawable) {
                StateListDrawable sld = (StateListDrawable) d;
                sld.setState(getDrawableState());
                sld.invalidateSelf();
            }
        }
    }

    void invalidatePressedFocusedStates() {
        mHolographicHelper.invalidatePressedFocusedStates(mImageView);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // One time call to generate the pressed/focused state -- must be called after
        // measure/layout
        if (mImageView == null) {
            mImageView = (ImageView) findViewById(mImageViewId);
        }
        mHolographicHelper.generatePressedFocusedStates(mImageView);
    }

    private boolean isHotwordOn() {
        return mHotwordOn;
    }

    public void setHotwordState(boolean on) {
        if (on == mHotwordOn) {
            return;
        }
        mHotwordOn = on;
        refreshDrawableState();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isHotwordOn()) {
            mergeDrawableStates(drawableState, new int[] {R.attr.stateHotwordOn});
        }
        return drawableState;
    }
}
