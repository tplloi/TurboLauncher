package com.roy.turbo.launcher;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class DrawableStateProxyView extends LinearLayout {

    private View mView;
    private final int mViewId;

    public DrawableStateProxyView(Context context) {
        this(context, null);
    }

    public DrawableStateProxyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public DrawableStateProxyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawableStateProxyView,
                defStyle, 0);
        mViewId = a.getResourceId(R.styleable.DrawableStateProxyView_sourceViewId, -1);
        a.recycle();

        setFocusable(false);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (mView == null) {
            View parent = (View) getParent();
            mView = parent.findViewById(mViewId);
        }
        if (mView != null) {
            mView.setPressed(isPressed());
            mView.setHovered(isHovered());
        }
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        return false;
    }
}
