package com.roy.android.gallery3d.exif

import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.math.min

internal class ByteBufferInputStream(
    private val mBuf: ByteBuffer
) : InputStream() {
    override fun read(): Int {
        return if (!mBuf.hasRemaining()) {
            -1
        } else mBuf.get().toInt() and 0xFF
    }

    override fun read(bytes: ByteArray, off: Int, len: Int): Int {
        var mLen = len
        if (!mBuf.hasRemaining()) {
            return -1
        }
        mLen = min(mLen, mBuf.remaining())
        mBuf[bytes, off, mLen]
        return mLen
    }
}
