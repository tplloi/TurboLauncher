package com.roy.android.photos.views;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.gallery3d.glrenderer.BasicTexture;

public interface TileSource {

    /**
     * If the source does not care about the tile size, it should use
     * {@link TiledImageRenderer#suggestedTileSize(Context)}
     */
    public int getTileSize();

    public int getImageWidth();

    public int getImageHeight();

    public int getRotation();

    /**
     * Return a Preview image if available. This will be used as the base layer
     * if higher res tiles are not yet available
     */
    public BasicTexture getPreview();

    /**
     * The tile returned by this method can be specified this way: Assuming
     * the image size is (width, height), first take the intersection of (0,
     * 0) - (width, height) and (x, y) - (x + tileSize, y + tileSize). If
     * in extending the region, we found some part of the region is outside
     * the image, those pixels are filled with black.
     * <p>
     * If level > 0, it does the same operation on a down-scaled version of
     * the original image (down-scaled by a factor of 2^level), but (x, y)
     * still refers to the coordinate on the original image.
     * <p>
     * The method would be called by the decoder thread.
     */
    public Bitmap getTile(int level, int x, int y, Bitmap reuse);
}
