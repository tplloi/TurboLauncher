package com.roy.turbo.launcher;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class ButtonDropTarget extends TextView implements DropTarget, DragController.DragListener {

    protected final int mTransitionDuration;

    protected Launcher mLauncher;
    private final int mBottomDragPadding;
    //    protected TextView mText;
    protected SearchDropTargetBar mSearchDropTargetBar;

    /**
     * Whether this drop target is active for the current drag
     */
    protected boolean mActive;

    /**
     * The paint applied to the drag view on hover
     */
    protected int mHoverColor = 0;

    public ButtonDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Resources r = getResources();
        mTransitionDuration = r.getInteger(R.integer.config_dropTargetBgTransitionDuration);
        mBottomDragPadding = r.getDimensionPixelSize(R.dimen.drop_target_drag_padding);
    }

    void setLauncher(Launcher launcher) {
        mLauncher = launcher;
    }

    public boolean acceptDrop(DragObject d) {
        return false;
    }

    public void setSearchDropTargetBar(SearchDropTargetBar searchDropTargetBar) {
        mSearchDropTargetBar = searchDropTargetBar;
    }

    protected Drawable getCurrentDrawable() {
        Drawable[] drawables = getCompoundDrawablesRelative();
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                return drawable;
            }
        }
        return null;
    }

    public void onDrop(DragObject d) {
    }

    public void onFlingToDelete(DragObject d, int x, int y, PointF vec) {

    }

    public void onDragEnter(DragObject d) {
        d.dragView.setColor(mHoverColor);
    }

    public void onDragOver(DragObject d) {

    }

    public void onDragExit(DragObject d) {
        d.dragView.setColor(0);
    }

    public void onDragStart(DragSource source, Object info, int dragAction) {

    }

    public boolean isDropEnabled() {
        return mActive;
    }

    public void onDragEnd() {

    }

    @Override
    public void getHitRectRelativeToDragLayer(android.graphics.Rect outRect) {
        super.getHitRect(outRect);
        outRect.bottom += mBottomDragPadding;

        int[] coords = new int[2];
        mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(this, coords);
        outRect.offsetTo(coords[0], coords[1]);
    }

    private boolean isRtl() {
        return (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL);
    }

    Rect getIconRect(int viewWidth, int viewHeight, int drawableWidth, int drawableHeight) {
        DragLayer dragLayer = mLauncher.getDragLayer();

        // Find the rect to animate to (the view is center aligned)
        Rect to = new Rect();
        dragLayer.getViewRectRelativeToSelf(this, to);

        final int left;
        final int right;

        if (isRtl()) {
            right = to.right - getPaddingRight();
            left = right - drawableWidth;
        } else {
            left = to.left + getPaddingLeft();
            right = left + drawableWidth;
        }

        final int top = to.top + (getMeasuredHeight() - drawableHeight) / 2;
        final int bottom = top + drawableHeight;

        to.set(left, top, right, bottom);

        // Center the destination rect about the trash icon
        final int xOffset = (int) -(viewWidth - drawableWidth) / 2;
        final int yOffset = (int) -(viewHeight - drawableHeight) / 2;
        to.offset(xOffset, yOffset);

        return to;
    }

    public void getLocationInDragLayer(int[] loc) {
        mLauncher.getDragLayer().getLocationInDragLayer(this, loc);
    }
}
