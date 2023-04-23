package com.roy.android.photos;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;

import com.android.gallery3d.common.BitmapUtils;
import com.android.gallery3d.common.Utils;
import com.android.gallery3d.exif.ExifInterface;
import com.android.gallery3d.glrenderer.BasicTexture;
import com.android.gallery3d.glrenderer.BitmapTexture;
import com.roy.android.photos.views.TileSource;
import com.roy.android.photos.views.TiledImageRenderer;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BitmapRegionTileSource implements TileSource {

    private static final String TAG = "BitmapRegionTileSource";

    private static final int GL_SIZE_LIMIT = 2048;
    // This must be no larger than half the size of the GL_SIZE_LIMIT
    // due to decodePreview being allowed to be up to 2x the size of the target
    public static final int MAX_PREVIEW_SIZE = GL_SIZE_LIMIT / 2;

    public static abstract class BitmapSource {
        private SimpleBitmapRegionDecoder mDecoder;
        private Bitmap mPreview;
        private final int mPreviewSize;
        private int mRotation;

        public enum State {NOT_LOADED, LOADED, ERROR_LOADING}

        private State mState = State.NOT_LOADED;

        public BitmapSource(int previewSize) {
            mPreviewSize = previewSize;
        }

        public void loadInBackground() {
            ExifInterface ei = new ExifInterface();
            if (readExif(ei)) {
                Integer ori = ei.getTagIntValue(ExifInterface.TAG_ORIENTATION);
                if (ori != null) {
                    mRotation = ExifInterface.getRotationForOrientationValue(ori.shortValue());
                }
            }
            mDecoder = loadBitmapRegionDecoder();
            if (mDecoder == null) {
                mState = State.ERROR_LOADING;
            } else {
                int width = mDecoder.getWidth();
                int height = mDecoder.getHeight();
                if (mPreviewSize != 0) {
                    int previewSize = Math.min(mPreviewSize, MAX_PREVIEW_SIZE);
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    opts.inPreferQualityOverSpeed = true;

                    float scale = (float) previewSize / Math.max(width, height);
                    opts.inSampleSize = BitmapUtils.computeSampleSizeLarger(scale);
                    opts.inJustDecodeBounds = false;
                    mPreview = loadPreviewBitmap(opts);
                }
                mState = State.LOADED;
            }
        }

        public State getLoadingState() {
            return mState;
        }

        public SimpleBitmapRegionDecoder getBitmapRegionDecoder() {
            return mDecoder;
        }

        public Bitmap getPreviewBitmap() {
            return mPreview;
        }

        public int getPreviewSize() {
            return mPreviewSize;
        }

        public int getRotation() {
            return mRotation;
        }

        public abstract boolean readExif(ExifInterface ei);

        public abstract SimpleBitmapRegionDecoder loadBitmapRegionDecoder();

        public abstract Bitmap loadPreviewBitmap(BitmapFactory.Options options);
    }

    public static class FilePathBitmapSource extends BitmapSource {
        private final String mPath;

        public FilePathBitmapSource(String path, int previewSize) {
            super(previewSize);
            mPath = path;
        }

        @Override
        public SimpleBitmapRegionDecoder loadBitmapRegionDecoder() {
            SimpleBitmapRegionDecoder d;
            d = SimpleBitmapRegionDecoderWrapper.newInstance(mPath, true);
            if (d == null) {
                d = DumbBitmapRegionDecoder.newInstance(mPath);
            }
            return d;
        }

        @Override
        public Bitmap loadPreviewBitmap(BitmapFactory.Options options) {
            return BitmapFactory.decodeFile(mPath, options);
        }

        @Override
        public boolean readExif(ExifInterface ei) {
            try {
                ei.readExif(mPath);
                return true;
            } catch (NullPointerException e) {
                Log.w("BitmapRegionTileSource", "reading exif failed", e);
                return false;
            } catch (IOException e) {
                Log.w("BitmapRegionTileSource", "getting decoder failed", e);
                return false;
            }
        }
    }

    public static class UriBitmapSource extends BitmapSource {
        private final Context mContext;
        private final Uri mUri;

        public UriBitmapSource(Context context, Uri uri, int previewSize) {
            super(previewSize);
            mContext = context;
            mUri = uri;
        }

        private InputStream regenerateInputStream() throws FileNotFoundException {
            InputStream is = mContext.getContentResolver().openInputStream(mUri);
            return new BufferedInputStream(is);
        }

        @Override
        public SimpleBitmapRegionDecoder loadBitmapRegionDecoder() {
            try {
                InputStream is = regenerateInputStream();
                SimpleBitmapRegionDecoder regionDecoder = SimpleBitmapRegionDecoderWrapper.newInstance(is, false);
                Utils.closeSilently(is);
                if (regionDecoder == null) {
                    is = regenerateInputStream();
                    regionDecoder = DumbBitmapRegionDecoder.newInstance(is);
                    Utils.closeSilently(is);
                }
                return regionDecoder;
            } catch (FileNotFoundException e) {
                Log.e("BitmapRegionTileSource", "Failed to load URI " + mUri, e);
                return null;
            }
        }

        @Override
        public Bitmap loadPreviewBitmap(BitmapFactory.Options options) {
            try {
                InputStream is = regenerateInputStream();
                Bitmap b = BitmapFactory.decodeStream(is, null, options);
                Utils.closeSilently(is);
                return b;
            } catch (FileNotFoundException e) {
                Log.e("BitmapRegionTileSource", "Failed to load URI " + mUri, e);
                return null;
            }
        }

        @Override
        public boolean readExif(ExifInterface ei) {
            InputStream is = null;
            try {
                is = regenerateInputStream();
                ei.readExif(is);
                Utils.closeSilently(is);
                return true;
            } catch (FileNotFoundException e) {
                Log.e("BitmapRegionTileSource", "Failed to load URI " + mUri, e);
                return false;
            } catch (IOException e) {
                Log.e("BitmapRegionTileSource", "Failed to load URI " + mUri, e);
                return false;
            } catch (NullPointerException e) {
                Log.e("BitmapRegionTileSource", "Failed to read EXIF for URI " + mUri, e);
                return false;
            } finally {
                Utils.closeSilently(is);
            }
        }
    }

    public static class ResourceBitmapSource extends BitmapSource {
        private final Resources mRes;
        private final int mResId;

        public ResourceBitmapSource(Resources res, int resId, int previewSize) {
            super(previewSize);
            mRes = res;
            mResId = resId;
        }

        private InputStream regenerateInputStream() {
            InputStream is = mRes.openRawResource(mResId);
            return new BufferedInputStream(is);
        }

        @Override
        public SimpleBitmapRegionDecoder loadBitmapRegionDecoder() {
            InputStream is = regenerateInputStream();
            SimpleBitmapRegionDecoder regionDecoder = SimpleBitmapRegionDecoderWrapper.newInstance(is, false);
            Utils.closeSilently(is);
            if (regionDecoder == null) {
                is = regenerateInputStream();
                regionDecoder = DumbBitmapRegionDecoder.newInstance(is);
                Utils.closeSilently(is);
            }
            return regionDecoder;
        }

        @Override
        public Bitmap loadPreviewBitmap(BitmapFactory.Options options) {
            return BitmapFactory.decodeResource(mRes, mResId, options);
        }

        @Override
        public boolean readExif(ExifInterface ei) {
            try {
                InputStream is = regenerateInputStream();
                ei.readExif(is);
                Utils.closeSilently(is);
                return true;
            } catch (IOException e) {
                Log.e("BitmapRegionTileSource", "Error reading resource", e);
                return false;
            }
        }
    }

    SimpleBitmapRegionDecoder mDecoder;
    int mWidth;
    int mHeight;
    int mTileSize;
    private BasicTexture mPreview;
    private final int mRotation;


    private final Rect mWantRegion = new Rect();

    private BitmapFactory.Options mOptions;


    public BitmapRegionTileSource(Context context, BitmapSource source) {
        mTileSize = TiledImageRenderer.suggestedTileSize(context);
        mRotation = source.getRotation();
        mDecoder = source.getBitmapRegionDecoder();
        if (mDecoder != null) {
            mWidth = mDecoder.getWidth();
            mHeight = mDecoder.getHeight();
            mOptions = new BitmapFactory.Options();
            mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            mOptions.inPreferQualityOverSpeed = true;
            mOptions.inTempStorage = new byte[16 * 1024];
            int previewSize = source.getPreviewSize();
            if (previewSize != 0) {
                previewSize = Math.min(previewSize, MAX_PREVIEW_SIZE);
                // Although this is the same size as the Bitmap that is likely already
                // loaded, the lifecycle is different and interactions are on a different
                // thread. Thus to simplify, this source will decode its own bitmap.
                Bitmap preview = decodePreview(source, previewSize);
                assert preview != null;
                if (preview.getWidth() <= GL_SIZE_LIMIT && preview.getHeight() <= GL_SIZE_LIMIT) {
                    mPreview = new BitmapTexture(preview);
                } else {
                    Log.w(TAG, String.format("Failed to create preview of apropriate size! " + " in: %dx%d, out: %dx%d", mWidth, mHeight, preview.getWidth(), preview.getHeight()));
                }
            }
        }
    }

    @Override
    public int getTileSize() {
        return mTileSize;
    }

    @Override
    public int getImageWidth() {
        return mWidth;
    }

    @Override
    public int getImageHeight() {
        return mHeight;
    }

    @Override
    public BasicTexture getPreview() {
        return mPreview;
    }

    @Override
    public int getRotation() {
        return mRotation;
    }

    @Override
    public Bitmap getTile(int level, int x, int y, Bitmap bitmap) {
        int tileSize = getTileSize();

        int t = tileSize << level;
        mWantRegion.set(x, y, x + t, y + t);

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888);
        }

        mOptions.inSampleSize = (1 << level);
        mOptions.inBitmap = bitmap;

        try {
            bitmap = mDecoder.decodeRegion(mWantRegion, mOptions);
        } finally {
            if (mOptions.inBitmap != bitmap && mOptions.inBitmap != null) {
                mOptions.inBitmap = null;
            }
        }

        if (bitmap == null) {
            Log.w("BitmapRegionTileSource", "fail in decoding region");
        }
        return bitmap;
    }


    /**
     * Note that the returned bitmap may have a long edge that's longer
     * than the targetSize, but it will always be less than 2x the targetSize
     */
    private Bitmap decodePreview(BitmapSource source, int targetSize) {
        Bitmap result = source.getPreviewBitmap();
        if (result == null) {
            return null;
        }

        // We need to resize down if the decoder does not support inSampleSize
        // or didn't support the specified inSampleSize (some decoders only do powers of 2)
        float scale = (float) targetSize / (float) (Math.max(result.getWidth(), result.getHeight()));

        if (scale <= 0.5) {
            result = BitmapUtils.resizeBitmapByScale(result, scale, true);
        }
        return ensureGLCompatibleBitmap(result);
    }

    private static Bitmap ensureGLCompatibleBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.getConfig() != null) {
            return bitmap;
        }
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        bitmap.recycle();
        return newBitmap;
    }
}

