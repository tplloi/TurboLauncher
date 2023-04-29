package com.roy.turbo.launcher;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a folder containing shortcuts or apps.
 */
class FolderInfo extends ItemInfo {

    /**
     * Whether this folder has been opened
     */
    boolean opened;

    /**
     * The apps and shortcuts and hidden status
     */
    ArrayList<ShortcutInfo> contents = new ArrayList<>();
    Boolean hidden = false;

    ArrayList<FolderListener> listeners = new ArrayList<>();

    FolderInfo() {
        itemType = LauncherSettings.Favorites.ITEM_TYPE_FOLDER;
    }

    /**
     * Add an app or shortcut
     */
    public void add(ShortcutInfo item) {
        contents.add(item);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onAdd(item);
        }
        itemsChanged();
    }

    /**
     * Remove an app or shortcut. Does not change the DB.
     */
    public void remove(ShortcutInfo item) {
        contents.remove(item);
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onRemove(item);
        }
        itemsChanged();
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onTitleChanged(title);
        }
    }

    @Override
    void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);
        values.put(LauncherSettings.Favorites.TITLE, title.toString());
        values.put(LauncherSettings.Favorites.HIDDEN, hidden ? 1 : 0);
    }

    void addListener(FolderListener listener) {
        listeners.add(listener);
    }

    void removeListener(FolderListener listener) {
        listeners.remove(listener);
    }

    void itemsChanged() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onItemsChanged();
        }
    }

    @Override
    void unbind() {
        super.unbind();
        listeners.clear();
    }

    interface FolderListener {
        void onAdd(ShortcutInfo item);

        void onRemove(ShortcutInfo item);

        void onTitleChanged(CharSequence title);

        void onItemsChanged();
    }

    @Override
    public String toString() {
        return "FolderInfo(id=" + this.id + " type=" + this.itemType
                + " container=" + this.container + " screen=" + screenId
                + " cellX=" + cellX + " cellY=" + cellY + " spanX=" + spanX
                + " spanY=" + spanY + " dropPos=" + Arrays.toString(dropPos) + ")";
    }
}
