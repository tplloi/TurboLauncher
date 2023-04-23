package com.roy.android.gallery3d.glrenderer;

public class GLPaint {
    private int mColor = 0;

    public void setColor(int color) {
        mColor = color;
    }

    public int getColor() {
        return mColor;
    }

//    public void setLineWidth(float width) {
////        Assert.assertTrue(width >= 0);
//        mLineWidth = width;
//    }

    public float getLineWidth() {
        return 1f;
    }
}
