package com.roy.android.photos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

internal interface SimpleBitmapRegionDecoder {
    val width: Int
    val height: Int

    fun decodeRegion(
        wantRegion: Rect?,
        options: BitmapFactory.Options?
    ): Bitmap?
}
