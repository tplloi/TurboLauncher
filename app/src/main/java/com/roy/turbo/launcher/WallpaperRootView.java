package com.roy.turbo.launcher;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

//done 2023.04.30
public class WallpaperRootView extends RelativeLayout {
    private final WallpaperPickerActivity mWallPicker;

    public WallpaperRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWallPicker = (WallpaperPickerActivity) context;
    }

    public WallpaperRootView(
            Context context,
            AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        mWallPicker = (WallpaperPickerActivity) context;
    }

    protected boolean fitSystemWindows(Rect insets) {
        mWallPicker.setWallpaperStripYOffset(insets.bottom);
        return true;
    }
}
