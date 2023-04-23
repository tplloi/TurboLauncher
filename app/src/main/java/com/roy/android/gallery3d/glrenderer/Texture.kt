package com.roy.android.gallery3d.glrenderer

// Texture is a rectangular image which can be drawn on GLCanvas.
// The isOpaque() function gives a hint about whether the texture is opaque,
// so the drawing can be done faster.
//
// This is the current texture hierarchy:
//
// Texture
// -- ColorTexture
// -- FadeInTexture
// -- BasicTexture
//    -- UploadedTexture
//       -- BitmapTexture
//       -- Tile
//       -- ResourceTexture
//          -- NinePatchTexture
//       -- CanvasTexture
//          -- StringTexture
//
interface Texture {
    val width: Int
    val height: Int
    fun draw(canvas: GLCanvas?, x: Int, y: Int)
    fun draw(canvas: GLCanvas?, x: Int, y: Int, w: Int, h: Int)
    val isOpaque: Boolean
}
