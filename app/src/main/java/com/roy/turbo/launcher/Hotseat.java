package com.roy.turbo.launcher;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.roy.turbo.launcher.view.HotseatIconKeyEventListener;

import java.util.ArrayList;

public class Hotseat extends FrameLayout {
    private static final String TAG = "Hotseat";

    private CellLayout mContent;

    private Launcher mLauncher;

    private int mAllAppsButtonRank;

    private final boolean mTransposeLayoutWithOrientation;
    private final boolean mIsLandscape;

    public Hotseat(Context context) {
        this(context, null);
    }

    public Hotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Hotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Resources r = context.getResources();
        mTransposeLayoutWithOrientation = 
                r.getBoolean(R.bool.hotseat_transpose_layout_with_orientation);
        mIsLandscape = context.getResources().getConfiguration().orientation ==
            Configuration.ORIENTATION_LANDSCAPE;
    }

    public void setup(Launcher launcher) {
        mLauncher = launcher;
        setOnKeyListener(new HotseatIconKeyEventListener());
    }

    CellLayout getLayout() {
        return mContent;
    }

    /**
     * Registers the specified listener on the cell layout of the hotseat.
     */
    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mContent.setOnLongClickListener(l);
    }
  
    private boolean hasVerticalHotseat() {
        return (mIsLandscape && mTransposeLayoutWithOrientation);
    }

    /* Get the orientation invariant order of the item in the hotseat for persistence. */
    int getOrderInHotseat(int x, int y) {
        return hasVerticalHotseat() ? (mContent.getCountY() - y - 1) : x;
    }
    /* Get the orientation specific coordinates given an invariant order in the hotseat. */
    int getCellXFromOrder(int rank) {
        return hasVerticalHotseat() ? 0 : rank;
    }
    int getCellYFromOrder(int rank) {
        return hasVerticalHotseat() ? (mContent.getCountY() - (rank + 1)) : 0;
    }

    /** This returns the coordinates of an app in a given cell, relative to the DragLayer */
    Rect getCellCoordinates(int cellX, int cellY) {
        Rect coords = new Rect();
        mContent.cellToRect(cellX, cellY, 1, 1, coords);
        int[] hotseatInParent = new int[2];
        Utilities.getDescendantCoordRelativeToParent(this, mLauncher.getDragLayer(),
                hotseatInParent, false);
        coords.offset(hotseatInParent[0], hotseatInParent[1]);

        // Center the icon
        int cWidth = mContent.getShortcutsAndWidgets().getCellContentWidth();
        int cHeight = mContent.getShortcutsAndWidgets().getCellContentHeight();
        int cellPaddingX = (int) Math.max(0, ((coords.width() - cWidth) / 2f));
        int cellPaddingY = (int) Math.max(0, ((coords.height() - cHeight) / 2f));
        coords.offset(cellPaddingX, cellPaddingY);

        return coords;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LauncherAppState app = LauncherAppState.getInstance();
        DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();

        mAllAppsButtonRank = grid.hotseatAllAppsRank;
        mContent = (CellLayout) findViewById(R.id.layout);
        if (grid.isLandscape && !grid.isLargeTablet()) {
            mContent.setGridSize(1, (int) grid.numHotseatIcons);
        } else {
            mContent.setGridSize((int) grid.numHotseatIcons, 1);
        }
        mContent.setIsHotseat(true);

        resetLayout();
    }

    void resetLayout() {
        mContent.removeAllViewsInLayout();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // We don't want any clicks to go through to the hotseat unless the workspace is in
        // the normal state.
        return mLauncher.getWorkspace().isSmall();
    }

    void addAllAppsFolder(IconCache iconCache,
            ArrayList<AppInfo> allApps, ArrayList<ComponentName> onWorkspace,
            Launcher launcher, Workspace workspace) {
        if (LauncherAppState.isDisableAllApps()) {
            FolderInfo fi = new FolderInfo();

            fi.cellX = getCellXFromOrder(mAllAppsButtonRank);
            fi.cellY = getCellYFromOrder(mAllAppsButtonRank);
            fi.spanX = 1;
            fi.spanY = 1;
            fi.container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
            fi.screenId = mAllAppsButtonRank;
            fi.itemType = LauncherSettings.Favorites.ITEM_TYPE_FOLDER;
            fi.title = "More Apps";
            LauncherModel.addItemToDatabase(launcher, fi, fi.container, fi.screenId, fi.cellX,
                    fi.cellY, false);
            FolderIcon folder = FolderIcon.fromXml(R.layout.v_folder_icon, launcher,
                    getLayout(), fi, iconCache);
            workspace.addInScreen(folder, fi.container, fi.screenId, fi.cellX, fi.cellY,
                    fi.spanX, fi.spanY);

            for (AppInfo info: allApps) {
                assert info.intent != null;
                ComponentName cn = info.intent.getComponent();
                if (!onWorkspace.contains(cn)) {
                    Log.d(TAG, "Adding to 'more apps': " + info.intent);
                    ShortcutInfo si = info.makeShortcut();
                    fi.add(si);
                }
            }
        }
    }

    void addAppsToAllAppsFolder(ArrayList<AppInfo> apps) {
        if (LauncherAppState.isDisableAllApps()) {
            View v = mContent.getChildAt(getCellXFromOrder(mAllAppsButtonRank), getCellYFromOrder(mAllAppsButtonRank));
            FolderIcon fi;

            if (v instanceof FolderIcon) {
                fi = (FolderIcon) v;
            } else {
                return;
            }

            FolderInfo info = fi.getFolderInfo();
            for (AppInfo a: apps) {
                ShortcutInfo si = a.makeShortcut();
                info.add(si);
            }
        }
    }
}
