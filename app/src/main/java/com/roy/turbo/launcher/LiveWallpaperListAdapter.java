package com.roy.turbo.launcher;

import android.annotation.SuppressLint;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//done 2023.04.29
public class LiveWallpaperListAdapter extends BaseAdapter implements ListAdapter {
    private static final String LOG_TAG = LiveWallpaperListAdapter.class.getSimpleName();

    private final LayoutInflater mInflater;
    private final PackageManager mPackageManager;

    private final List<LiveWallpaperTile> mWallpapers;

    @SuppressWarnings("unchecked")
    public LiveWallpaperListAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPackageManager = context.getPackageManager();

        List<ResolveInfo> list = mPackageManager.queryIntentServices(
                new Intent(WallpaperService.SERVICE_INTERFACE),
                PackageManager.GET_META_DATA);

        mWallpapers = new ArrayList<>();

        new LiveWallpaperEnumerator(context).execute(list);
    }

    public int getCount() {
        if (mWallpapers == null) {
            return 0;
        }
        return mWallpapers.size();
    }

    public LiveWallpaperTile getItem(int position) {
        return mWallpapers.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.wallpaper_picker_live_wallpaper_item, parent, false);
        } else {
            view = convertView;
        }

        WallpaperPickerActivity.setWallpaperItemPaddingToZero((FrameLayout) view);

        LiveWallpaperTile wallpaperInfo = mWallpapers.get(position);
        wallpaperInfo.setView(view);
        ImageView image = (ImageView) view.findViewById(R.id.wallpaper_image);
        ImageView icon = (ImageView) view.findViewById(R.id.wallpaper_icon);
        if (wallpaperInfo.mThumbnail != null) {
            image.setImageDrawable(wallpaperInfo.mThumbnail);
            icon.setVisibility(View.GONE);
        } else {
            icon.setImageDrawable(wallpaperInfo.mInfo.loadIcon(mPackageManager));
            icon.setVisibility(View.VISIBLE);
        }

        TextView label = (TextView) view.findViewById(R.id.wallpaper_item_label);
        label.setText(wallpaperInfo.mInfo.loadLabel(mPackageManager));

        return view;
    }

    public static class LiveWallpaperTile extends WallpaperPickerActivity.WallpaperTileInfo {
        private final Drawable mThumbnail;
        private final WallpaperInfo mInfo;

        public LiveWallpaperTile(
                Drawable thumbnail,
                WallpaperInfo info) {
            mThumbnail = thumbnail;
            mInfo = info;
        }

        @Override
        public void onClick(WallpaperPickerActivity a) {
            Intent preview = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            preview.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    mInfo.getComponent());
            a.onLiveWallpaperPickerLaunch(mInfo);
            a.startActivityForResultSafely(preview, WallpaperPickerActivity.PICK_LIVE_WALLPAPER);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LiveWallpaperEnumerator extends AsyncTask<List<ResolveInfo>, LiveWallpaperTile, Void> {
        private final Context mContext;
        private int mWallpaperPosition;

        public LiveWallpaperEnumerator(Context context) {
            super();
            mContext = context;
            mWallpaperPosition = 0;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<ResolveInfo>... params) {
            final PackageManager packageManager = mContext.getPackageManager();

            List<ResolveInfo> list = params[0];

            Collections.sort(list, new Comparator<>() {
                final Collator mCollator;

                {
                    mCollator = Collator.getInstance();
                }

                public int compare(ResolveInfo info1, ResolveInfo info2) {
                    return mCollator.compare(info1.loadLabel(packageManager),
                            info2.loadLabel(packageManager));
                }
            });

            for (ResolveInfo resolveInfo : list) {
                WallpaperInfo info;
                try {
                    info = new WallpaperInfo(mContext, resolveInfo);
                } catch (XmlPullParserException e) {
                    Log.w(LOG_TAG, "Skipping wallpaper " + resolveInfo.serviceInfo, e);
                    continue;
                } catch (IOException e) {
                    Log.w(LOG_TAG, "Skipping wallpaper " + resolveInfo.serviceInfo, e);
                    continue;
                }


                Drawable thumb = info.loadThumbnail(packageManager);
                Intent launchIntent = new Intent(WallpaperService.SERVICE_INTERFACE);
                launchIntent.setClassName(info.getPackageName(), info.getServiceName());
                LiveWallpaperTile wallpaper = new LiveWallpaperTile(thumb, info);
                publishProgress(wallpaper);
            }
            // Send a null object to show loading is finished
            publishProgress((LiveWallpaperTile) null);

            return null;
        }

        @Override
        protected void onProgressUpdate(LiveWallpaperTile...infos) {
            for (LiveWallpaperTile info : infos) {
                if (info == null) {
                    LiveWallpaperListAdapter.this.notifyDataSetChanged();
                    break;
                }
                if (info.mThumbnail != null) {
                    info.mThumbnail.setDither(true);
                }
                if (mWallpaperPosition < mWallpapers.size()) {
                    mWallpapers.set(mWallpaperPosition, info);
                } else {
                    mWallpapers.add(info);
                }
                mWallpaperPosition++;
            }
        }
    }
}
