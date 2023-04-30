package com.roy.turbo.launcher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Advanceable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.roy.turbo.launcher.PagedView.TransitionEffect;
import com.roy.turbo.launcher.helper.FirstFrameAnimatorHelper;
import com.roy.turbo.launcher.helper.LauncherAnimUtils;
import com.roy.turbo.launcher.helper.LauncherAppWidgetHost;
import com.roy.turbo.launcher.itf.DragSource;
import com.roy.turbo.launcher.itf.DropTarget;
import com.roy.turbo.launcher.itf.DropTarget.DragObject;
import com.roy.turbo.launcher.itf.LauncherTransitionable;
import com.roy.turbo.launcher.settings.SettingsPanel;
import com.roy.turbo.launcher.settings.SettingsProvider;
import com.roy.turbo.launcher.ui.LauncherWallpaperPickerActivity;
import com.roy.turbo.launcher.view.AppsCustomizeLayout;
import com.roy.turbo.launcher.view.BubbleTextView;
import com.roy.turbo.launcher.view.DragView;
import com.roy.turbo.launcher.view.HolographicImageView;
import com.roy.turbo.launcher.view.HolographicLinearLayout;
import com.roy.turbo.launcher.view.LauncherViewPropertyAnimator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Launcher extends Activity implements View.OnClickListener,
        OnLongClickListener, LauncherModel.Callbacks, View.OnTouchListener {
    static final String TAG = "Launcher";

    DeviceProfile mGrid;

    private static final int REQUEST_CREATE_SHORTCUT = 1;
    private static final int REQUEST_CREATE_APPWIDGET = 5;
    private static final int REQUEST_PICK_APPLICATION = 6;
    private static final int REQUEST_PICK_SHORTCUT = 7;
    private static final int REQUEST_PICK_APPWIDGET = 9;
    private static final int REQUEST_PICK_WALLPAPER = 10;

    private static final int REQUEST_BIND_APPWIDGET = 11;

//    static final int REQUEST_PICK_ICON = 13;

    private static final int REQUEST_LOCK_PATTERN = 14;

    /**
     * IntentStarter uses request codes starting with this. This must be greater
     * than all activity request codes used internally.
     */
//    protected static final int REQUEST_LAST = 100;

    static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";

    static final int SCREEN_COUNT = 5;
    static final int DEFAULT_SCREEN = 2;

    private static final String PREFERENCES = "launcher.preferences";

    static final String FORCE_ENABLE_ROTATION_PROPERTY = "launcher_force_rotate";

    static final String DISABLE_ALL_APPS_PROPERTY = "launcher_noallapps";

    // The Intent extra that defines whether to ignore the launch animation
    static final String INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION = "com.roy.turbo.launcher.intent.extra.shortcut.IGNORE_LAUNCH_ANIMATION";

    // Type: int
    private static final String RUNTIME_STATE_CURRENT_SCREEN = "launcher.current_screen";
    // Type: int
    private static final String RUNTIME_STATE = "launcher.state";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CONTAINER = "launcher.add_container";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SCREEN = "launcher.add_screen";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_X = "launcher.add_cell_x";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_Y = "launcher.add_cell_y";
    // Type: boolean
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME = "launcher.rename_folder";
    // Type: long
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME_ID = "launcher.rename_folder_id";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SPAN_X = "launcher.add_span_x";
    // Type: int
    private static final String RUNTIME_STATE_PENDING_ADD_SPAN_Y = "launcher.add_span_y";
    // Type: parcelable
    private static final String RUNTIME_STATE_PENDING_ADD_WIDGET_INFO = "launcher.add_widget_info";
    // Type: parcelable
    private static final String RUNTIME_STATE_PENDING_ADD_WIDGET_ID = "launcher.add_widget_id";
    // Type: int[]
    private static final String RUNTIME_STATE_VIEW_IDS = "launcher.view_ids";

    private static final String TOOLBAR_ICON_METADATA_NAME = "com.android.launcher.toolbar_icon";
    private static final String TOOLBAR_SEARCH_ICON_METADATA_NAME = "com.android.launcher.toolbar_search_icon";
    private static final String TOOLBAR_VOICE_SEARCH_ICON_METADATA_NAME = "com.android.launcher.toolbar_voice_search_icon";

//    public static final String USER_HAS_MIGRATED = "launcher.user_migrated_from_old_data";

    /**
     * The different states that Launcher can be in.
     */
    private enum State {
        NONE, WORKSPACE, APPS_CUSTOMIZE, APPS_CUSTOMIZE_SPRING_LOADED
    }

    private State mState = State.WORKSPACE;
    private AnimatorSet mStateAnimation;

    static final int APPWIDGET_HOST_ID = 1024;
    public static final int EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT = 300;
    public static final int EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT_FOLDER_CLOSE = 400;
    private static final int ON_ACTIVITY_RESULT_ANIMATION_DELAY = 500;

    private static final Object sLock = new Object();
    private static int sScreen = DEFAULT_SCREEN;

    private HashMap<Integer, Integer> mItemIdToViewId = new HashMap<>();
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    // How long to wait before the new-shortcut animation automatically pans the
    // workspace
    private static final int NEW_APPS_PAGE_MOVE_DELAY = 500;
    private static final int NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS = 5;
    private static final int NEW_APPS_ANIMATION_DELAY = 500;

    private final BroadcastReceiver mCloseSystemDialogsReceiver = new CloseSystemDialogsIntentReceiver();
    private final ContentObserver mWidgetObserver = new AppWidgetResetObserver();

    private LayoutInflater mInflater;

    private Workspace mWorkspace;
    private View mLauncherView;
    private View mPageIndicators;
    private DragLayer mDragLayer;
    private DragController mDragController;

    private TransitionEffectsFragment mTransitionEffectsFragment;

    protected HiddenFolderFragment mHiddenFolderFragment;

    private AppWidgetManager mAppWidgetManager;
    private LauncherAppWidgetHost mAppWidgetHost;

    private final ItemInfo mPendingAddInfo = new ItemInfo();
    private AppWidgetProviderInfo mPendingAddWidgetInfo;
    private int mPendingAddWidgetId = -1;

    private final int[] mTmpAddItemCellCoordinates = new int[2];

    private FolderInfo mFolderInfo;

    protected FolderIcon mHiddenFolderIcon;
    private boolean mHiddenFolderAuth = false;

    private Hotseat mHotseat;
    private ViewGroup mOverviewPanel;

    private View mDarkPanel;
    SettingsPanel mOverviewSettingsPanel;

    private View mAllAppsButton;

    private SearchDropTargetBar mSearchDropTargetBar;
    private AppsCustomizeLayout mAppsCustomizeLayout;
    private AppsCustomizePagedView mAppsCustomizeContent;
    private boolean mAutoAdvanceRunning = false;
    private View mQsb;

    private Bundle mSavedState;
    private State mOnResumeState = State.NONE;

    private SpannableStringBuilder mDefaultKeySsb = null;

    private boolean mWorkspaceLoading = true;

    private boolean mPaused = true;
    private boolean mRestoring;
    private boolean mWaitingForResult;
    private boolean mOnResumeNeedsLoad;

    private final ArrayList<Runnable> mBindOnResumeCallbacks = new ArrayList<>();
    private final ArrayList<Runnable> mOnResumeCallbacks = new ArrayList<>();

    // Keep track of whether the user has left launcher
    private static boolean sPausedFromUserAction = false;

    private Dialog mTransitionEffectDialog;

    protected LauncherModel mModel;
    private IconCache mIconCache;
    private boolean mUserPresent = true;
    private boolean mVisible = false;
    private boolean mHasFocus = false;
    private boolean mAttached = false;

    private static LocaleConfiguration sLocaleConfiguration = null;

    private static final HashMap<Long, FolderInfo> sFolders = new HashMap<>();

    private View.OnTouchListener mHapticFeedbackTouchListener;

    // Related to the auto-advancing of widgets
    private final int ADVANCE_MSG = 1;
    private final int mAdvanceInterval = 20000;
    private final int mAdvanceStagger = 250;
    private long mAutoAdvanceSentTime;
    private long mAutoAdvanceTimeLeft = -1;
    private final HashMap<View, AppWidgetProviderInfo> mWidgetsToAdvance = new HashMap<>();

    // Determines how long to wait after a rotation before restoring the screen
    // orientation to
    // match the sensor state.
    private final int mRestoreScreenOrientationDelay = 500;

    // External icons saved in case of resource changes, orientation, etc.
    private static final Drawable.ConstantState[] sGlobalSearchIcon = new Drawable.ConstantState[2];
    private static final Drawable.ConstantState[] sVoiceSearchIcon = new Drawable.ConstantState[2];

    private Drawable mWorkspaceBackgroundDrawable;

    private final ArrayList<Integer> mSynchronouslyBoundPages = new ArrayList<>();
    private static final boolean DISABLE_SYNCHRONOUS_BINDING_CURRENT_PAGE = false;

    static final ArrayList<String> sDumpLogs = new ArrayList<>();
//    static Date sDateStamp = new Date();
//    static DateFormat sDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
//    static long sRunStart = System.currentTimeMillis();
//    static final String CORRUPTION_EMAIL_SENT_KEY = "corruptionEmailSent";

    private SharedPreferences mSharedPrefs;

    private static ArrayList<ComponentName> mIntentsOnWorkspaceFromUpgradePath = null;

    // Holds the page that we need to animate to, and the icon views that we
    // need to animate up
    // when we scroll to that page on resume.
    private ImageView mFolderIconImageView;
    private Bitmap mFolderIconBitmap;
    private Canvas mFolderIconCanvas;
    private final Rect mRectForFolderAnimation = new Rect();

    private BubbleTextView mWaitingForResume;
    private boolean mShouldRestart = false;

    // Theme
    public static final String THEME_DEFAULT = "Turbo Launcher.Default theme";
    private Typeface themeFont = null;
    private boolean showThemeFont;

    // Preferences
    private boolean mHideIconLabels;

    private final Runnable mBuildLayersRunnable = new Runnable() {
        public void run() {
            if (mWorkspace != null) {
                mWorkspace.buildPageHardwareLayers();
            }
        }
    };

    private static final ArrayList<PendingAddArguments> sPendingAddList = new ArrayList<>();

    public static boolean sForceEnableRotation = isPropertyEnabled(FORCE_ENABLE_ROTATION_PROPERTY);

    private static class PendingAddArguments {
        int requestCode;
        Intent intent;
        long container;
        long screenId;
        int cellX;
        int cellY;
    }

    private Stats mStats;

    public Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator arg0) {
        }

        @Override
        public void onAnimationRepeat(Animator arg0) {
        }

        @Override
        public void onAnimationEnd(Animator arg0) {
            mDarkPanel.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator arg0) {
        }
    };

    static boolean isPropertyEnabled(String propertyName) {
        return Log.isLoggable(propertyName, Log.VERBOSE);
    }

    private final BroadcastReceiver protectedAppsChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Update the workspace
            updateDynamicGrid();
            mWorkspace.hideOutlines();
            mSearchDropTargetBar.showSearchBar(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        initializeDynamicGrid();

        // the LauncherApplication should call this, but in case of
        // Instrumentation it might not be present yet
        mSharedPrefs = getSharedPreferences(
                LauncherAppState.getSharedPreferencesKey(),
                Context.MODE_PRIVATE);

        mDragController = new DragController(this);

        mInflater = getLayoutInflater();

        mStats = new Stats(this);

        mAppWidgetManager = AppWidgetManager.getInstance(this);

        mAppWidgetHost = new LauncherAppWidgetHost(this, APPWIDGET_HOST_ID);
        mAppWidgetHost.startListening();

        mPaused = false;

        checkForLocaleChange();
        setContentView(R.layout.launcher);

        setupViews();
        mGrid.layout(this);

        registerContentObservers();

        lockAllApps();

        mSavedState = savedInstanceState;
        restoreState(mSavedState);

        if (!mRestoring) {
            if (DISABLE_SYNCHRONOUS_BINDING_CURRENT_PAGE
                    || sPausedFromUserAction) {
                // If the user leaves launcher, then we should just load items
                // asynchronously when
                // they return.
                mModel.startLoader(true, PagedView.INVALID_RESTORE_PAGE);
            } else {
                // We only load the page synchronously if the user rotates (or
                // triggers a
                // configuration change) while launcher is in the foreground
                mModel.startLoader(true, mWorkspace.getRestorePage());
            }
        }

        // For handling default keys
        mDefaultKeySsb = new SpannableStringBuilder();
        Selection.setSelection(mDefaultKeySsb, 0);

        IntentFilter filter = new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mCloseSystemDialogsReceiver, filter);

        updateGlobalIcons();

        // On large interfaces, we want the screen to auto-rotate based on the
        // current orientation
        unlockScreenOrientation(true);

        IntentFilter protectedAppsFilter = new IntentFilter(
                "phonemetra.intent.action.PROTECTED_COMPONENT_UPDATE");
        registerReceiver(protectedAppsChangedReceiver, protectedAppsFilter,
                "phonemetra.permission.PROTECTED_APP", null);
    }

    public Typeface getThemeFont() {
        return themeFont;
    }

    void initializeDynamicGrid() {
        LauncherAppState.setApplicationContext(getApplicationContext());
        LauncherAppState app = LauncherAppState.getInstance();

        mHideIconLabels = SettingsProvider
                .getBoolean(
                        this,
                        SettingsProvider.SETTINGS_UI_HOMESCREEN_HIDE_ICON_LABELS,
                        R.bool.preferences_interface_homescreen_hide_icon_labels_default);

        // Determine the dynamic grid properties
        Point smallestSize = new Point();
        Point largestSize = new Point();
        Point realSize = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getCurrentSizeRange(smallestSize, largestSize);
        display.getRealSize(realSize);
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        // Lazy-initialize the dynamic grid
        mGrid = app.initDynamicGrid(this,
                Math.min(smallestSize.x, smallestSize.y),
                Math.min(largestSize.x, largestSize.y), realSize.x, realSize.y,
                dm.widthPixels, dm.heightPixels);

        mModel = app.setLauncher(this);
        mIconCache = app.getIconCache();
        mIconCache.flushInvalidIcons(mGrid);
    }

    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        sPausedFromUserAction = true;
    }

    /**
     * To be overridden by subclasses to create the custom content and call
     * {@link #addToCustomContentPage}. This will only be invoked if
     * {@link #hasCustomContentToLeft()} is {@code true}.
     */
    protected void populateCustomContentContainer() {
    }

    /**
     * To be overridden by subclasses to indicate that there is an activity to
     * launch before showing the standard launcher experience.
     */
    protected boolean hasFirstRunActivity() {
        return false;
    }

    /**
     * To be overridden by subclasses to launch any first run activity
     */
    protected Intent getFirstRunActivity() {
        return null;
    }

    /**
     * To be overriden by subclasses to hint to Launcher that we have custom
     * content
     */
    protected boolean hasCustomContentToLeft() {
        return false;
    }

    /**
     * Invoked by subclasses to signal a change to the* addCustomContentToLeft} value to ensure the custom content page
     * is added or removed if necessary.
     */
    protected void invalidateHasCustomContentToLeft() {
        if (mWorkspace == null || mWorkspace.getScreenOrder().isEmpty()) {
            // Not bound yet, wait for bindScreens to be called.
            return;
        }

        if (!mWorkspace.hasCustomContent() && hasCustomContentToLeft()) {
            // Create the custom content page and call the subclass to populate
            // it.
            mWorkspace.createCustomContentContainer();
            populateCustomContentContainer();
        } else if (mWorkspace.hasCustomContent() && !hasCustomContentToLeft()) {
            mWorkspace.removeCustomContentPage();
        }
    }

    /**
     * To be overriden by subclasses to hint to Launcher that we have custom
     * content and support #hasCustomSearchSupport()}
     * com.android.launcher.home.Home#MODE_SEARCH_TEXT
     * com.android.launcher.home.Home#MODE_SEARCH_VOICE
     */
    protected void requestSearch(int mode) {
        // To be implemented
    }

    private void updateGlobalIcons() {
        boolean searchVisible = false;
        boolean voiceVisible = false;
        // If we have a saved version of these external icons, we load them up
        // immediately
        int coi = getCurrentOrientationIndexForGlobalIcons();
        if (sGlobalSearchIcon[coi] == null || sVoiceSearchIcon[coi] == null) {
            searchVisible = updateGlobalSearchIcon();
            voiceVisible = updateVoiceSearchIcon(searchVisible);
        }
        if (sGlobalSearchIcon[coi] != null) {
            updateGlobalSearchIcon(sGlobalSearchIcon[coi]);
            searchVisible = true;
        }
        if (sVoiceSearchIcon[coi] != null) {
            updateVoiceSearchIcon(sVoiceSearchIcon[coi]);
            voiceVisible = true;
        }
        if (mSearchDropTargetBar != null) {
            mSearchDropTargetBar.onSearchPackagesChanged(searchVisible,
                    voiceVisible);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void checkForLocaleChange() {
        if (sLocaleConfiguration == null) {
            new AsyncTask<Void, Void, LocaleConfiguration>() {
                @Override
                protected LocaleConfiguration doInBackground(Void... unused) {
                    LocaleConfiguration localeConfiguration = new LocaleConfiguration();
                    readConfiguration(Launcher.this, localeConfiguration);
                    return localeConfiguration;
                }

                @Override
                protected void onPostExecute(LocaleConfiguration result) {
                    sLocaleConfiguration = result;
                    checkForLocaleChange(); // recursive, but now with a locale
                    // configuration
                }
            }.execute();
            return;
        }

        final Configuration configuration = getResources().getConfiguration();

        final String previousLocale = sLocaleConfiguration.locale;
        final String locale = configuration.locale.toString();

        final int previousMcc = sLocaleConfiguration.mcc;
        final int mcc = configuration.mcc;

        final int previousMnc = sLocaleConfiguration.mnc;
        final int mnc = configuration.mnc;

        boolean localeChanged = !locale.equals(previousLocale)
                || mcc != previousMcc || mnc != previousMnc;

        if (localeChanged) {
            sLocaleConfiguration.locale = locale;
            sLocaleConfiguration.mcc = mcc;
            sLocaleConfiguration.mnc = mnc;

            mIconCache.flush();

            final LocaleConfiguration localeConfiguration = sLocaleConfiguration;
            new AsyncTask<Void, Void, Void>() {
                public Void doInBackground(Void... args) {
                    writeConfiguration(Launcher.this, localeConfiguration);
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        }
    }

    private static class LocaleConfiguration {
        public String locale;
        public int mcc = -1;
        public int mnc = -1;
    }

    private static void readConfiguration(Context context, LocaleConfiguration configuration) {
        DataInputStream in = null;
        try {
            in = new DataInputStream(context.openFileInput(PREFERENCES));
            configuration.locale = in.readUTF();
            configuration.mcc = in.readInt();
            configuration.mnc = in.readInt();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void writeConfiguration(Context context,
                                           LocaleConfiguration configuration) {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(context.openFileOutput(PREFERENCES, MODE_PRIVATE));
            out.writeUTF(configuration.locale);
            out.writeInt(configuration.mcc);
            out.writeInt(configuration.mnc);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            context.getFileStreamPath(PREFERENCES).delete();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Stats getStats() {
        return mStats;
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }

    boolean isDraggingEnabled() {
        return !mModel.isLoadingWorkspace();
    }

    static int getScreen() {
        synchronized (sLock) {
            return sScreen;
        }
    }

    static void setScreen(int screen) {
        synchronized (sLock) {
            sScreen = screen;
        }
    }

    /**
     * Copied from View -- the View version of the method isn't called anywhere
     * else in our process and only exists for API level 17+, so it's ok to keep
     * our own version with no API requirement.
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range
            // under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF)
                newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public int getViewIdForItem(ItemInfo info) {
        // This cast is safe given the > 2B range for int.
        int itemId = (int) info.id;
        if (mItemIdToViewId.containsKey(itemId)) {
            return mItemIdToViewId.get(itemId);
        }
        int viewId = generateViewId();
        mItemIdToViewId.put(itemId, viewId);
        return viewId;
    }

    /**
     * Returns whether we should delay spring loaded mode -- for shortcuts and
     * widgets that have a configuration step, this allows the proper animations
     * to run after other transitions.
     */
    private void completeAdd(PendingAddArguments args) {
        switch (args.requestCode) {
            case REQUEST_PICK_APPLICATION:
                completeAddApplication(args.intent, args.container, args.screenId, args.cellX, args.cellY);
                break;
            case REQUEST_PICK_SHORTCUT:
                processShortcut(args.intent);
                break;
            case REQUEST_CREATE_SHORTCUT:
                completeAddShortcut(args.intent, args.container, args.screenId, args.cellX, args.cellY);
                break;
            case REQUEST_CREATE_APPWIDGET:
                int appWidgetId = args.intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
                completeAddAppWidget(appWidgetId, args.container, args.screenId, null, null);
                break;
        }

        resetAddInfo();
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        // Reset the startActivity waiting flag
        mWaitingForResult = false;
        int pendingAddWidgetId = mPendingAddWidgetId;
        mPendingAddWidgetId = -1;

        Runnable exitSpringLoaded = () -> exitSpringLoadedDragModeDelayed(
                (resultCode != RESULT_CANCELED),
                EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT, null);

        if (requestCode == REQUEST_BIND_APPWIDGET) {
            final int appWidgetId = data != null ? data.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, -1) : -1;
            if (resultCode == RESULT_CANCELED) {
                completeTwoStageWidgetDrop(RESULT_CANCELED, appWidgetId);
                mWorkspace.removeExtraEmptyScreen(true, exitSpringLoaded,
                        ON_ACTIVITY_RESULT_ANIMATION_DELAY, false);
            } else if (resultCode == RESULT_OK) {
                addAppWidgetImpl(appWidgetId, mPendingAddInfo, null,
                        mPendingAddWidgetInfo,
                        ON_ACTIVITY_RESULT_ANIMATION_DELAY);
            }
            return;
        } else if (requestCode == REQUEST_PICK_WALLPAPER) {
            if (resultCode == RESULT_OK && mWorkspace.isInOverviewMode()) {
                mWorkspace.exitOverviewMode(false);
            }
            return;
        } else if (requestCode == REQUEST_LOCK_PATTERN) {
            mHiddenFolderAuth = true;
            switch (resultCode) {
                case RESULT_OK:
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(0, 0);
                    fragmentTransaction.replace(R.id.launcher,
                            mHiddenFolderFragment,
                            HiddenFolderFragment.HIDDEN_FOLDER_FRAGMENT);
                    fragmentTransaction.commit();
                    break;
                case RESULT_CANCELED:
                    break;
            }
            return;
        }

        boolean isWidgetDrop = (requestCode == REQUEST_PICK_APPWIDGET || requestCode == REQUEST_CREATE_APPWIDGET);

        // We have special handling for widgets
        if (isWidgetDrop) {
            final int appWidgetId;
            int widgetId = data != null ? data.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, -1) : -1;
            if (widgetId < 0) {
                appWidgetId = pendingAddWidgetId;
            } else {
                appWidgetId = widgetId;
            }

            final int result;
            final Runnable onComplete;
            if (appWidgetId < 0 || resultCode == RESULT_CANCELED) {
                Log.e(TAG,
                        "Error: appWidgetId (EXTRA_APPWIDGET_ID) was not returned from the \\"
                                + "widget configuration activity.");
                result = RESULT_CANCELED;
                completeTwoStageWidgetDrop(result, appWidgetId);
                onComplete = () -> exitSpringLoadedDragModeDelayed(false, 0, null);
            } else {
                result = resultCode;
                final CellLayout dropLayout = (CellLayout) mWorkspace
                        .getScreenWithId(mPendingAddInfo.screenId);
                dropLayout.setDropPending(true);
                onComplete = () -> {
                    completeTwoStageWidgetDrop(result, appWidgetId);
                    dropLayout.setDropPending(false);
                };
            }
            mWorkspace.removeExtraEmptyScreen(true, onComplete,
                    ON_ACTIVITY_RESULT_ANIMATION_DELAY, false);
            return;
        }

        if (resultCode == RESULT_OK
                && mPendingAddInfo.container != ItemInfo.NO_ID) {
            final PendingAddArguments args = new PendingAddArguments();
            args.requestCode = requestCode;
            args.intent = data;
            args.container = mPendingAddInfo.container;
            args.screenId = mPendingAddInfo.screenId;
            args.cellX = mPendingAddInfo.cellX;
            args.cellY = mPendingAddInfo.cellY;
            if (isWorkspaceLocked()) {
                sPendingAddList.add(args);
            } else {
                completeAdd(args);
            }
            mWorkspace.removeExtraEmptyScreen(true, exitSpringLoaded,
                    ON_ACTIVITY_RESULT_ANIMATION_DELAY, false);
        } else if (resultCode == RESULT_CANCELED) {
            mWorkspace.removeExtraEmptyScreen(true, exitSpringLoaded,
                    ON_ACTIVITY_RESULT_ANIMATION_DELAY, false);
        }
        mDragLayer.clearAnimatedView();
    }

    private void completeTwoStageWidgetDrop(final int resultCode,
                                            final int appWidgetId) {
        CellLayout cellLayout = (CellLayout) mWorkspace
                .getScreenWithId(mPendingAddInfo.screenId);
        Runnable onCompleteRunnable = null;
        int animationType = 0;

        AppWidgetHostView boundWidget = null;
        if (resultCode == RESULT_OK) {
            animationType = Workspace.COMPLETE_TWO_STAGE_WIDGET_DROP_ANIMATION;
            final AppWidgetHostView layout = mAppWidgetHost.createView(this,
                    appWidgetId, mPendingAddWidgetInfo);
            boundWidget = layout;
            onCompleteRunnable = () -> {
                completeAddAppWidget(appWidgetId,
                        mPendingAddInfo.container,
                        mPendingAddInfo.screenId, layout, null);
                exitSpringLoadedDragModeDelayed(
                        (resultCode != RESULT_CANCELED),
                        EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT, null);
            };
        } else if (resultCode == RESULT_CANCELED) {
            mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            animationType = Workspace.CANCEL_TWO_STAGE_WIDGET_DROP_ANIMATION;
        }
        if (mDragLayer.getAnimatedView() != null) {
            mWorkspace.animateWidgetDrop(mPendingAddInfo, cellLayout,
                    (DragView) mDragLayer.getAnimatedView(),
                    onCompleteRunnable, animationType, boundWidget, true);
        } else if (onCompleteRunnable != null) {
            // The animated view may be null in the case of a rotation during
            // widget configuration
            onCompleteRunnable.run();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirstFrameAnimatorHelper.setIsVisible(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirstFrameAnimatorHelper.setIsVisible(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRestart())
            return;

        // Restore the previous launcher state
        if (mOnResumeState == State.WORKSPACE) {
            showWorkspace(false);
        } else if (mOnResumeState == State.APPS_CUSTOMIZE) {
            showAllApps(false, mAppsCustomizeContent.getContentType(), false);
        }
        mOnResumeState = State.NONE;

        // Background was set to gradient in onPause(), restore to black if in
        // all apps.
        setWorkspaceBackground(mState == State.WORKSPACE);

        mPaused = false;
        sPausedFromUserAction = false;
        if (mRestoring || mOnResumeNeedsLoad) {
            mWorkspaceLoading = true;
            mModel.startLoader(true, PagedView.INVALID_RESTORE_PAGE);
            mRestoring = false;
            mOnResumeNeedsLoad = false;
        }
        if (mBindOnResumeCallbacks.size() > 0) {

            if (mAppsCustomizeContent != null) {
                mAppsCustomizeContent.setBulkBind(true);
            }
            for (int i = 0; i < mBindOnResumeCallbacks.size(); i++) {
                mBindOnResumeCallbacks.get(i).run();
            }
            if (mAppsCustomizeContent != null) {
                mAppsCustomizeContent.setBulkBind(false);
            }
            mBindOnResumeCallbacks.clear();

        }
        if (mOnResumeCallbacks.size() > 0) {
            for (int i = 0; i < mOnResumeCallbacks.size(); i++) {
                mOnResumeCallbacks.get(i).run();
            }
            mOnResumeCallbacks.clear();
        }

        // Reset the pressed state of icons that were locked in the press state
        // while activities
        // were launching
        if (mWaitingForResume != null) {
            // Resets the previous workspace icon press state
            mWaitingForResume.setStayPressed(false);
        }
        if (mAppsCustomizeContent != null) {
            // Resets the previous all apps icon press state
            mAppsCustomizeContent.resetDrawableState();
        }

        // It is possible that widgets can receive updates while launcher is not
        // in the foreground.
        // Consequently, the widgets will be inflated in the orientation of the
        // foreground activity
        // (framework issue). On resuming, we ensure that any widgets are
        // inflated for the current
        // orientation.
        getWorkspace().reinflateWidgetsIfNecessary();

        // Process any items that were added while Launcher was away.
        InstallShortcutReceiver.disableAndFlushInstallQueue(this);

        // Update the voice search button proxy
        updateVoiceButtonProxyVisible(false);

        // Again, as with the above scenario, it's possible that one or more of
        // the global icons
        // were updated in the wrong orientation.
        updateGlobalIcons();

        if (mWorkspace.getCustomContentCallbacks() != null) {
            // If we are resuming and the custom content is the current page, we
            // call onShow().
            // It is also poassible that onShow will instead be called slightly
            // after first layout
            // if PagedView#setRestorePage was set to the custom content page in
            // onCreate().
            if (mWorkspace.isOnOrMovingToCustomContent()) {
                mWorkspace.getCustomContentCallbacks().onShow();
            }
        }
        mWorkspace.updateInteractionForState();
        mWorkspace.onResume();
        mAppsCustomizeContent.onResume();

        // Close out Fragments
        Fragment f = getFragmentManager().findFragmentByTag(
                TransitionEffectsFragment.TRANSITION_EFFECTS_FRAGMENT);
        if (f != null) {
            mTransitionEffectsFragment.setEffect();
        }
        Fragment f1 = getFragmentManager().findFragmentByTag(
                HiddenFolderFragment.HIDDEN_FOLDER_FRAGMENT);
        if (f1 != null && !mHiddenFolderAuth) {
            mHiddenFolderFragment.saveHiddenFolderStatus(-1);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(mHiddenFolderFragment).commit();
        } else {
            mHiddenFolderAuth = false;
        }
    }

    @Override
    protected void onPause() {
        // Ensure that items added to Launcher are queued until Launcher returns
        InstallShortcutReceiver.enableInstallQueue();

        super.onPause();
        mPaused = true;
        mDragController.cancelDrag();
        mDragController.resetLastGestureUpTime();

        // We call onHide() aggressively. The custom content callbacks should be
        // able to
        // debounce excess onHide calls.
        if (mWorkspace.getCustomContentCallbacks() != null) {
            mWorkspace.getCustomContentCallbacks().onHide();
        }

        // Reset the OverviewPanel position
        ((SlidingUpPanelLayout) mOverviewPanel).collapsePane();
    }

    QSBScroller mQsbScroller = new QSBScroller() {
        int scrollY = 0;

        @Override
        public void setScrollY(int scroll) {
            scrollY = scroll;

            if (mWorkspace.isOnOrMovingToCustomContent()) {
                mSearchDropTargetBar.setTranslationY(-scrollY);
                getQsbBar().setTranslationY(-scrollY);
            }
        }
    };

    public void resetQSBScroll() {
        mSearchDropTargetBar.animate().translationY(0).start();
        getQsbBar().animate().translationY(0).start();
    }

    public interface CustomContentCallbacks {
        void onShow();

        void onHide();

        void onScrollProgressChanged(float progress);
    }

    public void startThemeSettings() {
        // Intent settings = new
        // Intent().setClassName(OverviewSettingsPanel.ANDROID_SETTINGS,
        // OverviewSettingsPanel.THEME_SETTINGS);
        // startActivity(settings);

        Intent themeSettings = new Intent();
        themeSettings.setClass(Launcher.this, ThemeSettingsActivity.class);
        startActivity(themeSettings);

        if (mWorkspace.isInOverviewMode()) {
            mWorkspace.exitOverviewMode(false);
        } else if (mAppsCustomizeContent.isInOverviewMode()) {
            mAppsCustomizeContent.exitOverviewMode(false);
        }
    }

    public void onClickSortModeButton(View v) {
        final PopupMenu popupMenu = new PopupMenu(this, v);
        final Menu menu = popupMenu.getMenu();
        popupMenu.inflate(R.menu.menu_apps_customize_sort_mode);
        switch (mAppsCustomizeContent.getSortMode()) {
            case Title:
                menu.findItem(R.id.itemSortModeTitle).setChecked(true);
                break;
            case LaunchCount:
                menu.findItem(R.id.itemSortModeLaunchCount).setChecked(true);
                break;
            case InstallTime:
                menu.findItem(R.id.itemSortModeInstallTime).setChecked(true);
                break;
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemSortModeTitle:
                    mAppsCustomizeContent.setSortMode(AppsCustomizePagedView.SortMode.Title);
                    break;
                case R.id.itemSortModeInstallTime:
                    mAppsCustomizeContent.setSortMode(AppsCustomizePagedView.SortMode.InstallTime);
                    break;
                case R.id.itemSortModeLaunchCount:
                    mAppsCustomizeContent.setSortMode(AppsCustomizePagedView.SortMode.LaunchCount);
                    break;
            }
            mOverviewSettingsPanel.notifyDataSetInvalidated();
            SettingsProvider.putInt(getBaseContext(),
                    SettingsProvider.SETTINGS_UI_DRAWER_SORT_MODE,
                    mAppsCustomizeContent.getSortMode().getValue());
            return true;
        });
        popupMenu.show();
    }

    public void onClickTransitionEffectButton(View v, final boolean pageOrDrawer) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(TransitionEffectsFragment.PAGE_OR_DRAWER_SCROLL_SELECT, pageOrDrawer);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mTransitionEffectsFragment = new TransitionEffectsFragment();
        mTransitionEffectsFragment.setArguments(bundle);
        fragmentTransaction.setCustomAnimations(0, 0);
        fragmentTransaction.replace(R.id.launcher, mTransitionEffectsFragment, TransitionEffectsFragment.TRANSITION_EFFECTS_FRAGMENT);
        fragmentTransaction.commit();
    }

    @SuppressLint("ResourceType")
    public void setTransitionEffect(boolean pageOrDrawer, String newTransitionEffect) {
        String mSettingsProviderValue = pageOrDrawer ? SettingsProvider.SETTINGS_UI_DRAWER_SCROLLING_TRANSITION_EFFECT : SettingsProvider.SETTINGS_UI_HOMESCREEN_SCROLLING_TRANSITION_EFFECT;
        PagedView pagedView = pageOrDrawer ? mAppsCustomizeContent : mWorkspace;

        SettingsProvider.get(getApplicationContext()).edit().putString(mSettingsProviderValue, newTransitionEffect).apply();
        TransitionEffect.setFromString(pagedView, newTransitionEffect);

        // Reset Settings Changed
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putBoolean(SettingsProvider.SETTINGS_CHANGED, false);
        editor.apply();

        mOverviewSettingsPanel.notifyDataSetInvalidated();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(0, R.anim.a_exit_out_right);
        fragmentTransaction.remove(mTransitionEffectsFragment).commit();

        mDarkPanel.setVisibility(View.VISIBLE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(mDarkPanel, "alpha", 0.3f, 0.0f);
        anim.start();
        anim.addListener(mAnimatorListener);
    }

    public void onClickTransitionEffectOverflowMenuButton(View v) {
        final PopupMenu popupMenu = new PopupMenu(this, v);

        final Menu menu = popupMenu.getMenu();
        popupMenu.inflate(R.menu.menu_scrolling_settings);
        MenuItem pageOutlines = menu.findItem(R.id.itemScrollingPageOutlines);
        MenuItem fadeAdjacent = menu.findItem(R.id.itemScrollingFadeAdjacent);

        pageOutlines.setVisible(!isAllAppsVisible());
        pageOutlines
                .setChecked(SettingsProvider
                        .getBoolean(
                                this,
                                SettingsProvider.SETTINGS_UI_HOMESCREEN_SCROLLING_PAGE_OUTLINES,
                                R.bool.preferences_interface_homescreen_scrolling_page_outlines_default));

        fadeAdjacent
                .setChecked(SettingsProvider.getBoolean(this, !isAllAppsVisible() ? SettingsProvider.SETTINGS_UI_HOMESCREEN_SCROLLING_FADE_ADJACENT : SettingsProvider.SETTINGS_UI_DRAWER_SCROLLING_FADE_ADJACENT, !isAllAppsVisible() ? R.bool.preferences_interface_homescreen_scrolling_fade_adjacent_default : R.bool.preferences_interface_drawer_scrolling_fade_adjacent_default));

        final PagedView pagedView = !isAllAppsVisible() ? mWorkspace : mAppsCustomizeContent;

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemScrollingPageOutlines:
                    SettingsProvider.get(Launcher.this).edit().putBoolean(SettingsProvider.SETTINGS_UI_HOMESCREEN_SCROLLING_PAGE_OUTLINES, !item.isChecked()).commit();
                    mWorkspace.setShowOutlines(!item.isChecked());
                    break;
                case R.id.itemScrollingFadeAdjacent:
                    SettingsProvider.get(Launcher.this).edit().putBoolean(!isAllAppsVisible() ? SettingsProvider.SETTINGS_UI_HOMESCREEN_SCROLLING_FADE_ADJACENT : SettingsProvider.SETTINGS_UI_DRAWER_SCROLLING_FADE_ADJACENT, !item.isChecked()).apply();
                    pagedView.setFadeInAdjacentScreens(!item.isChecked());
                    break;
                default:
                    return false;
            }

            return true;
        });

        popupMenu.show();
    }

    protected boolean hasSettings() {
        return false;
    }

    public void startSettings() {
        Intent settings;
        settings = new Intent(android.provider.Settings.ACTION_SETTINGS);
        startActivity(settings);
        if (mWorkspace.isInOverviewMode()) {
            mWorkspace.exitOverviewMode(false);
        }
    }


    public interface QSBScroller {
        void setScrollY(int scrollY);
    }

    public QSBScroller addToCustomContentPage(View customContent, CustomContentCallbacks callbacks, String description) {
        mWorkspace.addToCustomContentPage(customContent, callbacks, description);
        return mQsbScroller;
    }

    // The custom content needs to offset its content to account for the QSB
    public int getTopOffsetForCustomContent() {
        return mWorkspace.getPaddingTop();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        // Flag the loader to stop early before switching
        mModel.stopLoader();
        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.surrender();
        }
        return Boolean.TRUE;
    }

    // We can't hide the IME if it was forced open. So don't bother
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mHasFocus = hasFocus;
    }

    private boolean acceptFilter() {
        final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        return !inputManager.isFullscreenMode();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final int uniChar = event.getUnicodeChar();
        final boolean handled = super.onKeyDown(keyCode, event);
        final boolean isKeyNotWhitespace = uniChar > 0
                && !Character.isWhitespace(uniChar);
        if (!handled && acceptFilter() && isKeyNotWhitespace) {
            boolean gotKey = TextKeyListener.getInstance().onKeyDown(
                    mWorkspace, mDefaultKeySsb, keyCode, event);
            if (gotKey && mDefaultKeySsb != null && mDefaultKeySsb.length() > 0) {

                return onSearchRequested();
            }
        }

        if (keyCode == KeyEvent.KEYCODE_MENU && event.isLongPress()) {
            return true;
        }

        return handled;
    }

    private String getTypedText() {
        return mDefaultKeySsb.toString();
    }

    private void clearTypedText() {
        mDefaultKeySsb.clear();
        mDefaultKeySsb.clearSpans();
        Selection.setSelection(mDefaultKeySsb, 0);
    }

    /**
     * Given the integer (ordinal) value of a State enum instance, convert it to
     * a variable of type State
     */
    private static State intToState(int stateOrdinal) {
        State state = State.WORKSPACE;
        final State[] stateValues = State.values();
        for (State stateValue : stateValues) {
            if (stateValue.ordinal() == stateOrdinal) {
                state = stateValue;
                break;
            }
        }
        return state;
    }

    /**
     * Restores the previous state, if it exists.
     *
     * @param savedState The previous state.
     */
    private void restoreState(Bundle savedState) {
        if (savedState == null) {
            return;
        }

        State state = intToState(savedState.getInt(RUNTIME_STATE,
                State.WORKSPACE.ordinal()));
        if (state == State.APPS_CUSTOMIZE) {
            mOnResumeState = State.APPS_CUSTOMIZE;
        }

        int currentScreen = savedState.getInt(RUNTIME_STATE_CURRENT_SCREEN,
                PagedView.INVALID_RESTORE_PAGE);
        if (currentScreen != PagedView.INVALID_RESTORE_PAGE) {
            mWorkspace.setRestorePage(currentScreen);
        }

        final long pendingAddContainer = savedState.getLong(
                RUNTIME_STATE_PENDING_ADD_CONTAINER, -1);
        final long pendingAddScreen = savedState.getLong(
                RUNTIME_STATE_PENDING_ADD_SCREEN, -1);

        if (pendingAddContainer != ItemInfo.NO_ID && pendingAddScreen > -1) {
            mPendingAddInfo.container = pendingAddContainer;
            mPendingAddInfo.screenId = pendingAddScreen;
            mPendingAddInfo.cellX = savedState
                    .getInt(RUNTIME_STATE_PENDING_ADD_CELL_X);
            mPendingAddInfo.cellY = savedState
                    .getInt(RUNTIME_STATE_PENDING_ADD_CELL_Y);
            mPendingAddInfo.spanX = savedState
                    .getInt(RUNTIME_STATE_PENDING_ADD_SPAN_X);
            mPendingAddInfo.spanY = savedState
                    .getInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y);
            mPendingAddWidgetInfo = savedState
                    .getParcelable(RUNTIME_STATE_PENDING_ADD_WIDGET_INFO);
            mPendingAddWidgetId = savedState
                    .getInt(RUNTIME_STATE_PENDING_ADD_WIDGET_ID);
            mWaitingForResult = true;
            mRestoring = true;
        }

        boolean renameFolder = savedState.getBoolean(
                RUNTIME_STATE_PENDING_FOLDER_RENAME, false);
        if (renameFolder) {
            long id = savedState
                    .getLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID);
            mFolderInfo = mModel.getFolderById(this, sFolders, id);
            mRestoring = true;
        }

        // Restore the AppsCustomize tab
        if (mAppsCustomizeLayout != null) {
            String curContentType = savedState
                    .getString("apps_customize_currentContentType");
            if (curContentType != null) {
                mAppsCustomizeContent
                        .setContentType(AppsCustomizePagedView.ContentType
                                .valueOf(curContentType));
                mAppsCustomizeContent.loadAssociatedPages(mAppsCustomizeContent
                        .getCurrentPage());
            }

            int currentIndex = savedState.getInt("apps_customize_currentIndex");
            mAppsCustomizeContent.restorePageForIndex(currentIndex);
        }
        mItemIdToViewId = (HashMap<Integer, Integer>) savedState.getSerializable(RUNTIME_STATE_VIEW_IDS);
    }

    /**
     * Finds all the views we need and configure them properly.
     */
    private void setupViews() {
        final DragController dragController = mDragController;

        mLauncherView = findViewById(R.id.launcher);
        mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
        mWorkspace = (Workspace) mDragLayer.findViewById(R.id.workspace);
        mPageIndicators = mDragLayer.findViewById(R.id.page_indicator);

        mLauncherView
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mWorkspaceBackgroundDrawable = getResources().getDrawable(
                R.drawable.workspace_bg);

        // Setup the drag layer
        mDragLayer.setup(this, dragController);

        // Setup the hotseat
        mHotseat = (Hotseat) findViewById(R.id.hotseat);
        if (mHotseat != null) {
            mHotseat.setup(this);
            mHotseat.setOnLongClickListener(this);
        }

        mOverviewPanel = (ViewGroup) findViewById(R.id.overview_panel);
        mOverviewSettingsPanel = new SettingsPanel(this, mOverviewPanel);
        mOverviewSettingsPanel.initializeAdapter();
        mOverviewSettingsPanel.initializeViews();
        mDarkPanel = ((SlidingUpPanelLayout) mOverviewPanel)
                .findViewById(R.id.llDarkPanel);

        // Setup the workspace
        mWorkspace.setHapticFeedbackEnabled(false);
        mWorkspace.setOnLongClickListener(this);
        mWorkspace.setup(dragController);
        dragController.addDragListener(mWorkspace);

        // Get the search/delete bar
        mSearchDropTargetBar = (SearchDropTargetBar) mDragLayer
                .findViewById(R.id.search_drop_target_bar);

        // Setup AppsCustomize
        mAppsCustomizeLayout = (AppsCustomizeLayout) findViewById(R.id.apps_customize_pane);
        mAppsCustomizeContent = (AppsCustomizePagedView) mAppsCustomizeLayout
                .findViewById(R.id.pageAppsCustomize);
        mAppsCustomizeContent.setup(this, dragController);

        // Setup the drag controller (drop targets have to be added in reverse
        // order in priority)
        dragController.setDragScoller(mWorkspace);
        dragController.setScrollView(mDragLayer);
        dragController.setMoveTarget(mWorkspace);
        dragController.addDropTarget(mWorkspace);
        if (mSearchDropTargetBar != null) {
            mSearchDropTargetBar.setup(this, dragController);
        }

        // Load the specified theme
        String themePackage = SettingsProvider.getThemePackageName(this,
                THEME_DEFAULT);
        PackageManager pm = getPackageManager();
        Resources themeResources = null;
        assert themePackage != null;
        if (!themePackage.equals(THEME_DEFAULT)) {
            try {
                themeResources = pm.getResourcesForApplication(themePackage);
            } catch (NameNotFoundException e) {
                SettingsProvider.setThemePackageName(this, Launcher.THEME_DEFAULT);
            }
        }
        if (themeResources != null) {

            try {

                showThemeFont = SettingsProvider.getThemeFont(this);

                if (showThemeFont) {
                    themeFont = Typeface.createFromAsset(themeResources.getAssets(), "themefont.ttf");
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Creates a view representing a shortcut.
     *
     * @param info The data structure describing the shortcut.
     * @return A View inflated from R.layout.application.
     */
    View createShortcut(ShortcutInfo info) {
        return createShortcut(R.layout.v_application,
                (ViewGroup) mWorkspace.getChildAt(mWorkspace.getCurrentPage()),
                info);
    }

    /**
     * Creates a view representing a shortcut inflated from the specified
     * resource.
     *
     * @param layoutResId The id of the XML layout used to create the shortcut.
     * @param parent      The group the shortcut belongs to.
     * @param info        The data structure describing the shortcut.
     * @return A View inflated from layoutResId.
     */
    View createShortcut(int layoutResId, ViewGroup parent, ShortcutInfo info) {
        BubbleTextView favorite = (BubbleTextView) mInflater.inflate(
                layoutResId, parent, false);
        favorite.applyFromShortcutInfo(info, mIconCache);
        favorite.setTextVisibility(!mHideIconLabels);
        favorite.setOnClickListener(this);
        if (info.itemType == LauncherSettings.Favorites.ITEM_TYPE_ALLAPPS
                && info.getIcon(mIconCache) == null) {
            // All apps icon
            Drawable d = getResources().getDrawable(
                    R.drawable.selector_all_apps_button_icon);
            Utilities.resizeIconDrawable(d);
            favorite.setCompoundDrawables(null, d, null, null);
            favorite.setOnTouchListener(getHapticFeedbackTouchListener());
        }
        return favorite;
    }

    /**
     * Add an application shortcut to the workspace.
     *
     * @param data     The intent describing the application.
     * cellInfo The position on screen where to create the shortcut.
     */
    void completeAddApplication(Intent data, long container, long screenId,
                                int cellX, int cellY) {
        final int[] cellXY = mTmpAddItemCellCoordinates;
        final CellLayout layout = getCellLayout(container, screenId);

        // First we check if we already know the exact location where we want to
        // add this item.
        if (cellX >= 0 && cellY >= 0) {
            cellXY[0] = cellX;
            cellXY[1] = cellY;
        } else if (!layout.findCellForSpan(cellXY, 1, 1)) {
            showOutOfSpaceMessage(isHotseatLayout(layout));
            return;
        }

        final ShortcutInfo info = mModel.getShortcutInfo(getPackageManager(),
                data, this);

        if (info != null) {
            info.setActivity(this, data.getComponent(),
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            info.container = ItemInfo.NO_ID;
            mWorkspace.addApplicationShortcut(info, layout, container,
                    screenId, cellXY[0], cellXY[1], isWorkspaceLocked(), cellX,
                    cellY);
        }
    }

    /**
     * Add a shortcut to the workspace.
     *
     * @param data     The intent describing the shortcut.
     * cellInfo The position on screen where to create the shortcut.
     */
    private void completeAddShortcut(Intent data, long container,
                                     long screenId, int cellX, int cellY) {
        int[] cellXY = mTmpAddItemCellCoordinates;
        int[] touchXY = mPendingAddInfo.dropPos;
        CellLayout layout = getCellLayout(container, screenId);

        boolean foundCellSpan;

        ShortcutInfo info = mModel.infoFromShortcutIntent(this, data, null);
        if (info == null) {
            return;
        }
        final View view = createShortcut(info);

        // First we check if we already know the exact location where we want to
        // add this item.
        if (cellX >= 0 && cellY >= 0) {
            cellXY[0] = cellX;
            cellXY[1] = cellY;
            foundCellSpan = true;

            // If appropriate, either create a folder or add to an existing
            // folder
            if (mWorkspace.createUserFolderIfNecessary(view, container, layout,
                    cellXY, 0, true, null, null)) {
                return;
            }
            DragObject dragObject = new DragObject();
            dragObject.dragInfo = info;
            if (mWorkspace.addToExistingFolderIfNecessary(view, layout, cellXY,
                    0, dragObject, true)) {
                return;
            }
        } else if (touchXY != null) {
            // when dragging and dropping, just find the closest free spot
            int[] result = layout.findNearestVacantArea(touchXY[0], touchXY[1],
                    1, 1, cellXY);
            foundCellSpan = (result != null);
        } else {
            foundCellSpan = layout.findCellForSpan(cellXY, 1, 1);
        }

        if (!foundCellSpan) {
            showOutOfSpaceMessage(isHotseatLayout(layout));
            return;
        }

        LauncherModel.addItemToDatabase(this, info, container, screenId,
                cellXY[0], cellXY[1], false);

        if (!mRestoring) {
            mWorkspace.addInScreen(view, container, screenId, cellXY[0],
                    cellXY[1], 1, 1, isWorkspaceLocked());
        }
    }

    static int[] getSpanForWidget(Context context, ComponentName component,
                                  int minWidth, int minHeight) {
        Rect padding = AppWidgetHostView.getDefaultPaddingForWidget(context,
                component, null);
        // We want to account for the extra amount of padding that we are adding
        // to the widget
        // to ensure that it gets the full amount of space that it has requested
        int requiredWidth = minWidth + padding.left + padding.right;
        int requiredHeight = minHeight + padding.top + padding.bottom;
        return CellLayout.rectToCell(requiredWidth, requiredHeight, null);
    }

    static int[] getSpanForWidget(Context context, AppWidgetProviderInfo info) {
        return getSpanForWidget(context, info.provider, info.minWidth,
                info.minHeight);
    }

    static int[] getMinSpanForWidget(Context context, AppWidgetProviderInfo info) {
        return getSpanForWidget(context, info.provider, info.minResizeWidth,
                info.minResizeHeight);
    }

    static int[] getSpanForWidget(Context context, PendingAddWidgetInfo info) {
        return getSpanForWidget(context, info.componentName, info.minWidth,
                info.minHeight);
    }

    static int[] getMinSpanForWidget(Context context, PendingAddWidgetInfo info) {
        return getSpanForWidget(context, info.componentName,
                info.minResizeWidth, info.minResizeHeight);
    }

    /**
     * Add a widget to the workspace.
     *
     * @param appWidgetId The app widget id
     * cellInfo    The position on screen where to create the widget.
     */
    @SuppressLint("StaticFieldLeak")
    private void completeAddAppWidget(final int appWidgetId, long container,
                                      long screenId, AppWidgetHostView hostView,
                                      AppWidgetProviderInfo appWidgetInfo) {
        if (appWidgetInfo == null) {
            appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        }

        // Calculate the grid spans needed to fit this widget
        CellLayout layout = getCellLayout(container, screenId);

        int[] minSpanXY = getMinSpanForWidget(this, appWidgetInfo);
        int[] spanXY = getSpanForWidget(this, appWidgetInfo);

        // Try finding open space on Launcher screen
        // We have saved the position to which the widget was dragged-- this
        // really only matters
        // if we are placing widgets on a "spring-loaded" screen
        int[] cellXY = mTmpAddItemCellCoordinates;
        int[] touchXY = mPendingAddInfo.dropPos;
        int[] finalSpan = new int[2];
        boolean foundCellSpan;
        if (mPendingAddInfo.cellX >= 0 && mPendingAddInfo.cellY >= 0) {
            cellXY[0] = mPendingAddInfo.cellX;
            cellXY[1] = mPendingAddInfo.cellY;
            spanXY[0] = mPendingAddInfo.spanX;
            spanXY[1] = mPendingAddInfo.spanY;
            foundCellSpan = true;
        } else if (touchXY != null) {
            // when dragging and dropping, just find the closest free spot
            int[] result = layout.findNearestVacantArea(touchXY[0], touchXY[1],
                    minSpanXY[0], minSpanXY[1], spanXY[0], spanXY[1], cellXY,
                    finalSpan);
            spanXY[0] = finalSpan[0];
            spanXY[1] = finalSpan[1];
            foundCellSpan = (result != null);
        } else {
            foundCellSpan = layout.findCellForSpan(cellXY, minSpanXY[0],
                    minSpanXY[1]);
        }

        if (!foundCellSpan) {
            if (appWidgetId != -1) {
                // Deleting an app widget ID is a void call but writes to disk
                // before returning
                // to the caller...
                new AsyncTask<Void, Void, Void>() {
                    public Void doInBackground(Void... args) {
                        mAppWidgetHost.deleteAppWidgetId(appWidgetId);
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
            }
            showOutOfSpaceMessage(isHotseatLayout(layout));
            return;
        }

        // Build Launcher-specific widget info and save to database
        LauncherAppWidgetInfo launcherInfo = new LauncherAppWidgetInfo(
                appWidgetId, appWidgetInfo.provider);
        launcherInfo.spanX = spanXY[0];
        launcherInfo.spanY = spanXY[1];
        launcherInfo.minSpanX = mPendingAddInfo.minSpanX;
        launcherInfo.minSpanY = mPendingAddInfo.minSpanY;

        LauncherModel.addItemToDatabase(this, launcherInfo, container,
                screenId, cellXY[0], cellXY[1], false);

        if (!mRestoring) {
            if (hostView == null) {
                // Perform actual inflation because we're live
                launcherInfo.hostView = mAppWidgetHost.createView(this,
                        appWidgetId, appWidgetInfo);
                launcherInfo.hostView.setAppWidget(appWidgetId, appWidgetInfo);
            } else {
                // The AppWidgetHostView has already been inflated and
                // instantiated
                launcherInfo.hostView = hostView;
            }

            launcherInfo.hostView.setTag(launcherInfo);
            launcherInfo.hostView.setVisibility(View.VISIBLE);
            launcherInfo.notifyWidgetSizeChanged(this);

            mWorkspace.addInScreen(launcherInfo.hostView, container, screenId,
                    cellXY[0], cellXY[1], launcherInfo.spanX,
                    launcherInfo.spanY, isWorkspaceLocked());

            addWidgetToAutoAdvanceIfNeeded(launcherInfo.hostView, appWidgetInfo);
        }
        resetAddInfo();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                mUserPresent = false;
                mDragLayer.clearAllResizeFrames();
                updateRunning();

                // Reset AllApps to its initial state only if we are not in the
                // middle of
                // processing a multi-step drop
                if (mAppsCustomizeLayout != null
                        && mPendingAddInfo.container == ItemInfo.NO_ID) {
                    showWorkspace(false);
                }
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                mUserPresent = true;
                updateRunning();
            }
        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Listen for broadcasts related to user-presence
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(mReceiver, filter);
        FirstFrameAnimatorHelper.initializeDrawListener(getWindow()
                .getDecorView());
        mAttached = true;
        mVisible = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;

        if (mAttached) {
            unregisterReceiver(mReceiver);
            mAttached = false;
        }
        updateRunning();
    }

    public void onWindowVisibilityChanged(int visibility) {
        mVisible = visibility == View.VISIBLE;
        updateRunning();
        // The following code used to be in onResume, but it turns out onResume
        // is called when
        // you're in All Apps and click home to go to the workspace.
        // onWindowVisibilityChanged
        // is a more appropriate event to handle
        if (mVisible) {
            mAppsCustomizeLayout.onWindowVisible();
            if (!mWorkspaceLoading) {
                final ViewTreeObserver observer = mWorkspace
                        .getViewTreeObserver();
                // We want to let Launcher draw itself at least once before we
                // force it to build
                // layers on all the workspace pages, so that transitioning to
                // Launcher from other
                // apps is nice and speedy.
                observer.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                    private boolean mStarted = false;

                    public void onDraw() {
                        if (mStarted) return;
                        mStarted = true;
                        // We delay the layer building a bit in order to give
                        // other message processing a time to run. In particular
                        // this avoids a delay in hiding the IME if it was
                        // currently shown, because doing that may involve
                        // some communication back with the app.
                        mWorkspace.postDelayed(mBuildLayersRunnable, 500);
                        final ViewTreeObserver.OnDrawListener listener = this;
                        mWorkspace.post(() -> {
                            if (mWorkspace != null && mWorkspace.getViewTreeObserver() != null) {
                                mWorkspace.getViewTreeObserver().removeOnDrawListener(listener);
                            }
                        });
                    }
                });
            }
            clearTypedText();
        }
    }

    private void sendAdvanceMessage(long delay) {
        mHandler.removeMessages(ADVANCE_MSG);
        Message msg = mHandler.obtainMessage(ADVANCE_MSG);
        mHandler.sendMessageDelayed(msg, delay);
        mAutoAdvanceSentTime = System.currentTimeMillis();
    }

    private void updateRunning() {
        boolean autoAdvanceRunning = mVisible && mUserPresent
                && !mWidgetsToAdvance.isEmpty();
        if (autoAdvanceRunning != mAutoAdvanceRunning) {
            mAutoAdvanceRunning = autoAdvanceRunning;
            if (autoAdvanceRunning) {
                long delay = mAutoAdvanceTimeLeft == -1 ? mAdvanceInterval
                        : mAutoAdvanceTimeLeft;
                sendAdvanceMessage(delay);
            } else {
                if (!mWidgetsToAdvance.isEmpty()) {
                    mAutoAdvanceTimeLeft = Math
                            .max(0,
                                    mAdvanceInterval
                                            - (System.currentTimeMillis() - mAutoAdvanceSentTime));
                }
                mHandler.removeMessages(ADVANCE_MSG);
                mHandler.removeMessages(0);

            }
        }
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ADVANCE_MSG) {
                int i = 0;
                for (View key : mWidgetsToAdvance.keySet()) {
                    final View v = key.findViewById(mWidgetsToAdvance.get(key).autoAdvanceViewId);
                    final int delay = mAdvanceStagger * i;
                    if (v instanceof Advanceable) {
                        postDelayed(() -> ((Advanceable) v).advance(), delay);
                    }
                    i++;
                }
                sendAdvanceMessage(mAdvanceInterval);
            }
        }
    };

    void addWidgetToAutoAdvanceIfNeeded(View hostView,
                                        AppWidgetProviderInfo appWidgetInfo) {
        if (appWidgetInfo == null || appWidgetInfo.autoAdvanceViewId == -1)
            return;
        View v = hostView.findViewById(appWidgetInfo.autoAdvanceViewId);
        if (v instanceof Advanceable) {
            mWidgetsToAdvance.put(hostView, appWidgetInfo);
            ((Advanceable) v).fyiWillBeAdvancedByHostKThx();
            updateRunning();
        }
    }

    void removeWidgetToAutoAdvance(View hostView) {
        if (mWidgetsToAdvance.containsKey(hostView)) {
            mWidgetsToAdvance.remove(hostView);
            updateRunning();
        }
    }

    public void removeAppWidget(LauncherAppWidgetInfo launcherInfo) {
        removeWidgetToAutoAdvance(launcherInfo.hostView);
        launcherInfo.hostView = null;
    }

    void showOutOfSpaceMessage(boolean isHotseatLayout) {
        int strId = (isHotseatLayout ? R.string.hotseat_out_of_space
                : R.string.out_of_space);
        Toast.makeText(this, getString(strId), Toast.LENGTH_SHORT).show();
    }

    public DragLayer getDragLayer() {
        return mDragLayer;
    }

    public Workspace getWorkspace() {
        return mWorkspace;
    }

    public Hotseat getHotseat() {
        return mHotseat;
    }

    public View getDarkPanel() {
        return mDarkPanel;
    }

    public View getOverviewPanel() {
        return mOverviewPanel;
    }

    public SearchDropTargetBar getSearchBar() {
        return mSearchDropTargetBar;
    }

    public LauncherAppWidgetHost getAppWidgetHost() {
        return mAppWidgetHost;
    }

    public LauncherModel getModel() {
        return mModel;
    }

    protected SharedPreferences getSharedPrefs() {
        return mSharedPrefs;
    }

    public void closeSystemDialogs() {
        getWindow().closeAllPanels();

        mWaitingForResult = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Close the menu
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            // also will cancel mWaitingForResult.
            closeSystemDialogs();

            if (mTransitionEffectDialog != null) {
                mTransitionEffectDialog.cancel();
            }

            final boolean alreadyOnHome = mHasFocus
                    && ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

            if (mWorkspace == null) {
                // Can be cases where mWorkspace is null, this prevents a NPE
                return;
            }
            Folder openFolder = mWorkspace.getOpenFolder();
            // In all these cases, only animate if we're already on home
            mWorkspace.exitWidgetResizeMode();
            if (alreadyOnHome && mState == State.WORKSPACE
                    && !mWorkspace.isTouchActive() && openFolder == null
                    && shouldMoveToDefaultScreenOnHomeIntent()) {
                mWorkspace.moveToDefaultScreen(true);
            }

            closeFolder();
            exitSpringLoadedDragMode();

            // If we are already on home, then just animate back to the
            // workspace,
            // otherwise, just wait until onResume to set the state back to
            // Workspace
            if (alreadyOnHome) {
                showWorkspace(true);
            } else {
                mOnResumeState = State.WORKSPACE;
            }

            final View v = getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

            // Reset the apps customize page
            if (!alreadyOnHome && mAppsCustomizeLayout != null) {
                mAppsCustomizeLayout.reset();
            }

            onHomeIntent();
        }

    }

    /**
     * Override point for subclasses to prevent movement to the default screen
     * when the home button is pressed. Used (for example) in GEL, to prevent
     * movement during a search.
     */
    protected boolean shouldMoveToDefaultScreenOnHomeIntent() {
        return true;
    }

    /**
     * Override point for subclasses to provide custom behaviour for when a home
     * intent is fired.
     */
    protected void onHomeIntent() {
        // Do nothing
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        for (int page : mSynchronouslyBoundPages) {
            mWorkspace.restoreInstanceStateForChild(page);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mWorkspace.getChildCount() > 0) {
            outState.putInt(RUNTIME_STATE_CURRENT_SCREEN,
                    mWorkspace.getCurrentPageOffsetFromCustomContent());
        }
        super.onSaveInstanceState(outState);

        outState.putInt(RUNTIME_STATE, mState.ordinal());
        // We close any open folder since it will not be re-opened, and we need
        // to make sure
        // this state is reflected.
        closeFolder();

        if (mPendingAddInfo.container != ItemInfo.NO_ID
                && mPendingAddInfo.screenId > -1 && mWaitingForResult) {
            outState.putLong(RUNTIME_STATE_PENDING_ADD_CONTAINER,
                    mPendingAddInfo.container);
            outState.putLong(RUNTIME_STATE_PENDING_ADD_SCREEN,
                    mPendingAddInfo.screenId);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_X,
                    mPendingAddInfo.cellX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_Y,
                    mPendingAddInfo.cellY);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_X,
                    mPendingAddInfo.spanX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y,
                    mPendingAddInfo.spanY);
            outState.putParcelable(RUNTIME_STATE_PENDING_ADD_WIDGET_INFO,
                    mPendingAddWidgetInfo);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_WIDGET_ID,
                    mPendingAddWidgetId);
        }

        if (mFolderInfo != null && mWaitingForResult) {
            outState.putBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, true);
            outState.putLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID,
                    mFolderInfo.id);
        }

        // Save the current AppsCustomize tab
        if (mAppsCustomizeLayout != null) {
            AppsCustomizePagedView.ContentType type = mAppsCustomizeContent.getContentType();
            String currentTabTag = mAppsCustomizeContent.getContentType().name();
            outState.putString("apps_customize_currentContentType", currentTabTag);
            int currentIndex = mAppsCustomizeContent.getSaveInstanceStateIndex();
            outState.putInt("apps_customize_currentIndex", currentIndex);
        }
        outState.putSerializable(RUNTIME_STATE_VIEW_IDS, mItemIdToViewId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remove all pending runnables
        mHandler.removeMessages(ADVANCE_MSG);
        mHandler.removeMessages(0);
        mWorkspace.removeCallbacks(mBuildLayersRunnable);

        // Stop callbacks from LauncherModel
        LauncherAppState app = (LauncherAppState.getInstance());
        mModel.stopLoader();
        app.setLauncher(null);

        try {
            mAppWidgetHost.stopListening();
        } catch (NullPointerException ex) {
            Log.w(TAG,
                    "problem while stopping AppWidgetHost during Launcher destruction",
                    ex);
        }
        mAppWidgetHost = null;

        mWidgetsToAdvance.clear();

        TextKeyListener.getInstance().release();

        // Disconnect any of the callbacks and drawables associated with
        // ItemInfos on the workspace
        // to prevent leaking Launcher activities on orientation change.
        if (mModel != null) {
            mModel.unbindItemInfosAndClearQueuedBindRunnables();
        }

        getContentResolver().unregisterContentObserver(mWidgetObserver);
        unregisterReceiver(mCloseSystemDialogsReceiver);

        mDragLayer.clearAllResizeFrames();
        ((ViewGroup) mWorkspace.getParent()).removeAllViews();
        mWorkspace.removeAllWorkspaceScreens();
        mWorkspace = null;
        mDragController = null;

        LauncherAnimUtils.onDestroyActivity();

        unregisterReceiver(protectedAppsChangedReceiver);
    }

    public DragController getDragController() {
        return mDragController;
    }

    public void validateLockForHiddenFolders(Bundle bundle, FolderIcon info) {
        try {
            // Validate Lock Pattern
            Intent lockPatternActivity = new Intent();
            lockPatternActivity.setClassName("com.android.settings", "com.android.settings.applications.LockPatternActivity");
            startActivityForResult(lockPatternActivity, REQUEST_LOCK_PATTERN);
            mHiddenFolderAuth = false;
            mHiddenFolderIcon = info;
            mHiddenFolderFragment = new HiddenFolderFragment();
            mHiddenFolderFragment.setArguments(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (requestCode >= 0)
            mWaitingForResult = true;
        super.startActivityForResult(intent, requestCode);
    }

    /**
     * Indicates that we want global search for this activity by setting the
     * globalSearch argument for {@link #startSearch} to true.
     */
    @Override
    public void startSearch(String initialQuery, boolean selectInitialQuery,
                            Bundle appSearchData, boolean globalSearch) {

        showWorkspace(true);

        if (initialQuery == null) {
            // Use any text typed in the launcher as the initial query
            initialQuery = getTypedText();
        }
        if (appSearchData == null) {
            appSearchData = new Bundle();
            appSearchData.putString("source", "launcher-search");
        }
        Rect sourceBounds = new Rect();
        if (mSearchDropTargetBar != null) {
            sourceBounds = mSearchDropTargetBar.getSearchBarBounds();
        }

        startSearch(initialQuery, selectInitialQuery, appSearchData,
                sourceBounds);
    }

    public void startSearch(String initialQuery, boolean selectInitialQuery,
                            Bundle appSearchData, Rect sourceBounds) {
        startGlobalSearch(initialQuery, selectInitialQuery, appSearchData,
                sourceBounds);
    }

    /**
     * Starts the global search activity. This code is a copied from
     * SearchManager
     */
    private void startGlobalSearch(String initialQuery,
                                   boolean selectInitialQuery, Bundle appSearchData, Rect sourceBounds) {
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName globalSearchActivity = searchManager
                .getGlobalSearchActivity();
        if (globalSearchActivity == null) {
            return;
        }
        Intent intent = new Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(globalSearchActivity);
        // Make sure that we have a Bundle to put source in
        if (appSearchData == null) {
            appSearchData = new Bundle();
        } else {
            appSearchData = new Bundle(appSearchData);
        }
        // Set source to package name of app that starts global search, if not
        // set already.
        if (!appSearchData.containsKey("source")) {
            appSearchData.putString("source", getPackageName());
        }
        intent.putExtra(SearchManager.APP_DATA, appSearchData);
        if (!TextUtils.isEmpty(initialQuery)) {
            intent.putExtra(SearchManager.QUERY, initialQuery);
        }
        if (selectInitialQuery) {
            intent.putExtra(SearchManager.EXTRA_SELECT_QUERY, selectInitialQuery);
        }
        intent.setSourceBounds(sourceBounds);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isOnCustomContent() {
        return mWorkspace.isOnOrMovingToCustomContent();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (!isOnCustomContent()) {
            // Close any open folders
            closeFolder();
            // Stop resizing any widgets
            mWorkspace.exitWidgetResizeMode();
            if (!mWorkspace.isInOverviewMode()) {
                // Show the overview mode
                showOverviewMode(true);
            } else {
                showWorkspace(true);
            }
        }
        return false;
    }

    @Override
    public boolean onSearchRequested() {
        startSearch(null, false, null, true);
        // Use a custom animation for launching search
        return true;
    }

    public boolean isWorkspaceLocked() {
        return mWorkspaceLoading || mWaitingForResult;
    }

    public boolean isWorkspaceLoading() {
        return mWorkspaceLoading;
    }

    private void resetAddInfo() {
        mPendingAddInfo.container = ItemInfo.NO_ID;
        mPendingAddInfo.screenId = -1;
        mPendingAddInfo.cellX = mPendingAddInfo.cellY = -1;
        mPendingAddInfo.spanX = mPendingAddInfo.spanY = -1;
        mPendingAddInfo.minSpanX = mPendingAddInfo.minSpanY = -1;
        mPendingAddInfo.dropPos = null;
    }

    void addAppWidgetImpl(final int appWidgetId, final ItemInfo info,
                          final AppWidgetHostView boundWidget,
                          final AppWidgetProviderInfo appWidgetInfo) {
        addAppWidgetImpl(appWidgetId, info, boundWidget, appWidgetInfo, 0);
    }

    void addAppWidgetImpl(final int appWidgetId, final ItemInfo info,
                          final AppWidgetHostView boundWidget,
                          final AppWidgetProviderInfo appWidgetInfo, int delay) {
        if (appWidgetInfo.configure != null) {
            mPendingAddWidgetInfo = appWidgetInfo;
            mPendingAddWidgetId = appWidgetId;

            // Launch over to configure widget, if needed
            Intent intent = new Intent(
                    AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            Utilities.startActivityForResultSafely(this, intent,
                    REQUEST_CREATE_APPWIDGET);
        } else {
            // Otherwise just add it
            Runnable onComplete = () -> {
                // Exit spring loaded mode if necessary after adding the
                // widget
                exitSpringLoadedDragModeDelayed(true, EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT, null);
            };
            completeAddAppWidget(appWidgetId, info.container, info.screenId, boundWidget, appWidgetInfo);
            mWorkspace.removeExtraEmptyScreen(true, onComplete, delay, false);
        }
    }

    protected void moveToCustomContentScreen(boolean animate) {
        // Close any folders that may be open.
        closeFolder();
        mWorkspace.moveToCustomContentScreen(animate);
    }

    /**
     * Process a shortcut drop.
     *
     * @param componentName The name of the component
     * @param screenId      The ID of the screen where it should be added
     * @param cell          The cell it should be added to, optional
     *                      position      The location on the screen where it was dropped, optional
     */
    void processShortcutFromDrop(ComponentName componentName, long container, long screenId, int[] cell, int[] loc) {
        resetAddInfo();
        mPendingAddInfo.container = container;
        mPendingAddInfo.screenId = screenId;
        mPendingAddInfo.dropPos = loc;

        if (cell != null) {
            mPendingAddInfo.cellX = cell[0];
            mPendingAddInfo.cellY = cell[1];
        }

        Intent createShortcutIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        createShortcutIntent.setComponent(componentName);
        processShortcut(createShortcutIntent);
    }

    /**
     * Process a widget drop.
     *
     * @param info     The PendingAppWidgetInfo of the widget being added.
     * @param screenId The ID of the screen where it should be added
     * @param cell     The cell it should be added to, optional
     * position The location on the screen where it was dropped, optional
     */
    void addAppWidgetFromDrop(PendingAddWidgetInfo info, long container,
                              long screenId, int[] cell, int[] span, int[] loc) {
        resetAddInfo();
        mPendingAddInfo.container = info.container = container;
        mPendingAddInfo.screenId = info.screenId = screenId;
        mPendingAddInfo.dropPos = loc;
        mPendingAddInfo.minSpanX = info.minSpanX;
        mPendingAddInfo.minSpanY = info.minSpanY;

        if (cell != null) {
            mPendingAddInfo.cellX = cell[0];
            mPendingAddInfo.cellY = cell[1];
        }
        if (span != null) {
            mPendingAddInfo.spanX = span[0];
            mPendingAddInfo.spanY = span[1];
        }

        AppWidgetHostView hostView = info.boundWidget;
        int appWidgetId;
        if (hostView != null) {
            appWidgetId = hostView.getAppWidgetId();
            addAppWidgetImpl(appWidgetId, info, hostView, info.info);
        } else {
            // In this case, we either need to start an activity to get
            // permission to bind
            // the widget, or we need to start an activity to configure the
            // widget, or both.
            appWidgetId = getAppWidgetHost().allocateAppWidgetId();
            Bundle options = info.bindOptions;

            boolean success;
            if (options != null) {
                success = mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, info.componentName, options);
            } else {
                success = mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, info.componentName);
            }
            if (success) {
                addAppWidgetImpl(appWidgetId, info, null, info.info);
            } else {
                mPendingAddWidgetInfo = info.info;
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, info.componentName);
                // TODO: we need to make sure that this accounts for the options
                // bundle.
                // intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS,
                // options);
                startActivityForResult(intent, REQUEST_BIND_APPWIDGET);
            }
        }
    }

    void processShortcut(Intent intent) {
        // Handle case where user selected "Applications"
        String applicationName = getResources().getString(
                R.string.group_applications);
        String shortcutName = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

        if (applicationName != null && applicationName.equals(shortcutName)) {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
            pickIntent.putExtra(Intent.EXTRA_INTENT, mainIntent);
            pickIntent.putExtra(Intent.EXTRA_TITLE,
                    getText(R.string.title_select_application));
            Utilities.startActivityForResultSafely(this, pickIntent,
                    REQUEST_PICK_APPLICATION);
        } else {
            Utilities.startActivityForResultSafely(this, intent,
                    REQUEST_CREATE_SHORTCUT);
        }
    }

    void processWallpaper(Intent intent) {
        startActivityForResult(intent, REQUEST_PICK_WALLPAPER);
    }

    FolderIcon addFolder(CellLayout layout, long container,
                         final long screenId, int cellX, int cellY) {
        final FolderInfo folderInfo = new FolderInfo();
        folderInfo.title = getText(R.string.folder_name);

        // Update the model
        LauncherModel.addItemToDatabase(Launcher.this, folderInfo, container,
                screenId, cellX, cellY, false);
        sFolders.put(folderInfo.id, folderInfo);

        // Create the view
        FolderIcon newFolder = FolderIcon.fromXml(R.layout.v_folder_icon, this,
                layout, folderInfo, mIconCache);
        if (container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
            newFolder.setTextVisible(!mHideIconLabels);
        }
        mWorkspace.addInScreen(newFolder, container, screenId, cellX, cellY, 1,
                1, isWorkspaceLocked());
        // Force measure the new folder icon
        CellLayout parent = mWorkspace.getParentCellLayoutForView(newFolder);
        parent.getShortcutsAndWidgets().measureChild(newFolder);
        return newFolder;
    }

    void removeFolder(FolderInfo folder) {
        sFolders.remove(folder.id);
    }

    public void startWallpaper() {
        final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
        pickWallpaper.setComponent(getWallpaperPickerComponent());
        startActivityForResult(pickWallpaper, REQUEST_PICK_WALLPAPER);
    }

    protected ComponentName getWallpaperPickerComponent() {
        return new ComponentName(getPackageName(),
                LauncherWallpaperPickerActivity.class.getName());
    }

    /**
     * Registers various content observers. The current implementation registers
     * only a favorites observer to keep track of the favorites applications.
     */
    private void registerContentObservers() {
        ContentResolver resolver = getContentResolver();
        resolver.registerContentObserver(
                LauncherProvider.CONTENT_APPWIDGET_RESET_URI, true,
                mWidgetObserver);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
                return true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
                return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        Fragment f1 = getFragmentManager().findFragmentByTag(HiddenFolderFragment.HIDDEN_FOLDER_FRAGMENT);
        if (f1 != null) {
            mHiddenFolderFragment.saveHiddenFolderStatus(-1);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(mHiddenFolderFragment).commit();
        }
        if (isAllAppsVisible()) {
            if (mAppsCustomizeContent.isInOverviewMode()) {
                mAppsCustomizeContent.exitOverviewMode(true);
            } else {
                if (mAppsCustomizeContent.getContentType() == AppsCustomizePagedView.ContentType.Applications) {
                    showWorkspace(true);
                } else {
                    showOverviewMode(true);
                }
            }
        } else if (mWorkspace.isInOverviewMode()) {
            Fragment f = getFragmentManager().findFragmentByTag(
                    TransitionEffectsFragment.TRANSITION_EFFECTS_FRAGMENT);
            if (f != null) {
                mTransitionEffectsFragment.setEffect();
            } else {
                mWorkspace.exitOverviewMode(true);
            }
        } else if (mWorkspace.getOpenFolder() != null) {
            Folder openFolder = mWorkspace.getOpenFolder();
            if (openFolder.isEditingName()) {
                openFolder.dismissEditingName();
            } else {
                closeFolder();
            }
        } else {
            mWorkspace.exitWidgetResizeMode();

            // Back button is a no-op here, but give at least some feedback for
            // the button press
            mWorkspace.showOutlinesTemporarily();
        }
    }

    /**
     * Re-listen when widgets are reset.
     */
    private void onAppWidgetReset() {
        if (mAppWidgetHost != null) {
            mAppWidgetHost.startListening();
        }
    }

    /**
     * Launches the intent referred by the clicked shortcut.
     *
     * @param v The view representing the clicked shortcut.
     */
    public void onClick(View v) {
        // Make sure that rogue clicks don't get through while allapps is
        // launching, or after the
        // view has detached (it's possible for this to happen if the view is
        // removed mid touch).
        if (v.getWindowToken() == null) {
            return;
        }

        if (!mWorkspace.isFinishedSwitchingState()) {
            return;
        }

        if (v instanceof Workspace) {
            if (mWorkspace.isInOverviewMode()) {
                mWorkspace.exitOverviewMode(true);
            }
            return;
        }

        if (v instanceof CellLayout) {
            if (isAllAppsVisible()) {
                if (mAppsCustomizeContent.isInOverviewMode()) {
                    mAppsCustomizeContent.exitOverviewMode(
                            mAppsCustomizeContent.indexOfChild(v), true);
                }
            } else {
                if (mWorkspace.isInOverviewMode()) {
                    mWorkspace.exitOverviewMode(mWorkspace.indexOfChild(v),
                            true);
                }
            }
        }

        Object tag = v.getTag();
        if (tag instanceof ShortcutInfo) {
            // Open shortcut
            final ShortcutInfo shortcut = (ShortcutInfo) tag;
            if (shortcut.itemType == LauncherSettings.Favorites.ITEM_TYPE_ALLAPPS) {
                showAllApps(true,
                        AppsCustomizePagedView.ContentType.Applications, true);
            } else {
                final Intent intent = shortcut.intent;

                // Check for special shortcuts
                if (intent.getComponent() != null) {
                    final String shortcutClass = intent.getComponent()
                            .getClassName();

                    if (shortcutClass.equals(WidgetAdder.class.getName())) {
                        onClickAddWidgetButton();
                        return;
                    }
                }

                // Start activities
                int[] pos = new int[2];
                v.getLocationOnScreen(pos);
                intent.setSourceBounds(new Rect(pos[0], pos[1], pos[0]
                        + v.getWidth(), pos[1] + v.getHeight()));

                boolean success = startActivitySafely(v, intent, tag);

                mStats.recordLaunch(intent, shortcut);

                if (success && v instanceof BubbleTextView) {
                    mWaitingForResume = (BubbleTextView) v;
                    mWaitingForResume.setStayPressed(true);
                }
            }
        } else if (tag instanceof FolderInfo) {
            if (v instanceof FolderIcon) {
                FolderIcon fi = (FolderIcon) v;
                handleFolderClick(fi);
            }
        } else if (v == mAllAppsButton) {
            if (isAllAppsVisible()) {
                showWorkspace(true);
            } else {
                onClickAllAppsButton(v);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    /**
     * Event handler for the search button
     *
     * @param v The view that was clicked.
     */
    public void onClickSearchButton(View v) {

        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        onSearchRequested();
    }

    /**
     * Event handler for the voice button
     *
     * @param v The view that was clicked.
     */
    public void onClickVoiceButton(View v) {

        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        startVoice();
    }

    public void startVoice() {
        try {
            final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            ComponentName activityName = searchManager
                    .getGlobalSearchActivity();
            Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (activityName != null) {
                intent.setPackage(activityName.getPackageName());
            }
            startActivity(null, intent, "onClickVoiceButton");
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivitySafely(null, intent, "onClickVoiceButton");
        }
    }

    /**
     * Event handler for the "grid" button that appears on the home screen,
     * which enters all apps mode.
     *
     * @param v The view that was clicked.
     */
    public void onClickAllAppsButton(View v) {
        showAllApps(true, AppsCustomizePagedView.ContentType.Applications,
                false);
    }

    /**
     * Event handler for the (Add) Widgets button that appears after a long
     * press on the home screen.
     */
    protected void onClickAddWidgetButton() {
        showAllApps(true, AppsCustomizePagedView.ContentType.Widgets, true);
    }

    public void onTouchDownAllAppsButton(View v) {
        // Provide the same haptic feedback that the system offers for virtual
        // keys.
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    public void performHapticFeedbackOnTouchDown(View v) {
        // Provide the same haptic feedback that the system offers for virtual
        // keys.
        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    @SuppressLint("ClickableViewAccessibility")
    public View.OnTouchListener getHapticFeedbackTouchListener() {
        if (mHapticFeedbackTouchListener == null) {
            mHapticFeedbackTouchListener = (v, event) -> {
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                }
                return false;
            };
        }
        return mHapticFeedbackTouchListener;
    }

    /**
     * Called when the user stops interacting with the launcher. This implies
     * that the user is now on the homescreen and is not doing housekeeping.
     */
    protected void onInteractionEnd() {
    }

    /**
     * Called when the user starts interacting with the launcher. The possible
     * interactions are: - open all apps - reorder an app shortcut, or a widget
     * - open the overview mode. This is a good time to stop doing things that
     * only make sense when the user is on the homescreen and not doing
     * housekeeping.
     */
    protected void onInteractionBegin() {
    }

    void startApplicationDetailsActivity(ComponentName componentName) {
        String packageName = componentName.getPackageName();
        Intent intent = new Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivitySafely(null, intent, "startApplicationDetailsActivity");
    }

    // returns true if the activity was started
    boolean startApplicationUninstallActivity(ComponentName componentName,
                                              int flags) {
        if ((flags & AppInfo.DOWNLOADED_FLAG) == 0) {
            // System applications cannot be installed. For now, show a toast
            // explaining that.
            // We may give them the option of disabling apps this way.
            int messageId = R.string.uninstall_system_app_text;
            Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            String packageName = componentName.getPackageName();
            String className = componentName.getClassName();
            Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts(
                    "package", packageName, className));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
            return true;
        }
    }

    boolean startActivity(View v, Intent intent, Object tag) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            // Only launch using the new animation if the shortcut has not opted
            // out (this is a
            // private contract between launcher and may be ignored in the
            // future).
            boolean useLaunchAnimation = (v != null)
                    && !intent.hasExtra(INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION);
            if (useLaunchAnimation) {
                ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(v,
                        0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

                startActivity(intent, opts.toBundle());
            } else {
                startActivity(intent);
            }
            return true;
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found,
                    Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    boolean startActivitySafely(View v, Intent intent, Object tag) {
        boolean success = false;
        try {
            success = startActivity(v, intent, tag);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found,
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
        }
        return success;
    }

    private void handleFolderClick(FolderIcon folderIcon) {
        final FolderInfo info = folderIcon.getFolderInfo();
        Folder openFolder = mWorkspace.getFolderForTag(info);

        // If the folder info reports that the associated folder is open, then
        // verify that
        // it is actually opened. There have been a few instances where this
        // gets out of sync.
        if (info.opened && openFolder == null) {

            info.opened = false;
        }

        if (!info.opened && !folderIcon.getFolder().isDestroyed()) {
            // Close any open folder
            closeFolder();
            // Open the requested folder
            openFolder(folderIcon);
        } else {
            // Find the open folder...
            int folderScreen;
            if (openFolder != null) {
                folderScreen = mWorkspace.getPageForView(openFolder);
                // .. and close it
                closeFolder(openFolder);
                if (folderScreen != mWorkspace.getCurrentPage()) {
                    // Close any folder open on the current screen
                    closeFolder();
                    // Pull the folder onto this screen
                    openFolder(folderIcon);
                }
            }
        }
    }

    /**
     * This method draws the FolderIcon to an ImageView and then adds and
     * positions that ImageView in the DragLayer in the exact absolute location
     * of the original FolderIcon.
     */
    private void copyFolderIconToImage(FolderIcon fi) {
        final int width = fi.getMeasuredWidth();
        final int height = fi.getMeasuredHeight();

        // Lazy load ImageView, Bitmap and Canvas
        if (mFolderIconImageView == null) {
            mFolderIconImageView = new ImageView(this);
        }
        if (mFolderIconBitmap == null || mFolderIconBitmap.getWidth() != width
                || mFolderIconBitmap.getHeight() != height) {
            mFolderIconBitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            mFolderIconCanvas = new Canvas(mFolderIconBitmap);
        }

        DragLayer.LayoutParams lp;
        if (mFolderIconImageView.getLayoutParams() instanceof DragLayer.LayoutParams) {
            lp = (DragLayer.LayoutParams) mFolderIconImageView
                    .getLayoutParams();
        } else {
            lp = new DragLayer.LayoutParams(width, height);
        }

        // The layout from which the folder is being opened may be scaled,
        // adjust the starting
        // view size by this scale factor.
        float scale = mDragLayer.getDescendantRectRelativeToSelf(fi,
                mRectForFolderAnimation);
        lp.customPosition = true;
        lp.x = mRectForFolderAnimation.left;
        lp.y = mRectForFolderAnimation.top;
        lp.width = (int) (scale * width);
        lp.height = (int) (scale * height);

        mFolderIconCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        fi.draw(mFolderIconCanvas);
        mFolderIconImageView.setImageBitmap(mFolderIconBitmap);
        if (fi.getFolder() != null) {
            mFolderIconImageView.setPivotX(fi.getFolder()
                    .getPivotXForIconAnimation());
            mFolderIconImageView.setPivotY(fi.getFolder()
                    .getPivotYForIconAnimation());
        }
        // Just in case this image view is still in the drag layer from a
        // previous animation,
        // we remove it and re-add it.
        if (mDragLayer.indexOfChild(mFolderIconImageView) != -1) {
            mDragLayer.removeView(mFolderIconImageView);
        }
        mDragLayer.addView(mFolderIconImageView, lp);
        if (fi.getFolder() != null) {
            fi.getFolder().bringToFront();
        }
    }

    private void growAndFadeOutFolderIcon(FolderIcon fi) {
        if (fi == null)
            return;
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",
                1.5f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",
                1.5f);

        FolderInfo info = (FolderInfo) fi.getTag();
        if (info.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            CellLayout cl = (CellLayout) fi.getParent().getParent();
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) fi
                    .getLayoutParams();
            cl.setFolderLeaveBehindCell(lp.cellX, lp.cellY);
        }

        // Push an ImageView copy of the FolderIcon into the DragLayer and hide
        // the original
        copyFolderIconToImage(fi);
        fi.setVisibility(View.INVISIBLE);

        ObjectAnimator oa = LauncherAnimUtils.ofPropertyValuesHolder(
                mFolderIconImageView, alpha, scaleX, scaleY);
        oa.setDuration(getResources().getInteger(
                R.integer.config_folderAnimDuration));
        oa.start();
    }

    private void shrinkAndFadeInFolderIcon(final FolderIcon fi) {
        if (fi == null)
            return;
        PropertyValuesHolder alpha = PropertyValuesHolder
                .ofFloat("alpha", 1.0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX",
                1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY",
                1.0f);

        final CellLayout cl = (CellLayout) fi.getParent().getParent();

        // We remove and re-draw the FolderIcon in-case it has changed
        mDragLayer.removeView(mFolderIconImageView);
        copyFolderIconToImage(fi);
        ObjectAnimator oa = LauncherAnimUtils.ofPropertyValuesHolder(
                mFolderIconImageView, alpha, scaleX, scaleY);
        oa.setDuration(getResources().getInteger(
                R.integer.config_folderAnimDuration));
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (cl != null) {
                    cl.clearFolderLeaveBehind();
                    // Remove the ImageView copy of the FolderIcon and make the
                    // original visible.
                    mDragLayer.removeView(mFolderIconImageView);
                    fi.setVisibility(View.VISIBLE);
                }
            }
        });
        oa.start();
    }

    /**
     * Opens the user folder described by the specified tag. The opening of the
     * folder is animated relative to the specified View. If the View is null,
     * no animation is played.
     * folderInfo The FolderInfo describing the folder to open.
     */
    public void openFolder(FolderIcon folderIcon) {
        Folder folder = folderIcon.getFolder();
        FolderInfo info = folder.mInfo;

        if (info.hidden) {
            folder.startHiddenFolderManager();
            return;
        }

        info.opened = true;

        // Just verify that the folder hasn't already been added to the
        // DragLayer.
        // There was a one-off crash where the folder had a parent already.
        if (folder.getParent() == null) {
            mDragLayer.addView(folder);
            mDragController.addDropTarget((DropTarget) folder);
        }
        folder.animateOpen();
        growAndFadeOutFolderIcon(folderIcon);

        // Notify the accessibility manager that this folder "window" has
        // appeared and occluded
        // the workspace items
        folder.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
        getDragLayer().sendAccessibilityEvent(
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
    }

    public void closeFolder() {
        Folder folder = mWorkspace != null ? mWorkspace.getOpenFolder() : null;
        if (folder != null) {
            if (folder.isEditingName()) {
                folder.dismissEditingName();
            }
            closeFolder(folder);
        }
    }

    void closeFolder(Folder folder) {
        folder.getInfo().opened = false;

        ViewGroup parent = (ViewGroup) folder.getParent().getParent();
        if (parent != null) {
            FolderIcon fi = (FolderIcon) mWorkspace.getViewForTag(folder.mInfo);
            shrinkAndFadeInFolderIcon(fi);
        }
        folder.animateClosed();

        getDragLayer().sendAccessibilityEvent(
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    public boolean onLongClick(View v) {
        if (!isDraggingEnabled())
            return false;
        if (isWorkspaceLocked())
            return false;
        if (mState != State.WORKSPACE)
            return false;

        if (v instanceof Workspace) {
            if (!mWorkspace.isInOverviewMode()) {
                if (mWorkspace.enterOverviewMode()) {
                    mWorkspace.performHapticFeedback(
                            HapticFeedbackConstants.LONG_PRESS,
                            HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
                    return true;
                } else {
                    return false;
                }
            }
        }

        if (!(v instanceof CellLayout)) {
            v = (View) v.getParent().getParent();
        }

        resetAddInfo();
        CellLayout.CellInfo longClickCellInfo = (CellLayout.CellInfo) v
                .getTag();

        if (longClickCellInfo == null) {
            return true;
        }

        // The hotseat touch handling does not go through Workspace, and we
        // always allow long press
        // on hotseat items.
        final View itemUnderLongClick = longClickCellInfo.cell;
        final boolean inHotseat = isHotseatLayout(v);
        boolean allowLongPress = inHotseat || mWorkspace.allowLongPress();
        if (allowLongPress && !mDragController.isDragging()) {
            if (itemUnderLongClick == null) {
                // User long pressed on empty space
                mWorkspace.performHapticFeedback(
                        HapticFeedbackConstants.LONG_PRESS,
                        HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
                // Disabling reordering until we sort out some issues.
                if (mWorkspace.isInOverviewMode()) {
                    mWorkspace.startReordering(v);
                } else {
                    mWorkspace.enterOverviewMode();
                }
            } else {
                if (!(itemUnderLongClick instanceof Folder)) {
                    // User long pressed on an item
                    mWorkspace.startDrag(longClickCellInfo);
                }
            }
        }
        return true;
    }

    boolean isHotseatLayout(View layout) {
        return mHotseat != null && (layout instanceof CellLayout) && (layout == mHotseat.getLayout());
    }

    /**
     * Returns the CellLayout of the specified container at the specified
     * screen.
     */
    CellLayout getCellLayout(long container, long screenId) {
        if (container == LauncherSettings.Favorites.CONTAINER_HOTSEAT) {
            if (mHotseat != null) {
                return mHotseat.getLayout();
            } else {
                return null;
            }
        } else {
            return (CellLayout) mWorkspace.getScreenWithId(screenId);
        }
    }

    public AppsCustomizePagedView getAppsCustomizeContent() {
        return mAppsCustomizeContent;
    }

    public void updateOverviewPanel() {
        mOverviewSettingsPanel.update();
    }

    public boolean isAllAppsVisible() {
        return (mState == State.APPS_CUSTOMIZE)
                || (mOnResumeState == State.APPS_CUSTOMIZE);
    }

    /**
     * Helper method for the cameraZoomIn/cameraZoomOut animations
     *
     * @param view        The view being animated
     * @param scaleFactor The scale factor used for the zoom
     */
    private void setPivotsForZoom(View view, float scaleFactor) {
        view.setPivotX(view.getWidth() / 2.0f);
        view.setPivotY(view.getHeight() / 2.0f);
    }

    private void setWorkspaceBackground(boolean workspace) {
        mLauncherView.setBackground(workspace ? mWorkspaceBackgroundDrawable
                : null);
    }


    private void dispatchOnLauncherTransitionPrepare(View v, boolean animated,
                                                     boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionPrepare(this,
                    animated, toWorkspace);
        }
    }

    private void dispatchOnLauncherTransitionStart(View v, boolean animated,
                                                   boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionStart(this,
                    animated, toWorkspace);
        }

        // Update the workspace transition step as well
        dispatchOnLauncherTransitionStep(v, 0f);
    }

    private void dispatchOnLauncherTransitionStep(View v, float t) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionStep(this, t);
        }
    }

    private void dispatchOnLauncherTransitionEnd(View v, boolean animated,
                                                 boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionEnd(this,
                    animated, toWorkspace);
        }

        // Update the workspace transition step as well
        dispatchOnLauncherTransitionStep(v, 1f);
    }


    /**
     * Zoom the camera out from the workspace to reveal 'toView'. Assumes that
     * the view to show is anchored at either the very top or very bottom of the
     * screen.
     */
    private void showAppsCustomizeHelper(final boolean animated, final boolean springLoaded) {
        AppsCustomizePagedView.ContentType contentType = mAppsCustomizeContent.getContentType();
        showAppsCustomizeHelper(animated, springLoaded, contentType);
    }

    private void showAppsCustomizeHelper(
            final boolean animated,
            final boolean springLoaded,
            final AppsCustomizePagedView.ContentType contentType) {
        if (mStateAnimation != null) {
            mStateAnimation.setDuration(0);
            mStateAnimation.cancel();
            mStateAnimation = null;
        }
        final Resources res = getResources();

        final int duration = res
                .getInteger(R.integer.config_appsCustomizeZoomInTime);
        final int fadeDuration = res
                .getInteger(R.integer.config_appsCustomizeFadeInTime);
        final float scale = (float) res
                .getInteger(R.integer.config_appsCustomizeZoomScaleFactor);
        final View fromView = mWorkspace;
        final AppsCustomizeLayout toView = mAppsCustomizeLayout;
        final int startDelay = res
                .getInteger(R.integer.config_workspaceAppsCustomizeAnimationStagger);

        setPivotsForZoom(toView, scale);

        // Shrink workspaces away if going to AppsCustomize from workspace
        Animator workspaceAnim = mWorkspace.getChangeStateAnimation(
                Workspace.State.SMALL, animated);
        if (!LauncherAppState.isDisableAllApps()
                || contentType == AppsCustomizePagedView.ContentType.Widgets) {
            // Set the content type for the all apps/widgets space
            mAppsCustomizeContent.setContentType(contentType);
        }

        if (animated) {
            toView.setScaleX(scale);
            toView.setScaleY(scale);
            final LauncherViewPropertyAnimator scaleAnim = new LauncherViewPropertyAnimator(
                    toView);
            scaleAnim.scaleX(1f).scaleY(1f).setDuration(duration)
                    .setInterpolator(new Workspace.ZoomOutInterpolator());

            toView.setVisibility(View.VISIBLE);
            toView.setAlpha(0f);
            final ObjectAnimator alphaAnim = LauncherAnimUtils.ofFloat(toView,
                    "alpha", 0f, 1f).setDuration(fadeDuration);
            alphaAnim.setInterpolator(new DecelerateInterpolator(1.5f));
            alphaAnim.addUpdateListener(animation -> {
                if (animation == null) {
                    throw new RuntimeException("animation is null");
                }
                float t = (Float) animation.getAnimatedValue();
                dispatchOnLauncherTransitionStep(fromView, t);
                dispatchOnLauncherTransitionStep(toView, t);
            });

            // toView should appear right at the end of the workspace shrink
            // animation
            mStateAnimation = LauncherAnimUtils.createAnimatorSet();
            mStateAnimation.play(scaleAnim).after(startDelay);
            mStateAnimation.play(alphaAnim).after(startDelay);

            mStateAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    // Prepare the position
                    toView.setTranslationX(0.0f);
                    toView.setTranslationY(0.0f);
                    toView.setVisibility(View.VISIBLE);
                    toView.bringToFront();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    dispatchOnLauncherTransitionEnd(fromView, animated, false);
                    dispatchOnLauncherTransitionEnd(toView, animated, false);

                    // Hide the search bar
                    if (mSearchDropTargetBar != null) {
                        mSearchDropTargetBar.hideSearchBar(false);
                    }
                }
            });

            if (workspaceAnim != null) {
                mStateAnimation.play(workspaceAnim);
            }

            boolean delayAnim = false;

            dispatchOnLauncherTransitionPrepare(fromView, animated, false);
            dispatchOnLauncherTransitionPrepare(toView, animated, false);

            // If any of the objects being animated haven't been measured/laid
            // out
            // yet, delay the animation until we get a layout pass
            if ((((LauncherTransitionable) toView).getContent()
                    .getMeasuredWidth() == 0)
                    || (mWorkspace.getMeasuredWidth() == 0)
                    || (toView.getMeasuredWidth() == 0)) {
                delayAnim = true;
            }

            final AnimatorSet stateAnimation = mStateAnimation;
            final Runnable startAnimRunnable = () -> {
                // Check that mStateAnimation hasn't changed while
                // we waited for a layout/draw pass
                if (mStateAnimation != stateAnimation)
                    return;
                setPivotsForZoom(toView, scale);
                dispatchOnLauncherTransitionStart(fromView, animated, false);
                dispatchOnLauncherTransitionStart(toView, animated, false);
                LauncherAnimUtils.startAnimationAfterNextDraw(
                        mStateAnimation, toView);
            };
            if (delayAnim) {
                final ViewTreeObserver observer = toView.getViewTreeObserver();
                observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        startAnimRunnable.run();
                        toView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
            } else {
                startAnimRunnable.run();
            }
        } else {
            toView.setTranslationX(0.0f);
            toView.setTranslationY(0.0f);
            toView.setScaleX(1.0f);
            toView.setScaleY(1.0f);
            toView.setVisibility(View.VISIBLE);
            toView.bringToFront();

            if (!springLoaded
                    && !LauncherAppState.getInstance().isScreenLarge()) {
                // Hide the search bar
                if (mSearchDropTargetBar != null) {
                    mSearchDropTargetBar.hideSearchBar(false);
                }
            }
            dispatchOnLauncherTransitionPrepare(fromView, animated, false);
            dispatchOnLauncherTransitionStart(fromView, animated, false);
            dispatchOnLauncherTransitionEnd(fromView, animated, false);
            dispatchOnLauncherTransitionPrepare(toView, animated, false);
            dispatchOnLauncherTransitionStart(toView, animated, false);
            dispatchOnLauncherTransitionEnd(toView, animated, false);
        }
    }

    /**
     * Zoom the camera back into the workspace, hiding 'fromView'. This is the
     * opposite of showAppsCustomizeHelper.
     *
     * @param animated If true, the transition will be animated.
     */
    private void hideAppsCustomizeHelper(
            Workspace.State toState,
            final boolean animated,
            final boolean springLoaded,
            final Runnable onCompleteRunnable) {

        if (mStateAnimation != null) {
            mStateAnimation.setDuration(0);
            mStateAnimation.cancel();
            mStateAnimation = null;
        }
        Resources res = getResources();

        final int duration = res
                .getInteger(R.integer.config_appsCustomizeZoomOutTime);
        final int fadeOutDuration = res
                .getInteger(R.integer.config_appsCustomizeFadeOutTime);
        final float scaleFactor = (float) res
                .getInteger(R.integer.config_appsCustomizeZoomScaleFactor);
        final View fromView = mAppsCustomizeLayout;
        final View toView = mWorkspace;
        Animator workspaceAnim = null;
        if (toState == Workspace.State.NORMAL) {
            int stagger = res
                    .getInteger(R.integer.config_appsCustomizeWorkspaceAnimationStagger);
            workspaceAnim = mWorkspace.getChangeStateAnimation(toState,
                    animated, stagger, -1);
        } else if (toState == Workspace.State.SPRING_LOADED
                || toState == Workspace.State.OVERVIEW) {
            workspaceAnim = mWorkspace.getChangeStateAnimation(toState,
                    animated);
        }

        setPivotsForZoom(fromView, scaleFactor);
        showHotseat(animated);
        if (animated) {
            final LauncherViewPropertyAnimator scaleAnim = new LauncherViewPropertyAnimator(
                    fromView);
            scaleAnim.scaleX(scaleFactor).scaleY(scaleFactor)
                    .setDuration(duration)
                    .setInterpolator(new Workspace.ZoomInInterpolator());

            final ObjectAnimator alphaAnim = LauncherAnimUtils.ofFloat(fromView, "alpha", 1f, 0f).setDuration(fadeOutDuration);
            alphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            alphaAnim.addUpdateListener(animation -> {
                float t = 1f - (Float) animation.getAnimatedValue();
                dispatchOnLauncherTransitionStep(fromView, t);
                dispatchOnLauncherTransitionStep(toView, t);
            });

            mStateAnimation = LauncherAnimUtils.createAnimatorSet();

            dispatchOnLauncherTransitionPrepare(fromView, animated, true);
            dispatchOnLauncherTransitionPrepare(toView, animated, true);
            mAppsCustomizeContent.stopScrolling();

            mStateAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    fromView.setVisibility(View.GONE);
                    dispatchOnLauncherTransitionEnd(fromView, animated, true);
                    dispatchOnLauncherTransitionEnd(toView, animated, true);
                    if (onCompleteRunnable != null) {
                        onCompleteRunnable.run();
                    }
                    mAppsCustomizeContent.updateCurrentPageScroll();
                }
            });

            mStateAnimation.playTogether(scaleAnim, alphaAnim);
            if (workspaceAnim != null) {
                mStateAnimation.play(workspaceAnim);
            }
            dispatchOnLauncherTransitionStart(fromView, animated, true);
            dispatchOnLauncherTransitionStart(toView, animated, true);
            LauncherAnimUtils.startAnimationAfterNextDraw(mStateAnimation,
                    toView);
        } else {
            fromView.setVisibility(View.GONE);
            dispatchOnLauncherTransitionPrepare(fromView, animated, true);
            dispatchOnLauncherTransitionStart(fromView, animated, true);
            dispatchOnLauncherTransitionEnd(fromView, animated, true);
            dispatchOnLauncherTransitionPrepare(toView, animated, true);
            dispatchOnLauncherTransitionStart(toView, animated, true);
            dispatchOnLauncherTransitionEnd(toView, animated, true);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            mAppsCustomizeLayout.onTrimMemory();
        }
    }

    protected void showWorkspace(boolean animated) {
        showWorkspace(animated, null);
    }

    protected void showWorkspace() {
        showWorkspace(true);
    }

    void showWorkspace(boolean animated, Runnable onCompleteRunnable) {
        if (mWorkspace.isInOverviewMode()) {
            mWorkspace.exitOverviewMode(animated);
        }
        if (mState != State.WORKSPACE) {
            boolean wasInSpringLoadedMode = (mState != State.WORKSPACE);
            mWorkspace.setVisibility(View.VISIBLE);
            hideAppsCustomizeHelper(Workspace.State.NORMAL, animated, false,
                    onCompleteRunnable);

            // Show the search bar (only animate if we were showing the drop
            // target bar in spring
            // loaded mode)
            if (mSearchDropTargetBar != null) {
                mSearchDropTargetBar.showSearchBar(animated && wasInSpringLoadedMode);
            }

            // Set focus to the AppsCustomize button
            if (mAllAppsButton != null) {
                mAllAppsButton.requestFocus();
            }
        }

        // Change the state *after* we've called all the transition code
        mState = State.WORKSPACE;

        // Resume the auto-advance of widgets
        mUserPresent = true;
        updateRunning();

        // Send an accessibility event to announce the context change
        getWindow().getDecorView().sendAccessibilityEvent(
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);

        onWorkspaceShown(animated);
    }

    void showOverviewMode(boolean animated) {
        mWorkspace.setVisibility(View.VISIBLE);
        hideAppsCustomizeHelper(Workspace.State.OVERVIEW, animated, false, null);
        mState = State.WORKSPACE;
        onWorkspaceShown(animated);
    }

    public void onWorkspaceShown(boolean animated) {
    }

    public void showAllApps(boolean animated,
                            AppsCustomizePagedView.ContentType contentType,
                            boolean resetPageToZero) {
        if (mAppsCustomizeContent.isInOverviewMode()) {
            mAppsCustomizeContent.exitOverviewMode(false);
        }

        if (mState != State.WORKSPACE)
            return;

        if (resetPageToZero) {
            mAppsCustomizeLayout.reset();
        }
        showAppsCustomizeHelper(animated, false, contentType);
        mAppsCustomizeLayout.requestFocus();

        // Change the state *after* we've called all the transition code
        mState = State.APPS_CUSTOMIZE;

        // Pause the auto-advance of widgets until we are out of AllApps
        mUserPresent = false;
        updateRunning();
        closeFolder();

        // Send an accessibility event to announce the context change
        getWindow().getDecorView().sendAccessibilityEvent(
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    void enterSpringLoadedDragMode() {
        if (isAllAppsVisible()) {
            hideAppsCustomizeHelper(Workspace.State.SPRING_LOADED, true, true,
                    null);
            mState = State.APPS_CUSTOMIZE_SPRING_LOADED;
        }
    }

    void exitSpringLoadedDragModeDelayed(
            final boolean successfulDrop,
            int delay,
            final Runnable onCompleteRunnable) {
        if (mState != State.APPS_CUSTOMIZE_SPRING_LOADED)
            return;

        mHandler.postDelayed(() -> {
            if (successfulDrop) {
                mAppsCustomizeLayout.setVisibility(View.GONE);
                showWorkspace(true, onCompleteRunnable);
            } else {
                exitSpringLoadedDragMode();
            }
        }, delay);

    }

    void exitSpringLoadedDragMode() {
        if (mState == State.APPS_CUSTOMIZE_SPRING_LOADED) {
            final boolean animated = true;
            final boolean springLoaded = true;
            showAppsCustomizeHelper(animated, springLoaded);
            mState = State.APPS_CUSTOMIZE;
        }

    }

    void lockAllApps() {

    }

    void unlockAllApps() {

    }

    /**
     * Shows the hotseat area.
     */
    void showHotseat(boolean animated) {
        if (!LauncherAppState.getInstance().isScreenLarge()) {
            if (animated) {
                if (mHotseat.getAlpha() != 1f) {
                    int duration = 0;
                    if (mSearchDropTargetBar != null) {
                        duration = mSearchDropTargetBar
                                .getTransitionInDuration();
                    }
                    mHotseat.animate().alpha(1f).setDuration(duration);
                }
            } else {
                mHotseat.setAlpha(1f);
            }
        }
    }

    /**
     * Hides the hotseat area.
     */
    void hideHotseat(boolean animated) {
        if (!LauncherAppState.getInstance().isScreenLarge()) {
            if (animated) {
                if (mHotseat.getAlpha() != 0f) {
                    int duration = 0;
                    if (mSearchDropTargetBar != null) {
                        duration = mSearchDropTargetBar
                                .getTransitionOutDuration();
                    }
                    mHotseat.animate().alpha(0f).setDuration(duration);
                }
            } else {
                mHotseat.setAlpha(0f);
            }
        }
    }

    /**
     * Add an item from all apps or customize onto the given workspace screen.
     * If layout is null, add to the current screen.
     */
    void addExternalItemToScreen(ItemInfo itemInfo, final CellLayout layout) {
        if (!mWorkspace.addExternalItemToScreen(itemInfo, layout)) {
            showOutOfSpaceMessage(isHotseatLayout(layout));
        }
    }

    /**
     * Maps the current orientation to an index for referencing orientation
     * correct global icons
     */
    private int getCurrentOrientationIndexForGlobalIcons() {
        // default - 0, landscape - 1
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 1;
        }
        return 0;
    }

    private Drawable getExternalPackageToolbarIcon(ComponentName activityName,
                                                   String resourceName) {
        try {
            PackageManager packageManager = getPackageManager();
            // Look for the toolbar icon specified in the activity meta-data
            Bundle metaData = packageManager.getActivityInfo(activityName,
                    PackageManager.GET_META_DATA).metaData;
            if (metaData != null) {
                int iconResId = metaData.getInt(resourceName);
                if (iconResId != 0) {
                    Resources res = packageManager
                            .getResourcesForActivity(activityName);
                    return res.getDrawable(iconResId);
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException nfe) {
            nfe.printStackTrace();
        }
        return null;
    }

    // if successful in getting icon, return it; otherwise, set button to use
    // default drawable
    private Drawable.ConstantState updateButtonWithIconFromExternalActivity(
            int buttonId, ComponentName activityName, int fallbackDrawableId,
            String toolbarResourceName) {
        ImageView button = (ImageView) findViewById(buttonId);
        Drawable toolbarIcon = getExternalPackageToolbarIcon(activityName,
                toolbarResourceName);

        if (button != null) {
            // If we were unable to find the icon via the meta-data, use a
            // generic one
            if (toolbarIcon == null) {
                button.setImageResource(fallbackDrawableId);
            } else {
                button.setImageDrawable(toolbarIcon);
            }
        }

        return toolbarIcon != null ? toolbarIcon.getConstantState() : null;

    }

    private void updateButtonWithDrawable(int buttonId, Drawable.ConstantState d) {
        ImageView button = (ImageView) findViewById(buttonId);
        button.setImageDrawable(d.newDrawable(getResources()));
    }

    private void invalidatePressedFocusedStates(View container, View button) {
        if (container instanceof HolographicLinearLayout) {
            HolographicLinearLayout layout = (HolographicLinearLayout) container;
            layout.invalidatePressedFocusedStates();
        } else if (button instanceof HolographicImageView) {
            HolographicImageView view = (HolographicImageView) button;
            view.invalidatePressedFocusedStates();
        }
    }

    public View getQsbBar() {
        if (mQsb == null) {
            mQsb = mInflater.inflate(R.layout.qsb, mSearchDropTargetBar, false);
            mSearchDropTargetBar.addView(mQsb);
        }
        return mQsb;
    }

    protected boolean updateGlobalSearchIcon() {
        final View searchButtonContainer = findViewById(R.id.search_button_container);
        final ImageView searchButton = (ImageView) findViewById(R.id.ivSearchButton);
        final View voiceButtonContainer = findViewById(R.id.voice_button_container);
        final View voiceButton = findViewById(R.id.ivVoiceButton);

        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName activityName = searchManager.getGlobalSearchActivity();
        if (activityName != null) {
            int coi = getCurrentOrientationIndexForGlobalIcons();
            sGlobalSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
                    R.id.ivSearchButton, activityName,
                    R.drawable.ic_home_search_normal_holo,
                    TOOLBAR_SEARCH_ICON_METADATA_NAME);
            if (sGlobalSearchIcon[coi] == null) {
                sGlobalSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
                        R.id.ivSearchButton, activityName,
                        R.drawable.ic_home_search_normal_holo,
                        TOOLBAR_ICON_METADATA_NAME);
            }

            if (searchButtonContainer != null)
                searchButtonContainer.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.VISIBLE);
            invalidatePressedFocusedStates(searchButtonContainer, searchButton);
            return true;
        } else {
            // We disable both search and voice search when there is no global
            // search provider
            if (searchButtonContainer != null)
                searchButtonContainer.setVisibility(View.GONE);
            if (voiceButtonContainer != null)
                voiceButtonContainer.setVisibility(View.GONE);
            if (searchButton != null)
                searchButton.setVisibility(View.GONE);
            if (voiceButton != null)
                voiceButton.setVisibility(View.GONE);
            updateVoiceButtonProxyVisible(false);
            return false;
        }
    }

    protected void updateGlobalSearchIcon(Drawable.ConstantState d) {
        final View searchButtonContainer = findViewById(R.id.search_button_container);
        final View searchButton = (ImageView) findViewById(R.id.ivSearchButton);
        updateButtonWithDrawable(R.id.ivSearchButton, d);
        invalidatePressedFocusedStates(searchButtonContainer, searchButton);
    }

    protected boolean updateVoiceSearchIcon(boolean searchVisible) {
        final View voiceButtonContainer = findViewById(R.id.voice_button_container);
        final View voiceButton = findViewById(R.id.ivVoiceButton);

        // We only show/update the voice search icon if the search icon is
        // enabled as well
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ComponentName globalSearchActivity = searchManager
                .getGlobalSearchActivity();

        ComponentName activityName = null;
        if (globalSearchActivity != null) {
            // Check if the global search activity handles voice search
            Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
            intent.setPackage(globalSearchActivity.getPackageName());
            activityName = intent.resolveActivity(getPackageManager());
        }

        if (activityName == null) {
            // Fallback: check if an activity other than the global search
            // activity
            // resolves this
            Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
            activityName = intent.resolveActivity(getPackageManager());
        }
        if (searchVisible && activityName != null) {
            int coi = getCurrentOrientationIndexForGlobalIcons();
            sVoiceSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
                    R.id.ivVoiceButton, activityName,
                    R.drawable.ic_home_voice_search_holo,
                    TOOLBAR_VOICE_SEARCH_ICON_METADATA_NAME);
            if (sVoiceSearchIcon[coi] == null) {
                sVoiceSearchIcon[coi] = updateButtonWithIconFromExternalActivity(
                        R.id.ivVoiceButton, activityName,
                        R.drawable.ic_home_voice_search_holo,
                        TOOLBAR_ICON_METADATA_NAME);
            }
            if (voiceButtonContainer != null)
                voiceButtonContainer.setVisibility(View.VISIBLE);
            voiceButton.setVisibility(View.VISIBLE);
            updateVoiceButtonProxyVisible(false);
            invalidatePressedFocusedStates(voiceButtonContainer, voiceButton);
            return true;
        } else {
            if (voiceButtonContainer != null)
                voiceButtonContainer.setVisibility(View.GONE);
            if (voiceButton != null)
                voiceButton.setVisibility(View.GONE);
            updateVoiceButtonProxyVisible(false);
            return false;
        }
    }

    protected void updateVoiceSearchIcon(Drawable.ConstantState d) {
        final View voiceButtonContainer = findViewById(R.id.voice_button_container);
        final View voiceButton = findViewById(R.id.ivVoiceButton);
        updateButtonWithDrawable(R.id.ivVoiceButton, d);
        invalidatePressedFocusedStates(voiceButtonContainer, voiceButton);
    }

    public void updateVoiceButtonProxyVisible(
            boolean forceDisableVoiceButtonProxy) {
        final View voiceButtonProxy = findViewById(R.id.voice_button_proxy);
        if (voiceButtonProxy != null) {
            boolean visible = !forceDisableVoiceButtonProxy
                    && mWorkspace.shouldVoiceButtonProxyBeVisible();
            voiceButtonProxy.setVisibility(visible ? View.VISIBLE : View.GONE);
            voiceButtonProxy.bringToFront();
        }
    }

    /**
     * This is an overrid eot disable the voice button proxy. If disabled is
     * true, then the voice button proxy will be hidden regardless of what
     * shouldVoiceButtonProxyBeVisible() returns.
     */
    public void disableVoiceButtonProxy(boolean disabled) {
        updateVoiceButtonProxyVisible(disabled);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        final boolean result = super.dispatchPopulateAccessibilityEvent(event);
        final List<CharSequence> text = event.getText();
        text.clear();
        // Populate event with a fake title based on the current state.
        if (mState != State.APPS_CUSTOMIZE) {
            text.add(getString(R.string.all_apps_button_label));
        } else {
            text.add(getString(R.string.all_apps_home_button_label));
        }
        return result;
    }

    /**
     * Receives notifications when system dialogs are to be closed.
     */
    private class CloseSystemDialogsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            closeSystemDialogs();
        }
    }

    /**
     * Receives notifications whenever the appwidgets are reset.
     */
    private class AppWidgetResetObserver extends ContentObserver {
        public AppWidgetResetObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            onAppWidgetReset();
        }
    }

    /**
     * If the activity is currently paused, signal that we need to run the
     * passed Runnable in onResume.
     * <p>
     * This needs to be called from incoming places where resources might have
     * been loaded while we are paused. That is becaues the Configuration might
     * be wrong when we're not running, and if it comes back to what it was when
     * we were paused, we are not restarted.
     * <p>
     * Implementation of the method from LauncherModel.Callbacks.
     *
     * @return true if we are currently paused. The caller might be able to skip
     * some work in that case since we will come back again.
     */
    private boolean waitUntilResume(Runnable run, boolean deletePreviousRunnables) {
        if (mPaused) {

            if (deletePreviousRunnables) {
                while (mBindOnResumeCallbacks.remove(run)) {
                }
            }
            mBindOnResumeCallbacks.add(run);
            return true;
        } else {
            return false;
        }
    }

    private boolean waitUntilResume(Runnable run) {
        return waitUntilResume(run, false);
    }

    public void addOnResumeCallback(Runnable run) {
        mOnResumeCallbacks.add(run);
    }

    /**
     * If the activity is currently paused, signal that we need to re-run the
     * loader in onResume.
     * <p>
     * This needs to be called from incoming places where resources might have
     * been loaded while we are paused. That is becaues the Configuration might
     * be wrong when we're not running, and if it comes back to what it was when
     * we were paused, we are not restarted.
     * <p>
     * Implementation of the method from LauncherModel.Callbacks.
     *
     * @return true if we are currently paused. The caller might be able to skip
     * some work in that case since we will come back again.
     */
    public boolean setLoadOnResume() {
        if (mPaused) {

            mOnResumeNeedsLoad = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public int getCurrentWorkspaceScreen() {
        if (mWorkspace != null) {
            return mWorkspace.getCurrentPage();
        } else {
            return SCREEN_COUNT / 2;
        }
    }

    /**
     * Refreshes the shortcuts shown on the workspace.
     * <p>
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void startBinding() {

        mBindOnResumeCallbacks.clear();

        // Clear the workspace because it's going to be rebound
        mWorkspace.clearDropTargets();
        mWorkspace.removeAllWorkspaceScreens();

        mWidgetsToAdvance.clear();
        if (mHotseat != null) {
            mHotseat.resetLayout();
        }
    }

    @Override
    public void bindScreens(ArrayList<Long> orderedScreenIds) {
        bindAddScreens(orderedScreenIds);

        // If there are no screens, we need to have an empty screen
        if (orderedScreenIds.size() == 0) {
            mWorkspace.addExtraEmptyScreen();
        }

        // Create the custom content page (this call updates mDefaultScreen
        // which calls
        // setCurrentPage() so ensure that all pages are added before calling
        // this).
        if (hasCustomContentToLeft()) {
            mWorkspace.createCustomContentContainer();
            populateCustomContentContainer();
        }
    }

    @Override
    public void bindAddScreens(ArrayList<Long> orderedScreenIds) {

        int count = orderedScreenIds.size();
        for (int i = 0; i < count; i++) {
            mWorkspace
                    .insertNewWorkspaceScreenBeforeEmptyScreen(orderedScreenIds
                            .get(i));
        }
    }

    public void bindAppsAdded(
            final ArrayList<Long> newScreens,
            final ArrayList<ItemInfo> addNotAnimated,
            final ArrayList<ItemInfo> addAnimated,
            final ArrayList<AppInfo> addedApps) {
        Runnable r = () -> bindAppsAdded(newScreens, addNotAnimated, addAnimated,
                addedApps);
        if (waitUntilResume(r)) {
            return;
        }

        // Add the new screens
        if (newScreens != null) {
            bindAddScreens(newScreens);
        }

        // We add the items without animation on non-visible pages, and with
        // animations on the new page (which we will try and snap to).
        if (addNotAnimated != null && !addNotAnimated.isEmpty()) {
            bindItems(addNotAnimated, 0, addNotAnimated.size(), false);
        }
        if (addAnimated != null && !addAnimated.isEmpty()) {
            bindItems(addAnimated, 0, addAnimated.size(), true);
        }

        // Remove the extra empty screen
        mWorkspace.removeExtraEmptyScreen(false, null);

        if (!LauncherAppState.isDisableAllApps() && addedApps != null
                && mAppsCustomizeContent != null) {
            mAppsCustomizeContent.addApps(addedApps);
        }
    }

    /**
     * Bind the items start-end from the list.
     * <p>
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindItems(ArrayList<ItemInfo> shortcuts, int start, int end,
                          final boolean forceAnimateIcons) {
        final ArrayList<ItemInfo> items = shortcuts;
        final int s = start;
        final int e = end;
        Runnable r = () -> bindItems(items, s, e, forceAnimateIcons);
        if (waitUntilResume(r)) {
            return;
        }

        // Get the list of added shortcuts and intersect them with the set of
        // shortcuts here
        final AnimatorSet anim = LauncherAnimUtils.createAnimatorSet();
        final Collection<Animator> bounceAnims = new ArrayList<>();
        final boolean animateIcons = forceAnimateIcons && canRunNewAppsAnimation();
        Workspace workspace = mWorkspace;
        long newShortcutsScreenId = -1;
        for (int i = start; i < end; i++) {
            final ItemInfo item = shortcuts.get(i);

            // Short circuit if we are loading dock items for a configuration
            // which has no dock
            if (item.container == LauncherSettings.Favorites.CONTAINER_HOTSEAT
                    && mHotseat == null) {
                continue;
            }

            switch (item.itemType) {
                case LauncherSettings.Favorites.ITEM_TYPE_APPLICATION:
                case LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT:
                case LauncherSettings.Favorites.ITEM_TYPE_ALLAPPS:
                    ShortcutInfo info = (ShortcutInfo) item;
                    View shortcut = createShortcut(info);

                    /*
                     * TODO: FIX collision case
                     */
                    if (item.container == LauncherSettings.Favorites.CONTAINER_DESKTOP) {
                        CellLayout cl = mWorkspace.getScreenWithId(item.screenId);
                        if (cl != null && cl.isOccupied(item.cellX, item.cellY)) {
                            throw new RuntimeException("OCCUPIED");
                        }
                    }

                    workspace.addInScreenFromBind(shortcut, item.container,
                            item.screenId, item.cellX, item.cellY, 1, 1);
                    if (animateIcons) {
                        // Animate all the applications up now
                        shortcut.setAlpha(0f);
                        shortcut.setScaleX(0f);
                        shortcut.setScaleY(0f);
                        bounceAnims.add(createNewAppBounceAnimation(shortcut, i));
                        newShortcutsScreenId = item.screenId;
                    }
                    break;
                case LauncherSettings.Favorites.ITEM_TYPE_FOLDER:
                    FolderIcon newFolder = FolderIcon.fromXml(R.layout.v_folder_icon,
                            this, (ViewGroup) workspace.getChildAt(workspace
                                    .getCurrentPage()), (FolderInfo) item,
                            mIconCache);
                    newFolder.setTextVisible(!mHideIconLabels);
                    workspace.addInScreenFromBind(newFolder, item.container,
                            item.screenId, item.cellX, item.cellY, 1, 1);
                    break;
                default:
                    throw new RuntimeException("Invalid Item Type");
            }
        }

        if (animateIcons) {
            // Animate to the correct page
            if (newShortcutsScreenId > -1) {
                long currentScreenId = mWorkspace
                        .getScreenIdForPageIndex(mWorkspace.getNextPage());
                final int newScreenIndex = mWorkspace
                        .getPageIndexForScreenId(newShortcutsScreenId);
                final Runnable startBounceAnimRunnable = () -> {
                    anim.playTogether(bounceAnims);
                    anim.start();
                };
                if (newShortcutsScreenId != currentScreenId) {
                    // We post the animation slightly delayed to prevent
                    // slowdowns
                    // when we are loading right after we return to launcher.
                    mWorkspace.postDelayed(() -> {
                        if (mWorkspace != null) {
                            mWorkspace.snapToPage(newScreenIndex);
                            mWorkspace.postDelayed(startBounceAnimRunnable,
                                    NEW_APPS_ANIMATION_DELAY);
                        }
                    }, NEW_APPS_PAGE_MOVE_DELAY);
                } else {
                    mWorkspace.postDelayed(startBounceAnimRunnable,
                            NEW_APPS_ANIMATION_DELAY);
                }
            }
        }
        workspace.requestLayout();
    }

    /**
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindFolders(final HashMap<Long, FolderInfo> folders) {
        Runnable r = () -> bindFolders(folders);
        if (waitUntilResume(r)) {
            return;
        }
        sFolders.clear();
        sFolders.putAll(folders);
    }

    /**
     * Add the views for a widget to the workspace.
     * <p>
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppWidget(final LauncherAppWidgetInfo item) {
        Runnable r = () -> bindAppWidget(item);
        if (waitUntilResume(r)) {
            return;
        }

        final Workspace workspace = mWorkspace;

        final int appWidgetId = item.appWidgetId;
        final AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        item.hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);

        item.hostView.setTag(item);
        item.onBindAppWidget(this);

        workspace.addInScreen(item.hostView, item.container, item.screenId,
                item.cellX, item.cellY, item.spanX, item.spanY, false);
        addWidgetToAutoAdvanceIfNeeded(item.hostView, appWidgetInfo);

        workspace.requestLayout();


    }

    public void onPageBoundSynchronously(int page) {
        mSynchronouslyBoundPages.add(page);
    }

    /**
     * Callback saying that there aren't any more items to bind.
     * <p>
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void finishBindingItems(final boolean upgradePath) {
        Runnable r = () -> finishBindingItems(upgradePath);
        if (waitUntilResume(r)) {
            return;
        }
        if (mSavedState != null) {
            if (!mWorkspace.hasFocus()) {
                mWorkspace.getChildAt(mWorkspace.getCurrentPage()).requestFocus();
            }
            mSavedState = null;
        }

        mWorkspace.restoreInstanceStateForRemainingPages();

        // If we received the result of any pending adds while the loader was
        // running (e.g. the
        // widget configuration forced an orientation change), process them now.
        for (int i = 0; i < sPendingAddList.size(); i++) {
            completeAdd(sPendingAddList.get(i));
        }
        sPendingAddList.clear();

        mWorkspaceLoading = false;
        if (upgradePath) {
            mWorkspace.getUniqueComponents(true, null);
            mIntentsOnWorkspaceFromUpgradePath = mWorkspace
                    .getUniqueComponents(true, null);
        }
    }

    private boolean canRunNewAppsAnimation() {
        long diff = System.currentTimeMillis()
                - mDragController.getLastGestureUpTime();
        return diff > (NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS * 1000L);
    }

    private ValueAnimator createNewAppBounceAnimation(View v, int i) {
        ValueAnimator bounceAnim = LauncherAnimUtils.ofPropertyValuesHolder(v,
                PropertyValuesHolder.ofFloat("alpha", 1f),
                PropertyValuesHolder.ofFloat("scaleX", 1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f));
        bounceAnim.setDuration(InstallShortcutReceiver.NEW_SHORTCUT_BOUNCE_DURATION);
        bounceAnim.setStartDelay((long) i * InstallShortcutReceiver.NEW_SHORTCUT_STAGGER_DELAY);
        bounceAnim.setInterpolator(new SmoothPagedView.OvershootInterpolator());
        return bounceAnim;
    }

    public boolean useVerticalBarLayout() {
        return LauncherAppState.getInstance().getDynamicGrid()
                .getDeviceProfile().isVerticalBarLayout();
    }

    protected Rect getSearchBarBounds() {
        return LauncherAppState.getInstance().getDynamicGrid()
                .getDeviceProfile().getSearchBarBounds();
    }

    @Override
    public void bindSearchablesChanged() {
        boolean searchVisible = updateGlobalSearchIcon();
        boolean voiceVisible = updateVoiceSearchIcon(searchVisible);
        if (mSearchDropTargetBar != null) {
            mSearchDropTargetBar.onSearchPackagesChanged(searchVisible,
                    voiceVisible);
        }
    }

    /**
     * Add the icons for all apps.
     * <p>
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAllApplications(final ArrayList<AppInfo> apps) {
        if (LauncherAppState.isDisableAllApps()) {
            if (mIntentsOnWorkspaceFromUpgradePath != null) {
                if (LauncherModel.UPGRADE_USE_MORE_APPS_FOLDER) {
                    getHotseat().addAllAppsFolder(mIconCache, apps,
                            mIntentsOnWorkspaceFromUpgradePath, Launcher.this,
                            mWorkspace);
                }
                mIntentsOnWorkspaceFromUpgradePath = null;
            }
            if (mAppsCustomizeContent != null) {
                mAppsCustomizeContent.onPackagesUpdated(LauncherModel
                        .getSortedWidgetsAndShortcuts(this));
            }
        } else {
            if (mAppsCustomizeContent != null) {
                mAppsCustomizeContent.setApps(apps);
                mAppsCustomizeContent.onPackagesUpdated(LauncherModel
                        .getSortedWidgetsAndShortcuts(this));
            }
        }
    }

    /**
     * A package was updated.
     * <p>
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindAppsUpdated(final ArrayList<AppInfo> apps) {
        Runnable r = () -> bindAppsUpdated(apps);
        if (waitUntilResume(r)) {
            return;
        }

        if (mWorkspace != null) {
            mWorkspace.updateShortcuts(apps);
        }

        if (!LauncherAppState.isDisableAllApps()
                && mAppsCustomizeContent != null) {
            mAppsCustomizeContent.updateApps(apps);
        }
    }

    /**
     * A package was uninstalled. We take both the super set of packageNames in
     * addition to specific applications to remove, the reason being that this
     * can be called when a package is updated as well. In that scenario, we
     * only remove specific components from the workspace, where as
     * package-removal should clear all items by package name.
     * <p>
     * Implementation of the method from LauncherModel.Callbacks.
     */
    public void bindComponentsRemoved(final ArrayList<String> packageNames,
                                      final ArrayList<AppInfo> appInfos) {
        Runnable r = () -> bindComponentsRemoved(packageNames, appInfos);
        if (waitUntilResume(r)) {
            return;
        }

        if (!packageNames.isEmpty()) {
            mWorkspace.removeItemsByPackageName(packageNames);
        }
        if (!appInfos.isEmpty()) {
            mWorkspace.removeItemsByApplicationInfo(appInfos);
        }

        // Notify the drag controller
        mDragController.onAppsRemoved(packageNames, appInfos);

        // Update AllApps
        if (!LauncherAppState.isDisableAllApps()
                && mAppsCustomizeContent != null) {
            mAppsCustomizeContent.removeApps(appInfos);
        }
    }

    /**
     * A number of packages were updated.
     */
    private ArrayList<Object> mWidgetsAndShortcuts;
    private final Runnable mBindPackagesUpdatedRunnable = new Runnable() {
        public void run() {
            bindPackagesUpdated(mWidgetsAndShortcuts);
            mWidgetsAndShortcuts = null;
        }
    };

    public void bindPackagesUpdated(final ArrayList<Object> widgetsAndShortcuts) {
        if (waitUntilResume(mBindPackagesUpdatedRunnable, true)) {
            mWidgetsAndShortcuts = widgetsAndShortcuts;
            return;
        }

        // Update the widgets pane
        if (mAppsCustomizeContent != null) {
            mAppsCustomizeContent.onPackagesUpdated(widgetsAndShortcuts);
        }
    }

    private int mapConfigurationOriActivityInfoOri(int configOri) {
        final Display d = getWindowManager().getDefaultDisplay();
        int naturalOri = Configuration.ORIENTATION_LANDSCAPE;
        switch (d.getRotation()) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                // We are currently in the same basic orientation as the natural
                // orientation
                naturalOri = configOri;
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                // We are currently in the other basic orientation to the natural
                // orientation
                naturalOri = (configOri == Configuration.ORIENTATION_LANDSCAPE) ? Configuration.ORIENTATION_PORTRAIT
                        : Configuration.ORIENTATION_LANDSCAPE;
                break;
        }

        int[] oriMap = {ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE};
        // Since the map starts at portrait, we need to offset if this device's
        // natural orientation
        // is landscape.
        int indexOffset = 0;
        if (naturalOri == Configuration.ORIENTATION_LANDSCAPE) {
            indexOffset = 1;
        }
        return oriMap[(d.getRotation() + indexOffset) % 4];
    }

    public boolean isRotationEnabled() {
        return sForceEnableRotation || getResources().getBoolean(R.bool.allow_rotation);
    }

    public void lockScreenOrientation() {
        if (isRotationEnabled()) {
            setRequestedOrientation(mapConfigurationOriActivityInfoOri(getResources()
                    .getConfiguration().orientation));
        }
    }

    public void unlockScreenOrientation(boolean immediate) {
        if (isRotationEnabled()) {
            if (immediate) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            } else {
                mHandler.postDelayed(() -> setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED), mRestoreScreenOrientationDelay);
            }
        }
    }


    protected boolean isLauncherPreinstalled() {
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(getComponentName()
                    .getPackageName(), 0);
            return (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (NameNotFoundException e) {

            return false;
        }
    }


    void showWorkspaceSearchAndHotseat() {
        if (mWorkspace != null)
            mWorkspace.setAlpha(1f);
        if (mHotseat != null)
            mHotseat.setAlpha(1f);
        if (mPageIndicators != null)
            mPageIndicators.setAlpha(1f);
        if (mSearchDropTargetBar != null)
            mSearchDropTargetBar.showSearchBar(false);
    }

    void hideWorkspaceSearchAndHotseat() {
        if (mWorkspace != null)
            mWorkspace.setAlpha(0f);
        if (mHotseat != null)
            mHotseat.setAlpha(0f);
        if (mPageIndicators != null)
            mPageIndicators.setAlpha(0f);
        if (mSearchDropTargetBar != null)
            mSearchDropTargetBar.hideSearchBar(false);
    }

    public ItemInfo createAppDragInfo(Intent appLaunchIntent) {
        ResolveInfo ri = getPackageManager().resolveActivity(appLaunchIntent, 0);
        if (ri == null) {
            return null;
        }
        return new AppInfo(getPackageManager(), ri, mIconCache, null);
    }

    public ItemInfo createShortcutDragInfo(Intent shortcutIntent, CharSequence caption, Bitmap icon) {
        return new ShortcutInfo(shortcutIntent, caption, icon);
    }

    public void startDrag(View dragView, ItemInfo dragInfo, DragSource source) {
        dragView.setTag(dragInfo);
        mWorkspace.onDragStartedWithItem(dragView);
        mWorkspace.beginDragShared(dragView, source);
    }

    /**
     * To avoid managing preference change listeners for various parts of the
     * launcher we simply kill the process and let it reload from scratch.
     */
    public boolean settingsChanged() {
        SharedPreferences prefs = getSharedPreferences(
                SettingsProvider.SETTINGS_KEY, Context.MODE_PRIVATE);
        boolean settingsChanged = prefs.getBoolean(
                SettingsProvider.SETTINGS_CHANGED, false);
        if (settingsChanged) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(SettingsProvider.SETTINGS_CHANGED, false);
            editor.apply();
        }
        mShouldRestart = true;
        return settingsChanged;
    }

    public AppsCustomizePagedView.SortMode getAppsCustomizeContentSortMode() {
        return mAppsCustomizeContent.getSortMode();
    }

    public boolean shouldShowSearchBar() {
        return mWorkspace.getShowSearchBar();
    }

    public boolean shouldHideWorkspaceIconLables() {
        return mWorkspace.getHideIconLables();
    }

    public String getWorkspaceTransitionEffect() {
        TransitionEffect effect = mWorkspace.getTransitionEffect();
        return effect == null ? TransitionEffect.TRANSITION_EFFECT_NONE
                : effect.getName();
    }

    public String getAppsCustomizeTransitionEffect() {
        TransitionEffect effect = mAppsCustomizeContent.getTransitionEffect();
        return effect == null ? TransitionEffect.TRANSITION_EFFECT_NONE
                : effect.getName();
    }

    public void updateDynamicGrid() {
        mSearchDropTargetBar.setupQSB(this);
        mSearchDropTargetBar.hideSearchBar(false);

        initializeDynamicGrid();

        mGrid.layout(this);
        mWorkspace.showOutlines();

        mModel.startLoader(true, mWorkspace.getCurrentPage());
    }

    private boolean shouldRestart() {
        try {
            if (mShouldRestart) {
                android.os.Process.killProcess(android.os.Process.myPid());
                finish();
                startActivity(getIntent());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}

//interface DebugIntents {
//    static final String DELETE_DATABASE = "com.roy.turbo.launcher.action.DELETE_DATABASE";
//    static final String MIGRATE_DATABASE = "com.roy.turbo.launcher.action.MIGRATE_DATABASE";
//}


