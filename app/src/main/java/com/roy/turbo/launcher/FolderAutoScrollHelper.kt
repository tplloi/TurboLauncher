package com.roy.turbo.launcher;

import android.support.v4.widget.AutoScrollHelper;
import android.widget.ScrollView;

/**
 * An implementation of {@link AutoScrollHelper} that knows how to scroll
 * through a {@link Folder}.
 */
public class FolderAutoScrollHelper extends AutoScrollHelper {
    private static final float MAX_SCROLL_VELOCITY = 1500f;

    private final ScrollView mTarget;

    public FolderAutoScrollHelper(ScrollView target) {
        super(target);

        mTarget = target;

        setActivationDelay(0);
        setEdgeType(EDGE_TYPE_INSIDE_EXTEND);
        setExclusive(true);
        setMaximumVelocity(MAX_SCROLL_VELOCITY, MAX_SCROLL_VELOCITY);
        setRampDownDuration(0);
        setRampUpDuration(0);
    }

    @Override
    public void scrollTargetBy(int deltaX, int deltaY) {
        mTarget.scrollBy(deltaX, deltaY);
    }

    @Override
    public boolean canTargetScrollHorizontally(int direction) {
        // List do not scroll horizontally.
        return false;
    }

    @Override
    public boolean canTargetScrollVertically(int direction) {
        return mTarget.canScrollVertically(direction);
    }
}