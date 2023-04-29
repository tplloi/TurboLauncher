package com.roy.turbo.launcher.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.roy.android.gallery3d.glrenderer.BasicTexture
import com.roy.android.gallery3d.glrenderer.BitmapTexture
import com.roy.android.photos.views.TileSource
import com.roy.android.photos.views.TiledImageRenderer
import kotlin.math.min

class DrawableTileSource(context: Context?, d: Drawable, previewSize: Int) : TileSource {
    private val mTileSize: Int
    private val mPreviewSize: Int
    private val mDrawable: Drawable
    private var mPreview: BitmapTexture? = null

    init {
        mTileSize = TiledImageRenderer.suggestedTileSize(context)
        mDrawable = d
        mPreviewSize = min(previewSize, MAX_PREVIEW_SIZE)
    }

    override fun getTileSize(): Int {
        return mTileSize
    }

    override fun getImageWidth(): Int {
        return mDrawable.intrinsicWidth
    }

    override fun getImageHeight(): Int {
        return mDrawable.intrinsicHeight
    }

    override fun getRotation(): Int {
        return 0
    }

    override fun getPreview(): BasicTexture? {
        if (mPreviewSize == 0) {
            return null
        }
        if (mPreview == null) {
            var width = imageWidth.toFloat()
            var height = imageHeight.toFloat()
            while (width > MAX_PREVIEW_SIZE || height > MAX_PREVIEW_SIZE) {
                width /= 2f
                height /= 2f
            }
            val b = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            mDrawable.bounds = Rect(0, 0, width.toInt(), height.toInt())
            mDrawable.draw(c)
            c.setBitmap(null)
            mPreview = BitmapTexture(b)
        }
        return mPreview
    }

    override fun getTile(level: Int, x: Int, y: Int, bitmap: Bitmap?): Bitmap? {
        var mBitmap = bitmap
        val tileSize = tileSize
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(tileSize, tileSize, Bitmap.Config.ARGB_8888)
        }
        if (mBitmap == null) {
            return null
        }
        val c = Canvas(mBitmap)
        val bounds = Rect(0, 0, imageWidth, imageHeight)
        bounds.offset(-x, -y)
        mDrawable.bounds = bounds
        mDrawable.draw(c)
        c.setBitmap(null)
        return mBitmap
    }

    companion object {
        private const val GL_SIZE_LIMIT = 2048

        // This must be no larger than half the size of the GL_SIZE_LIMIT
        // due to decodePreview being allowed to be up to 2x the size of the target
        const val MAX_PREVIEW_SIZE = GL_SIZE_LIMIT / 2
    }
}
