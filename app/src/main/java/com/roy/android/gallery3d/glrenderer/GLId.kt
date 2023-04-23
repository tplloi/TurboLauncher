package com.roy.android.gallery3d.glrenderer

import javax.microedition.khronos.opengles.GL11

// This mimics corresponding GL functions.
interface GLId {
    fun generateTexture(): Int
    fun glGenBuffers(n: Int, buffers: IntArray?, offset: Int)
    fun glDeleteTextures(gl: GL11?, n: Int, textures: IntArray?, offset: Int)
    fun glDeleteBuffers(
        gl: GL11?,
        n: Int,
        buffers: IntArray?,
        offset: Int
    ) //    void glDeleteFramebuffers(GL11ExtensionPack gl11ep, int n, int[] buffers, int offset);
}
