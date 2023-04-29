package com.roy.turbo.launcher;

import android.graphics.Bitmap;

import com.roy.turbo.launcher.itf.AsyncTaskCallback;

import java.util.ArrayList;

public class AsyncTaskPageData {
    public enum Type {
        LoadWidgetPreviewData
    }

    AsyncTaskPageData(int p, ArrayList<Object> l, int cw, int ch, AsyncTaskCallback bgR,
                      AsyncTaskCallback postR, WidgetPreviewLoader w) {
        page = p;
        items = l;
        generatedImages = new ArrayList<>();
        maxImageWidth = cw;
        maxImageHeight = ch;
        doInBackgroundCallback = bgR;
        postExecuteCallback = postR;
        widgetPreviewLoader = w;
    }

    void cleanup(boolean cancelled) {
        // Clean up any references to source/generated bitmaps
        if (generatedImages != null) {
            if (cancelled) {
                for (int i = 0; i < generatedImages.size(); i++) {
                    widgetPreviewLoader.recycleBitmap(items.get(i), generatedImages.get(i));
                }
            }
            generatedImages.clear();
        }
    }

    int page;
    ArrayList<Object> items;
    //    ArrayList<Bitmap> sourceImages;
    ArrayList<Bitmap> generatedImages;
    int maxImageWidth;
    int maxImageHeight;
    public AsyncTaskCallback doInBackgroundCallback;
    public AsyncTaskCallback postExecuteCallback;
    WidgetPreviewLoader widgetPreviewLoader;
}
